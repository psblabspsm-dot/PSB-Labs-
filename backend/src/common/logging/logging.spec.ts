import { Test, TestingModule } from '@nestjs/testing';
import { ExecutionContext, CallHandler } from '@nestjs/common';
import { of, throwError } from 'rxjs';
import { CorrelationIdMiddleware, correlationIdStorage } from './correlation-id.middleware';
import { WinstonLogger } from './winston.logger';
import { HttpLoggingInterceptor } from './http-logging.interceptor';
import { Request, Response } from 'express';

describe('Enterprise Logging & Audit Suite', () => {
  let middleware: CorrelationIdMiddleware;
  let interceptor: HttpLoggingInterceptor;
  let logger: WinstonLogger;

  const mockLogger = {
    log: jest.fn(),
    error: jest.fn(),
    warn: jest.fn(),
    debug: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        CorrelationIdMiddleware,
        HttpLoggingInterceptor,
        {
          provide: WinstonLogger,
          useValue: mockLogger,
        },
      ],
    }).compile();

    middleware = module.get<CorrelationIdMiddleware>(CorrelationIdMiddleware);
    interceptor = module.get<HttpLoggingInterceptor>(HttpLoggingInterceptor);
    logger = module.get<WinstonLogger>(WinstonLogger);

    jest.clearAllMocks();
  });

  describe('CorrelationIdMiddleware', () => {
    it('should generate a new UUID correlation ID if not present in request headers', () => {
      const mockReq = {
        header: jest.fn().mockReturnValue(undefined),
      } as unknown as Request;

      const mockRes = {
        setHeader: jest.fn(),
      } as unknown as Response;

      const mockNext = jest.fn();

      middleware.use(mockReq, mockRes, mockNext);

      expect(mockReq['correlationId']).toBeDefined();
      expect(typeof mockReq['correlationId']).toBe('string');
      expect(mockRes.setHeader).toHaveBeenCalledWith('X-Correlation-ID', mockReq['correlationId']);
      expect(mockNext).toHaveBeenCalled();
    });

    it('should reuse existing x-correlation-id header if provided by external client', () => {
      const existingCid = 'client-provided-correlation-uuid-2026';
      const mockReq = {
        header: jest.fn().mockReturnValue(existingCid),
      } as unknown as Request;

      const mockRes = {
        setHeader: jest.fn(),
      } as unknown as Response;

      const mockNext = jest.fn();

      middleware.use(mockReq, mockRes, mockNext);

      expect(mockReq['correlationId']).toBe(existingCid);
      expect(mockRes.setHeader).toHaveBeenCalledWith('X-Correlation-ID', existingCid);
      expect(mockNext).toHaveBeenCalled();
    });
  });

  describe('HttpLoggingInterceptor', () => {
    let mockExecutionContext: ExecutionContext;
    let mockCallHandler: CallHandler;

    beforeEach(() => {
      const mockReq = {
        method: 'POST',
        originalUrl: '/api/v1/wallet/credit-limit',
        ip: '127.0.0.1',
        get: jest.fn().mockReturnValue('Mozilla/5.0'),
      };

      const mockRes = {
        statusCode: 201,
      };

      mockExecutionContext = {
        switchToHttp: () => ({
          getRequest: () => mockReq,
          getResponse: () => mockRes,
        }),
      } as unknown as ExecutionContext;

      mockCallHandler = {
        handle: () => of({ success: true }),
      } as CallHandler;
    });

    it('should log request onset, log successful completions, and measure latency', (done) => {
      interceptor.intercept(mockExecutionContext, mockCallHandler).subscribe({
        next: () => {
          expect(mockLogger.log).toHaveBeenCalledTimes(2);
          expect(mockLogger.log.mock.calls[0][0]).toContain('--> POST /api/v1/wallet/credit-limit');
          expect(mockLogger.log.mock.calls[1][0]).toContain('<-- POST /api/v1/wallet/credit-limit [Status: 201]');
          done();
        },
        error: (err) => done(err),
      });
    });

    it('should intercept exceptions, log errors with stack trace, and propagate the error', (done) => {
      const sampleError = new Error('Database connection failed');
      const mockFaultyCallHandler = {
        handle: () => throwError(() => sampleError),
      } as CallHandler;

      interceptor.intercept(mockExecutionContext, mockFaultyCallHandler).subscribe({
        next: () => {
          done(new Error('Should have thrown an error'));
        },
        error: (err) => {
          expect(err).toBe(sampleError);
          expect(mockLogger.error).toHaveBeenCalled();
          expect(mockLogger.error.mock.calls[0][0]).toContain('<-- POST /api/v1/wallet/credit-limit [Status: 500] - Error: Database connection failed');
          done();
        },
      });
    });
  });
});
