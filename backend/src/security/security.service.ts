import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { 
  CreateSecurityEventDto, 
  ResolveEventDto, 
  CreateApiKeyDto, 
  CreateDeviceDto, 
  TriggerBackupDto, 
  TriggerDrDto 
} from './security.dto';
import * as crypto from 'crypto';

export interface SecurityEvent {
  id: string;
  tenantId: string;
  userId: string | null;
  eventType: string; // THREAT, INTRUSION, ANOMALY, FAILED_LOGIN, WALLET_FRAUD, CREDIT_ABUSE
  severity: string; // LOW, MEDIUM, HIGH, CRITICAL
  status: 'OPEN' | 'UNDER_INVESTIGATION' | 'RESOLVED' | 'IGNORED';
  sourceIp: string;
  userAgent: string;
  description: string;
  details: string | null; // JSON details
  remediation: string | null;
  createdAt: Date;
  updatedAt: Date;
}

export interface LoginSession {
  id: string;
  tenantId: string;
  userId: string;
  email: string;
  mfaVerified: boolean;
  mpinUsed: boolean;
  biometricUsed: boolean;
  ipAddress: string;
  userAgent: string;
  location: string;
  deviceFingerprint: string;
  isActive: boolean;
  expiresAt: Date;
  createdAt: Date;
}

export interface Device {
  id: string;
  tenantId: string;
  userId: string;
  deviceName: string;
  deviceType: string; // MOBILE, TABLET, DESKTOP
  osVersion: string;
  pushToken: string | null;
  fingerprint: string;
  status: 'ACTIVE' | 'REVOKED' | 'BLOCKED';
  lastActiveIp: string;
  lastActiveAt: Date;
  createdAt: Date;
}

export interface TrustedDevice {
  id: string;
  tenantId: string;
  userId: string;
  deviceId: string;
  deviceName: string;
  fingerprint: string;
  trustedUntil: Date;
  createdAt: Date;
}

export interface ApiKey {
  id: string;
  tenantId: string;
  name: string;
  keyPrefix: string;
  keyHash: string;
  clearKey?: string; // only returned once upon creation
  scopes: string; // comma separated e.g. "wallet:read,wallet:write"
  ipWhitelist: string | null; // comma separated IPs
  rateLimitRps: number;
  isActive: boolean;
  expiresAt: Date | null;
  lastUsedAt: Date | null;
  createdAt: Date;
}

export interface AccessLog {
  id: string;
  tenantId: string;
  apiKeyId: string | null;
  userId: string | null;
  method: string;
  path: string;
  statusCode: number;
  responseTimeMs: number;
  ipAddress: string;
  userAgent: string;
  requestSize: number;
  responseSize: number;
  createdAt: Date;
}

export interface SystemLog {
  id: string;
  tenantId: string | null;
  level: 'INFO' | 'WARN' | 'ERROR' | 'DEBUG';
  module: 'GATEWAY' | 'WALLET' | 'AUTH' | 'LEDGER' | 'COMPLIANCE' | 'BACKUP' | 'DR';
  message: string;
  stackTrace: string | null;
  metadata: string | null; // JSON String
  createdAt: Date;
}

export interface MonitoringMetric {
  id: string;
  metricName: string; // CPU_USAGE, MEM_USAGE, REDIS_LATENCY, QUEUE_SIZE, RESP_TIME
  metricValue: number;
  serviceName: string; // API_GATEWAY, BACKEND, REDIS, WORKER
  metadata: string | null;
  createdAt: Date;
}

export interface BackupJob {
  id: string;
  backupType: 'FULL' | 'INCREMENTAL';
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
  filePath: string | null;
  fileSize: number; // in bytes
  encryptionKeyId: string;
  verified: boolean;
  verificationLog: string | null;
  durationMs: number;
  error: string | null;
  createdAt: Date;
  completedAt: Date | null;
}

export interface DrRecord {
  id: string;
  scenarioName: string; // DB_FAILOVER, REGION_DOWN, REDIS_CRASH
  status: 'SCHEDULED' | 'IN_PROGRESS' | 'SUCCESS' | 'FAILED';
  recoveryTimeMs: number; // Actual RTO
  dataLossSeconds: number; // Actual RPO
  runbookExecuted: string;
  verificationLog: string;
  testedBy: string;
  createdAt: Date;
}

@Injectable()
export class SecurityService {
  private securityEvents: SecurityEvent[] = [];
  private loginSessions: LoginSession[] = [];
  private devices: Device[] = [];
  private trustedDevices: TrustedDevice[] = [];
  private apiKeys: ApiKey[] = [];
  private accessLogs: AccessLog[] = [];
  private systemLogs: SystemLog[] = [];
  private monitoringMetrics: MonitoringMetric[] = [];
  private backupJobs: BackupJob[] = [];
  private drRecords: DrRecord[] = [];

  constructor() {
    this.seedSecurityData();
  }

  private seedSecurityData() {
    const tenantId = 'default-tenant-1';

    // 1. Seed Security Events (SOC Alerts)
    this.securityEvents.push({
      id: crypto.randomUUID(),
      tenantId,
      userId: 'user-id-1',
      eventType: 'FAILED_LOGIN',
      severity: 'LOW',
      status: 'RESOLVED',
      sourceIp: '103.45.201.12',
      userAgent: 'Mozilla/5.0 Android App Chrome/120',
      description: '3 consecutive failed login attempts on retailer account subramanyampaipuri14@gmail.com',
      details: JSON.stringify({ deviceFingerprint: 'fg_mobile_a12', attemptHistory: ['12:10', '12:11', '12:12'] }),
      remediation: 'IP temporary lockout for 15 mins. Account unlocked after successful biometrics.',
      createdAt: new Date(Date.now() - 3 * 3600 * 1000),
      updatedAt: new Date(Date.now() - 2.8 * 3600 * 1000),
    }, {
      id: crypto.randomUUID(),
      tenantId,
      userId: null,
      eventType: 'API_ABUSE',
      severity: 'HIGH',
      status: 'OPEN',
      sourceIp: '185.220.101.4',
      userAgent: 'Go-http-client/1.1',
      description: 'Rate limit tripped on DMT transaction API. 450 requests/sec from single IP address.',
      details: JSON.stringify({ apiKeyPrefix: 'sc_live_f82', requestPath: '/api/v1/dmt/transfer' }),
      remediation: null,
      createdAt: new Date(Date.now() - 30 * 60 * 1000),
      updatedAt: new Date(Date.now() - 30 * 60 * 1000),
    }, {
      id: crypto.randomUUID(),
      tenantId,
      userId: 'user-id-7182',
      eventType: 'WALLET_FRAUD',
      severity: 'CRITICAL',
      status: 'UNDER_INVESTIGATION',
      sourceIp: '192.168.1.1',
      userAgent: 'Dart/3.0 (flutter)',
      description: 'Suspicious immediate wallet cash out following high-value AePS credit transaction.',
      details: JSON.stringify({ walletId: 'w-7182', creditAmount: 250000, debitAmount: 245000, latencySec: 4.5 }),
      remediation: 'Temporarily suspended wallet outgoing transaction channels. Alert routed to compliance officer.',
      createdAt: new Date(Date.now() - 10 * 60 * 1000),
      updatedAt: new Date(Date.now() - 5 * 60 * 1000),
    }, {
      id: crypto.randomUUID(),
      tenantId,
      userId: 'user-id-5',
      eventType: 'CREDIT_ABUSE',
      severity: 'MEDIUM',
      status: 'OPEN',
      sourceIp: '49.206.12.89',
      userAgent: 'Mozilla/5.0 MacOS Chrome/120',
      description: 'Multiple credit line utility drawdown applications matching shell-company patterns.',
      details: JSON.stringify({ entityName: 'Dummy Trade Corp', requestedCredit: 500000 }),
      remediation: 'Flagged for verification against MCA registry. Pending physical site check.',
      createdAt: new Date(),
      updatedAt: new Date(),
    });

    // 2. Seed Login Sessions
    this.loginSessions.push({
      id: 'sess-1',
      tenantId,
      userId: 'user-id-1',
      email: 'subramanyampaipuri14@gmail.com',
      mfaVerified: true,
      mpinUsed: true,
      biometricUsed: true,
      ipAddress: '157.48.91.22',
      userAgent: 'Dart/3.1 (Android; Mobile)',
      location: 'Bengaluru, India',
      deviceFingerprint: 'dev_fp_1001',
      isActive: true,
      expiresAt: new Date(Date.now() + 8 * 3600 * 1000),
      createdAt: new Date(Date.now() - 2 * 3600 * 1000),
    }, {
      id: 'sess-2',
      tenantId,
      userId: 'user-id-2',
      email: 'sales-manager@suryacredit.com',
      mfaVerified: true,
      mpinUsed: false,
      biometricUsed: false,
      ipAddress: '103.20.144.18',
      userAgent: 'Mozilla/5.0 Windows NT Edge/120',
      location: 'Mumbai, India',
      deviceFingerprint: 'dev_fp_1002',
      isActive: true,
      expiresAt: new Date(Date.now() + 6 * 3600 * 1000),
      createdAt: new Date(Date.now() - 1.5 * 3600 * 1000),
    });

    // 3. Seed Devices & Trusted
    this.devices.push({
      id: 'dev-1',
      tenantId,
      userId: 'user-id-1',
      deviceName: 'OnePlus 11 Pro',
      deviceType: 'MOBILE',
      osVersion: 'Android 13',
      pushToken: 'push_token_abc_123',
      fingerprint: 'dev_fp_1001',
      status: 'ACTIVE',
      lastActiveIp: '157.48.91.22',
      lastActiveAt: new Date(),
      createdAt: new Date(Date.now() - 60 * 24 * 3600 * 1000),
    }, {
      id: 'dev-2',
      tenantId,
      userId: 'user-id-2',
      deviceName: 'ThinkPad X1 Carbon',
      deviceType: 'DESKTOP',
      osVersion: 'Windows 11',
      pushToken: null,
      fingerprint: 'dev_fp_1002',
      status: 'ACTIVE',
      lastActiveIp: '103.20.144.18',
      lastActiveAt: new Date(),
      createdAt: new Date(Date.now() - 150 * 24 * 3600 * 1000),
    }, {
      id: 'dev-3',
      tenantId,
      userId: 'user-id-3',
      deviceName: 'Unknown Android Phone',
      deviceType: 'MOBILE',
      osVersion: 'Android 9',
      pushToken: null,
      fingerprint: 'dev_fp_suspicious_999',
      status: 'REVOKED',
      lastActiveIp: '185.220.101.4',
      lastActiveAt: new Date(Date.now() - 5 * 24 * 3600 * 1000),
      createdAt: new Date(Date.now() - 5 * 24 * 3600 * 1000),
    });

    this.trustedDevices.push({
      id: crypto.randomUUID(),
      tenantId,
      userId: 'user-id-1',
      deviceId: 'dev-1',
      deviceName: 'OnePlus 11 Pro',
      fingerprint: 'dev_fp_1001',
      trustedUntil: new Date(Date.now() + 90 * 24 * 3600 * 1000),
      createdAt: new Date(),
    });

    // 4. Seed API Keys
    this.apiKeys.push({
      id: crypto.randomUUID(),
      tenantId,
      name: 'Retail Gateway Live Integration',
      keyPrefix: 'sc_live_f82',
      keyHash: crypto.createHash('sha256').update('sc_live_f82_secret_key_string').digest('hex'),
      scopes: 'wallet:read,wallet:write,transaction:init',
      ipWhitelist: '13.125.10.41,52.78.20.89',
      rateLimitRps: 50,
      isActive: true,
      expiresAt: null,
      lastUsedAt: new Date(),
      createdAt: new Date(Date.now() - 30 * 24 * 3600 * 1000),
    }, {
      id: crypto.randomUUID(),
      tenantId,
      name: 'Testing Sandbox Key',
      keyPrefix: 'sc_test_b12',
      keyHash: crypto.createHash('sha256').update('sc_test_b12_test_key_string').digest('hex'),
      scopes: 'wallet:read,transaction:init',
      ipWhitelist: null,
      rateLimitRps: 10,
      isActive: true,
      expiresAt: new Date(Date.now() + 6 * 30 * 24 * 3600 * 1000),
      lastUsedAt: new Date(Date.now() - 2 * 3600 * 1000),
      createdAt: new Date(Date.now() - 10 * 24 * 3600 * 1000),
    });

    // 5. Seed System and Access Logs (Audit Trail)
    for (let i = 1; i <= 20; i++) {
      this.accessLogs.push({
        id: crypto.randomUUID(),
        tenantId,
        apiKeyId: 'sc_live_f82',
        userId: i % 2 === 0 ? 'user-id-1' : null,
        method: i % 5 === 0 ? 'POST' : 'GET',
        path: i % 5 === 0 ? '/api/v1/payments/initiate' : '/api/v1/wallet/balance',
        statusCode: i === 12 ? 429 : (i % 8 === 0 ? 401 : 200),
        responseTimeMs: Math.floor(Math.random() * 80) + 10,
        ipAddress: i % 3 === 0 ? '157.48.91.22' : '13.125.10.41',
        userAgent: 'SuryaGatewayHttpClient/2.1',
        requestSize: i % 5 === 0 ? 450 : 0,
        responseSize: i % 5 === 0 ? 1200 : 250,
        createdAt: new Date(Date.now() - i * 5 * 60 * 1000),
      });

      this.systemLogs.push({
        id: crypto.randomUUID(),
        tenantId,
        level: i % 8 === 0 ? 'ERROR' : (i % 6 === 0 ? 'WARN' : 'INFO'),
        module: i % 4 === 0 ? 'WALLET' : (i % 4 === 1 ? 'GATEWAY' : (i % 4 === 2 ? 'AUTH' : 'COMPLIANCE')),
        message: i % 8 === 0 ? `Transaction timeout on AEPS gateway connection pool.` : `Successfully executed transaction ledger double entry postings.`,
        stackTrace: i % 8 === 0 ? `Error: Connection timeout\n  at GatewayPool.acquireConnection (/src/gateway/pool.ts:48)\n  at processTicksAndRejections (node:internal/process/task_queues:95)` : null,
        metadata: JSON.stringify({ executionDurationMs: Math.random() * 200, dbShardId: 'shard-ap-south-1' }),
        createdAt: new Date(Date.now() - i * 15 * 60 * 1000),
      });
    }

    // 6. Seed Telemetry Monitoring Metrics
    const services = ['API_GATEWAY', 'BACKEND', 'REDIS', 'WORKER'];
    const metrics = ['CPU_USAGE', 'MEM_USAGE', 'REDIS_LATENCY', 'QUEUE_SIZE', 'RESP_TIME'];
    services.forEach(svc => {
      metrics.forEach(mtr => {
        let val = 0;
        if (mtr === 'CPU_USAGE') val = Math.random() * 30 + 15; // 15% - 45%
        else if (mtr === 'MEM_USAGE') val = Math.random() * 20 + 55; // 55% - 75%
        else if (mtr === 'REDIS_LATENCY') val = Math.random() * 3 + 1; // 1ms - 4ms
        else if (mtr === 'QUEUE_SIZE') val = Math.floor(Math.random() * 12); // 0 - 12 jobs
        else val = Math.random() * 120 + 20; // 20ms - 140ms response time

        this.monitoringMetrics.push({
          id: crypto.randomUUID(),
          metricName: mtr,
          metricValue: parseFloat(val.toFixed(2)),
          serviceName: svc,
          metadata: JSON.stringify({ region: 'asia-south1', host: `k8s-pod-${svc.toLowerCase()}-7d8` }),
          createdAt: new Date(),
        });
      });
    });

    // 7. Seed Backup Jobs
    this.backupJobs.push({
      id: 'back-1',
      backupType: 'FULL',
      status: 'COMPLETED',
      filePath: 's3://surya-backups/fy2026/full_backup_1703212800.enc',
      fileSize: 458921820, // 437MB
      encryptionKeyId: 'kms-key-aes256-01',
      verified: true,
      verificationLog: 'Backup integrity matches sha256. Restore test succeeded on sandbox db instance with zero parity differences.',
      durationMs: 45000,
      error: null,
      createdAt: new Date(Date.now() - 24 * 3600 * 1000),
      completedAt: new Date(Date.now() - 24 * 3600 * 1000 + 45000),
    }, {
      id: 'back-2',
      backupType: 'INCREMENTAL',
      status: 'COMPLETED',
      filePath: 's3://surya-backups/fy2026/inc_backup_1703256000.enc',
      fileSize: 12590210, // 12MB
      encryptionKeyId: 'kms-key-aes256-01',
      verified: true,
      verificationLog: 'Verification successful. Hash verify OK.',
      durationMs: 8200,
      error: null,
      createdAt: new Date(Date.now() - 6 * 3600 * 1000),
      completedAt: new Date(Date.now() - 6 * 3600 * 1000 + 8200),
    });

    // 8. Seed Disaster Recovery Records
    this.drRecords.push({
      id: crypto.randomUUID(),
      scenarioName: 'DB_FAILOVER',
      status: 'SUCCESS',
      recoveryTimeMs: 4200, // achieved 4.2 seconds RTO! (Industry standard ceiling: 30 seconds)
      dataLossSeconds: 0, // 0 data loss achieved RPO (Active-Active replication)
      runbookExecuted: 'RUNBOOK_FAILOVER_POSTGRESQL_V2.md: Check primary state; promote replica-ap-south-1b to master; update route53 DNS record with TTL 5s; verify active transactional log synchronisation.',
      verificationLog: 'Automated health audit: Replica promoted smoothly. High-availability verification tests successfully passed. Zero transactions skipped or dropped.',
      testedBy: 'devops-chief@suryacredit.com',
      createdAt: new Date(Date.now() - 30 * 24 * 3600 * 1000),
    }, {
      id: crypto.randomUUID(),
      scenarioName: 'REDIS_CRASH',
      status: 'SUCCESS',
      recoveryTimeMs: 1800, // achieved 1.8 seconds RTO!
      dataLossSeconds: 0,
      runbookExecuted: 'RUNBOOK_REDIS_SENTINEL_SWAP.md: Verify master down; check quorum; promote sentinel slave; update app connection pool configs dynamically without system reboot.',
      verificationLog: 'Quorum elected node-02 master. Web sockets & sessions recovered instantly from memory snapshots.',
      testedBy: 'devops-chief@suryacredit.com',
      createdAt: new Date(Date.now() - 12 * 24 * 3600 * 1000),
    });
  }

  // ---------------------------------------------------------------------------
  // 1. SOC ALERTS & INCIDENT QUEUE
  // ---------------------------------------------------------------------------
  async getSecurityEvents(tenantId: string) {
    return this.securityEvents.filter(e => e.tenantId === tenantId);
  }

  async createSecurityEvent(tenantId: string, dto: CreateSecurityEventDto) {
    const newEv: SecurityEvent = {
      id: crypto.randomUUID(),
      tenantId,
      userId: dto.userId || null,
      eventType: dto.eventType,
      severity: dto.severity,
      status: 'OPEN',
      sourceIp: dto.sourceIp || '0.0.0.0',
      userAgent: dto.userAgent || 'Unknown',
      description: dto.description,
      details: dto.details || null,
      remediation: null,
      createdAt: new Date(),
      updatedAt: new Date(),
    };
    this.securityEvents.unshift(newEv);

    // Create a corresponding System Log entry for compliance auditing
    this.createSystemLogEntry(tenantId, 'WARN', 'COMPLIANCE', `SOC Event triggered: ${dto.eventType} [${dto.severity}] - ${dto.description}`);

    return newEv;
  }

  async resolveSecurityEvent(tenantId: string, eventId: string, dto: ResolveEventDto) {
    const ev = this.securityEvents.find(e => e.id === eventId && e.tenantId === tenantId);
    if (!ev) throw new NotFoundException('Security incident not found in SOC queue.');

    ev.status = 'RESOLVED';
    ev.remediation = dto.resolution;
    ev.updatedAt = new Date();

    this.createSystemLogEntry(tenantId, 'INFO', 'COMPLIANCE', `SOC Event ${eventId} resolved: ${dto.resolution}`);
    return ev;
  }

  // ---------------------------------------------------------------------------
  // 2. IAM & DEVICE POLICIES
  // ---------------------------------------------------------------------------
  async getSessions(tenantId: string) {
    return this.loginSessions.filter(s => s.tenantId === tenantId);
  }

  async terminateSession(tenantId: string, sessionId: string) {
    const sess = this.loginSessions.find(s => s.id === sessionId && s.tenantId === tenantId);
    if (!sess) throw new NotFoundException('Login session not found or expired.');

    sess.isActive = false;
    this.createSystemLogEntry(tenantId, 'INFO', 'AUTH', `Session ${sessionId} terminated by administrative command.`);
    return { success: true, message: 'Session successfully invalidated.' };
  }

  async getDevices(tenantId: string) {
    return this.devices.filter(d => d.tenantId === tenantId);
  }

  async registerDevice(tenantId: string, userId: string, dto: CreateDeviceDto) {
    const existing = this.devices.find(d => d.fingerprint === dto.fingerprint);
    if (existing) {
      existing.lastActiveAt = new Date();
      existing.status = 'ACTIVE';
      return existing;
    }

    const newDev: Device = {
      id: crypto.randomUUID(),
      tenantId,
      userId,
      deviceName: dto.deviceName,
      deviceType: dto.deviceType,
      osVersion: dto.osVersion || 'Unknown',
      pushToken: dto.pushToken || null,
      fingerprint: dto.fingerprint,
      status: 'ACTIVE',
      lastActiveIp: '127.0.0.1',
      lastActiveAt: new Date(),
      createdAt: new Date(),
    };
    this.devices.push(newDev);
    return newDev;
  }

  async revokeDevice(tenantId: string, deviceId: string) {
    const dev = this.devices.find(d => d.id === deviceId && d.tenantId === tenantId);
    if (!dev) throw new NotFoundException('Device configuration not found.');

    dev.status = 'REVOKED';
    this.createSystemLogEntry(tenantId, 'WARN', 'AUTH', `Device ${dev.deviceName} (${deviceId}) revoked and blacklisted from system access.`);
    return dev;
  }

  async toggleTrustedDevice(tenantId: string, deviceId: string, userId: string) {
    const dev = this.devices.find(d => d.id === deviceId && d.tenantId === tenantId);
    if (!dev) throw new NotFoundException('Device not registered.');

    const idx = this.trustedDevices.findIndex(td => td.deviceId === deviceId && td.userId === userId);
    if (idx >= 0) {
      this.trustedDevices.splice(idx, 1);
      return { trusted: false, message: 'Device removed from Trusted circle.' };
    } else {
      this.trustedDevices.push({
        id: crypto.randomUUID(),
        tenantId,
        userId,
        deviceId,
        deviceName: dev.deviceName,
        fingerprint: dev.fingerprint,
        trustedUntil: new Date(Date.now() + 90 * 24 * 3600 * 1000),
        createdAt: new Date(),
      });
      return { trusted: true, message: 'Device added to Trusted bypass list successfully.' };
    }
  }

  async getTrustedDevices(tenantId: string, userId: string) {
    return this.trustedDevices.filter(td => td.tenantId === tenantId && td.userId === userId);
  }

  // ---------------------------------------------------------------------------
  // 3. API KEY CREDENTIAL MANAGEMENT
  // ---------------------------------------------------------------------------
  async getApiKeys(tenantId: string) {
    return this.apiKeys.filter(k => k.tenantId === tenantId);
  }

  async createApiKey(tenantId: string, dto: CreateApiKeyDto) {
    const clearSecret = `sc_live_${crypto.randomBytes(24).toString('hex')}`;
    const hash = crypto.createHash('sha256').update(clearSecret).digest('hex');

    const newKey: ApiKey = {
      id: crypto.randomUUID(),
      tenantId,
      name: dto.name,
      keyPrefix: clearSecret.substring(0, 11),
      keyHash: hash,
      clearKey: clearSecret, // returned strictly once!
      scopes: dto.scopes || '*',
      ipWhitelist: dto.ipWhitelist || null,
      rateLimitRps: dto.rateLimitRps || 10,
      isActive: true,
      expiresAt: new Date(Date.now() + 365 * 24 * 3600 * 1000), // 1 year default
      lastUsedAt: null,
      createdAt: new Date(),
    };

    this.apiKeys.push(newKey);
    this.createSystemLogEntry(tenantId, 'INFO', 'GATEWAY', `New API Credentials issued: "${dto.name}" with scope constraints.`);
    return newKey;
  }

  async revokeApiKey(tenantId: string, keyId: string) {
    const key = this.apiKeys.find(k => k.id === keyId && k.tenantId === tenantId);
    if (!key) throw new NotFoundException('API Key credentials not found.');

    key.isActive = false;
    this.createSystemLogEntry(tenantId, 'WARN', 'GATEWAY', `API Key "${key.name}" revoked permanently from access gateway.`);
    return key;
  }

  // ---------------------------------------------------------------------------
  // 4. METRICS & TELEMETRY OBSERVABILITY
  // ---------------------------------------------------------------------------
  async getMonitoringMetrics() {
    // Generate fresh real-time noise around seeded metrics to keep dials live!
    this.monitoringMetrics.forEach(m => {
      if (m.metricName === 'CPU_USAGE') {
        m.metricValue = parseFloat((Math.random() * 20 + 20).toFixed(2));
      } else if (m.metricName === 'MEM_USAGE') {
        m.metricValue = parseFloat((Math.random() * 10 + 60).toFixed(2));
      } else if (m.metricName === 'RESP_TIME') {
        m.metricValue = parseFloat((Math.random() * 40 + 35).toFixed(2));
      } else if (m.metricName === 'QUEUE_SIZE') {
        m.metricValue = Math.floor(Math.random() * 8);
      } else if (m.metricName === 'REDIS_LATENCY') {
        m.metricValue = parseFloat((Math.random() * 1.5 + 0.8).toFixed(2));
      }
    });
    return this.monitoringMetrics;
  }

  // ---------------------------------------------------------------------------
  // 5. SECURITY & AUDIT SYSTEM LOGGING
  // ---------------------------------------------------------------------------
  async queryLogs(
    tenantId: string, 
    type: 'ACCESS' | 'SYSTEM' | 'SECURITY', 
    search: string = '', 
    level: string = '', 
    module: string = ''
  ) {
    if (type === 'ACCESS') {
      return this.accessLogs.filter(log => {
        if (log.tenantId !== tenantId) return false;
        if (search) {
          const s = search.toLowerCase();
          return log.path.toLowerCase().includes(s) || log.ipAddress.includes(s) || (log.userAgent && log.userAgent.toLowerCase().includes(s));
        }
        return true;
      });
    }

    if (type === 'SECURITY') {
      return this.securityEvents.filter(ev => {
        if (ev.tenantId !== tenantId) return false;
        if (search) {
          const s = search.toLowerCase();
          return ev.description.toLowerCase().includes(s) || ev.eventType.toLowerCase().includes(s) || ev.sourceIp.includes(s);
        }
        if (level && ev.severity !== level) return false;
        return true;
      });
    }

    // Default System logs query
    return this.systemLogs.filter(log => {
      if (log.tenantId !== tenantId) return false;
      if (level && log.level !== level) return false;
      if (module && log.module !== module) return false;
      if (search) {
        const s = search.toLowerCase();
        return log.message.toLowerCase().includes(s) || (log.stackTrace && log.stackTrace.toLowerCase().includes(s));
      }
      return true;
    });
  }

  private createSystemLogEntry(tenantId: string | null, level: 'INFO' | 'WARN' | 'ERROR' | 'DEBUG', module: any, message: string) {
    this.systemLogs.unshift({
      id: crypto.randomUUID(),
      tenantId,
      level,
      module,
      message,
      stackTrace: null,
      metadata: null,
      createdAt: new Date(),
    });
  }

  // ---------------------------------------------------------------------------
  // 6. BACKUP & CONTINUITY CONTROL
  // ---------------------------------------------------------------------------
  async getBackupJobs() {
    return this.backupJobs;
  }

  async triggerBackup(tenantId: string, dto: TriggerBackupDto) {
    const jobId = `back-${1000 + this.backupJobs.length + 1}`;
    
    const newJob: BackupJob = {
      id: jobId,
      backupType: dto.backupType as any,
      status: 'IN_PROGRESS',
      filePath: null,
      fileSize: 0,
      encryptionKeyId: 'kms-key-aes256-01',
      verified: false,
      verificationLog: null,
      durationMs: 0,
      error: null,
      createdAt: new Date(),
      completedAt: null,
    };
    this.backupJobs.unshift(newJob);

    this.createSystemLogEntry(tenantId, 'INFO', 'BACKUP', `Database backup triggered manually: Type [${dto.backupType}]`);

    // Simulate asynchronous backup processing completion
    setTimeout(() => {
      newJob.status = 'COMPLETED';
      newJob.filePath = `s3://surya-backups/fy2026/manual_${dto.backupType.toLowerCase()}_${Date.now()}.enc`;
      newJob.fileSize = dto.backupType === 'FULL' ? 461298100 : 14210980;
      newJob.durationMs = dto.backupType === 'FULL' ? 38000 : 4200;
      newJob.verified = true;
      newJob.verificationLog = 'Automatic backup checksum validation succeeded. Symmetric encryption cipher verification matches KMS policy.';
      newJob.completedAt = new Date();

      this.createSystemLogEntry(tenantId, 'INFO', 'BACKUP', `Manual backup ${jobId} finished and verified (Size: ${(newJob.fileSize / (1024 * 1024)).toFixed(2)} MB, took ${newJob.durationMs}ms)`);
    }, 1500);

    return newJob;
  }

  // ---------------------------------------------------------------------------
  // 7. DISASTER RECOVERY COMPLIANCE CHECKS
  // ---------------------------------------------------------------------------
  async getDrRecords() {
    return this.drRecords;
  }

  async triggerDrSimulation(tenantId: string, dto: TriggerDrDto) {
    const achievedRto = dto.scenarioName === 'DB_FAILOVER' ? 3900 : (dto.scenarioName === 'REGION_DOWN' ? 45000 : 1200);
    const achievedRpo = dto.scenarioName === 'REGION_DOWN' ? 5 : 0; // 5s lag max on inter-region replication, 0s local replicas

    const newDr: DrRecord = {
      id: crypto.randomUUID(),
      scenarioName: dto.scenarioName,
      status: 'SUCCESS',
      recoveryTimeMs: achievedRto,
      dataLossSeconds: achievedRpo,
      runbookExecuted: `RUNBOOK_AUTO_${dto.scenarioName}_SIMULATION.md: Trigger simulated crash; launch heartbeat failure checker; verify continuous health recovery; redirect target traffic APIs.`,
      verificationLog: `Disaster Recovery Simulation verified successfully! achieved RTO of ${(achievedRto/1000).toFixed(1)}s (RTO ceiling limit is 60s). achieved RPO of ${achievedRpo}s data loss (Compliance limit is 10s). All state replication integrity audits passed correctly.`,
      testedBy: dto.testedBy,
      createdAt: new Date(),
    };

    this.drRecords.unshift(newDr);
    this.createSystemLogEntry(tenantId, 'INFO', 'DR', `Disaster recovery drill "${dto.scenarioName}" initiated by ${dto.testedBy}. Status: SUCCESS.`);
    return newDr;
  }
}
