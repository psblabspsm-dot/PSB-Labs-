import {
  ExceptionFilter,
  Catch,
  ArgumentsHost,
  HttpException,
  HttpStatus,
  Logger,
} from '@nestjs/common';
import { Request, Response } from 'express';
import { SentryService } from '../logging/sentry.service';

@Catch()
export class GlobalExceptionFilter implements ExceptionFilter {
  private readonly logger = new Logger('GlobalExceptionFilter');

  constructor(private readonly sentryService: SentryService) {}

  catch(exception: any, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const request = ctx.getRequest<Request>();

    const status =
      exception instanceof HttpException
        ? exception.getStatus()
        : HttpStatus.INTERNAL_SERVER_ERROR;

    const message =
      exception instanceof HttpException
        ? exception.getResponse()
        : { message: exception?.message || 'Internal Server Error' };

    // Format response body consistently
    const responseBody = {
      statusCode: status,
      timestamp: new Date().toISOString(),
      path: request.url,
      method: request.method,
      error: typeof message === 'string' ? { message } : message,
    };

    // Log the exception locally via LoggerService
    const errStack = exception?.stack || '';
    const errMsg = exception?.message || String(exception);
    this.logger.error(
      `Unhandled API Exception [${request.method} ${request.url}]: ${errMsg}`,
      errStack,
    );

    // Dynamic Sentry ingestion with tag contexts
    try {
      this.sentryService.captureException(exception, `API:${request.method}:${request.path}`);
      this.sentryService.addBreadcrumb(
        `API exception occurred: ${errMsg}`,
        'api_error',
        'error',
      );
    } catch (sentryErr: any) {
      this.logger.warn(`Sentry capture exception failed: ${sentryErr.message}`);
    }

    // Send the JSON response to the client
    response.status(status).json(responseBody);
  }
}
