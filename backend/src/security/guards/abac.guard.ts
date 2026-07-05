import { Injectable, CanActivate, ExecutionContext, ForbiddenException } from '@nestjs/common';

@Injectable()
export class AbacGuard implements CanActivate {
  canActivate(context: ExecutionContext): boolean {
    const request = context.switchToHttp().getRequest();
    const sourceIp = request.ip || request.headers['x-forwarded-for'] || '127.0.0.1';
    
    // Example ABAC rule: Blacklist certain suspicious IP attributes or require specific tenant context headers
    const tenantId = request.headers['x-tenant-id'] || 'default-tenant-1';
    
    if (sourceIp === '185.220.101.5') { // Mock suspicious TOR node IP
      throw new ForbiddenException('Access Blocked: Source IP matches a known malicious botnet or exit node list.');
    }

    if (!tenantId) {
      throw new ForbiddenException('Access Blocked: Missing active multi-tenant identification headers.');
    }

    return true;
  }
}
