import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { 
  CreateAccountDto, 
  CreateJournalEntryDto, 
  CreateInvoiceDto, 
  CreateAdjustmentNoteDto, 
  CreateExpenseDto, 
  CreateBudgetDto, 
  CreateComplianceRecordDto 
} from './finance.dto';
import * as crypto from 'crypto';

export interface FinancialAccount {
  id: string;
  tenantId: string;
  code: string;
  name: string;
  type: string; // ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE
  currency: string;
  balance: number;
  createdAt: Date;
  updatedAt: Date;
}

export interface JournalItem {
  id: string;
  entryId: string;
  accountId: string;
  accountName: string;
  accountCode: string;
  debit: number;
  credit: number;
  description: string | null;
  createdAt: Date;
}

export interface JournalEntry {
  id: string;
  tenantId: string;
  entryNumber: string;
  reference: string | null;
  description: string;
  postingDate: Date;
  financialYear: string;
  status: 'DRAFT' | 'POSTED' | 'REJECTED';
  createdBy: string;
  approvedBy: string | null;
  createdAt: Date;
  updatedAt: Date;
  items: JournalItem[];
}

export interface GstInvoiceItem {
  id: string;
  invoiceId: string;
  description: string;
  hsnSacCode: string;
  quantity: number;
  unitPrice: number;
  taxRatePercent: number;
  taxableValue: number;
  taxAmount: number;
  totalValue: number;
}

export interface GstInvoice {
  id: string;
  tenantId: string;
  invoiceNumber: string;
  invoiceType: string; // TAX_INVOICE, PROFORMA_INVOICE, RETAIL_INVOICE, PURCHASE_INVOICE
  customerId: string;
  customerName: string;
  customerGstin: string | null;
  customerAddress: string | null;
  placeOfSupply: string;
  totalTaxableVal: number;
  cgst: number;
  sgst: number;
  igst: number;
  utgst: number;
  grandTotal: number;
  status: 'UNPAID' | 'PAID' | 'CANCELLED';
  qrCodeUrl: string | null;
  pdfUrl: string | null;
  createdAt: Date;
  updatedAt: Date;
  items: GstInvoiceItem[];
}

export interface FinAdjustmentNote {
  id: string;
  tenantId: string;
  noteNumber: string;
  noteType: 'CREDIT_NOTE' | 'DEBIT_NOTE';
  referenceInvoice: string;
  customerId: string;
  customerName: string;
  reason: string;
  taxableValue: number;
  gstAmount: number;
  totalValue: number;
  createdAt: Date;
}

export interface FinExpense {
  id: string;
  tenantId: string;
  category: string;
  amount: number;
  currency: string;
  description: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'PAID';
  claimedBy: string;
  approvedBy: string | null;
  vendorName: string | null;
  isRecurring: boolean;
  recurrenceCron: string | null;
  invoiceUrl: string | null;
  paymentDate: Date | null;
  createdAt: Date;
  updatedAt: Date;
}

export interface FinBudget {
  id: string;
  tenantId: string;
  category: string;
  allocatedAmount: number;
  spentAmount: number;
  fiscalYear: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface ComplianceRecord {
  id: string;
  tenantId: string;
  recordType: string; // KYC_REVIEW, AML_ALERT, RISK_SCORE, TRANSACTION_MONITOR
  status: string; // APPROVED, SUSPICIOUS, INVESTIGATING, CLEAR
  entityId: string | null;
  description: string;
  riskScore: number | null; // 0 to 100
  investigator: string | null;
  createdAt: Date;
}

@Injectable()
export class FinanceService {
  private accounts: FinancialAccount[] = [];
  private journalEntries: JournalEntry[] = [];
  private invoices: GstInvoice[] = [];
  private adjustmentNotes: FinAdjustmentNote[] = [];
  private expenses: FinExpense[] = [];
  private budgets: FinBudget[] = [];
  private complianceRecords: ComplianceRecord[] = [];

  constructor() {
    this.seedDefaultData();
  }

  private seedDefaultData() {
    const tenantId = 'default-tenant-1';
    
    // Seed default chart of accounts
    const initialAccounts = [
      { code: '1010', name: 'Cash Account', type: 'ASSET' },
      { code: '1020', name: 'HDFC Bank Account', type: 'ASSET' },
      { code: '1110', name: 'Accounts Receivable', type: 'ASSET' },
      { code: '1210', name: 'Office Equipment', type: 'ASSET' },
      { code: '2010', name: 'Accounts Payable', type: 'LIABILITY' },
      { code: '2110', name: 'CGST Output Tax Payable', type: 'LIABILITY' },
      { code: '2120', name: 'SGST Output Tax Payable', type: 'LIABILITY' },
      { code: '2130', name: 'IGST Output Tax Payable', type: 'LIABILITY' },
      { code: '3010', name: 'Shareholder Equity', type: 'EQUITY' },
      { code: '4010', name: 'SaaS Subscription Revenue', type: 'REVENUE' },
      { code: '4020', name: 'Marketplace commission fees', type: 'REVENUE' },
      { code: '5010', name: 'Cloud Server Infrastructure', type: 'EXPENSE' },
      { code: '5020', name: 'Office rent & utilities', type: 'EXPENSE' },
      { code: '5030', name: 'Marketing & advertising', type: 'EXPENSE' },
    ];

    initialAccounts.forEach(acc => {
      this.accounts.push({
        id: crypto.randomUUID(),
        tenantId,
        code: acc.code,
        name: acc.name,
        type: acc.type,
        currency: 'INR',
        balance: acc.type === 'ASSET' ? 1500000.0 : (acc.type === 'EQUITY' ? 1500000.0 : 0.0),
        createdAt: new Date(),
        updatedAt: new Date(),
      });
    });

    // Seed budget allocations
    this.budgets.push({
      id: crypto.randomUUID(),
      tenantId,
      category: 'TECH_INFRA',
      allocatedAmount: 500000.0,
      spentAmount: 120000.0,
      fiscalYear: 'FY-2026-27',
      createdAt: new Date(),
      updatedAt: new Date(),
    }, {
      id: crypto.randomUUID(),
      tenantId,
      category: 'MARKETING',
      allocatedAmount: 300000.0,
      spentAmount: 85000.0,
      fiscalYear: 'FY-2026-27',
      createdAt: new Date(),
      updatedAt: new Date(),
    });

    // Seed corporate expense
    this.expenses.push({
      id: 'exp-1',
      tenantId,
      category: 'TECH_INFRA',
      amount: 120000.0,
      currency: 'INR',
      description: 'AWS Web Services June Bill',
      status: 'APPROVED',
      claimedBy: 'tech-lead@suryacredit.com',
      approvedBy: 'finance-admin@suryacredit.com',
      vendorName: 'Amazon Web Services Inc',
      isRecurring: true,
      recurrenceCron: '0 0 1 * *',
      invoiceUrl: 'https://aws.amazon.com/invoice_6281.pdf',
      paymentDate: new Date(),
      createdAt: new Date(),
      updatedAt: new Date(),
    });

    // Seed dynamic KYC reviews
    this.complianceRecords.push({
      id: crypto.randomUUID(),
      tenantId,
      recordType: 'KYC_REVIEW',
      status: 'APPROVED',
      entityId: 'user-id-7182',
      description: 'Aadhaar and PAN matches e-KYC database successfully.',
      riskScore: 12.5,
      investigator: 'compliance-officer@suryacredit.com',
      createdAt: new Date(),
    }, {
      id: crypto.randomUUID(),
      tenantId,
      recordType: 'AML_ALERT',
      status: 'CLEAR',
      entityId: 'txn-id-99128',
      description: 'Transaction volume of INR 4,50,000 cleared due to legitimate distributor balance settlement documentation.',
      riskScore: 45.0,
      investigator: 'compliance-officer@suryacredit.com',
      createdAt: new Date(),
    });
  }

  // ---------------------------------------------------------------------------
  // 1. CHART OF ACCOUNTS & LEDGERS
  // ---------------------------------------------------------------------------
  async getAccounts(tenantId: string) {
    return this.accounts.filter(a => a.tenantId === tenantId);
  }

  async createAccount(tenantId: string, dto: CreateAccountDto) {
    const existing = this.accounts.find(a => a.tenantId === tenantId && a.code === dto.code);
    if (existing) {
      throw new BadRequestException(`Account with code ${dto.code} already exists.`);
    }

    const newAcc: FinancialAccount = {
      id: crypto.randomUUID(),
      tenantId,
      code: dto.code,
      name: dto.name,
      type: dto.type,
      currency: dto.currency || 'INR',
      balance: 0.0,
      createdAt: new Date(),
      updatedAt: new Date(),
    };
    this.accounts.push(newAcc);
    return newAcc;
  }

  // ---------------------------------------------------------------------------
  // 2. DOUBLE ENTRY JOURNALS & MAKER-CHECKER WORKFLOWS
  // ---------------------------------------------------------------------------
  async getJournalEntries(tenantId: string) {
    return this.journalEntries.filter(je => je.tenantId === tenantId);
  }

  async createJournalEntry(tenantId: string, createdBy: string, dto: CreateJournalEntryDto) {
    // Verify total debits match credits for proper ledger double entry
    let totalDebit = 0;
    let totalCredit = 0;
    for (const item of dto.items) {
      totalDebit += item.debit;
      totalCredit += item.credit;
      
      const acc = this.accounts.find(a => a.id === item.accountId && a.tenantId === tenantId);
      if (!acc) {
        throw new BadRequestException(`Account ID ${item.accountId} does not exist for this tenant.`);
      }
    }

    if (Math.abs(totalDebit - totalCredit) > 0.01) {
      throw new BadRequestException(`Double Entry Failure: Total Debits (INR ${totalDebit}) must exactly equal Total Credits (INR ${totalCredit}).`);
    }

    const entryNumber = `JE-${new Date().getFullYear()}-${1001 + this.journalEntries.length}`;
    
    const entryItems: JournalItem[] = dto.items.map(item => {
      const acc = this.accounts.find(a => a.id === item.accountId)!;
      return {
        id: crypto.randomUUID(),
        entryId: '',
        accountId: item.accountId,
        accountName: acc.name,
        accountCode: acc.code,
        debit: item.debit,
        credit: item.credit,
        description: item.description || null,
        createdAt: new Date(),
      };
    });

    const newEntry: JournalEntry = {
      id: crypto.randomUUID(),
      tenantId,
      entryNumber,
      reference: dto.reference || null,
      description: dto.description,
      postingDate: new Date(),
      financialYear: dto.financialYear,
      status: 'DRAFT', // Maker creates, Checker posts
      createdBy,
      approvedBy: null,
      createdAt: new Date(),
      updatedAt: new Date(),
      items: entryItems,
    };

    newEntry.items.forEach(i => i.entryId = newEntry.id);
    this.journalEntries.push(newEntry);
    return newEntry;
  }

  async approveJournalEntry(tenantId: string, entryId: string, approvedBy: string) {
    const entry = this.journalEntries.find(je => je.id === entryId && je.tenantId === tenantId);
    if (!entry) {
      throw new NotFoundException(`Journal entry not found.`);
    }

    if (entry.status !== 'DRAFT') {
      throw new BadRequestException(`Journal entry has already been processed.`);
    }

    // Apply adjustments to account balances
    entry.items.forEach(item => {
      const acc = this.accounts.find(a => a.id === item.accountId)!;
      
      // Debit increases Assets & Expenses, decreases Liabilities, Equity & Revenues
      if (acc.type === 'ASSET' || acc.type === 'EXPENSE') {
        acc.balance += item.debit;
        acc.balance -= item.credit;
      } else {
        acc.balance -= item.debit;
        acc.balance += item.credit;
      }
      acc.updatedAt = new Date();
    });

    entry.status = 'POSTED';
    entry.approvedBy = approvedBy;
    entry.updatedAt = new Date();

    return entry;
  }

  // ---------------------------------------------------------------------------
  // 3. TAX BILLING & INVOICING CORE
  // ---------------------------------------------------------------------------
  async getInvoices(tenantId: string) {
    return this.invoices.filter(inv => inv.tenantId === tenantId);
  }

  async createInvoice(tenantId: string, dto: CreateInvoiceDto) {
    const isInterState = dto.placeOfSupply.toLowerCase() !== 'karnataka'; // Default tenant state is Karnataka

    let totalTaxableVal = 0;
    let cgst = 0;
    let sgst = 0;
    let igst = 0;

    const invoiceId = crypto.randomUUID();
    const invoiceNumber = `INV-${new Date().getFullYear()}-${10001 + this.invoices.length}`;

    const invoiceItems: GstInvoiceItem[] = dto.items.map(item => {
      const taxableValue = item.quantity * item.unitPrice;
      const taxAmount = (taxableValue * item.taxRatePercent) / 100;
      const totalValue = taxableValue + taxAmount;

      totalTaxableVal += taxableValue;

      if (isInterState) {
        igst += taxAmount;
      } else {
        cgst += taxAmount / 2;
        sgst += taxAmount / 2;
      }

      return {
        id: crypto.randomUUID(),
        invoiceId,
        description: item.description,
        hsnSacCode: item.hsnSacCode,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        taxRatePercent: item.taxRatePercent,
        taxableValue,
        taxAmount,
        totalValue,
      };
    });

    const grandTotal = totalTaxableVal + cgst + sgst + igst;

    const newInvoice: GstInvoice = {
      id: invoiceId,
      tenantId,
      invoiceNumber,
      invoiceType: dto.invoiceType,
      customerId: dto.customerId,
      customerName: dto.customerName,
      customerGstin: dto.customerGstin || null,
      customerAddress: dto.customerAddress || null,
      placeOfSupply: dto.placeOfSupply,
      totalTaxableVal,
      cgst,
      sgst,
      igst,
      utgst: 0.0,
      grandTotal,
      status: 'UNPAID',
      qrCodeUrl: `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=surya-credit:${invoiceNumber}:total:${grandTotal}`,
      pdfUrl: `https://suryacredit-invoices.s3.amazonaws.com/${tenantId}/${invoiceNumber}.pdf`,
      createdAt: new Date(),
      updatedAt: new Date(),
      items: invoiceItems,
    };

    this.invoices.push(newInvoice);

    // Automatically generate General Ledger Journal entry for double-entry bookkeeping!
    await this.generateInvoiceJournalEntry(tenantId, newInvoice);

    return newInvoice;
  }

  private async generateInvoiceJournalEntry(tenantId: string, invoice: GstInvoice) {
    try {
      const accounts = await this.getAccounts(tenantId);
      const accountsReceivable = accounts.find(a => a.code === '1110')!;
      const subscriptionRev = accounts.find(a => a.code === '4010')!;
      const cgstOutput = accounts.find(a => a.code === '2110')!;
      const sgstOutput = accounts.find(a => a.code === '2120')!;
      const igstOutput = accounts.find(a => a.code === '2130')!;

      const itemsDto = [
        { accountId: accountsReceivable.id, debit: invoice.grandTotal, credit: 0.0 },
        { accountId: subscriptionRev.id, debit: 0.0, credit: invoice.totalTaxableVal },
      ];

      if (invoice.igst > 0) {
        itemsDto.push({ accountId: igstOutput.id, debit: 0.0, credit: invoice.igst });
      } else {
        itemsDto.push(
          { accountId: cgstOutput.id, debit: 0.0, credit: invoice.cgst },
          { accountId: sgstOutput.id, debit: 0.0, credit: invoice.sgst }
        );
      }

      const je = await this.createJournalEntry(tenantId, 'SYSTEM_INVOICING', {
        description: `Revenue Recognised & GST output for Invoice ${invoice.invoiceNumber}`,
        reference: invoice.id,
        financialYear: 'FY-2026-27',
        items: itemsDto,
      });

      // System auto-approves system invoices postings
      await this.approveJournalEntry(tenantId, je.id, 'SYSTEM_AUTO_POST');
    } catch (err) {
      console.error('Failed to post automatic journal entry for invoice', err);
    }
  }

  async markInvoicePaid(tenantId: string, invoiceId: string) {
    const inv = this.invoices.find(i => i.id === invoiceId && i.tenantId === tenantId);
    if (!inv) throw new NotFoundException('Invoice not found.');
    inv.status = 'PAID';
    inv.updatedAt = new Date();

    // Journal Entry for payment: Debit Bank, Credit Accounts Receivable
    try {
      const accounts = await this.getAccounts(tenantId);
      const bankAcc = accounts.find(a => a.code === '1020')!;
      const recAcc = accounts.find(a => a.code === '1110')!;

      const je = await this.createJournalEntry(tenantId, 'SYSTEM_INVOICING', {
        description: `Payment Received for Invoice ${inv.invoiceNumber}`,
        reference: inv.id,
        financialYear: 'FY-2026-27',
        items: [
          { accountId: bankAcc.id, debit: inv.grandTotal, credit: 0.0 },
          { accountId: recAcc.id, debit: 0.0, credit: inv.grandTotal },
        ],
      });
      await this.approveJournalEntry(tenantId, je.id, 'SYSTEM_AUTO_POST');
    } catch (err) {
      console.error('Failed to auto-post invoice payment entry', err);
    }

    return inv;
  }

  // ---------------------------------------------------------------------------
  // 4. CREDIT AND DEBIT ADJUSTMENT NOTES
  // ---------------------------------------------------------------------------
  async getAdjustmentNotes(tenantId: string) {
    return this.adjustmentNotes.filter(n => n.tenantId === tenantId);
  }

  async createAdjustmentNote(tenantId: string, dto: CreateAdjustmentNoteDto) {
    const noteNumber = `${dto.noteType === 'CREDIT_NOTE' ? 'CN' : 'DN'}-${1001 + this.adjustmentNotes.length}`;
    
    const newNote: FinAdjustmentNote = {
      id: crypto.randomUUID(),
      tenantId,
      noteNumber,
      noteType: dto.noteType as any,
      referenceInvoice: dto.referenceInvoice,
      customerId: dto.customerId,
      customerName: dto.customerName,
      reason: dto.reason,
      taxableValue: dto.taxableValue,
      gstAmount: dto.gstAmount,
      totalValue: dto.taxableValue + dto.gstAmount,
      createdAt: new Date(),
    };

    this.adjustmentNotes.push(newNote);
    return newNote;
  }

  // ---------------------------------------------------------------------------
  // 5. CORPORATE EXPENSES & BUDGET MANAGER
  // ---------------------------------------------------------------------------
  async getExpenses(tenantId: string) {
    return this.expenses.filter(e => e.tenantId === tenantId);
  }

  async getBudgets(tenantId: string) {
    return this.budgets.filter(b => b.tenantId === tenantId);
  }

  async createBudget(tenantId: string, dto: CreateBudgetDto) {
    const key = `${tenantId}-${dto.category}-${dto.fiscalYear}`;
    const existing = this.budgets.find(b => `${b.tenantId}-${b.category}-${b.fiscalYear}` === key);
    if (existing) {
      existing.allocatedAmount = dto.allocatedAmount;
      existing.updatedAt = new Date();
      return existing;
    }

    const budget: FinBudget = {
      id: crypto.randomUUID(),
      tenantId,
      category: dto.category,
      allocatedAmount: dto.allocatedAmount,
      spentAmount: 0.0,
      fiscalYear: dto.fiscalYear,
      createdAt: new Date(),
      updatedAt: new Date(),
    };
    this.budgets.push(budget);
    return budget;
  }

  async createExpense(tenantId: string, dto: CreateExpenseDto) {
    // Check Budget Limit before logging expense
    const budget = this.budgets.find(b => b.tenantId === tenantId && b.category === dto.category && b.fiscalYear === 'FY-2026-27');
    if (budget) {
      const projectSpent = budget.spentAmount + dto.amount;
      if (projectSpent > budget.allocatedAmount) {
        // Log warnings but permit creation with ALERT
        console.warn(`Budget limit alert! Category ${dto.category} would exceed limit of ${budget.allocatedAmount} (Currently spent: ${budget.spentAmount}, new claim: ${dto.amount})`);
      }
    }

    const exp: FinExpense = {
      id: crypto.randomUUID(),
      tenantId,
      category: dto.category,
      amount: dto.amount,
      currency: dto.currency || 'INR',
      description: dto.description,
      status: 'PENDING',
      claimedBy: dto.claimedBy,
      approvedBy: null,
      vendorName: dto.vendorName || null,
      isRecurring: dto.isRecurring || false,
      recurrenceCron: dto.recurrenceCron || null,
      invoiceUrl: dto.invoiceUrl || null,
      paymentDate: null,
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    this.expenses.push(exp);
    return exp;
  }

  async approveExpense(tenantId: string, expenseId: string, approvedBy: string) {
    const exp = this.expenses.find(e => e.id === expenseId && e.tenantId === tenantId);
    if (!exp) throw new NotFoundException('Expense claim not found.');
    if (exp.status !== 'PENDING') throw new BadRequestException('Expense claim is already processed.');

    // Update budget spent
    const budget = this.budgets.find(b => b.tenantId === tenantId && b.category === exp.category && b.fiscalYear === 'FY-2026-27');
    if (budget) {
      budget.spentAmount += exp.amount;
      budget.updatedAt = new Date();
    }

    // Ledger double entry: Debit Expense Account, Credit Bank Account
    try {
      const accounts = await this.getAccounts(tenantId);
      const expenseAcc = accounts.find(a => a.type === 'EXPENSE' && a.code === (exp.category === 'TECH_INFRA' ? '5010' : '5030')) || accounts.find(a => a.type === 'EXPENSE')!;
      const bankAcc = accounts.find(a => a.code === '1020')!;

      const je = await this.createJournalEntry(tenantId, 'SYSTEM_EXPENSES', {
        description: `Expense approved: ${exp.description}`,
        reference: exp.id,
        financialYear: 'FY-2026-27',
        items: [
          { accountId: expenseAcc.id, debit: exp.amount, credit: 0.0 },
          { accountId: bankAcc.id, debit: 0.0, credit: exp.amount },
        ],
      });
      await this.approveJournalEntry(tenantId, je.id, 'SYSTEM_AUTO_POST');
    } catch (err) {
      console.error('Failed to post expense double entry journal', err);
    }

    exp.status = 'APPROVED';
    exp.approvedBy = approvedBy;
    exp.paymentDate = new Date();
    exp.updatedAt = new Date();

    return exp;
  }

  // ---------------------------------------------------------------------------
  // 6. COMPLIANCE & AML WORKFLOWS
  // ---------------------------------------------------------------------------
  async getComplianceRecords(tenantId: string) {
    return this.complianceRecords.filter(cr => cr.tenantId === tenantId);
  }

  async createComplianceRecord(tenantId: string, dto: CreateComplianceRecordDto) {
    const rec: ComplianceRecord = {
      id: crypto.randomUUID(),
      tenantId,
      recordType: dto.recordType,
      status: dto.status,
      entityId: dto.entityId || null,
      description: dto.description,
      riskScore: dto.riskScore || null,
      investigator: dto.investigator || null,
      createdAt: new Date(),
    };
    this.complianceRecords.push(rec);
    return rec;
  }

  async analyzeTransactionAmlRisk(tenantId: string, amount: number, customerId: string) {
    // Dynamic rule engine checks
    let riskScore = 10.0; // baseline
    const reasons: string[] = [];

    if (amount > 200000) {
      riskScore += 45.0;
      reasons.push('High-value transaction above INR 2 Lakh threshold.');
    } else if (amount > 50000) {
      riskScore += 20.0;
      reasons.push('Medium-value transaction above INR 50k.');
    }

    // Check transaction frequency spikes
    const customerInvoices = this.invoices.filter(i => i.customerId === customerId && i.tenantId === tenantId);
    if (customerInvoices.length > 5) {
      riskScore -= 10.0; // Verified loyalty/history reduces risk
    } else {
      riskScore += 15.0; // New accounts trigger higher early vigilance
      reasons.push('Limited invoice trading history.');
    }

    const finalScore = Math.min(Math.max(riskScore, 0.0), 100.0);
    const status = finalScore > 50.0 ? 'SUSPICIOUS' : 'CLEAR';

    // Auto-create alert logs if suspicious
    if (status === 'SUSPICIOUS') {
      await this.createComplianceRecord(tenantId, {
        recordType: 'AML_ALERT',
        status: 'INVESTIGATING',
        entityId: customerId,
        description: `AML risk alert triggered for transaction level of INR ${amount}. Trigger reasons: ${reasons.join('; ')}`,
        riskScore: finalScore,
        investigator: 'SYSTEM_MONITOR',
      });
    }

    return {
      riskScore: finalScore,
      status,
      reasons,
    };
  }

  // ---------------------------------------------------------------------------
  // 7. REAL-TIME MULTI-TENANT FINANCIAL STATEMENTS & REGULATORY CORES
  // ---------------------------------------------------------------------------
  async getTrialBalance(tenantId: string) {
    const accounts = await this.getAccounts(tenantId);
    const lines = accounts.map(a => {
      const balance = a.balance;
      const isDebitSide = a.type === 'ASSET' || a.type === 'EXPENSE';
      return {
        id: a.id,
        code: a.code,
        name: a.name,
        type: a.type,
        debit: isDebitSide ? balance : 0.0,
        credit: !isDebitSide ? balance : 0.0,
      };
    });

    const totalDebit = lines.reduce((acc, l) => acc + l.debit, 0.0);
    const totalCredit = lines.reduce((acc, l) => acc + l.credit, 0.0);

    return {
      financialYear: 'FY-2026-27',
      lines,
      totalDebit,
      totalCredit,
    };
  }

  async getProfitAndLoss(tenantId: string) {
    const accounts = await this.getAccounts(tenantId);
    
    // Revenue calculations
    const revenueAccounts = accounts.filter(a => a.type === 'REVENUE');
    const revenueLines = revenueAccounts.map(a => ({ name: a.name, code: a.code, amount: a.balance }));
    const totalRevenue = revenueLines.reduce((acc, l) => acc + l.amount, 0.0);

    // Expense calculations
    const expenseAccounts = accounts.filter(a => a.type === 'EXPENSE');
    const expenseLines = expenseAccounts.map(a => ({ name: a.name, code: a.code, amount: a.balance }));
    const totalExpense = expenseLines.reduce((acc, l) => acc + l.amount, 0.0);

    const netProfit = totalRevenue - totalExpense;

    return {
      financialYear: 'FY-2026-27',
      revenueLines,
      totalRevenue,
      expenseLines,
      totalExpense,
      netProfit,
    };
  }

  async getBalanceSheet(tenantId: string) {
    const accounts = await this.getAccounts(tenantId);

    // Assets
    const assetAccounts = accounts.filter(a => a.type === 'ASSET');
    const assetLines = assetAccounts.map(a => ({ name: a.name, code: a.code, amount: a.balance }));
    const totalAssets = assetLines.reduce((acc, l) => acc + l.amount, 0.0);

    // Liabilities
    const liabilityAccounts = accounts.filter(a => a.type === 'LIABILITY');
    const liabilityLines = liabilityAccounts.map(a => ({ name: a.name, code: a.code, amount: a.balance }));
    const totalLiabilities = liabilityLines.reduce((acc, l) => acc + l.amount, 0.0);

    // Equity
    const equityAccounts = accounts.filter(a => a.type === 'EQUITY');
    const equityLines = equityAccounts.map(a => ({ name: a.name, code: a.code, amount: a.balance }));
    
    // Retained Earnings (Net profit from P&L)
    const pAndL = await this.getProfitAndLoss(tenantId);
    const retainedEarnings = pAndL.netProfit;
    equityLines.push({ name: 'Retained Earnings (P&L)', code: '3020', amount: retainedEarnings });

    const totalEquity = equityLines.reduce((acc, l) => acc + l.amount, 0.0);

    const totalLiabilitiesAndEquity = totalLiabilities + totalEquity;

    return {
      financialYear: 'FY-2026-27',
      assets: assetLines,
      totalAssets,
      liabilities: liabilityLines,
      totalLiabilities,
      equity: equityLines,
      totalEquity,
      totalLiabilitiesAndEquity,
    };
  }

  async getGstSummary(tenantId: string) {
    const accounts = await this.getAccounts(tenantId);
    
    const cgstOutput = accounts.find(a => a.code === '2110')?.balance || 0.0;
    const sgstOutput = accounts.find(a => a.code === '2120')?.balance || 0.0;
    const igstOutput = accounts.find(a => a.code === '2130')?.balance || 0.0;

    const totalGstCollected = cgstOutput + sgstOutput + igstOutput;

    // Calculate state breakdown metrics based on invoice places of supply
    const stateBreakdown: Record<string, { count: number, taxableVal: number, gstCollected: number }> = {};
    const tenantInvoices = this.invoices.filter(i => i.tenantId === tenantId);

    tenantInvoices.forEach(inv => {
      const state = inv.placeOfSupply;
      const gst = inv.cgst + inv.sgst + inv.igst;
      if (!stateBreakdown[state]) {
        stateBreakdown[state] = { count: 0, taxableVal: 0, gstCollected: 0 };
      }
      stateBreakdown[state].count += 1;
      stateBreakdown[state].taxableVal += inv.totalTaxableVal;
      stateBreakdown[state].gstCollected += gst;
    });

    return {
      financialYear: 'FY-2026-27',
      cgstPayable: cgstOutput,
      sgstPayable: sgstOutput,
      igstPayable: igstOutput,
      totalGstLiability: totalGstCollected,
      stateWiseBreakdown: Object.entries(stateBreakdown).map(([state, data]) => ({
        state,
        ...data,
      })),
    };
  }

  // ---------------------------------------------------------------------------
  // 8. TENANT BACKUP & RESTORE UTILITIES
  // ---------------------------------------------------------------------------
  async backupTenantFinancials(tenantId: string) {
    const dataToBackup = {
      tenantId,
      timestamp: new Date(),
      accounts: this.accounts.filter(a => a.tenantId === tenantId),
      journalEntries: this.journalEntries.filter(je => je.tenantId === tenantId),
      invoices: this.invoices.filter(i => i.tenantId === tenantId),
      adjustmentNotes: this.adjustmentNotes.filter(n => n.tenantId === tenantId),
      expenses: this.expenses.filter(e => e.tenantId === tenantId),
      budgets: this.budgets.filter(b => b.tenantId === tenantId),
      complianceRecords: this.complianceRecords.filter(cr => cr.tenantId === tenantId),
    };

    const backupString = JSON.stringify(dataToBackup, null, 2);
    return {
      filename: `fin_backup_${tenantId}_${Date.now()}.json`,
      payload: backupString,
    };
  }

  async restoreTenantFinancials(tenantId: string, backupPayload: string) {
    try {
      const parsed = JSON.parse(backupPayload);
      if (parsed.tenantId !== tenantId) {
        throw new BadRequestException('Backup matches a different tenant ID.');
      }

      // Flush existing tenant data
      this.accounts = this.accounts.filter(a => a.tenantId !== tenantId);
      this.journalEntries = this.journalEntries.filter(je => je.tenantId !== tenantId);
      this.invoices = this.invoices.filter(i => i.tenantId !== tenantId);
      this.adjustmentNotes = this.adjustmentNotes.filter(n => n.tenantId !== tenantId);
      this.expenses = this.expenses.filter(e => e.tenantId !== tenantId);
      this.budgets = this.budgets.filter(b => b.tenantId !== tenantId);
      this.complianceRecords = this.complianceRecords.filter(cr => cr.tenantId !== tenantId);

      // Re-populate
      if (parsed.accounts) this.accounts.push(...parsed.accounts);
      if (parsed.journalEntries) this.journalEntries.push(...parsed.journalEntries);
      if (parsed.invoices) this.invoices.push(...parsed.invoices);
      if (parsed.adjustmentNotes) this.adjustmentNotes.push(...parsed.adjustmentNotes);
      if (parsed.expenses) this.expenses.push(...parsed.expenses);
      if (parsed.budgets) this.budgets.push(...parsed.budgets);
      if (parsed.complianceRecords) this.complianceRecords.push(...parsed.complianceRecords);

      return { success: true, message: 'Tenant financial registry restored successfully from secure backup.' };
    } catch (err) {
      throw new BadRequestException(`Restore failed: ${err.message}`);
    }
  }
}
