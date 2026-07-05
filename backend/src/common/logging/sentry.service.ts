import { Injectable, OnModuleInit, Logger } from '@nestjs/common';
import * as Sentry from '@sentry/node';

@Injectable()
export class SentryService implements OnModuleInit {
  private readonly logger = new Logger('SentryService');
  private isInitialized = false;

  onModuleInit() {
    const dsn = process.env.SENTRY_DSN;
    if (!dsn) {
      this.logger.warn('SENTRY_DSN is not set. Sentry error tracking is disabled (running in sandbox fallback mode).');
      return;
    }

    try {
      Sentry.init({
        dsn: dsn,
        environment: process.env.NODE_ENV || 'development',
        tracesSampleRate: 1.0,
      });
      this.isInitialized = true;
      this.logger.log('Sentry SDK successfully initialized for real-time production error telemetry.');
    } catch (err: any) {
      this.logger.error(`Failed to initialize Sentry SDK: ${err.message}`);
    }
  }

  captureException(exception: any, context?: string) {
    if (this.isInitialized) {
      Sentry.withScope((scope) => {
        if (context) {
          scope.setTag('context', context);
        }
        Sentry.captureException(exception);
      });
    } else {
      // Graceful local debug logging if Sentry is disabled or offline
      this.logger.debug(`[Sentry DryRun] Captured exception in ${context || 'unknown'}: ${exception?.message || exception}`);
    }
  }

  addBreadcrumb(message: string, category?: string, level: 'info' | 'warning' | 'error' = 'info') {
    if (this.isInitialized) {
      Sentry.addBreadcrumb({
        message,
        category,
        level: level as Sentry.SeverityLevel,
      });
    }
  }
}
