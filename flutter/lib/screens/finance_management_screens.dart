import 'package:flutter/material.dart';

// --- MODELS FOR MOBILE STATE ---
enum AccountType { asset, liability, equity, revenue, expense }

class LedgerAccount {
  final String code;
  final String name;
  final AccountType type;
  double balance;

  LedgerAccount({
    required this.code,
    required this.name,
    required this.type,
    required this.balance,
  });
}

class JournalLine {
  final String accountCode;
  final String accountName;
  final double debit;
  final double credit;

  JournalLine({
    required this.accountCode,
    required this.accountName,
    required this.debit,
    required this.credit,
  });
}

class JournalEntry {
  final String entryNumber;
  final String description;
  final String reference;
  final DateTime postingDate;
  final String status; // 'DRAFT', 'POSTED'
  final String createdBy;
  String? approvedBy;
  final List<JournalLine> lines;

  JournalEntry({
    required this.entryNumber,
    required this.description,
    required this.reference,
    required this.postingDate,
    required this.status,
    required this.createdBy,
    this.approvedBy,
    required this.lines,
  });
}

class GstInvoiceItem {
  final String description;
  final String hsnCode;
  final int quantity;
  final double unitPrice;
  final double taxRatePercent;

  GstInvoiceItem({
    required this.description,
    required this.hsnCode,
    required this.quantity,
    required this.unitPrice,
    required this.taxRatePercent,
  });

  double get taxableValue => quantity * unitPrice;
  double get taxAmount => (taxableValue * taxRatePercent) / 100;
  double get totalValue => taxableValue + taxAmount;
}

class GstInvoice {
  final String invoiceNumber;
  final String customerName;
  final String? customerGstin;
  final String placeOfSupply;
  final String status; // 'UNPAID', 'PAID'
  final DateTime createdAt;
  final List<GstInvoiceItem> items;

  GstInvoice({
    required this.invoiceNumber,
    required this.customerName,
    this.customerGstin,
    required this.placeOfSupply,
    required this.status,
    required this.createdAt,
    required this.items,
  });

  double get totalTaxable => items.fold(0.0, (sum, i) => sum + i.taxableValue);
  double get totalTax => items.fold(0.0, (sum, i) => sum + i.taxAmount);
  double get grandTotal => totalTaxable + totalTax;
  bool get isInterState => placeOfSupply.toLowerCase() != 'karnataka';
}

class ExpenseClaim {
  final String id;
  final String description;
  final String category;
  final double amount;
  final String claimedBy;
  String status; // 'PENDING', 'APPROVED'
  final DateTime createdAt;

  ExpenseClaim({
    required this.id,
    required this.description,
    required this.category,
    required this.amount,
    required this.claimedBy,
    required this.status,
    required this.createdAt,
  });
}

class BudgetAllocation {
  final String category;
  final double allocated;
  double spent;

  BudgetAllocation({
    required this.category,
    required this.allocated,
    required this.spent,
  });
}

// --- MAIN SCREEN ---
class FinanceManagementScreens extends StatefulWidget {
  const FinanceManagementScreens({Key? key}) : super(key: key);

  @override
  State<FinanceManagementScreens> createState() => _FinanceManagementScreensState();
}

class _FinanceManagementScreensState extends State<FinanceManagementScreens> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  // --- CORE SYSTEM STATE ---
  final List<LedgerAccount> _accounts = [
    LedgerAccount(code: '1010', name: 'Cash Account', type: AccountType.asset, balance: 500000.0),
    LedgerAccount(code: '1020', name: 'HDFC Bank Account', type: AccountType.asset, balance: 1200000.0),
    LedgerAccount(code: '1110', name: 'Accounts Receivable', type: AccountType.asset, balance: 177000.0),
    LedgerAccount(code: '2010', name: 'Accounts Payable', type: AccountType.liability, balance: 80000.0),
    LedgerAccount(code: '2110', name: 'CGST Output Tax', type: AccountType.liability, balance: 13500.0),
    LedgerAccount(code: '2120', name: 'SGST Output Tax', type: AccountType.liability, balance: 13500.0),
    LedgerAccount(code: '2130', name: 'IGST Output Tax', type: AccountType.liability, balance: 18000.0),
    LedgerAccount(code: '3010', name: 'Shareholder Equity', type: AccountType.equity, balance: 1500000.0),
    LedgerAccount(code: '4010', name: 'SaaS Subscription Revenue', type: AccountType.revenue, balance: 250000.0),
    LedgerAccount(code: '5010', name: 'Cloud Server Infrastructure', type: AccountType.expense, balance: 120000.0),
    LedgerAccount(code: '5020', name: 'Office rent & utilities', type: AccountType.expense, balance: 45000.0),
  ];

  late final List<JournalEntry> _journals = [
    JournalEntry(
      entryNumber: 'JE-2026-1001',
      description: 'Capital Contribution from Founders',
      reference: 'BANK-TR-1290',
      postingDate: DateTime.now().subtract(const Duration(days: 10)),
      status: 'POSTED',
      createdBy: 'founder@suryacredit.com',
      approvedBy: 'audit-checker@suryacredit.com',
      lines: [
        JournalLine(accountCode: '1020', accountName: 'HDFC Bank Account', debit: 1500000.0, credit: 0.0),
        JournalLine(accountCode: '3010', accountName: 'Shareholder Equity', debit: 0.0, credit: 1500000.0),
      ],
    ),
    JournalEntry(
      entryNumber: 'JE-2026-1002',
      description: 'Acrued monthly AWS costs',
      reference: 'AWS-9912',
      postingDate: DateTime.now().subtract(const Duration(days: 2)),
      status: 'DRAFT',
      createdBy: 'devops@suryacredit.com',
      lines: [
        JournalLine(accountCode: '5010', accountName: 'Cloud Server Infrastructure', debit: 120000.0, credit: 0.0),
        JournalLine(accountCode: '2010', accountName: 'Accounts Payable', debit: 0.0, credit: 120000.0),
      ],
    ),
  ];

  late final List<GstInvoice> _invoices = [
    GstInvoice(
      invoiceNumber: 'INV-2026-10001',
      customerName: 'Shree Balaji Communications',
      customerGstin: '29AAECS4512A1Z4',
      placeOfSupply: 'Karnataka',
      status: 'UNPAID',
      createdAt: DateTime.now().subtract(const Duration(days: 3)),
      items: [
        GstInvoiceItem(description: 'FinTech Custom Gateway setup', hsnCode: '998311', quantity: 1, unitPrice: 150000.0, taxRatePercent: 18.0),
      ],
    ),
    GstInvoice(
      invoiceNumber: 'INV-2026-10002',
      customerName: 'Vikas Digital Maharashtra',
      customerGstin: '27AAKCV8192K2Z3',
      placeOfSupply: 'Maharashtra',
      status: 'PAID',
      createdAt: DateTime.now().subtract(const Duration(days: 1)),
      items: [
        GstInvoiceItem(description: 'MicroATM Device Procurement API', hsnCode: '998311', quantity: 1, unitPrice: 100000.0, taxRatePercent: 18.0),
      ],
    ),
  ];

  final List<ExpenseClaim> _expenses = [
    ExpenseClaim(id: 'exp-1', description: 'AWS Server Bill June', category: 'TECH_INFRA', amount: 120000.0, claimedBy: 'tech@suryacredit.com', status: 'APPROVED', createdAt: DateTime.now().subtract(const Duration(days: 5))),
    ExpenseClaim(id: 'exp-2', description: 'Brochure designs and printing', category: 'MARKETING', amount: 10000.0, claimedBy: 'sales@suryacredit.com', status: 'APPROVED', createdAt: DateTime.now().subtract(const Duration(days: 1))),
    ExpenseClaim(id: 'exp-3', description: 'Office Broadband Router replacement', category: 'OFFICE', amount: 4500.0, claimedBy: 'admin@suryacredit.com', status: 'PENDING', createdAt: DateTime.now()),
  ];

  final List<BudgetAllocation> _budgets = [
    BudgetAllocation(category: 'TECH_INFRA', allocated: 500000.0, spent: 120000.0),
    BudgetAllocation(category: 'MARKETING', allocated: 300000.0, spent: 10000.0),
    BudgetAllocation(category: 'OFFICE', allocated: 100000.0, spent: 0.0),
  ];

  // --- CONTROLLERS FOR DATA INSERTION ---
  final _customerNameCtrl = TextEditingController();
  final _invoiceQtyCtrl = TextEditingController(text: '1');
  final _invoicePriceCtrl = TextEditingController(text: '25000');
  String _placeOfSupply = 'Karnataka';

  final _expClaimantCtrl = TextEditingController();
  final _expDescCtrl = TextEditingController();
  final _expAmountCtrl = TextEditingController(text: '5000');
  String _expCat = 'TECH_INFRA';

  // --- AML COMPLIANCE SANDBOX ---
  final _amlAmountCtrl = TextEditingController(text: '250000');
  final _amlCustCtrl = TextEditingController(text: 'Ramesh Digital Systems');
  double _amlScore = 0.0;
  String _amlRisk = 'CLEAR';
  List<String> _amlFactors = [];

  @override
  void initState() {
    super.override absent;
    _tabController = TabController(length: 5, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    _customerNameCtrl.dispose();
    _invoiceQtyCtrl.dispose();
    _invoicePriceCtrl.dispose();
    _expClaimantCtrl.dispose();
    _expDescCtrl.dispose();
    _expAmountCtrl.dispose();
    _amlAmountCtrl.dispose();
    _amlCustCtrl.dispose();
    super.dispose();
  }

  // --- CORE TRANSACTION LOGIC ENGINES ---
  void _calculateAmlRisk() {
    final amt = double.tryParse(_amlAmountCtrl.text) ?? 0.0;
    final name = _amlCustCtrl.text;

    double score = 10.0;
    final factors = <String>[];

    if (amt > 200000) {
      score += 55.0;
      factors.add('Transaction exceeds High Value compliance ceiling (INR 2 Lakhs).');
    } else if (amt > 50000) {
      score += 20.0;
      factors.add('Volume crosses standard ledger threshold.');
    }

    if (name.length < 5 || name.toLowerCase().contains('unknown')) {
      score += 25.0;
      factors.add('Flagged counterparty details: Anonymous or incomplete name registration.');
    }

    setState(() {
      _amlScore = score > 100.0 ? 100.0 : score;
      _amlRisk = _amlScore > 50.0 ? 'SUSPICIOUS' : 'CLEAR';
      _amlFactors = factors;
    });

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(_amlRisk == 'SUSPICIOUS' 
            ? '⚠️ AML Suspicious Alert flagged! Logged to security audit logs.' 
            : '✓ AML Screening verified. Risk verified low.'),
        backgroundColor: _amlRisk == 'SUSPICIOUS' ? Colors.red.shade800 : Colors.green.shade800,
      ),
    );
  }

  void _issueInvoice() {
    final customer = _customerNameCtrl.text;
    final qty = int.tryParse(_invoiceQtyCtrl.text) ?? 1;
    final price = double.tryParse(_invoicePriceCtrl.text) ?? 0.0;

    if (customer.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please enter customer name')),
      );
      return;
    }

    final item = GstInvoiceItem(
      description: 'Custom SaaS platform integration service',
      hsnCode: '998311',
      quantity: qty,
      unitPrice: price,
      taxRatePercent: 18.0,
    );

    final newInv = GstInvoice(
      invoiceNumber: 'INV-2026-1000${_invoices.length + 1}',
      customerName: customer,
      placeOfSupply: _placeOfSupply,
      status: 'UNPAID',
      createdAt: DateTime.now(),
      items: [item],
    );

    setState(() {
      _invoices.insert(0, newInv);
      
      // Update Accounts Receivable & Revenue in local COA balances
      final rec = _accounts.firstWhere((a) => a.code == '1110');
      final rev = _accounts.firstWhere((a) => a.code == '4010');
      rec.balance += newInv.grandTotal;
      rev.balance += newInv.totalTaxable;

      final isInter = newInv.isInterState;
      if (isInter) {
        final igst = _accounts.firstWhere((a) => a.code == '2130');
        igst.balance += newInv.totalTax;
      } else {
        final cgst = _accounts.firstWhere((a) => a.code == '2110');
        final sgst = _accounts.firstWhere((a) => a.code == '2120');
        cgst.balance += newInv.totalTax / 2;
        sgst.balance += newInv.totalTax / 2;
      }
    });

    Navigator.pop(context);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('Tax Invoice ${newInv.invoiceNumber} generated & ledger balances updated.'),
        backgroundColor: Colors.green.shade800,
      ),
    );

    _customerNameCtrl.clear();
  }

  void _submitExpense() {
    final desc = _expDescCtrl.text;
    final claimant = _expClaimantCtrl.text;
    final amt = double.tryParse(_expAmountCtrl.text) ?? 0.0;

    if (desc.isEmpty || claimant.isEmpty || amt <= 0.0) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please enter all details')),
      );
      return;
    }

    final newExp = ExpenseClaim(
      id: 'exp-${DateTime.now().millisecondsSinceEpoch}',
      description: desc,
      category: _expCat,
      amount: amt,
      claimedBy: claimant,
      status: 'PENDING',
      createdAt: DateTime.now(),
    );

    final budget = _budgets.firstWhere((b) => b.category == _expCat);
    if (budget.spent + amt > budget.allocated) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('⚠️ Budget Alert! Category "$_expCat" limit exceeded with this claim.'),
          backgroundColor: Colors.amber.shade800,
        ),
      );
    }

    setState(() {
      _expenses.insert(0, newExp);
    });

    Navigator.pop(context);
    _expDescCtrl.clear();
    _expClaimantCtrl.clear();
  }

  void _approveExpense(ExpenseClaim claim) {
    setState(() {
      claim.status = 'APPROVED';
      final budget = _budgets.firstWhere((b) => b.category == claim.category);
      budget.spent += claim.amount;

      // Adjust COA: Debit Expense, Credit Bank
      final expCode = claim.category == 'TECH_INFRA' ? '5010' : '5020';
      final exp = _accounts.firstWhere((a) => a.code == expCode);
      final bank = _accounts.firstWhere((a) => a.code == '1020');
      exp.balance += claim.amount;
      bank.balance -= claim.amount;
    });

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('Expense "${claim.description}" Approved! Treasury credited.'),
        backgroundColor: Colors.green.shade800,
      ),
    );
  }

  // --- DYNAMIC FINANCIAL COMPILATION COMPUTATIONS ---
  double get _totalAssets => _accounts.where((a) => a.type == AccountType.asset).fold(0.0, (sum, a) => sum + a.balance);
  double get _totalLiabilities => _accounts.where((a) => a.type == AccountType.liability).fold(0.0, (sum, a) => sum + a.balance);
  double get _totalRevenue => _accounts.where((a) => a.type == AccountType.revenue).fold(0.0, (sum, a) => sum + a.balance);
  double get _totalExpense => _accounts.where((a) => a.type == AccountType.expense).fold(0.0, (sum, a) => sum + a.balance);
  double get _netProfit => _totalRevenue - _totalExpense;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Surya Treasury & Finance',
          style: TextStyle(fontWeight: FontWeight.bold, letterSpacing: 0.5),
        ),
        backgroundColor: const Color(0xFF0F172A),
        foregroundColor: Colors.white,
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          labelColor: const Color(0xFF38BDF8),
          unselectedLabelColor: Colors.white70,
          indicatorColor: const Color(0xFF38BDF8),
          tabs: const [
            Tab(text: 'Overview', icon: Icon(Icons.analytics_outlined)),
            Tab(text: 'Chart of Accounts', icon: Icon(Icons.account_tree_outlined)),
            Tab(text: 'Tax Invoices', icon: Icon(Icons.receipt_long_outlined)),
            Tab(text: 'Budgets & Expenses', icon: Icon(Icons.monetization_on_outlined)),
            Tab(text: 'AML Sandbox', icon: Icon(Icons.shield_outlined)),
          ],
        ),
      ),
      body: Container(
        color: const Color(0xFFF8FAFC),
        child: TabBarView(
          controller: _tabController,
          children: [
            _buildOverviewTab(),
            _buildChartAccountsTab(),
            _buildInvoicesTab(),
            _buildExpensesTab(),
            _buildAmlSandboxTab(),
          ],
        ),
      ),
    );
  }

  // 1. OVERVIEW TAB
  Widget _buildOverviewTab() {
    return ListView(
      padding: const EdgeInsets.all(24.0),
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: const [
            Text(
              'Corporate Treasury & GAAP Indicators',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
            ),
            Chip(
              label: Text('FY-2026-27 | Consolidated'),
              backgroundColor: Color(0xFFE2E8F0),
            )
          ],
        ),
        const SizedBox(height: 16),

        // KPI grid
        GridView.count(
          crossAxisCount: 2,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          childAspectRatio: 1.8,
          crossAxisSpacing: 16,
          mainAxisSpacing: 16,
          children: [
            _buildKpiCard('TREASURY CASH', '₹${_accounts.firstWhere((a) => a.code == '1020').balance.toStringAsFixed(0)}', '● HDFC Bank Main', Colors.green),
            _buildKpiCard('GST LIABILITY', '₹${(_accounts.firstWhere((a) => a.code == '2110').balance + _accounts.firstWhere((a) => a.code == '2120').balance + _accounts.firstWhere((a) => a.code == '2130').balance).toStringAsFixed(0)}', '● GST Output Due', Colors.orange),
            _buildKpiCard('UNPAID RECEIVABLES', '₹${_accounts.firstWhere((a) => a.code == '1110').balance.toStringAsFixed(0)}', '● Commercial trade accounts', Colors.blue),
            _buildKpiCard('NET SAAS PROFIT', '₹${_netProfit.toStringAsFixed(0)}', '● Operating Revenue vs Cost', Colors.purple),
          ],
        ),
        const SizedBox(height: 24),

        // Double-entry balancing integrity banner
        Card(
          color: _totalAssets == (_totalLiabilities + 1500000.0 + _netProfit) ? Colors.green.shade50 : Colors.red.shade50,
          elevation: 0,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: BorderSide(color: _totalAssets == (_totalLiabilities + 1500000.0 + _netProfit) ? Colors.green.shade200 : Colors.red.shade200)),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Row(
              children: [
                Icon(
                  _totalAssets == (_totalLiabilities + 1500000.0 + _netProfit) ? Icons.verified_user : Icons.warning_amber_rounded,
                  color: _totalAssets == (_totalLiabilities + 1500000.0 + _netProfit) ? Colors.green.shade700 : Colors.red.shade700,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        _totalAssets == (_totalLiabilities + 1500000.0 + _netProfit) ? 'Double-Entry Integrity Audit: Verified' : 'Integrity Audit Mismatch',
                        style: TextStyle(fontWeight: FontWeight.bold, color: _totalAssets == (_totalLiabilities + 1500000.0 + _netProfit) ? Colors.green.shade900 : Colors.red.shade900),
                      ),
                      const SizedBox(height: 2),
                      Text(
                        _totalAssets == (_totalLiabilities + 1500000.0 + _netProfit) 
                            ? 'Consolidated debit ledgers match credit liabilities perfectly (GAAP standard).'
                            : 'Adjust balances to align double entries.',
                        style: TextStyle(fontSize: 12, color: _totalAssets == (_totalLiabilities + 1500000.0 + _netProfit) ? Colors.green.shade700 : Colors.red.shade700),
                      ),
                    ],
                  ),
                )
              ],
            ),
          ),
        ),
        const SizedBox(height: 24),

        // Detailed Operating Statements
        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16), side: const BorderSide(color: Color(0xFFE2E8F0))),
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'Consolidated Income Statement (P&L)',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
                ),
                const Divider(height: 24),
                
                // Revenue lines
                const Text('OPERATING REVENUES', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.green)),
                const SizedBox(height: 8),
                ..._accounts.where((a) => a.type == AccountType.revenue).map((a) => Padding(
                  padding: const EdgeInsets.symmetric(vertical: 4.0),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('${a.name} (${a.code})', style: const TextStyle(fontSize: 13)),
                      Text('₹${a.balance.toStringAsFixed(0)}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                    ],
                  ),
                )).toList(),
                const Divider(),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text('Total Revenue', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                    Text('₹${_totalRevenue.toStringAsFixed(0)}', style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.green, fontSize: 13)),
                  ],
                ),
                const SizedBox(height: 20),

                // Expense lines
                const Text('OPERATING EXPENSES', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.red)),
                const SizedBox(height: 8),
                ..._accounts.where((a) => a.type == AccountType.expense).map((a) => Padding(
                  padding: const EdgeInsets.symmetric(vertical: 4.0),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('${a.name} (${a.code})', style: const TextStyle(fontSize: 13)),
                      Text('₹${a.balance.toStringAsFixed(0)}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                    ],
                  ),
                )).toList(),
                const Divider(),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text('Total Expenses', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                    Text('₹${_totalExpense.toStringAsFixed(0)}', style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.red, fontSize: 13)),
                  ],
                ),
                const Divider(thickness: 2),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text('NET COMPILATION PROFIT', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    Text('₹${_netProfit.toStringAsFixed(0)}', style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.purple, fontSize: 14)),
                  ],
                ),
              ],
            ),
          ),
        )
      ],
    );
  }

  // 2. CHART OF ACCOUNTS TAB
  Widget _buildChartAccountsTab() {
    return ListView(
      padding: const EdgeInsets.all(24.0),
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'Ledger Accounts Registry (COA)',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
            ),
            ElevatedButton.icon(
              onPressed: () => _showAddAccountDialog(),
              icon: const Icon(Icons.add),
              label: const Text('Add Account'),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0F172A),
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              ),
            )
          ],
        ),
        const SizedBox(height: 16),
        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16), side: const BorderSide(color: Color(0xFFE2E8F0))),
          child: Column(
            children: [
              Container(
                padding: const EdgeInsets.all(16.0),
                color: const Color(0xFFF1F5F9),
                child: Row(
                  children: const [
                    Expanded(flex: 2, child: Text('CODE', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: Color(0xFF475569)))),
                    Expanded(flex: 4, child: Text('ACCOUNT NAME', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: Color(0xFF475569)))),
                    Expanded(flex: 3, child: Text('TYPE', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: Color(0xFF475569)))),
                    Expanded(flex: 3, child: Text('BALANCE', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: Color(0xFF475569)), textAlign: Alignment.centerRight)),
                  ],
                ),
              ),
              ..._accounts.map((a) => Container(
                padding: const EdgeInsets.all(16.0),
                decoration: const BoxDecoration(border: Border(bottom: BorderSide(color: Color(0xFFF1F5F9)))),
                child: Row(
                  children: [
                    Expanded(flex: 2, child: Text(a.code, style: const TextStyle(fontFamily: 'monospace', fontWeight: FontWeight.bold))),
                    Expanded(flex: 4, child: Text(a.name, style: const TextStyle(fontWeight: FontWeight.w500))),
                    Expanded(
                      flex: 3,
                      child: Text(
                        a.type.toString().split('.').last.toUpperCase(),
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 11,
                          color: a.type == AccountType.asset ? Colors.blue : (a.type == AccountType.liability ? Colors.red : Colors.purple),
                        ),
                      ),
                    ),
                    Expanded(flex: 3, child: Text('₹${a.balance.toStringAsFixed(0)}', style: const TextStyle(fontWeight: FontWeight.bold), textAlign: TextAlign.right)),
                  ],
                ),
              )).toList(),
            ],
          ),
        )
      ],
    );
  }

  // 3. TAX INVOICE TAB
  Widget _buildInvoicesTab() {
    return ListView(
      padding: const EdgeInsets.all(24.0),
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'Dynamic GST Invoices Center',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
            ),
            ElevatedButton.icon(
              onPressed: () => _showIssueInvoiceDialog(),
              icon: const Icon(Icons.receipt_long),
              label: const Text('New Invoice'),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0F172A),
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              ),
            )
          ],
        ),
        const SizedBox(height: 16),
        ..._invoices.map((inv) => Card(
          elevation: 0,
          margin: const EdgeInsets.only(bottom: 16),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: const BorderSide(color: Color(0xFFE2E8F0))),
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: [
                        Text(inv.invoiceNumber, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                        const SizedBox(width: 12),
                        Container(
                          padding: const EdgeInsets.all(6),
                          decoration: BoxDecoration(color: inv.status == 'PAID' ? Colors.green.shade50 : Colors.amber.shade50, borderRadius: BorderRadius.circular(8)),
                          child: Text(inv.status, style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: inv.status == 'PAID' ? Colors.green.shade800 : Colors.amber.shade800)),
                        )
                      ],
                    ),
                    Text('₹${inv.grandTotal.toStringAsFixed(0)}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
                  ],
                ),
                const SizedBox(height: 12),
                Text(inv.customerName, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                const SizedBox(height: 4),
                Text('Supply state: ${inv.placeOfSupply} | GSTIN: ${inv.customerGstin ?? 'Unregistered'}', style: const TextStyle(fontSize: 12, color: Colors.grey)),
                const Divider(height: 24),
                
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text('Taxable Total: ₹${inv.totalTaxable.toStringAsFixed(0)}', style: const TextStyle(fontSize: 12)),
                    Text(
                      inv.isInterState ? 'IGST (Inter-state): ₹${inv.totalTax.toStringAsFixed(0)}' : 'CGST/SGST (Intra-state): ₹${inv.totalTax.toStringAsFixed(0)}',
                      style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
                    ),
                  ],
                ),
                if (inv.status == 'UNPAID') ...[
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: () {
                      setState(() {
                        final idx = _invoices.indexOf(inv);
                        _invoices[idx] = GstInvoice(
                          invoiceNumber: inv.invoiceNumber,
                          customerName: inv.customerName,
                          placeOfSupply: inv.placeOfSupply,
                          status: 'PAID',
                          createdAt: inv.createdAt,
                          items: inv.items,
                        );

                        // Capture payment double entry: Bank debit, Accounts Receivable credit
                        final bank = _accounts.firstWhere((a) => a.code == '1020');
                        final rec = _accounts.firstWhere((a) => a.code == '1110');
                        bank.balance += inv.grandTotal;
                        rec.balance -= inv.grandTotal;
                      });
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text('Payment captured & double entries updated for ${inv.invoiceNumber}'), backgroundColor: Colors.green.shade800),
                      );
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.green.shade700,
                      foregroundColor: Colors.white,
                      minimumSize: const Size(double.infinity, 36),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                    ),
                    child: const Text('Capture Treasury Payment', style: TextStyle(fontWeight: FontWeight.bold)),
                  )
                ]
              ],
            ),
          ),
        )).toList()
      ],
    );
  }

  // 4. CORPORATE EXPENSES TAB
  Widget _buildExpensesTab() {
    return ListView(
      padding: const EdgeInsets.all(24.0),
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'Expenses Claim & Budgets Control',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
            ),
            ElevatedButton.icon(
              onPressed: () => _showAddExpenseDialog(),
              icon: const Icon(Icons.add_shopping_cart),
              label: const Text('Log Claim'),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0F172A),
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              ),
            )
          ],
        ),
        const SizedBox(height: 16),

        // Budgets progress meters
        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16), side: const BorderSide(color: Color(0xFFE2E8F0))),
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Corporate Category Budgets', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                const SizedBox(height: 16),
                ..._budgets.map((b) => Padding(
                  padding: const EdgeInsets.only(bottom: 12.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text('📁 ${b.category}', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                          Text('₹${b.spent.toStringAsFixed(0)} / ₹${b.allocated.toStringAsFixed(0)}', style: const TextStyle(fontSize: 12)),
                        ],
                      ),
                      const SizedBox(height: 6),
                      LinearProgressIndicator(
                        value: b.spent / b.allocated,
                        backgroundColor: const Color(0xFFE2E8F0),
                        color: (b.spent / b.allocated) > 0.85 ? Colors.red : Colors.green,
                        minHeight: 6,
                      )
                    ],
                  ),
                )).toList()
              ],
            ),
          ),
        ),
        const SizedBox(height: 24),

        // Claims list
        ..._expenses.map((e) => Card(
          elevation: 0,
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: const BorderSide(color: Color(0xFFE2E8F0))),
          child: ListTile(
            title: Text(e.description, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
            subtitle: Text('Claimed by: ${e.claimedBy} | ${e.category}', style: const TextStyle(fontSize: 12)),
            trailing: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text('₹${e.amount.toStringAsFixed(0)}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                const SizedBox(height: 4),
                if (e.status == 'PENDING')
                  GestureDetector(
                    onTap: () => _approveExpense(e),
                    child: Container(
                      padding: const EdgeInsets.all(4),
                      decoration: BoxDecoration(color: Colors.blue.shade50, borderRadius: BorderRadius.circular(6)),
                      child: Text('APPROVE', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Colors.blue.shade800)),
                    ),
                  )
                else
                  Text('APPROVED', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.green.shade800)),
              ],
            ),
          ),
        )).toList()
      ],
    );
  }

  // 5. AML COMPLIANCE TAB
  Widget _buildAmlSandboxTab() {
    return ListView(
      padding: const EdgeInsets.all(24.0),
      children: [
        const Text(
          'AML Transaction Pre-Screening Sandbox',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
        ),
        const SizedBox(height: 6),
        const Text(
          'Automated dynamic checks for money laundering, high-volume trading triggers and shell-company pattern checks.',
          style: TextStyle(fontSize: 13, color: Colors.grey),
        ),
        const SizedBox(height: 24),

        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16), side: const BorderSide(color: Color(0xFFE2E8F0))),
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                TextField(
                  controller: _amlCustCtrl,
                  decoration: const InputDecoration(
                    labelText: 'Proposed Entity Customer Name',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: _amlAmountCtrl,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(
                    labelText: 'Proposed Transaction Amount (INR)',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: _calculateAmlRisk,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF0F172A),
                    foregroundColor: Colors.white,
                    minimumSize: const Size(double.infinity, 48),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  ),
                  child: const Text('Verify Compliance', style: TextStyle(fontWeight: FontWeight.bold)),
                ),

                if (_amlScore > 0.0) ...[
                  const SizedBox(height: 32),
                  const Text('AUDIT REPORT DETAILS', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Color(0xFF475569))),
                  const Divider(),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text('Dynamic Risk Score', style: TextStyle(fontSize: 14)),
                      Text('${_amlScore.toStringAsFixed(0)}%', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20, color: _amlRisk == 'SUSPICIOUS' ? Colors.red : Colors.green)),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text('Audit Status Result', style: TextStyle(fontSize: 14)),
                      Text(_amlRisk, style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: _amlRisk == 'SUSPICIOUS' ? Colors.red : Colors.green)),
                    ],
                  ),
                  const SizedBox(height: 16),
                  if (_amlFactors.isNotEmpty) ...[
                    const Text('Audit Flags Triggered:', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 6),
                    ..._amlFactors.map((f) => Padding(
                      padding: const EdgeInsets.only(bottom: 4.0),
                      child: Text('• $f', style: const TextStyle(fontSize: 12, color: Colors.grey)),
                    )).toList()
                  ]
                ]
              ],
            ),
          ),
        )
      ],
    );
  }

  // --- DYNAMIC DIALOG BUILDERS ---
  void _showAddAccountDialog() {
    showDialog(
      context: context,
      builder: (context) {
        String typeStr = 'ASSET';
        final codeCtrl = TextEditingController();
        final nameCtrl = TextEditingController();

        return StatefulBuilder(
          builder: (context, setDialogState) {
            return AlertDialog(
              title: const Text('Add COA Ledger Account'),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  TextField(controller: codeCtrl, decoration: const InputDecoration(labelText: 'Unique Account Code (e.g. 1030)')),
                  TextField(controller: nameCtrl, decoration: const InputDecoration(labelText: 'Ledger Name (e.g. SBI Savings)')),
                  DropdownButtonFormField<String>(
                    value: typeStr,
                    items: const [
                      DropdownMenuItem(value: 'ASSET', child: Text('ASSET')),
                      DropdownMenuItem(value: 'LIABILITY', child: Text('LIABILITY')),
                      DropdownMenuItem(value: 'EQUITY', child: Text('EQUITY')),
                      DropdownMenuItem(value: 'REVENUE', child: Text('REVENUE')),
                      DropdownMenuItem(value: 'EXPENSE', child: Text('EXPENSE')),
                    ],
                    onChanged: (val) {
                      setDialogState(() {
                        typeStr = val!;
                      });
                    },
                    decoration: const InputDecoration(labelText: 'Ledger Category'),
                  )
                ],
              ),
              actions: [
                TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
                ElevatedButton(
                  onPressed: () {
                    final code = codeCtrl.text;
                    final name = nameCtrl.text;
                    if (code.isEmpty || name.isEmpty) return;

                    final type = AccountType.values.firstWhere((e) => e.toString().split('.').last.toUpperCase() == typeStr);
                    setState(() {
                      _accounts.add(LedgerAccount(code: code, name: name, type: type, balance: 0.0));
                    });
                    Navigator.pop(context);
                    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Account $code registered successfully.')));
                  },
                  child: const Text('Register'),
                )
              ],
            );
          },
        );
      },
    );
  }

  void _showIssueInvoiceDialog() {
    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setDialogState) {
            return AlertDialog(
              title: const Text('Issue Tax Invoice'),
              content: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    TextField(controller: _customerNameCtrl, decoration: const InputDecoration(labelText: 'Customer Business Name')),
                    const SizedBox(height: 8),
                    DropdownButtonFormField<String>(
                      value: _placeOfSupply,
                      items: const [
                        DropdownMenuItem(value: 'Karnataka', child: Text('Karnataka (Intra-state GST)')),
                        DropdownMenuItem(value: 'Maharashtra', child: Text('Maharashtra (Inter-state IGST)')),
                        DropdownMenuItem(value: 'Delhi', child: Text('Delhi (Inter-state IGST)')),
                      ],
                      onChanged: (val) {
                        setDialogState(() {
                          _placeOfSupply = val!;
                        });
                      },
                      decoration: const InputDecoration(labelText: 'Place of Supply'),
                    ),
                    const SizedBox(height: 8),
                    TextField(controller: _invoicePriceCtrl, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: 'Item price / fee (INR)')),
                  ],
                ),
              ),
              actions: [
                TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
                ElevatedButton(onPressed: _issueInvoice, child: const Text('Post Invoice')),
              ],
            );
          },
        );
      },
    );
  }

  void _showAddExpenseDialog() {
    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setDialogState) {
            return AlertDialog(
              title: const Text('Log Expense Claim'),
              content: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    TextField(controller: _expClaimantCtrl, decoration: const InputDecoration(labelText: 'Claimant Email')),
                    TextField(controller: _expDescCtrl, decoration: const InputDecoration(labelText: 'Expense Description')),
                    DropdownButtonFormField<String>(
                      value: _expCat,
                      items: const [
                        DropdownMenuItem(value: 'TECH_INFRA', child: Text('TECH INFRASTRUCTURE')),
                        DropdownMenuItem(value: 'MARKETING', child: Text('MARKETING & SALES')),
                        DropdownMenuItem(value: 'OFFICE', child: Text('OFFICE RENT & UTILS')),
                      ],
                      onChanged: (val) {
                        setDialogState(() {
                          _expCat = val!;
                        });
                      },
                      decoration: const InputDecoration(labelText: 'Budget Category'),
                    ),
                    TextField(controller: _expAmountCtrl, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: 'Amount (INR)')),
                  ],
                ),
              ),
              actions: [
                TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
                ElevatedButton(onPressed: _submitExpense, child: const Text('File Claim')),
              ],
            );
          },
        );
      },
    );
  }

  // --- REUSABLE COMPONENT BUILDERS ---
  Widget _buildKpiCard(String label, String value, String subtext, Color color) {
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16), side: const BorderSide(color: Color(0xFFE2E8F0))),
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(label, style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.grey)),
            const SizedBox(height: 8),
            Text(value, style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: Color(0xFF0F172A))),
            const SizedBox(height: 6),
            Text(subtext, style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: color)),
          ],
        ),
      ),
    );
  }
}
