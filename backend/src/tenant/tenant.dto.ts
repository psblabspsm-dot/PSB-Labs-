import { IsNotEmpty, IsString, IsBoolean, IsOptional, IsEnum, IsArray } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class CreateTenantDto {
  @ApiProperty({ example: 'Alpha Retailers', description: 'Name of the tenant organization' })
  @IsString()
  @IsNotEmpty()
  name: string;

  @ApiProperty({ example: 'alpha', description: 'Subdomain prefix for multi-tenant isolation' })
  @IsString()
  @IsNotEmpty()
  subdomain: string;

  @ApiProperty({ example: 'alpha.suryacredit.com', description: 'Custom white-label domain mapped to the tenant', required: false })
  @IsString()
  @IsOptional()
  domain?: string;

  @ApiProperty({ example: 'PROFESSIONAL', description: 'Subscription tier for resource limitations' })
  @IsEnum(['FREE', 'STARTER', 'PROFESSIONAL', 'ENTERPRISE'])
  @IsNotEmpty()
  plan: 'FREE' | 'STARTER' | 'PROFESSIONAL' | 'ENTERPRISE';
}

export class UpdateBrandingDto {
  @ApiProperty({ example: 'Alpha Finance Solutions', description: 'White labeled company name' })
  @IsString()
  @IsOptional()
  companyName?: string;

  @ApiProperty({ example: 'https://images.unsplash.com/photo-1599305445671-ac291c95aba9', description: 'Tenant logo URL' })
  @IsString()
  @IsOptional()
  logoUrl?: string;

  @ApiProperty({ example: '#0F172A', description: 'Theme primary color' })
  @IsString()
  @IsOptional()
  primaryColor?: string;

  @ApiProperty({ example: '#38BDF8', description: 'Theme secondary color' })
  @IsString()
  @IsOptional()
  secondaryColor?: string;

  @ApiProperty({ example: 'Inter', description: 'Primary branding typography' })
  @IsString()
  @IsOptional()
  fontName?: string;

  @ApiProperty({ example: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe', description: 'Splash screen illustration URL' })
  @IsString()
  @IsOptional()
  splashScreenUrl?: string;

  @ApiProperty({ example: 'https://images.unsplash.com/photo-1557683316-973673baf926', description: 'Mobile app icon URL' })
  @IsString()
  @IsOptional()
  appIconUrl?: string;

  @ApiProperty({ example: 'Dear {{name}}, your OTP is {{otp}}.', description: 'White label SMS layout template' })
  @IsString()
  @IsOptional()
  smsTemplate?: string;

  @ApiProperty({ example: '<h1>Invoice {{invoiceNumber}}</h1>', description: 'White label Invoice HTML/PDF template' })
  @IsString()
  @IsOptional()
  invoiceBranding?: string;

  @ApiProperty({ example: 'https://suryacredit.com/terms', description: 'Terms of service URL' })
  @IsString()
  @IsOptional()
  termsAndConditions?: string;

  @ApiProperty({ example: 'support@alpha.com', description: 'Branded email support desk contact' })
  @IsString()
  @IsOptional()
  supportEmail?: string;
}

export class UpdateFeatureFlagsDto {
  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  marketplace?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  wallet?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  credit?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  aeps?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  dmt?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  bbps?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  recharge?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  pan?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  insurance?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  travel?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  loans?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  crm?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  ai?: boolean;

  @ApiProperty({ example: true })
  @IsBoolean()
  @IsOptional()
  analytics?: boolean;
}

export class TriggerWebhookTestDto {
  @ApiProperty({ example: 'payment.captured', description: 'Simulated webhook event name' })
  @IsString()
  @IsNotEmpty()
  event: string;

  @ApiProperty({ example: { amount: 2500, orderId: 'ord_9182', status: 'SUCCESS' }, description: 'Simulated webhook payload JSON' })
  @IsNotEmpty()
  payload: any;

  @ApiProperty({ example: 'sha256_mock_sig_header_xyz', description: 'Simulated digital signature header' })
  @IsString()
  @IsOptional()
  signatureHeader?: string;
}
