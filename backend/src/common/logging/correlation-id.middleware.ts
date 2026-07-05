import { Injectable, NestMiddleware } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';
import { AsyncLocalStorage } from 'async_hooks';
import * as crypto from 'crypto';

export const correlationIdStorage = new AsyncLocalStorage<string>();

@Injectable()
export class CorrelationIdMiddleware implements NestMiddleware {
  use(req: Request, res: Response, next: NextFunction) {
    const correlationIdHeader = req.header('x-correlation-id') || req.header('X-Correlation-Id');
    const correlationId = correlationIdHeader || crypto.randomUUID();
    
    // Attach to request and response
    req['correlationId'] = correlationId;
    res.setHeader('X-Correlation-ID', correlationId);
    
    correlationIdStorage.run(correlationId, () => {
      next();
    });
  }
}
