import { 
  Controller, 
  Get, 
  Post, 
  Patch, 
  Delete, 
  Body, 
  Param, 
  Query, 
  Headers, 
  UseGuards 
} from '@nestjs/common';
import { SecurityService } from './security.service';
import { 
  CreateSecurityEventDto, 
  ResolveEventDto, 
  CreateApiKeyDto, 
  CreateDeviceDto, 
  TriggerBackupDto, 
  TriggerDrDto 
} from './security.dto';
import { RbacGuard } from './guards/rbac.guard';
import { AbacGuard } from './guards/abac.guard';

@Controller('api/v1/security')
@UseGuards(RbacGuard, AbacGuard)
export class SecurityController {
  constructor(private readonly securityService: SecurityService) {}

  // ---------------------------------------------------------------------------
  // 1. SOC INCIDENTS & THREAT DETECTION
  // ---------------------------------------------------------------------------
  @Get('soc/alerts')
  async getAlerts(@Headers('x-tenant-id') tenantId: string) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.getSecurityEvents(tId);
  }

  @Post('soc/alerts')
  async createAlert(
    @Headers('x-tenant-id') tenantId: string,
    @Body() dto: CreateSecurityEventDto
  ) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.createSecurityEvent(tId, dto);
  }

  @Patch('soc/alerts/:id/resolve')
  async resolveAlert(
    @Headers('x-tenant-id') tenantId: string,
    @Param('id') id: string,
    @Body() dto: ResolveEventDto
  ) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.resolveSecurityEvent(tId, id, dto);
  }

  // ---------------------------------------------------------------------------
  // 2. IAM & SESSION MANAGEMENT
  // ---------------------------------------------------------------------------
  @Get('iam/sessions')
  async getSessions(@Headers('x-tenant-id') tenantId: string) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.getSessions(tId);
  }

  @Delete('iam/sessions/:id')
  async terminateSession(
    @Headers('x-tenant-id') tenantId: string,
    @Param('id') id: string
  ) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.terminateSession(tId, id);
  }

  @Get('iam/devices')
  async getDevices(@Headers('x-tenant-id') tenantId: string) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.getDevices(tId);
  }

  @Post('iam/devices')
  async registerDevice(
    @Headers('x-tenant-id') tenantId: string,
    @Headers('x-user-id') userId: string,
    @Body() dto: CreateDeviceDto
  ) {
    const tId = tenantId || 'default-tenant-1';
    const uId = userId || 'user-id-1';
    return this.securityService.registerDevice(tId, uId, dto);
  }

  @Patch('iam/devices/:id/revoke')
  async revokeDevice(
    @Headers('x-tenant-id') tenantId: string,
    @Param('id') id: string
  ) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.revokeDevice(tId, id);
  }

  @Patch('iam/devices/:id/trust')
  async toggleTrustedDevice(
    @Headers('x-tenant-id') tenantId: string,
    @Headers('x-user-id') userId: string,
    @Param('id') id: string
  ) {
    const tId = tenantId || 'default-tenant-1';
    const uId = userId || 'user-id-1';
    return this.securityService.toggleTrustedDevice(tId, id, uId);
  }

  @Get('iam/devices/trusted')
  async getTrustedDevices(
    @Headers('x-tenant-id') tenantId: string,
    @Headers('x-user-id') userId: string
  ) {
    const tId = tenantId || 'default-tenant-1';
    const uId = userId || 'user-id-1';
    return this.securityService.getTrustedDevices(tId, uId);
  }

  // ---------------------------------------------------------------------------
  // 3. DEVELOPER API KEYS
  // ---------------------------------------------------------------------------
  @Get('iam/apikeys')
  async getApiKeys(@Headers('x-tenant-id') tenantId: string) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.getApiKeys(tId);
  }

  @Post('iam/apikeys')
  async createApiKey(
    @Headers('x-tenant-id') tenantId: string,
    @Body() dto: CreateApiKeyDto
  ) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.createApiKey(tId, dto);
  }

  @Delete('iam/apikeys/:id')
  async revokeApiKey(
    @Headers('x-tenant-id') tenantId: string,
    @Param('id') id: string
  ) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.revokeApiKey(tId, id);
  }

  // ---------------------------------------------------------------------------
  // 4. OBSERVABILITY METRICS & LOGS
  // ---------------------------------------------------------------------------
  @Get('observability/metrics')
  async getMetrics() {
    return this.securityService.getMonitoringMetrics();
  }

  @Get('observability/logs')
  async getLogs(
    @Headers('x-tenant-id') tenantId: string,
    @Query('type') type: 'ACCESS' | 'SYSTEM' | 'SECURITY',
    @Query('search') search?: string,
    @Query('level') level?: string,
    @Query('module') module?: string
  ) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.queryLogs(tId, type || 'SYSTEM', search, level, module);
  }

  // ---------------------------------------------------------------------------
  // 5. CONTINUITY: BACKUPS & DISASTER RECOVERY
  // ---------------------------------------------------------------------------
  @Get('continuity/backups')
  async getBackups() {
    return this.securityService.getBackupJobs();
  }

  @Post('continuity/backups')
  async triggerBackup(
    @Headers('x-tenant-id') tenantId: string,
    @Body() dto: TriggerBackupDto
  ) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.triggerBackup(tId, dto);
  }

  @Get('continuity/dr')
  async getDrRuns() {
    return this.securityService.getDrRecords();
  }

  @Post('continuity/dr')
  async triggerDrSimulation(
    @Headers('x-tenant-id') tenantId: string,
    @Body() dto: TriggerDrDto
  ) {
    const tId = tenantId || 'default-tenant-1';
    return this.securityService.triggerDrSimulation(tId, dto);
  }
}
