import { Injectable, NestInterceptor, ExecutionContext, CallHandler, HttpStatus } from '@nestjs/common';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { WinstonLogger } from './winston.logger';

@Injectable()
export class HttpLoggingInterceptor implements NestInterceptor {
  constructor(private readonly logger: WinstonLogger) {}

  intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
    const httpContext = context.switchToHttp();
    const req = httpContext.getRequest();
    const res = httpContext.getResponse();
    
    const { method, originalUrl, ip } = req;
    const userAgent = req.get('user-agent') || '';
    const startTime = Date.now();

    this.logger.log(`--> ${method} ${originalUrl} [Client-IP: ${ip}] [Agent: ${userAgent}]`, 'HttpTraffic');

    return next.handle().pipe(
      tap(() => {
        const duration = Date.now() - startTime;
        const statusCode = res.statusCode;
        this.logger.log(`<-- ${method} ${originalUrl} [Status: ${statusCode}] [Duration: ${duration}ms]`, 'HttpTraffic');
      }),
      catchError((error) => {
        const duration = Date.now() - startTime;
        const statusCode = error.status || HttpStatus.INTERNAL_SERVER_ERROR;
        this.logger.error(
          `<-- ${method} ${originalUrl} [Status: ${statusCode}] [Duration: ${duration}ms] - Error: ${error.message}`,
          error.stack,
          'HttpTraffic'
        );
        return throwError(() => error);
      }),
    );
  }
}
