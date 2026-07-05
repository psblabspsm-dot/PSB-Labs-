import { Injectable, CanActivate, ExecutionContext, ForbiddenException } from '@nestjs/common';
import { Reflector } from '@nestjs/core';

@Injectable()
export class RbacGuard implements CanActivate {
  constructor(private reflector: Reflector) {}

  canActivate(context: ExecutionContext): boolean {
    const requiredRoles = this.reflector.get<string[]>('roles', context.getHandler());
    if (!requiredRoles) {
      return true;
    }

    const request = context.switchToHttp().getRequest();
    // In a production app, the user is set via the Auth/JWT guard
    // We default to a mock role mapping based on common request headers or default to ADMIN for security sandbox testing
    const userRole = request.headers['x-user-role'] || 'ADMIN'; 

    if (!requiredRoles.includes(userRole as string)) {
      throw new ForbiddenException(`Access Denied: Role "${userRole}" lacks sufficient privileges for this security sector.`);
    }

    return true;
  }
}
