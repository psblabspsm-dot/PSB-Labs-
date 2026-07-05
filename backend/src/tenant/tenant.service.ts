import { Injectable, BadRequestException, NotFoundException, Logger } from '@nestjs/common';
import { CreateTenantDto, UpdateBrandingDto, UpdateFeatureFlagsDto, TriggerWebhookTestDto } from './tenant.dto';
import * as crypto from 'crypto';

export interface TenantMemoryState {
  id: string;
  name: string;
  subdomain: string;
  domain: string | null;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
  settings: {
    kycAutoApprove: boolean;
    autoIncreaseCredit: boolean;
    dailyBiReports: boolean;
    commissionSettle: boolean;
    lowCreditSms: boolean;
    invoiceMail: boolean;
  };
  branding: {
    companyName: string;
    logoUrl: string;
    splashScreenUrl: string;
    appIconUrl: string;
    primaryColor: string;
    secondaryColor: string;
    fontName: string;
    emailTemplate: string;
    smsTemplate: string;
    invoiceBranding: string;
    receiptBranding: string;
    termsAndConditions: string;
    privacyPolicy: string;
    supportEmail: string;
    supportPhone: string;
  };
  subscription: {
    plan: 'FREE' | 'STARTER' | 'PROFESSIONAL' | 'ENTERPRISE';
    billingCycle: 'MONTHLY' | 'ANNUAL';
    startDate: Date;
    endDate: Date;
    status: 'ACTIVE' | 'TRIAL' | 'EXPIRED' | 'SUSPENDED';
    price: number;
    autoRenew: boolean;
  };
  featureFlags: {
    marketplace: boolean;
    wallet: boolean;
    credit: boolean;
    aeps: boolean;
    dmt: boolean;
    bbps: boolean;
    recharge: boolean;
    pan: boolean;
    insurance: boolean;
    travel: boolean;
    loans: boolean;
    crm: boolean;
    ai: boolean;
    analytics: boolean;
  };
  analytics: {
    usersCount: number;
    transactionsCount: number;
    volumeProcessed: number;
    usageLimitPercentage: number;
  };
}

export interface WebhookDebugLog {
  id: string;
  tenantId: string;
  eventType: string;
  rawPayload: string;
  signatureHeader: string | null;
  signatureValid: boolean;
  deliveryStatus: 'SUCCESS' | 'FAILED';
  retryCount: number;
  errorMessage: string | null;
  retryLogs: Array<{
    attempt: number;
    timestamp: Date;
    status: 'SUCCESS' | 'FAILED';
    error: string | null;
  }>;
  createdAt: Date;
  updatedAt: Date;
}

export interface FranchiseRecord {
  id: string;
  name: string;
  ownerName: string;
  email: string;
  phoneNumber: string;
  region: string;
  walletBalance: number;
  creditAllocated: number;
  activeRetailers: number;
  revenueThisMonth: number;
  territoryLimit: string;
  createdAt: Date;
}

@Injectable()
export class TenantService {
  private readonly logger = new Logger('SuryaSaaSTenantCore');

  // In-Memory databases for SaaS multitenancy keeping state aligned
  private tenants: Record<string, TenantMemoryState> = {
    'tenant-default': {
      id: 'tenant-default',
      name: 'Surya Primary FinTech Hub',
      subdomain: 'main',
      domain: 'suryacredit.com',
      isActive: true,
      createdAt: new Date('2026-01-01'),
      updatedAt: new Date(),
      settings: {
        kycAutoApprove: true,
        autoIncreaseCredit: true,
        dailyBiReports: true,
        commissionSettle: true,
        lowCreditSms: true,
        invoiceMail: true,
      },
      branding: {
        companyName: 'Surya Credit Solutions',
        logoUrl: 'https://images.unsplash.com/photo-1599305445671-ac291c95aba9?q=80&w=300',
        splashScreenUrl: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=600',
        appIconUrl: 'https://images.unsplash.com/photo-1557683316-973673baf926?q=80&w=150',
        primaryColor: '#0F172A',
        secondaryColor: '#38BDF8',
        fontName: 'Space Grotesk',
        emailTemplate: 'Hi {{name}}, welcome to Surya!',
        smsTemplate: 'Surya OTP: {{otp}}.',
        invoiceBranding: 'Surya Credit Invoice',
        receiptBranding: 'Surya Merchant Receipt',
        termsAndConditions: 'https://suryacredit.com/terms',
        privacyPolicy: 'https://suryacredit.com/privacy',
        supportEmail: 'support@suryacredit.com',
        supportPhone: '+91 80 4912 3811',
      },
      subscription: {
        plan: 'ENTERPRISE',
        billingCycle: 'ANNUAL',
        startDate: new Date('2026-01-01'),
        endDate: new Date('2027-01-01'),
        status: 'ACTIVE',
        price: 9999,
        autoRenew: true,
      },
      featureFlags: {
        marketplace: true,
        wallet: true,
        credit: true,
        aeps: true,
        dmt: true,
        bbps: true,
        recharge: true,
        pan: true,
        insurance: true,
        travel: true,
        loans: true,
        crm: true,
        ai: true,
        analytics: true,
      },
      analytics: {
        usersCount: 1420,
        transactionsCount: 98124,
        volumeProcessed: 8904500,
        usageLimitPercentage: 42.5,
      },
    },
    'tenant-alpha': {
      id: 'tenant-alpha',
      name: 'Alpha Retail Franchise',
      subdomain: 'alpha',
      domain: 'alphafintech.in',
      isActive: true,
      createdAt: new Date('2026-03-15'),
      updatedAt: new Date(),
      settings: {
        kycAutoApprove: false,
        autoIncreaseCredit: true,
        dailyBiReports: false,
        commissionSettle: true,
        lowCreditSms: true,
        invoiceMail: true,
      },
      branding: {
        companyName: 'Alpha Finance Group',
        logoUrl: 'https://images.unsplash.com/photo-1606857521015-7f9fcf423740?q=80&w=300',
        splashScreenUrl: 'https://images.unsplash.com/photo-1639762681485-074b7f938ba0?q=80&w=600',
        appIconUrl: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=150',
        primaryColor: '#1E3A8A',
        secondaryColor: '#F59E0B',
        fontName: 'Inter',
        emailTemplate: 'Welcome to Alpha Ledger systems.',
        smsTemplate: 'Alpha OTP is: {{otp}}.',
        invoiceBranding: 'Alpha Invoice Sheet',
        receiptBranding: 'Alpha POS Slip',
        termsAndConditions: 'https://alphafintech.in/terms',
        privacyPolicy: 'https://alphafintech.in/privacy',
        supportEmail: 'desk@alphafintech.in',
        supportPhone: '+91 22 8493 0219',
      },
      subscription: {
        plan: 'PROFESSIONAL',
        billingCycle: 'MONTHLY',
        startDate: new Date('2026-03-15'),
        endDate: new Date('2026-08-15'),
        status: 'ACTIVE',
        price: 499,
        autoRenew: true,
      },
      featureFlags: {
        marketplace: true,
        wallet: true,
        credit: true,
        aeps: true,
        dmt: false,
        bbps: true,
        recharge: true,
        pan: false,
        insurance: false,
        travel: true,
        loans: false,
        crm: true,
        ai: false,
        analytics: true,
      },
      analytics: {
        usersCount: 85,
        transactionsCount: 2314,
        volumeProcessed: 450000,
        usageLimitPercentage: 23.1,
      },
    },
  };

  // In-Memory Webhook debugger history
  private webhookLogs: WebhookDebugLog[] = [
    {
      id: 'wh_dbg_9018',
      tenantId: 'tenant-default',
      eventType: 'payment.captured',
      rawPayload: '{"entity":"event","account_id":"acc_92182","event":"payment.captured","payload":{"payment":{"entity":{"id":"pay_8291","amount":500000,"currency":"INR","status":"captured","method":"upi"}}}}',
      signatureHeader: 't=1782910,v1=58bc79e6f362db80164c48972ca81938b812f8629087e9c56782',
      signatureValid: true,
      deliveryStatus: 'SUCCESS',
      retryCount: 0,
      errorMessage: null,
      retryLogs: [],
      createdAt: new Date('2026-07-03T11:45:00Z'),
      updatedAt: new Date('2026-07-03T11:45:00Z'),
    },
    {
      id: 'wh_dbg_4128',
      tenantId: 'tenant-default',
      eventType: 'payout.processed',
      rawPayload: '{"event":"payout.processed","data":{"payout":{"id":"pout_9281","amount":150000,"status":"processed","bank_reference":"RRN29103981"}}}',
      signatureHeader: 't=1782980,v1=error_mismatched_sig_payload',
      signatureValid: false,
      deliveryStatus: 'FAILED',
      retryCount: 3,
      errorMessage: 'Digital Signature verification mismatch. Secure signature validation failed.',
      retryLogs: [
        { attempt: 1, timestamp: new Date('2026-07-03T10:15:00Z'), status: 'FAILED', error: 'Signature mismatch' },
        { attempt: 2, timestamp: new Date('2026-07-03T10:17:00Z'), status: 'FAILED', error: 'Signature mismatch' },
        { attempt: 3, timestamp: new Date('2026-07-03T10:21:00Z'), status: 'FAILED', error: 'Signature mismatch' },
      ],
      createdAt: new Date('2026-07-03T10:15:00Z'),
      updatedAt: new Date('2026-07-03T10:21:00Z'),
    },
  ];

  // Franchise database records
  private franchises: FranchiseRecord[] = [
    {
      id: 'fran-01',
      name: 'Surya Franchise North West',
      ownerName: 'Subramanyam Bhat',
      email: 'north_west@suryacredit.com',
      phoneNumber: '+91 94812 39018',
      region: 'Karnataka - Mumbai Border',
      walletBalance: 850000.0,
      creditAllocated: 2000000.0,
      activeRetailers: 45,
      revenueThisMonth: 124500.00,
      territoryLimit: 'North Zone Hub',
      createdAt: new Date('2026-01-10'),
    },
    {
      id: 'fran-02',
      name: 'Alpha Bangalore Central Node',
      ownerName: 'Ananth Pai',
      email: 'central_blr@alphafintech.in',
      phoneNumber: '+91 80234 90123',
      region: 'Bangalore Urban Core',
      walletBalance: 320000.0,
      creditAllocated: 1000000.0,
      activeRetailers: 22,
      revenueThisMonth: 48900.00,
      territoryLimit: 'BBMP Corporate Limit',
      createdAt: new Date('2026-03-20'),
    },
  ];

  // Simulated backup vaults
  private backupVault: Record<string, string> = {};

  // ----------------- TENANT MANAGEMENT CORE -----------------

  async getAllTenants(): Promise<TenantMemoryState[]> {
    return Object.values(this.tenants);
  }

  async getTenantById(id: string): Promise<TenantMemoryState> {
    const tenant = this.tenants[id];
    if (!tenant) {
      throw new NotFoundException(`SaaS Tenant with ID: ${id} could not be resolved.`);
    }
    return tenant;
  }

  async resolveTenant(subdomain: string, hostHeader?: string): Promise<TenantMemoryState> {
    this.logger.log(`Resolving Tenant for subdomain: [${subdomain}] or domain: [${hostHeader}]`);
    
    // 1. Resolve by subdomain
    let tenant = Object.values(this.tenants).find(t => t.subdomain === subdomain);
    
    // 2. Resolve by custom host header
    if (!tenant && hostHeader) {
      tenant = Object.values(this.tenants).find(t => t.domain === hostHeader);
    }

    if (!tenant) {
      throw new NotFoundException(`Multi-Tenant Handshake Error. Host '${hostHeader || subdomain}' is not registered in Surya White-Label SaaS network.`);
    }

    if (!tenant.isActive) {
      throw new BadRequestException(`The tenant system '${tenant.name}' is currently deactivated by system compliance administration.`);
    }

    return tenant;
  }

  async createTenant(dto: CreateTenantDto): Promise<TenantMemoryState> {
    const existingSub = Object.values(this.tenants).find(t => t.subdomain === dto.subdomain);
    if (existingSub) {
      throw new BadRequestException(`Subdomain prefix '${dto.subdomain}' is already claimed.`);
    }

    const tenantId = `tenant-${Math.floor(100000 + Math.random() * 900000)}`;
    const freshTenant: TenantMemoryState = {
      id: tenantId,
      name: dto.name,
      subdomain: dto.subdomain,
      domain: dto.domain || null,
      isActive: true,
      createdAt: new Date(),
      updatedAt: new Date(),
      settings: {
        kycAutoApprove: true,
        autoIncreaseCredit: false,
        dailyBiReports: true,
        commissionSettle: true,
        lowCreditSms: true,
        invoiceMail: true,
      },
      branding: {
        companyName: dto.name,
        logoUrl: 'https://images.unsplash.com/photo-1599305445671-ac291c95aba9?q=80&w=300',
        splashScreenUrl: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=600',
        appIconUrl: 'https://images.unsplash.com/photo-1557683316-973673baf926?q=80&w=150',
        primaryColor: '#0F172A',
        secondaryColor: '#38BDF8',
        fontName: 'Inter',
        emailTemplate: `Hi {{name}}, Welcome to ${dto.name}!`,
        smsTemplate: `${dto.name} Alert: Your OTP is {{otp}}.`,
        invoiceBranding: `${dto.name} Invoice Ledger`,
        receiptBranding: `${dto.name} Payment Receipt`,
        termsAndConditions: 'https://suryacredit.com/terms',
        privacyPolicy: 'https://suryacredit.com/privacy',
        supportEmail: `support@${dto.subdomain}.suryacredit.com`,
        supportPhone: '+91 80 4000 0000',
      },
      subscription: {
        plan: dto.plan,
        billingCycle: 'MONTHLY',
        startDate: new Date(),
        endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // 30 days trial/billing
        status: dto.plan === 'FREE' ? 'ACTIVE' : 'TRIAL',
        price: dto.plan === 'ENTERPRISE' ? 999 : dto.plan === 'PROFESSIONAL' ? 499 : dto.plan === 'STARTER' ? 199 : 0,
        autoRenew: true,
      },
      featureFlags: {
        marketplace: true,
        wallet: true,
        credit: true,
        aeps: dto.plan !== 'FREE',
        dmt: dto.plan !== 'FREE',
        bbps: true,
        recharge: true,
        pan: dto.plan === 'PROFESSIONAL' || dto.plan === 'ENTERPRISE',
        insurance: dto.plan === 'PROFESSIONAL' || dto.plan === 'ENTERPRISE',
        travel: true,
        loans: dto.plan === 'ENTERPRISE',
        crm: true,
        ai: dto.plan === 'ENTERPRISE',
        analytics: dto.plan !== 'FREE',
      },
      analytics: {
        usersCount: 1,
        transactionsCount: 0,
        volumeProcessed: 0,
        usageLimitPercentage: 0,
      },
    };

    this.tenants[tenantId] = freshTenant;
    this.logger.log(`Multi-Tenant SaaS provision completed for [${dto.name}] on plan ${dto.plan}`);
    return freshTenant;
  }

  async toggleTenantStatus(id: string, active: boolean): Promise<any> {
    const tenant = await this.getTenantById(id);
    tenant.isActive = active;
    tenant.updatedAt = new Date();
    this.logger.warn(`Tenant state altered for [${tenant.name}] -> Active: ${active}`);
    return {
      tenantId: id,
      isActive: tenant.isActive,
      message: tenant.isActive ? 'Tenant activation completed.' : 'Tenant deactivation applied.',
    };
  }

  async updateBranding(id: string, dto: UpdateBrandingDto): Promise<TenantMemoryState> {
    const tenant = await this.getTenantById(id);
    tenant.branding = {
      ...tenant.branding,
      ...dto,
    };
    tenant.updatedAt = new Date();
    this.logger.log(`Tenant custom branding updated for [${tenant.name}]`);
    return tenant;
  }

  async updateFeatureFlags(id: string, dto: UpdateFeatureFlagsDto): Promise<TenantMemoryState> {
    const tenant = await this.getTenantById(id);
    tenant.featureFlags = {
      ...tenant.featureFlags,
      ...dto,
    };
    tenant.updatedAt = new Date();
    this.logger.log(`Tenant feature toggles refreshed for [${tenant.name}]`);
    return tenant;
  }

  async executeBackup(id: string): Promise<any> {
    const tenant = await this.getTenantById(id);
    const payload = JSON.stringify(tenant);
    const backupId = `bkp_${tenant.subdomain}_${Date.now()}`;
    this.backupVault[backupId] = payload;
    this.logger.log(`Secure system backup completed for tenant [${tenant.name}]. BackupID: ${backupId}`);
    return {
      backupId,
      timestamp: new Date(),
      fileSize: `${Buffer.byteLength(payload, 'utf8')} bytes`,
      message: 'Isolation snapshot database backup completed successfully.',
    };
  }

  async executeRestore(id: string, backupId: string): Promise<any> {
    const rawData = this.backupVault[backupId];
    if (!rawData) {
      throw new NotFoundException(`Restore failed. Backup file with identifier '${backupId}' not found.`);
    }
    const tenantData = JSON.parse(rawData) as TenantMemoryState;
    this.tenants[id] = tenantData;
    this.logger.warn(`Tenant restored from backup [${backupId}] into Tenant [${tenantData.name}]`);
    return {
      restoredTenantId: id,
      restoredAt: new Date(),
      message: 'Restore completed. Subsystem database states synced successfully.',
    };
  }

  // ----------------- FRANCHISE PORTAL CORE -----------------

  async getAllFranchises(): Promise<FranchiseRecord[]> {
    return this.franchises;
  }

  async registerFranchise(data: Partial<FranchiseRecord>): Promise<FranchiseRecord> {
    const franId = `fran-${Math.floor(100 + Math.random() * 900)}`;
    const newFran: FranchiseRecord = {
      id: franId,
      name: data.name || 'New Regional Franchise',
      ownerName: data.ownerName || 'Unknown Partner',
      email: data.email || 'partner@suryacredit.com',
      phoneNumber: data.phoneNumber || '+91 90000 00000',
      region: data.region || 'Default Zone Area',
      walletBalance: 0,
      creditAllocated: data.creditAllocated || 500000,
      activeRetailers: 0,
      revenueThisMonth: 0,
      territoryLimit: data.territoryLimit || 'Unassigned',
      createdAt: new Date(),
    };
    this.franchises.push(newFran);
    return newFran;
  }

  // ----------------- WEBHOOK DEBUGGER CENTRAL -----------------

  async getWebhookLogs(tenantId: string): Promise<WebhookDebugLog[]> {
    // Return logs corresponding to this tenant
    return this.webhookLogs.filter(log => log.tenantId === tenantId);
  }

  async getWebhookLogById(id: string): Promise<WebhookDebugLog> {
    const log = this.webhookLogs.find(l => l.id === id);
    if (!log) {
      throw new NotFoundException(`Webhook debugger trace with ID: ${id} could not be resolved.`);
    }
    return log;
  }

  async testIncomingWebhook(tenantId: string, dto: TriggerWebhookTestDto): Promise<WebhookDebugLog> {
    this.logger.log(`Simulating dynamic webhook debugger route: event ${dto.event}`);

    // Check signature validity
    let isSigValid = false;
    if (dto.signatureHeader) {
      // Simulate checking real SHA256 Webhook Signing Secret Key validation
      isSigValid = dto.signatureHeader.includes('v1=58bc') || !dto.signatureHeader.includes('error');
    }

    const logId = `wh_dbg_${Math.floor(100000 + Math.random() * 900000)}`;
    const freshLog: WebhookDebugLog = {
      id: logId,
      tenantId,
      eventType: dto.event,
      rawPayload: JSON.stringify(dto.payload),
      signatureHeader: dto.signatureHeader || null,
      signatureValid: isSigValid,
      deliveryStatus: isSigValid ? 'SUCCESS' : 'FAILED',
      retryCount: isSigValid ? 0 : 1,
      errorMessage: isSigValid ? null : 'Digital signature check failed. Rejected webhook processing.',
      retryLogs: isSigValid ? [] : [
        {
          attempt: 1,
          timestamp: new Date(),
          status: 'FAILED',
          error: 'Digital signature mismatch validation error.',
        }
      ],
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    this.webhookLogs.unshift(freshLog);
    return freshLog;
  }

  async retryWebhookDelivery(id: string): Promise<WebhookDebugLog> {
    const log = await this.getWebhookLogById(id);
    
    // Simulate retrying payload dispatch internally or checking signature again
    log.retryCount += 1;
    log.updatedAt = new Date();

    const attemptNumber = log.retryCount;
    
    // If we've hit 4 retries, let's say it succeeded for debugging convenience
    const isFixed = attemptNumber >= 4;
    
    if (isFixed) {
      log.deliveryStatus = 'SUCCESS';
      log.errorMessage = null;
      log.signatureValid = true;
      log.retryLogs.push({
        attempt: attemptNumber,
        timestamp: new Date(),
        status: 'SUCCESS',
        error: null,
      });
    } else {
      log.retryLogs.push({
        attempt: attemptNumber,
        timestamp: new Date(),
        status: 'FAILED',
        error: 'Simulation mode: Retrying payload routing but connection dropped.',
      });
    }

    return log;
  }
}
