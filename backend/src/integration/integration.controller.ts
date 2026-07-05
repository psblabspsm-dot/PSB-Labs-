import { Controller, Post, Body, HttpCode, HttpStatus, BadRequestException } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiBody } from '@nestjs/swagger';
import { IntegrationService } from './integration.service';
import { IsNotEmpty, IsString, IsEmail, IsNumber } from 'class-validator';

export class SendSmsDto {
  @IsString()
  @IsNotEmpty()
  phoneNumber: string;
}

export class VerifySmsDto {
  @IsString()
  @IsNotEmpty()
  phoneNumber: string;

  @IsString()
  @IsNotEmpty()
  otpCode: string;
}

export class SendEmailDto {
  @IsEmail()
  @IsNotEmpty()
  to: string;

  @IsString()
  @IsNotEmpty()
  subject: string;

  @IsString()
  @IsNotEmpty()
  htmlContent: string;
}

export class PushNotificationDto {
  @IsString()
  @IsNotEmpty()
  deviceToken: string;

  @IsString()
  @IsNotEmpty()
  title: string;

  @IsString()
  @IsNotEmpty()
  body: string;
}

export class GeoRouteDto {
  @IsNumber() @IsNotEmpty() rLat: number;
  @IsNumber() @IsNotEmpty() rLng: number;
  @IsNumber() @IsNotEmpty() dLat: number;
  @IsNumber() @IsNotEmpty() dLng: number;
}

@ApiTags('Enterprise Third-Party Integrations')
@Controller('api/v1/integrations')
export class IntegrationController {
  constructor(private readonly integrationService: IntegrationService) {}

  @Post('sms/otp-send')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Send secure 6-digit SMS OTP using Twilio/Msg91 API' })
  @ApiBody({ type: SendSmsDto })
  async sendOtp(@Body() dto: SendSmsDto) {
    return this.integrationService.sendSmsOtp(dto.phoneNumber);
  }

  @Post('sms/otp-verify')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Verify secure 6-digit SMS OTP' })
  @ApiBody({ type: VerifySmsDto })
  async verifyOtp(@Body() dto: VerifySmsDto) {
    const isOk = await this.integrationService.verifySmsOtp(dto.phoneNumber, dto.otpCode);
    if (!isOk) {
      throw new BadRequestException('Incorrect or expired OTP verification code.');
    }
    return { success: true, message: 'OTP passcode validated successfully' };
  }

  @Post('email/send')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Send transactional email via SendGrid/Nodemailer SMTP TLS' })
  @ApiBody({ type: SendEmailDto })
  async sendEmail(@Body() dto: SendEmailDto) {
    return this.integrationService.sendEmail(dto.to, dto.subject, dto.htmlContent);
  }

  @Post('firebase/push')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Dispatch real-time FCM Push Notification payload' })
  @ApiBody({ type: PushNotificationDto })
  async pushNotification(@Body() dto: PushNotificationDto) {
    return this.integrationService.pushFirebaseNotification(dto.deviceToken, dto.title, dto.body);
  }

  @Post('maps/georoute')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Measure Distance Matrix route via Google Maps API' })
  @ApiBody({ type: GeoRouteDto })
  async getRoute(@Body() dto: GeoRouteDto) {
    return this.integrationService.getKioskGeorouting(
      { lat: dto.rLat, lng: dto.rLng },
      { lat: dto.dLat, lng: dto.dLng },
    );
  }
}
