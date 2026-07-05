import { Controller, Post, Get, Body, Param, HttpCode, HttpStatus } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiBody } from '@nestjs/swagger';
import { GatewayService, PaymentInitiation } from './gateway.service';
import { IsNotEmpty, IsNumber, IsString, IsEnum } from 'class-validator';

export class InitiatePaymentDto {
  @IsNumber()
  @IsNotEmpty()
  amount: number;

  @IsString()
  @IsNotEmpty()
  orderId: string;

  @IsEnum(['RAZORPAY', 'CASHFREE', 'PINELABS', 'CCAVENUE', 'PAYTM', 'ZAAKPAY'])
  @IsNotEmpty()
  gateway: 'RAZORPAY' | 'CASHFREE' | 'PINELABS' | 'CCAVENUE' | 'PAYTM' | 'ZAAKPAY';
}

export class RefundPaymentDto {
  @IsString()
  @IsNotEmpty()
  paymentId: string;

  @IsNumber()
  @IsNotEmpty()
  amount: number;

  @IsString()
  @IsNotEmpty()
  gateway: string;

  @IsString()
  reason?: string;
}

export class ReconcilePaymentDto {
  @IsString()
  @IsNotEmpty()
  paymentId: string;

  @IsString()
  @IsNotEmpty()
  gateway: string;
}

@ApiTags('Payment Gateways Network')
@Controller('api/v1/payments')
export class GatewayController {
  constructor(private readonly gatewayService: GatewayService) {}

  @Post('initiate')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Initiate payment session through any verified provider gateway' })
  @ApiBody({ type: InitiatePaymentDto })
  @ApiResponse({ status: 200, description: 'Payment routing handshake completed successfully' })
  async initiatePayment(@Body() dto: InitiatePaymentDto) {
    return this.gatewayService.initiatePayment({
      amount: dto.amount,
      currency: 'INR',
      gateway: dto.gateway,
      orderId: dto.orderId,
    });
  }

  @Post('webhook/:gateway')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Catch webhooks from integrated payment aggregators' })
  async handleWebhook(@Param('gateway') gateway: string, @Body() payload: any) {
    return this.gatewayService.processWebhook(gateway, payload);
  }

  @Post('refund')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Initiate full or partial refund for a processed gateway transaction' })
  @ApiBody({ type: RefundPaymentDto })
  async refundPayment(@Body() dto: RefundPaymentDto) {
    return this.gatewayService.initiateRefund(dto.paymentId, dto.amount, dto.gateway, dto.reason);
  }

  @Post('reconcile')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Execute real-time reconciliation and settle transaction discrepancies' })
  @ApiBody({ type: ReconcilePaymentDto })
  async reconcilePayment(@Body() dto: ReconcilePaymentDto) {
    return this.gatewayService.reconcilePayment(dto.paymentId, dto.gateway);
  }

  @Get('audit-logs')
  @ApiOperation({ summary: 'Fetch dynamic logs of incoming aggregator webhook payloads' })
  async getAuditLogs() {
    return this.gatewayService.getPaymentLogs();
  }
}
