import { Controller, Get, Post, Body, HttpCode, HttpStatus, Param, Query, Put } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiParam, ApiBody, ApiQuery } from '@nestjs/swagger';
import { WalletService } from './wallet.service';
import { IsNotEmpty, IsNumber, Min, IsString, IsOptional, IsBoolean } from 'class-validator';

export class TransactionDto {
  @IsNumber()
  @Min(0.01)
  amount: number;

  @IsString()
  @IsNotEmpty()
  service: string;

  @IsString()
  @IsNotEmpty()
  description: string;

  @IsString()
  @IsOptional()
  paymentMethod?: string; // "WALLET" or "CREDIT_LINE"

  @IsString()
  @IsOptional()
  mpin?: string;
}

export class RepayCreditDto {
  @IsNumber()
  @Min(0.01)
  amount: number;

  @IsString()
  @IsOptional()
  emiId?: string;
}

export class CreditUpgradeDto {
  @IsNumber()
  @Min(1000)
  requestedLimit: number;
}

export class BankSettlementDto {
  @IsString()
  @IsNotEmpty()
  beneficiaryName: string;

  @IsString()
  @IsNotEmpty()
  bankName: string;

  @IsString()
  @IsNotEmpty()
  accountNumber: string;

  @IsString()
  @IsNotEmpty()
  ifscCode: string;

  @IsNumber()
  @Min(1)
  amount: number;
}

export class ToggleFreezeDto {
  @IsBoolean()
  isFrozen: boolean;
}

@ApiTags('Wallet and Credit Engine')
@Controller('api/v1/wallet')
export class WalletController {
  constructor(private readonly walletService: WalletService) {}

  @Get(':userId/balance')
  @ApiOperation({ summary: 'Retrieve dynamic multi-tenant ledger balances and active credit lines' })
  @ApiParam({ name: 'userId', description: 'User / Merchant Unique Identifier' })
  @ApiResponse({ status: 200, description: 'Balancing accounts successfully loaded.' })
  getWalletBalance(@Param('userId') userId: string) {
    return this.walletService.getWallet(userId);
  }

  @Post(':userId/transaction')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Post real-time trade transactions (AEPS, DMT, BBPS) with auto-ledgering and commission split' })
  @ApiParam({ name: 'userId', description: 'User / Merchant Unique Identifier' })
  @ApiBody({ type: TransactionDto })
  async postTransaction(@Param('userId') userId: string, @Body() body: TransactionDto) {
    return await this.walletService.processTransaction(userId, {
      type: 'DEBIT',
      amount: body.amount,
      service: body.service,
      description: body.description,
      paymentMethod: body.paymentMethod,
      mpin: body.mpin,
    });
  }

  @Post(':userId/credit-repay')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Repay outstanding B2B credit line balances or specific EMIs using wallet balance' })
  @ApiParam({ name: 'userId', description: 'User / Merchant Unique Identifier' })
  @ApiBody({ type: RepayCreditDto })
  async repayCredit(@Param('userId') userId: string, @Body() body: RepayCreditDto) {
    return await this.walletService.repayCredit(userId, body.amount, body.emiId);
  }

  @Post(':userId/credit-upgrade')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Apply/upgrade active B2B credit line buffers' })
  @ApiParam({ name: 'userId', description: 'User / Merchant Unique Identifier' })
  @ApiBody({ type: CreditUpgradeDto })
  creditUpgrade(@Param('userId') userId: string, @Body() body: CreditUpgradeDto) {
    return this.walletService.requestCreditLineIncrease(userId, body.requestedLimit);
  }

  @Post(':userId/settle-bank')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Settle trading funds outward into verified merchant bank nodal account' })
  @ApiParam({ name: 'userId', description: 'User / Merchant Unique Identifier' })
  @ApiBody({ type: BankSettlementDto })
  async settleBank(@Param('userId') userId: string, @Body() body: BankSettlementDto) {
    return await this.walletService.executeBankSettlement(userId, body);
  }

  @Get(':userId/settlements')
  @ApiOperation({ summary: 'Retrieve merchant nodal settlements history log' })
  @ApiParam({ name: 'userId', description: 'User / Merchant Unique Identifier' })
  getSettlements(@Param('userId') userId: string) {
    return this.walletService.getSettlements(userId);
  }

  @Put(':userId/toggle-freeze')
  @ApiOperation({ summary: 'Admin security-compliance action to freeze/unfreeze merchant ledger operations' })
  @ApiParam({ name: 'userId', description: 'User / Merchant Unique Identifier' })
  @ApiBody({ type: ToggleFreezeDto })
  toggleFreeze(@Param('userId') userId: string, @Body() body: ToggleFreezeDto) {
    return this.walletService.toggleWalletFreeze(userId, body.isFrozen);
  }

  @Get('ledger')
  @ApiOperation({ summary: 'Retrieve audit logs for all trade transactions (Global Double Entry)' })
  @ApiQuery({ name: 'userId', required: false, description: 'Filter logs by tenant unique ID' })
  getLedger(@Query('userId') userId?: string) {
    return this.walletService.getLedger(userId);
  }

  @Get('commission-rules')
  @ApiOperation({ summary: 'Fetch dynamic platform commission split ratios' })
  getCommissionRules() {
    return this.walletService.getCommissionRules();
  }

  @Get('audit-logs')
  @ApiOperation({ summary: 'Fetch admin dashboard global audit compliance logs' })
  getAuditLogs() {
    return this.walletService.getAuditLogs();
  }

  @Post('reconcile')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Admin security-audit action to verify mathematical double-entry balance parity' })
  reconcileGlobalLedger() {
    return this.walletService.reconcileGlobalLedger();
  }
}
