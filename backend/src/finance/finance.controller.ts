import { 
  Controller, 
  Get, 
  Post, 
  Body, 
  Param, 
  Headers, 
  Query, 
  HttpCode, 
  HttpStatus,
  BadRequestException
} from '@nestjs/common';
import { FinanceService } from './finance.service';
import { 
  CreateAccountDto, 
  CreateJournalEntryDto, 
  CreateInvoiceDto, 
  CreateAdjustmentNoteDto, 
  CreateExpenseDto, 
  CreateBudgetDto, 
  CreateComplianceRecordDto,
  ApproveJournalEntryDto
} from './finance.dto';

@Controller('finance')
export class FinanceController {
  constructor(private readonly financeService: FinanceService) {}

  private getTenantId(tenantHeader?: string): string {
    return tenantHeader || 'default-tenant-1';
  }

  // ---------------------------------------------------------------------------
  // CHART OF ACCOUNTS
  // ---------------------------------------------------------------------------
  @Get('accounts')
  async getAccounts(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.getAccounts(tenantId);
  }

  @Post('accounts')
  async createAccount(
    @Body() dto: CreateAccountDto,
    @Headers('x-tenant-id') tenantHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.createAccount(tenantId, dto);
  }

  // ---------------------------------------------------------------------------
  // JOURNAL ENTRIES
  // ---------------------------------------------------------------------------
  @Get('journals')
  async getJournalEntries(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.getJournalEntries(tenantId);
  }

  @Post('journals')
  async createJournalEntry(
    @Body() dto: CreateJournalEntryDto,
    @Headers('x-tenant-id') tenantHeader?: string,
    @Headers('x-user-email') userEmailHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    const userEmail = userEmailHeader || 'admin@suryacredit.com';
    return this.financeService.createJournalEntry(tenantId, userEmail, dto);
  }

  @Post('journals/:id/approve')
  async approveJournalEntry(
    @Param('id') id: string,
    @Body() dto: ApproveJournalEntryDto,
    @Headers('x-tenant-id') tenantHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.approveJournalEntry(tenantId, id, dto.approvedBy);
  }

  // ---------------------------------------------------------------------------
  // TAX INVOICES & BILLING
  // ---------------------------------------------------------------------------
  @Get('invoices')
  async getInvoices(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.getInvoices(tenantId);
  }

  @Post('invoices')
  async createInvoice(
    @Body() dto: CreateInvoiceDto,
    @Headers('x-tenant-id') tenantHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.createInvoice(tenantId, dto);
  }

  @Post('invoices/:id/pay')
  async payInvoice(
    @Param('id') id: string,
    @Headers('x-tenant-id') tenantHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.markInvoicePaid(tenantId, id);
  }

  // ---------------------------------------------------------------------------
  // ADJUSTMENT NOTES
  // ---------------------------------------------------------------------------
  @Get('adjustment-notes')
  async getAdjustmentNotes(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.getAdjustmentNotes(tenantId);
  }

  @Post('adjustment-notes')
  async createAdjustmentNote(
    @Body() dto: CreateAdjustmentNoteDto,
    @Headers('x-tenant-id') tenantHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.createAdjustmentNote(tenantId, dto);
  }

  // ---------------------------------------------------------------------------
  // EXPENSES & BUDGETS
  // ---------------------------------------------------------------------------
  @Get('expenses')
  async getExpenses(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.getExpenses(tenantId);
  }

  @Post('expenses')
  async createExpense(
    @Body() dto: CreateExpenseDto,
    @Headers('x-tenant-id') tenantHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.createExpense(tenantId, dto);
  }

  @Post('expenses/:id/approve')
  async approveExpense(
    @Param('id') id: string,
    @Body('approvedBy') approvedBy: string,
    @Headers('x-tenant-id') tenantHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    if (!approvedBy) {
      throw new BadRequestException('approvedBy is required.');
    }
    return this.financeService.approveExpense(tenantId, id, approvedBy);
  }

  @Get('budgets')
  async getBudgets(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.getBudgets(tenantId);
  }

  @Post('budgets')
  async createBudget(
    @Body() dto: CreateBudgetDto,
    @Headers('x-tenant-id') tenantHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.createBudget(tenantId, dto);
  }

  // ---------------------------------------------------------------------------
  // COMPLIANCE & AML WORKFLOWS
  // ---------------------------------------------------------------------------
  @Get('compliance')
  async getComplianceRecords(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.getComplianceRecords(tenantId);
  }

  @Post('compliance')
  async createComplianceRecord(
    @Body() dto: CreateComplianceRecordDto,
    @Headers('x-tenant-id') tenantHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.createComplianceRecord(tenantId, dto);
  }

  @Get('compliance/analyze')
  async analyzeAml(
    @Query('amount') amount: number,
    @Query('customerId') customerId: string,
    @Headers('x-tenant-id') tenantHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    if (!amount || !customerId) {
      throw new BadRequestException('amount and customerId are required.');
    }
    return this.financeService.analyzeTransactionAmlRisk(tenantId, Number(amount), customerId);
  }

  // ---------------------------------------------------------------------------
  // FINANCE REPORTS
  // ---------------------------------------------------------------------------
  @Get('reports/trial-balance')
  async getTrialBalance(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.getTrialBalance(tenantId);
  }

  @Get('reports/p-and-l')
  async getProfitAndLoss(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.getProfitAndLoss(tenantId);
  }

  @Get('reports/balance-sheet')
  async getBalanceSheet(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.getBalanceSheet(tenantId);
  }

  @Get('reports/gst')
  async getGstSummary(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.getGstSummary(tenantId);
  }

  // ---------------------------------------------------------------------------
  // TENANT DISASTER BACKUP & RESTORE
  // ---------------------------------------------------------------------------
  @Get('backup')
  async backupTenant(@Headers('x-tenant-id') tenantHeader?: string) {
    const tenantId = this.getTenantId(tenantHeader);
    return this.financeService.backupTenantFinancials(tenantId);
  }

  @Post('restore')
  @HttpCode(HttpStatus.OK)
  async restoreTenant(
    @Body('payload') payload: string,
    @Headers('x-tenant-id') tenantHeader?: string
  ) {
    const tenantId = this.getTenantId(tenantHeader);
    if (!payload) {
      throw new BadRequestException('payload is required.');
    }
    return this.financeService.restoreTenantFinancials(tenantId, payload);
  }
}
