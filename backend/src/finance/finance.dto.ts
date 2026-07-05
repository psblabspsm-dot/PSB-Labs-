import { IsString, IsNotEmpty, IsNumber, IsOptional, IsBoolean, IsArray, ValidateNested, IsDateString } from 'class-validator';
import { Type } from 'class-transformer';

export class CreateAccountDto {
  @IsString()
  @IsNotEmpty()
  code: string;

  @IsString()
  @IsNotEmpty()
  name: string;

  @IsString()
  @IsNotEmpty()
  type: string; // ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE

  @IsString()
  @IsOptional()
  currency?: string;
}

export class CreateJournalItemDto {
  @IsString()
  @IsNotEmpty()
  accountId: string;

  @IsNumber()
  debit: number;

  @IsNumber()
  credit: number;

  @IsString()
  @IsOptional()
  description?: string;
}

export class CreateJournalEntryDto {
  @IsString()
  @IsNotEmpty()
  description: string;

  @IsString()
  @IsOptional()
  reference?: string;

  @IsString()
  @IsNotEmpty()
  financialYear: string;

  @IsArray()
  @ValidateNested({ friendships: true })
  @Type(() => CreateJournalItemDto)
  items: CreateJournalItemDto[];
}

export class ApproveJournalEntryDto {
  @IsString()
  @IsNotEmpty()
  approvedBy: string;
}

export class CreateInvoiceItemDto {
  @IsString()
  @IsNotEmpty()
  description: string;

  @IsString()
  @IsNotEmpty()
  hsnSacCode: string;

  @IsNumber()
  quantity: number;

  @IsNumber()
  unitPrice: number;

  @IsNumber()
  taxRatePercent: number; // e.g. 18.0
}

export class CreateInvoiceDto {
  @IsString()
  @IsNotEmpty()
  invoiceType: string; // TAX_INVOICE, PROFORMA_INVOICE, RETAIL_INVOICE, PURCHASE_INVOICE

  @IsString()
  @IsNotEmpty()
  customerId: string;

  @IsString()
  @IsNotEmpty()
  customerName: string;

  @IsString()
  @IsOptional()
  customerGstin?: string;

  @IsString()
  @IsOptional()
  customerAddress?: string;

  @IsString()
  @IsNotEmpty()
  placeOfSupply: string; // State Name (e.g. "Karnataka", "Maharashtra")

  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => CreateInvoiceItemDto)
  items: CreateInvoiceItemDto[];
}

export class CreateAdjustmentNoteDto {
  @IsString()
  @IsNotEmpty()
  noteType: string; // CREDIT_NOTE, DEBIT_NOTE

  @IsString()
  @IsNotEmpty()
  referenceInvoice: string;

  @IsString()
  @IsNotEmpty()
  customerId: string;

  @IsString()
  @IsNotEmpty()
  customerName: string;

  @IsString()
  @IsNotEmpty()
  reason: string;

  @IsNumber()
  taxableValue: number;

  @IsNumber()
  gstAmount: number;
}

export class CreateExpenseDto {
  @IsString()
  @IsNotEmpty()
  category: string; // MARKETING, OFFICE, TRAVEL, TECH_INFRA, REIMBURSEMENT

  @IsNumber()
  amount: number;

  @IsString()
  @IsOptional()
  currency?: string;

  @IsString()
  @IsNotEmpty()
  description: string;

  @IsString()
  @IsNotEmpty()
  claimedBy: string;

  @IsString()
  @IsOptional()
  vendorName?: string;

  @IsBoolean()
  @IsOptional()
  isRecurring?: boolean;

  @IsString()
  @IsOptional()
  recurrenceCron?: string;

  @IsString()
  @IsOptional()
  invoiceUrl?: string;
}

export class CreateBudgetDto {
  @IsString()
  @IsNotEmpty()
  category: string;

  @IsNumber()
  allocatedAmount: number;

  @IsString()
  @IsNotEmpty()
  fiscalYear: string;
}

export class CreateComplianceRecordDto {
  @IsString()
  @IsNotEmpty()
  recordType: string; // KYC_REVIEW, AML_ALERT, RISK_SCORE, TRANSACTION_MONITOR

  @IsString()
  @IsNotEmpty()
  status: string; // APPROVED, SUSPICIOUS, INVESTIGATING, CLEAR

  @IsString()
  @IsOptional()
  entityId?: string;

  @IsString()
  @IsNotEmpty()
  description: string;

  @IsNumber()
  @IsOptional()
  riskScore?: number;

  @IsString()
  @IsOptional()
  investigator?: string;
}
