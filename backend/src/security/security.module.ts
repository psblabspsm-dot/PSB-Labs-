import { Module } from '@nestjs/common';
import { SecurityController } from './security.controller';
import { SecurityService } from './security.service';
import { RbacGuard } from './guards/rbac.guard';
import { AbacGuard } from './guards/abac.guard';

@Module({
  controllers: [SecurityController],
  providers: [
    SecurityService,
    RbacGuard,
    AbacGuard,
  ],
  exports: [SecurityService],
})
export class SecurityModule {}
