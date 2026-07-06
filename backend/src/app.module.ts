import { Module, NestModule, MiddlewareConsumer } from '@nestjs/common';
import { JwtModule } from '@nestjs/jwt';
import { ThrottlerModule } from '@nestjs/throttler';
import { PassportModule } from '@nestjs/passport';
import { APP_INTERCEPTOR } from '@nestjs/core';

// Authentication Core & SSO Strategies
import { AuthController } from './auth/auth.controller';
import { AuthService } from './auth/auth.service';
import { GoogleStrategy } from './auth/strategies/google.strategy';
import { MicrosoftStrategy } from './auth/strategies/microsoft.strategy';

// Wallet Central
import { WalletController } from './wallet/wallet.controller';
import { WalletService } from './wallet/wallet.service';

// Multi-Gateway Payments Hub
import { GatewayController } from './gateway/gateway.controller';
import { GatewayService } from './gateway/gateway.service';

// B2B Marketplace Hub
import { MarketplaceController } from './marketplace/marketplace.controller';
import { MarketplaceService } from './marketplace/marketplace.service';

// Third-Party Enterprise APIs (Firebase, SMS OTP, Email, Google Maps)
import { IntegrationController } from './integration/integration.controller';
import { IntegrationService } from './integration/integration.service';

// Multi-Tenant SaaS Subsystems
import { TenantModule } from './tenant/tenant.module';

// Support & Ticketing Subsystem
import { SupportModule } from './support/support.module';

// Corporate Finance & Accounting Subsystem
import { FinanceModule } from './finance/finance.module';

// Security Operations, IAM, Monitoring & DR Subsystem
import { SecurityModule } from './security/security.module';

// Employee HRMS & Payroll Subsystem
import { HrmsModule } from './hrms/hrms.module';

// Enterprise Logging Suite
import { CorrelationIdMiddleware } from './common/logging/correlation-id.middleware';
import { WinstonLogger } from './common/logging/winston.logger';
import { HttpLoggingInterceptor } from './common/logging/http-logging.interceptor';
import { SentryService } from './common/logging/sentry.service';

@Module({
  imports: [
    // Passport configuration for Google and Microsoft strategy bindings
    PassportModule.register({ defaultStrategy: 'jwt' }),
    
    // JWT Module setup for security guards
    JwtModule.register({
      secret: process.env.JWT_SECRET || 'SURYA_CREDIT_SECURE_KEY_2026',
      signOptions: { expiresIn: '8h' },
    }),

    // Rate limiter configuration (100 operations max in 60s window)
    ThrottlerModule.forRoot([
      {
        ttl: 60000,
        limit: 100,
      },
    ]),

    // SaaS Multi-Tenant Registration
    TenantModule,

    // Support and Ticketing Multi-Tenant Submodule
    SupportModule,

    // Finance and Accounting Core
    FinanceModule,

    // Security operations core
    SecurityModule,

    // Employee HRMS & Payroll
    HrmsModule,
  ],
  controllers: [
    AuthController,
    WalletController,
    GatewayController,
    IntegrationController,
    MarketplaceController,
  ],
  providers: [
    AuthService,
    GoogleStrategy,
    MicrosoftStrategy,
    WalletService,
    GatewayService,
    IntegrationService,
    MarketplaceService,
    WinstonLogger,
    SentryService,
    {
      provide: APP_INTERCEPTOR,
      useClass: HttpLoggingInterceptor,
    },
  ],
})
export class AppModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(CorrelationIdMiddleware).forRoutes('*');
  }
}
