import { IsString, IsNotEmpty, IsNumber, IsOptional, IsBoolean, IsArray, IsEnum } from 'class-validator';

export class CreateSecurityEventDto {
  @IsString()
  @IsNotEmpty()
  eventType: string; // THREAT, INTRUSION, ANOMALY, FAILED_LOGIN, WALLET_FRAUD, CREDIT_ABUSE

  @IsString()
  @IsNotEmpty()
  severity: string; // LOW, MEDIUM, HIGH, CRITICAL

  @IsString()
  @IsNotEmpty()
  description: string;

  @IsString()
  @IsOptional()
  userId?: string;

  @IsString()
  @IsOptional()
  sourceIp?: string;

  @IsString()
  @IsOptional()
  userAgent?: string;

  @IsString()
  @IsOptional()
  details?: string;
}

export class ResolveEventDto {
  @IsString()
  @IsNotEmpty()
  resolution: string;
}

export class CreateApiKeyDto {
  @IsString()
  @IsNotEmpty()
  name: string;

  @IsString()
  @IsOptional()
  scopes?: string;

  @IsString()
  @IsOptional()
  ipWhitelist?: string;

  @IsNumber()
  @IsOptional()
  rateLimitRps?: number;
}

export class CreateDeviceDto {
  @IsString()
  @IsNotEmpty()
  deviceName: string;

  @IsString()
  @IsNotEmpty()
  deviceType: string; // MOBILE, TABLET, DESKTOP

  @IsString()
  @IsNotEmpty()
  fingerprint: string;

  @IsString()
  @IsOptional()
  osVersion?: string;

  @IsString()
  @IsOptional()
  pushToken?: string;
}

export class TriggerBackupDto {
  @IsString()
  @IsNotEmpty()
  backupType: string; // FULL, INCREMENTAL
}

export class TriggerDrDto {
  @IsString()
  @IsNotEmpty()
  scenarioName: string; // DB_FAILOVER, REGION_DOWN, REDIS_CRASH

  @IsString()
  @IsNotEmpty()
  testedBy: string;
}
