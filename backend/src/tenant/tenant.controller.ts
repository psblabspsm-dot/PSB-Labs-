import { Controller, Get, Post, Patch, Body, Param, Query, HttpCode, HttpStatus, Headers } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiHeader, ApiQuery, ApiBody } from '@nestjs/swagger';
import { TenantService, TenantMemoryState, WebhookDebugLog, FranchiseRecord } from './tenant.service';
import { CreateTenantDto, UpdateBrandingDto, UpdateFeatureFlagsDto, TriggerWebhookTestDto } from './tenant.dto';

@ApiTags('Multi-Tenant SaaS Core')
@Controller('api/v1/saas')
export class TenantController {
  constructor(private readonly tenantService: TenantService) {}

  @Get('tenants')
  @ApiOperation({ summary: 'List all multi-tenant white-label organizations' })
  @ApiResponse({ status: 200, description: 'List of registered SaaS tenants retrieved successfully' })
  async getTenants(): Promise<TenantMemoryState[]> {
    return this.tenantService.getAllTenants();
  }

  @Get('resolve')
  @ApiOperation({ summary: 'Resolve Tenant organization using subdomain or custom host headers' })
  @ApiQuery({ name: 'subdomain', required: true, example: 'alpha' })
  @ApiHeader({ name: 'host', required: false, description: 'Host domain header for custom domains mapping' })
  async resolve(
    @Query('subdomain') subdomain: string,
    @Headers('host') hostHeader?: string
  ): Promise<TenantMemoryState> {
    return this.tenantService.resolveTenant(subdomain, hostHeader);
  }

  @Get('tenants/:id')
  @ApiOperation({ summary: 'Fetch detail of specific Tenant organization' })
  async getTenantById(@Param('id') id: string): Promise<TenantMemoryState> {
    return this.tenantService.getTenantById(id);
  }

  @Post('tenants')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Provision new multi-tenant organization with standard sandbox defaults' })
  @ApiBody({ type: CreateTenantDto })
  async createTenant(@Body() dto: CreateTenantDto): Promise<TenantMemoryState> {
    return this.tenantService.createTenant(dto);
  }

  @Post('tenants/:id/toggle-status')
  @ApiOperation({ summary: 'Activate or deactivate a Tenant organization' })
  async toggleStatus(@Param('id') id: string, @Body('active') active: boolean): Promise<any> {
    return this.tenantService.toggleTenantStatus(id, active);
  }

  @Patch('tenants/:id/branding')
  @ApiOperation({ summary: 'Update custom branding elements (Logo, Colors, Templates) of Tenant' })
  @ApiBody({ type: UpdateBrandingDto })
  async updateBranding(@Param('id') id: string, @Body() dto: UpdateBrandingDto): Promise<TenantMemoryState> {
    return this.tenantService.updateBranding(id, dto);
  }

  @Patch('tenants/:id/feature-flags')
  @ApiOperation({ summary: 'Enable/disable workspace modules (AEPS, Loans, Credit, DMT) for Tenant' })
  @ApiBody({ type: UpdateFeatureFlagsDto })
  async updateFeatureFlags(@Param('id') id: string, @Body() dto: UpdateFeatureFlagsDto): Promise<TenantMemoryState> {
    return this.tenantService.updateFeatureFlags(id, dto);
  }

  @Post('tenants/:id/backup')
  @ApiOperation({ summary: 'Take snapshot isolation system backup of Tenant state' })
  async executeBackup(@Param('id') id: string): Promise<any> {
    return this.tenantService.executeBackup(id);
  }

  @Post('tenants/:id/restore')
  @ApiOperation({ summary: 'Restore Tenant state from backup id' })
  async executeRestore(@Param('id') id: string, @Body('backupId') backupId: string): Promise<any> {
    return this.tenantService.executeRestore(id, backupId);
  }

  // ----------------- FRANCHISE PORTAL ROUTES -----------------

  @Get('franchises')
  @ApiOperation({ summary: 'List registered regional franchises and territorial nodes' })
  async getFranchises(): Promise<FranchiseRecord[]> {
    return this.tenantService.getAllFranchises();
  }

  @Post('franchises')
  @ApiOperation({ summary: 'Onboard fresh regional franchise partner' })
  async registerFranchise(@Body() data: any): Promise<FranchiseRecord> {
    return this.tenantService.registerFranchise(data);
  }

  // ----------------- WEBHOOK DEBUGGER MODULE -----------------

  @Get('tenants/:id/webhooks')
  @ApiOperation({ summary: 'Fetch trace log records of incoming partner payloads for webhook debugger UI' })
  async getWebhookLogs(@Param('id') tenantId: string): Promise<WebhookDebugLog[]> {
    return this.tenantService.getWebhookLogs(tenantId);
  }

  @Post('tenants/:id/webhook-test')
  @ApiOperation({ summary: 'Simulate/Test incoming provider webhook with digital signature checking' })
  @ApiBody({ type: TriggerWebhookTestDto })
  async testIncomingWebhook(
    @Param('id') tenantId: string,
    @Body() dto: TriggerWebhookTestDto
  ): Promise<WebhookDebugLog> {
    return this.tenantService.testIncomingWebhook(tenantId, dto);
  }

  @Post('webhooks/retry/:id')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Re-trigger/Retry a failed webhook payload delivery' })
  async retryWebhookDelivery(@Param('id') logId: string): Promise<WebhookDebugLog> {
    return this.tenantService.retryWebhookDelivery(logId);
  }
}
