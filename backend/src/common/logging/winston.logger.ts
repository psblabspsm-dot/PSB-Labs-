import { LoggerService, Injectable } from '@nestjs/common';
import * as winston from 'winston';
import { correlationIdStorage } from './correlation-id.middleware';

@Injectable()
export class WinstonLogger implements LoggerService {
  private logger: winston.Logger;

  constructor() {
    const logFormat = winston.format.combine(
      winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
      winston.format.errors({ stack: true }),
      winston.format.json(),
    );

    this.logger = winston.createLogger({
      level: process.env.NODE_ENV === 'production' ? 'info' : 'debug',
      format: logFormat,
      defaultMeta: { service: 'surya-credit-core' },
      transports: [
        new winston.transports.Console({
          format: winston.format.combine(
            winston.format.colorize(),
            winston.format.printf(({ timestamp, level, message, context, stack, ...meta }) => {
              const correlationId = correlationIdStorage.getStore();
              const cidStr = correlationId ? `[CID: ${correlationId}] ` : '';
              const contextStr = context ? `[${context}] ` : '';
              const stackStr = stack ? `\n${stack}` : '';
              const metaStr = Object.keys(meta).length ? ` ${JSON.stringify(meta)}` : '';
              return `${timestamp} ${level}: ${cidStr}${contextStr}${message}${metaStr}${stackStr}`;
            }),
          ),
        }),
      ],
    });
  }

  log(message: any, context?: string) {
    this.logger.info(message, { context });
  }

  error(message: any, trace?: string, context?: string) {
    this.logger.error(message, { stack: trace, context });
  }

  warn(message: any, context?: string) {
    this.logger.warn(message, { context });
  }

  debug(message: any, context?: string) {
    this.logger.debug(message, { context });
  }

  verbose(message: any, context?: string) {
    this.logger.verbose(message, { context });
  }
}
