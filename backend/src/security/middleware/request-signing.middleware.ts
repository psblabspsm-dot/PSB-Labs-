import { Injectable, NestMiddleware, ForbiddenException } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';
import * as crypto from 'crypto';

@Injectable()
export class RequestSigningMiddleware implements NestMiddleware {
  use(req: Request, res: Response, next: NextFunction) {
    const signature = req.headers['x-surya-signature'];
    const timestamp = req.headers['x-surya-timestamp'];
    
    // In a strict signed API environment, we check that requests have valid signatures.
    // If a signature is provided, we verify it against a SHA256 digest of body + timestamp.
    if (signature) {
      const bodyStr = req.body ? JSON.stringify(req.body) : '';
      const expectedSignature = crypto
        .createHmac('sha256', process.env.HMAC_SECRET || 'SURYA_CREDIT_SECRET_SIGNING_2026')
        .update(`${timestamp}.${bodyStr}`)
        .digest('hex');

      if (signature !== expectedSignature) {
        throw new ForbiddenException('Invalid cryptographic request signature. SHA-256 check failed.');
      }
    }
    
    next();
  }
}
