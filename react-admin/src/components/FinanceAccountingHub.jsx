import React, { useState, useEffect } from 'react';

export default function FinanceAccountingHub() {
  const [activeTab, setActiveTab] = useState('finance_dashboard'); // finance_dashboard, chart_accounts, journals, invoice_center, expense_manager, aml_compliance, backup_disaster
  const [message, setMessage] = useState(null);
  const [tenantId, setTenantId] = useState('default-tenant-1');

  // --- COMPONENT NOTIFICATIONS (TOASTS) ---
  const triggerToast = (text) => {
    setMessage(text);
    setTimeout(() => setMessage(null), 4500);
  };

  // ---------------------------------------------------------------------------
  // 1. CHART OF ACCOUNTS STATE
  // ---------------------------------------------------------------------------
  const [accounts, setAccounts] = useState([
    { id: 'acc-1', code: '1010', name: 'Cash Account', type: 'ASSET', currency: 'INR', balance: 500000.0 },
    { id: 'acc-2', code: '1020', name: 'HDFC Bank Account', type: 'ASSET', currency: 'INR', balance: 1000000.0 },
    { id: 'acc-3', code: '1110', name: 'Accounts Receivable', type: 'ASSET', currency: 'INR', balance: 150000.0 },
    { id: 'acc-4', code: '1210', name: 'Office Equipment', type: 'ASSET', currency: 'INR', balance: 75000.0 },
    { id: 'acc-5', code: '2010', name: 'Accounts Payable', type: 'LIABILITY', currency: 'INR', balance: 80000.0 },
    { id: 'acc-6', code: '2110', name: 'CGST Output Tax Payable', type: 'LIABILITY', currency: 'INR', balance: 22500.0 },
    { id: 'acc-7', code: '2120', name: 'SGST Output Tax Payable', type: 'LIABILITY', currency: 'INR', balance: 22500.0 },
    { id: 'acc-8', code: '2130', name: 'IGST Output Tax Payable', type: 'LIABILITY', currency: 'INR', balance: 35000.0 },
    { id: 'acc-9', code: '3010', name: 'Shareholder Equity', type: 'EQUITY', currency: 'INR', balance: 1500000.0 },
    { id: 'acc-10', code: '4010', name: 'SaaS Subscription Revenue', type: 'REVENUE', currency: 'INR', balance: 250000.0 },
    { id: 'acc-11', code: '4020', name: 'Marketplace commission fees', type: 'REVENUE', currency: 'INR', balance: 45000.0 },
    { id: 'acc-12', code: '5010', name: 'Cloud Server Infrastructure', type: 'EXPENSE', currency: 'INR', balance: 120000.0 },
    { id: 'acc-13', code: '5020', name: 'Office rent & utilities', type: 'EXPENSE', currency: 'INR', balance: 45000.0 },
    { id: 'acc-14', code: '5030', name: 'Marketing & advertising', type: 'EXPENSE', currency: 'INR', balance: 10000.0 },
  ]);

  const [newAccCode, setNewAccCode] = useState('');
  const [newAccName, setNewAccName] = useState('');
  const [newAccType, setNewAccType] = useState('ASSET');

  const handleAddAccount = (e) => {
    e.preventDefault();
    if (!newAccCode || !newAccName) {
      triggerToast('Code and Name are mandatory.');
      return;
    }
    if (accounts.some(a => a.code === newAccCode)) {
      triggerToast(`Account with code ${newAccCode} already exists.`);
      return;
    }
    const newAcc = {
      id: `acc-${Date.now()}`,
      code: newAccCode,
      name: newAccName,
      type: newAccType,
      currency: 'INR',
      balance: 0.0
    };
    setAccounts([...accounts, newAcc]);
    triggerToast(`Ledger Account [${newAccCode}] ${newAccName} initialized successfully.`);
    setNewAccCode('');
    setNewAccName('');
  };

  // ---------------------------------------------------------------------------
  // 2. DOUBLE ENTRY JOURNALS & MAKER-CHECKER STATE
  // ---------------------------------------------------------------------------
  const [journalEntries, setJournalEntries] = useState([
    {
      id: 'je-101',
      entryNumber: 'JE-2026-1001',
      description: 'Investment capital from founder',
      reference: 'BANK-DEPOSIT-8122',
      postingDate: '2026-06-15',
      financialYear: 'FY-2026-27',
      status: 'POSTED',
      createdBy: 'founder@suryacredit.com',
      approvedBy: 'system-checker@suryacredit.com',
      items: [
        { accountCode: '1020', accountName: 'HDFC Bank Account', debit: 1500000.0, credit: 0.0 },
        { accountCode: '3010', accountName: 'Shareholder Equity', debit: 0.0, credit: 1500000.0 }
      ]
    },
    {
      id: 'je-102',
      entryNumber: 'JE-2026-1002',
      description: 'AWS Web Services June Bill Provision',
      reference: 'INV-AWS-889',
      postingDate: '2026-07-01',
      financialYear: 'FY-2026-27',
      status: 'DRAFT',
      createdBy: 'tech-operator@suryacredit.com',
      approvedBy: null,
      items: [
        { accountCode: '5010', accountName: 'Cloud Server Infrastructure', debit: 120000.0, credit: 0.0 },
        { accountCode: '2010', accountName: 'Accounts Payable', debit: 0.0, credit: 120000.0 }
      ]
    }
  ]);

  const [jeDescription, setJeDescription] = useState('');
  const [jeReference, setJeReference] = useState('');
  const [jeRows, setJeRows] = useState([
    { accountId: '', debit: 0, credit: 0 },
    { accountId: '', debit: 0, credit: 0 }
  ]);

  const handleAddJeRow = () => {
    setJeRows([...jeRows, { accountId: '', debit: 0, credit: 0 }]);
  };

  const handleRemoveJeRow = (index) => {
    setJeRows(jeRows.filter((_, i) => i !== index));
  };

  const handleJeRowChange = (index, field, value) => {
    const updated = [...jeRows];
    updated[index][field] = value;
    setJeRows(updated);
  };

  const submitJournalEntry = (e) => {
    e.preventDefault();
    let sumDebits = 0;
    let sumCredits = 0;
    const items = [];

    for (const r of jeRows) {
      if (!r.accountId) {
        triggerToast('Please select accounts for all journal rows.');
        return;
      }
      const deb = parseFloat(r.debit) || 0;
      const cred = parseFloat(r.credit) || 0;
      sumDebits += deb;
      sumCredits += cred;

      const acc = accounts.find(a => a.id === r.accountId);
      items.push({
        accountCode: acc.code,
        accountName: acc.name,
        debit: deb,
        credit: cred
      });
    }

    if (Math.abs(sumDebits - sumCredits) > 0.01) {
      triggerToast(`Double-Entry Balancing Error: Sum of Debits (${sumDebits} INR) must exactly equal Sum of Credits (${sumCredits} INR). Difference: ${Math.abs(sumDebits - sumCredits)}`);
      return;
    }

    const entryNumber = `JE-2026-100${journalEntries.length + 1}`;
    const newEntry = {
      id: `je-${Date.now()}`,
      entryNumber,
      description: jeDescription,
      reference: jeReference || 'N/A',
      postingDate: new Date().toISOString().split('T')[0],
      financialYear: 'FY-2026-27',
      status: 'DRAFT',
      createdBy: 'maker-admin@suryacredit.com',
      approvedBy: null,
      items
    };

    setJournalEntries([newEntry, ...journalEntries]);
    triggerToast(`Journal Entry ${entryNumber} created as DRAFT. Waiting for Checker Approval.`);
    setJeDescription('');
    setJeReference('');
    setJeRows([
      { accountId: '', debit: 0, credit: 0 },
      { accountId: '', debit: 0, credit: 0 }
    ]);
  };

  const handleApproveJournal = (jeId) => {
    const entryIndex = journalEntries.findIndex(je => je.id === jeId);
    if (entryIndex === -1) return;

    const entry = journalEntries[entryIndex];
    if (entry.status === 'POSTED') {
      triggerToast('Entry is already posted.');
      return;
    }

    // Apply adjustments to balance
    const updatedAccounts = [...accounts];
    entry.items.forEach(item => {
      const acc = updatedAccounts.find(a => a.code === item.accountCode);
      if (acc) {
        if (acc.type === 'ASSET' || acc.type === 'EXPENSE') {
          acc.balance += item.debit;
          acc.balance -= item.credit;
        } else {
          acc.balance -= item.debit;
          acc.balance += item.credit;
        }
      }
    });

    const updatedEntries = [...journalEntries];
    updatedEntries[entryIndex] = {
      ...entry,
      status: 'POSTED',
      approvedBy: 'checker-admin@suryacredit.com'
    };

    setAccounts(updatedAccounts);
    setJournalEntries(updatedEntries);
    triggerToast(`Audit approved & posted: balances updated for entry ${entry.entryNumber}.`);
  };

  // ---------------------------------------------------------------------------
  // 3. TAX INVOICE CENTER STATE
  // ---------------------------------------------------------------------------
  const [invoices, setInvoices] = useState([
    {
      id: 'inv-1',
      invoiceNumber: 'INV-2026-10001',
      invoiceType: 'TAX_INVOICE',
      customerName: 'Shree Balaji Communications',
      customerGstin: '29AAECS4512A1Z4',
      placeOfSupply: 'Karnataka',
      totalTaxableVal: 150000.0,
      cgst: 13500.0,
      sgst: 13500.0,
      igst: 0.0,
      grandTotal: 177000.0,
      status: 'UNPAID',
      createdAt: '2026-07-01'
    },
    {
      id: 'inv-2',
      invoiceNumber: 'INV-2026-10002',
      invoiceType: 'RETAIL_INVOICE',
      customerName: 'Vikas Electronics Maharashtra',
      customerGstin: '27AAKCV8192K2Z3',
      placeOfSupply: 'Maharashtra',
      totalTaxableVal: 100000.0,
      cgst: 0.0,
      sgst: 0.0,
      igst: 18000.0,
      grandTotal: 118000.0,
      status: 'PAID',
      createdAt: '2026-07-02'
    }
  ]);

  const [customerName, setCustomerName] = useState('');
  const [customerGstin, setCustomerGstin] = useState('');
  const [placeOfSupply, setPlaceOfSupply] = useState('Karnataka');
  const [invoiceType, setInvoiceType] = useState('TAX_INVOICE');
  const [invoiceDescription, setInvoiceDescription] = useState('FinTech SaaS Module License Fee');
  const [invoiceHsn, setInvoiceHsn] = useState('998311');
  const [invoiceQty, setInvoiceQty] = useState(1);
  const [invoicePrice, setInvoicePrice] = useState(50000);
  const [invoiceTaxPercent, setInvoiceTaxPercent] = useState(18);

  const handleIssueInvoice = (e) => {
    e.preventDefault();
    if (!customerName) {
      triggerToast('Customer Name is mandatory.');
      return;
    }

    const taxableValue = invoiceQty * invoicePrice;
    const taxAmt = (taxableValue * invoiceTaxPercent) / 100;
    const isInterState = placeOfSupply.toLowerCase() !== 'karnataka';

    const cgst = isInterState ? 0 : taxAmt / 2;
    const sgst = isInterState ? 0 : taxAmt / 2;
    const igst = isInterState ? taxAmt : 0;
    const grandTotal = taxableValue + taxAmt;

    const invoiceNumber = `INV-2026-1000${invoices.length + 1}`;
    const newInv = {
      id: `inv-${Date.now()}`,
      invoiceNumber,
      invoiceType,
      customerName,
      customerGstin: customerGstin || 'Unregistered',
      placeOfSupply,
      totalTaxableVal: taxableValue,
      cgst,
      sgst,
      igst,
      grandTotal,
      status: 'UNPAID',
      createdAt: new Date().toISOString().split('T')[0]
    };

    setInvoices([newInv, ...invoices]);
    triggerToast(`Tax invoice ${invoiceNumber} issued with ${invoiceTaxPercent}% GST successfully.`);

    // Auto update Accounts Receivable & Output Tax & Revenue on Invoice issue
    const updatedAccounts = [...accounts];
    const recAcc = updatedAccounts.find(a => a.code === '1110');
    const revAcc = updatedAccounts.find(a => a.code === '4010');
    const cgstAcc = updatedAccounts.find(a => a.code === '2110');
    const sgstAcc = updatedAccounts.find(a => a.code === '2120');
    const igstAcc = updatedAccounts.find(a => a.code === '2130');

    if (recAcc) recAcc.balance += grandTotal;
    if (revAcc) revAcc.balance += taxableValue;
    if (isInterState && igstAcc) igstAcc.balance += igst;
    if (!isInterState && cgstAcc && sgstAcc) {
      cgstAcc.balance += cgst;
      sgstAcc.balance += sgst;
    }

    setAccounts(updatedAccounts);

    // Reset fields
    setCustomerName('');
    setCustomerGstin('');
    setInvoiceQty(1);
    setInvoicePrice(50000);
  };

  const handlePayInvoice = (invId) => {
    const invIndex = invoices.findIndex(i => i.id === invId);
    if (invIndex === -1) return;

    const inv = invoices[invIndex];
    if (inv.status === 'PAID') return;

    const updatedInvoices = [...invoices];
    updatedInvoices[invIndex] = { ...inv, status: 'PAID' };
    setInvoices(updatedInvoices);

    // Real double entry: Debit Bank, Credit Accounts Receivable
    const updatedAccounts = [...accounts];
    const bankAcc = updatedAccounts.find(a => a.code === '1020');
    const recAcc = updatedAccounts.find(a => a.code === '1110');
    if (bankAcc) bankAcc.balance += inv.grandTotal;
    if (recAcc) recAcc.balance -= inv.grandTotal;
    setAccounts(updatedAccounts);

    triggerToast(`Payment captured! Debited Bank and Credited Accounts Receivable for ${inv.invoiceNumber}.`);
  };

  // ---------------------------------------------------------------------------
  // 4. ADJUSTMENT NOTES (CREDIT/DEBIT NOTES) STATE
  // ---------------------------------------------------------------------------
  const [adjustments, setAdjustments] = useState([
    { id: 'adj-1', noteNumber: 'CN-1001', noteType: 'CREDIT_NOTE', refInvoice: 'INV-2026-10001', customer: 'Shree Balaji Communications', reason: 'Discount applied retroactively', total: 11800 }
  ]);
  const [adjType, setAdjType] = useState('CREDIT_NOTE');
  const [adjRef, setAdjRef] = useState('');
  const [adjCustomer, setAdjCustomer] = useState('');
  const [adjReason, setAdjReason] = useState('');
  const [adjTotal, setAdjTotal] = useState(5000);

  const handleIssueAdjustment = (e) => {
    e.preventDefault();
    if (!adjCustomer || !adjRef) {
      triggerToast('All fields are mandatory.');
      return;
    }
    const noteNumber = `${adjType === 'CREDIT_NOTE' ? 'CN' : 'DN'}-${1001 + adjustments.length}`;
    const newAdj = {
      id: `adj-${Date.now()}`,
      noteNumber,
      noteType: adjType,
      refInvoice: adjRef,
      customer: adjCustomer,
      reason: adjReason,
      total: parseFloat(adjTotal) || 0
    };
    setAdjustments([newAdj, ...adjustments]);
    triggerToast(`${adjType === 'CREDIT_NOTE' ? 'Credit Note' : 'Debit Note'} ${noteNumber} issued successfully.`);
    setAdjCustomer('');
    setAdjRef('');
    setAdjReason('');
  };

  // ---------------------------------------------------------------------------
  // 5. CORPORATE EXPENSES & BUDGETS STATE
  // ---------------------------------------------------------------------------
  const [budgets, setBudgets] = useState([
    { id: 'b1', category: 'TECH_INFRA', allocated: 500000, spent: 120000 },
    { id: 'b2', category: 'MARKETING', allocated: 300000, spent: 10000 },
    { id: 'b3', category: 'OFFICE_RENT', allocated: 400000, spent: 45000 }
  ]);

  const [expenses, setExpenses] = useState([
    { id: 'e1', description: 'AWS Infra June Bill', category: 'TECH_INFRA', amount: 120000, claimedBy: 'tech-lead@suryacredit.com', status: 'APPROVED', date: '2026-07-01' },
    { id: 'e2', description: 'Rent for Bangalore Corporate Hub', category: 'OFFICE_RENT', amount: 45000, claimedBy: 'admin-hr@suryacredit.com', status: 'APPROVED', date: '2026-07-02' },
    { id: 'e3', description: 'New Sales Brochures print', category: 'MARKETING', amount: 10000, claimedBy: 'marketing-lead@suryacredit.com', status: 'APPROVED', date: '2026-07-03' }
  ]);

  const [expClaimedBy, setExpClaimedBy] = useState('');
  const [expDesc, setExpDesc] = useState('');
  const [expCat, setExpCat] = useState('TECH_INFRA');
  const [expAmount, setExpAmount] = useState(25000);

  const handleAddExpenseClaim = (e) => {
    e.preventDefault();
    if (!expClaimedBy || !expDesc) {
      triggerToast('Description and Claimed By are mandatory.');
      return;
    }

    const budget = budgets.find(b => b.category === expCat);
    if (budget) {
      const projected = budget.spent + parseFloat(expAmount);
      if (projected > budget.allocated) {
        triggerToast(`⚠️ Budget Overflow Alert! Category "${expCat}" has allocated limit of INR ${budget.allocated}, claim of INR ${expAmount} will overrun this budget.`);
      }
    }

    const newExp = {
      id: `exp-${Date.now()}`,
      description: expDesc,
      category: expCat,
      amount: parseFloat(expAmount) || 0,
      claimedBy: expClaimedBy,
      status: 'PENDING',
      date: new Date().toISOString().split('T')[0]
    };

    setExpenses([newExp, ...expenses]);
    triggerToast(`Expense claim registered and logged under category ${expCat}. Waiting for Checker approval.`);
    setExpDesc('');
    setExpClaimedBy('');
  };

  const handleApproveExpense = (expId) => {
    const expIndex = expenses.findIndex(e => e.id === expId);
    if (expIndex === -1) return;
    const exp = expenses[expIndex];
    if (exp.status !== 'PENDING') return;

    // Deduct from budget
    setBudgets(prev => prev.map(b => {
      if (b.category === exp.category) {
        return { ...b, spent: b.spent + exp.amount };
      }
      return b;
    }));

    // Update balances: Debit Expense (increases balance), Credit Bank (decreases balance)
    const updatedAccounts = [...accounts];
    const expCode = exp.category === 'TECH_INFRA' ? '5010' : (exp.category === 'MARKETING' ? '5030' : '5020');
    const expAcc = updatedAccounts.find(a => a.code === expCode);
    const bankAcc = updatedAccounts.find(a => a.code === '1020');
    if (expAcc) expAcc.balance += exp.amount;
    if (bankAcc) bankAcc.balance -= exp.amount;
    setAccounts(updatedAccounts);

    const updatedExpenses = [...expenses];
    updatedExpenses[expIndex] = { ...exp, status: 'APPROVED' };
    setExpenses(updatedExpenses);

    triggerToast(`Expense claim approved. Ledger debited [${expCode}] expense account, credited Bank Account.`);
  };

  // ---------------------------------------------------------------------------
  // 6. COMPLIANCE & AML WORKFLOWS STATE
  // ---------------------------------------------------------------------------
  const [complianceLogs, setComplianceLogs] = useState([
    { id: 'c1', type: 'KYC_REVIEW', status: 'APPROVED', entity: 'Distributor Ramesh Hegde', desc: 'Aadhaar, PAN & Shop Establishment License fully matches digital registries.', investigator: 'compliance-officer@suryacredit.com', date: '2026-07-01' },
    { id: 'c2', type: 'AML_ALERT', status: 'CLEAR', entity: 'Retailer Shankar Gowda', desc: 'Transaction size INR 4,50,000 matches verified seasonal capital declaration.', investigator: 'compliance-officer@suryacredit.com', date: '2026-07-02' }
  ]);

  const [amlSandboxAmount, setAmlSandboxAmount] = useState(250000);
  const [amlSandboxCust, setAmlSandboxCust] = useState('John Doe Communications');
  const [amlResult, setAmlResult] = useState(null);

  const runAmlAnalysis = () => {
    let score = 15;
    const factors = [];

    if (amlSandboxAmount > 200000) {
      score += 50;
      factors.push('Transaction volume exceeds High Risk threshold (INR 2 Lakhs).');
    } else if (amlSandboxAmount > 50000) {
      score += 20;
      factors.push('Transaction size exceeds Standard Alert limits (INR 50k).');
    }

    if (amlSandboxCust.toLowerCase().includes('unknown') || amlSandboxCust.length < 5) {
      score += 25;
      factors.push('Incomplete customer identity name details.');
    } else {
      score -= 5;
    }

    const finalScore = Math.min(Math.max(score, 0), 100);
    const riskLevel = finalScore > 50 ? 'SUSPICIOUS' : 'CLEAR';

    setAmlResult({
      riskScore: finalScore,
      riskLevel,
      factors
    });

    if (riskLevel === 'SUSPICIOUS') {
      const newLog = {
        id: `c-${Date.now()}`,
        type: 'AML_ALERT',
        status: 'INVESTIGATING',
        entity: amlSandboxCust,
        desc: `System risk score ${finalScore}% triggered. Flagged factors: ${factors.join(' | ')}`,
        investigator: 'SYSTEM_MONITOR',
        date: new Date().toISOString().split('T')[0]
      };
      setComplianceLogs([newLog, ...complianceLogs]);
      triggerToast(`⚠️ AML Suspicious Alert created for audit analysis.`);
    } else {
      triggerToast(`AML transaction check cleared. Risk level: ${finalScore}%`);
    }
  };

  // ---------------------------------------------------------------------------
  // 7. REAL-TIME REPORTS ENGINE
  // ---------------------------------------------------------------------------
  const totalAssets = accounts.filter(a => a.type === 'ASSET').reduce((sum, a) => sum + a.balance, 0);
  const totalLiabilities = accounts.filter(a => a.type === 'LIABILITY').reduce((sum, a) => sum + a.balance, 0);
  const totalRevenue = accounts.filter(a => a.type === 'REVENUE').reduce((sum, a) => sum + a.balance, 0);
  const totalExpense = accounts.filter(a => a.type === 'EXPENSE').reduce((sum, a) => sum + a.balance, 0);
  
  const netProfit = totalRevenue - totalExpense;
  const totalEquity = accounts.filter(a => a.type === 'EQUITY').reduce((sum, a) => sum + a.balance, 0) + netProfit;

  // ---------------------------------------------------------------------------
  // 8. BACKUP & RESTORE
  // ---------------------------------------------------------------------------
  const handleDownloadBackup = () => {
    const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify({
      accounts,
      journalEntries,
      invoices,
      expenses,
      budgets,
      complianceLogs
    }, null, 2));
    const dlAnchorElem = document.createElement('a');
    dlAnchorElem.setAttribute("href",     dataStr     );
    dlAnchorElem.setAttribute("download", `surya_fin_backup_tenant_${Date.now()}.json`);
    dlAnchorElem.click();
    triggerToast('Tenant corporate ledger snapshot file successfully generated.');
  };

  const handleUploadBackup = (e) => {
    const fileReader = new FileReader();
    fileReader.readAsText(e.target.files[0], "UTF-8");
    fileReader.onload = (event) => {
      try {
        const parsed = JSON.parse(event.target.result);
        if (parsed.accounts) setAccounts(parsed.accounts);
        if (parsed.journalEntries) setJournalEntries(parsed.journalEntries);
        if (parsed.invoices) setInvoices(parsed.invoices);
        if (parsed.expenses) setExpenses(parsed.expenses);
        if (parsed.budgets) setBudgets(parsed.budgets);
        if (parsed.complianceLogs) setComplianceLogs(parsed.complianceLogs);
        triggerToast('Disaster Recovery complete! Tenant accounting database successfully restored.');
      } catch (err) {
        triggerToast('Failed to parse financial restore document.');
      }
    };
  };

  return (
    <div style={{ fontFamily: 'Inter, sans-serif', backgroundColor: '#F8FAFC', minHeight: '100vh', display: 'flex' }}>
      
      {/* SaaS Admin Sidebar */}
      <div style={{ width: '250px', backgroundColor: '#0F172A', color: '#FFF', display: 'flex', flexDirection: 'column', borderRight: '1px solid #1E293B' }}>
        <div style={{ padding: '24px', borderBottom: '1px solid #1E293B' }}>
          <h2 style={{ margin: 0, fontSize: '18px', fontWeight: 700, letterSpacing: '0.5px', color: '#38BDF8' }}>Surya FinTech Core</h2>
          <p style={{ margin: '4px 0 0 0', fontSize: '11px', color: '#94A3B8', textTransform: 'uppercase' }}>Tenant Administration</p>
        </div>

        <div style={{ padding: '16px', flex: 1 }}>
          <nav style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <button 
              onClick={() => setActiveTab('finance_dashboard')}
              style={{
                display: 'flex', alignItems: 'center', gap: '12px', padding: '12px 16px', background: activeTab === 'finance_dashboard' ? '#1E293B' : 'transparent',
                color: activeTab === 'finance_dashboard' ? '#38BDF8' : '#94A3B8', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 500, fontSize: '14px', textAlign: 'left', width: '100%'
              }}>
              📊 Executive Dashboard
            </button>
            <button 
              onClick={() => setActiveTab('chart_accounts')}
              style={{
                display: 'flex', alignItems: 'center', gap: '12px', padding: '12px 16px', background: activeTab === 'chart_accounts' ? '#1E293B' : 'transparent',
                color: activeTab === 'chart_accounts' ? '#38BDF8' : '#94A3B8', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 500, fontSize: '14px', textAlign: 'left', width: '100%'
              }}>
              🗂️ Chart of Accounts
            </button>
            <button 
              onClick={() => setActiveTab('journals')}
              style={{
                display: 'flex', alignItems: 'center', gap: '12px', padding: '12px 16px', background: activeTab === 'journals' ? '#1E293B' : 'transparent',
                color: activeTab === 'journals' ? '#38BDF8' : '#94A3B8', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 500, fontSize: '14px', textAlign: 'left', width: '100%'
              }}>
              📝 Ledger Journals (Maker)
            </button>
            <button 
              onClick={() => setActiveTab('invoice_center')}
              style={{
                display: 'flex', alignItems: 'center', gap: '12px', padding: '12px 16px', background: activeTab === 'invoice_center' ? '#1E293B' : 'transparent',
                color: activeTab === 'invoice_center' ? '#38BDF8' : '#94A3B8', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 500, fontSize: '14px', textAlign: 'left', width: '100%'
              }}>
              🧾 Tax Invoice Center
            </button>
            <button 
              onClick={() => setActiveTab('expense_manager')}
              style={{
                display: 'flex', alignItems: 'center', gap: '12px', padding: '12px 16px', background: activeTab === 'expense_manager' ? '#1E293B' : 'transparent',
                color: activeTab === 'expense_manager' ? '#38BDF8' : '#94A3B8', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 500, fontSize: '14px', textAlign: 'left', width: '100%'
              }}>
              💰 Expense & Budgets
            </button>
            <button 
              onClick={() => setActiveTab('aml_compliance')}
              style={{
                display: 'flex', alignItems: 'center', gap: '12px', padding: '12px 16px', background: activeTab === 'aml_compliance' ? '#1E293B' : 'transparent',
                color: activeTab === 'aml_compliance' ? '#38BDF8' : '#94A3B8', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 500, fontSize: '14px', textAlign: 'left', width: '100%'
              }}>
              🛡️ AML Compliance Hub
            </button>
            <button 
              onClick={() => setActiveTab('backup_disaster')}
              style={{
                display: 'flex', alignItems: 'center', gap: '12px', padding: '12px 16px', background: activeTab === 'backup_disaster' ? '#1E293B' : 'transparent',
                color: activeTab === 'backup_disaster' ? '#38BDF8' : '#94A3B8', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 500, fontSize: '14px', textAlign: 'left', width: '100%'
              }}>
              💾 Disaster DR Backup
            </button>
          </nav>
        </div>

        <div style={{ padding: '16px', borderTop: '1px solid #1E293B', fontSize: '12px', color: '#64748B', display: 'flex', flexDirection: 'column', gap: '4px' }}>
          <div>Active Tenant ID:</div>
          <div style={{ fontFamily: 'monospace', color: '#38BDF8', fontWeight: 'bold' }}>{tenantId}</div>
        </div>
      </div>

      {/* Main Panel Frame */}
      <div style={{ flex: 1, padding: '40px', overflowY: 'auto' }}>
        
        {/* Toast Notifier */}
        {message && (
          <div style={{
            position: 'fixed', top: '24px', right: '24px', backgroundColor: '#0F172A', color: '#FFF', padding: '16px 24px',
            borderRadius: '12px', boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)', zIndex: 9999, fontSize: '14px', borderLeft: '5px solid #38BDF8',
            fontWeight: 500, display: 'flex', alignItems: 'center', gap: '10px'
          }}>
            🛡️ <span>{message}</span>
          </div>
        )}

        {/* 1. EXECUTIVE FINANCE DASHBOARD */}
        {activeTab === 'finance_dashboard' && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
              <div>
                <h1 style={{ margin: 0, fontSize: '28px', fontWeight: 800, color: '#0F172A' }}>Financial Control Center</h1>
                <p style={{ margin: '4px 0 0 0', color: '#64748B', fontSize: '14px' }}>Real-time accounting ledger, consolidated balance sheet & dynamic P&L parameters.</p>
              </div>
              <div style={{ display: 'flex', gap: '12px' }}>
                <span style={{ padding: '8px 16px', backgroundColor: '#E2E8F0', borderRadius: '20px', fontSize: '12px', fontWeight: 'bold', color: '#475569' }}>Financial Year: FY-2026-27</span>
                <span style={{ padding: '8px 16px', backgroundColor: '#DCFCE7', borderRadius: '20px', fontSize: '12px', fontWeight: 'bold', color: '#166534' }}>Audit Standard: GAAP / IndAS</span>
              </div>
            </div>

            {/* Top Row KPI Cards */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '24px', marginBottom: '40px' }}>
              
              <div style={{ padding: '24px', backgroundColor: '#FFF', borderRadius: '16px', border: '1px solid #E2E8F0', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
                <span style={{ fontSize: '12px', color: '#64748B', fontWeight: 600, textTransform: 'uppercase' }}>Consolidated Bank Cash</span>
                <div style={{ fontSize: '24px', fontWeight: 800, color: '#0F172A', margin: '8px 0' }}>₹{accounts.find(a => a.code === '1020')?.balance.toLocaleString() || '0'}</div>
                <div style={{ fontSize: '12px', color: '#166534', fontWeight: 600 }}>● HDFC Core Treasury</div>
              </div>

              <div style={{ padding: '24px', backgroundColor: '#FFF', borderRadius: '16px', border: '1px solid #E2E8F0', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
                <span style={{ fontSize: '12px', color: '#64748B', fontWeight: 600, textTransform: 'uppercase' }}>Output GST Collected</span>
                <div style={{ fontSize: '24px', fontWeight: 800, color: '#0F172A', margin: '8px 0' }}>₹{(accounts.find(a => a.code === '2110')?.balance + accounts.find(a => a.code === '2120')?.balance + accounts.find(a => a.code === '2130')?.balance).toLocaleString() || '0'}</div>
                <div style={{ fontSize: '12px', color: '#9A3412', fontWeight: 600 }}>● CGST, SGST, IGST Payable</div>
              </div>

              <div style={{ padding: '24px', backgroundColor: '#FFF', borderRadius: '16px', border: '1px solid #E2E8F0', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
                <span style={{ fontSize: '12px', color: '#64748B', fontWeight: 600, textTransform: 'uppercase' }}>Unpaid Receivables</span>
                <div style={{ fontSize: '24px', fontWeight: 800, color: '#0F172A', margin: '8px 0' }}>₹{accounts.find(a => a.code === '1110')?.balance.toLocaleString() || '0'}</div>
                <div style={{ fontSize: '12px', color: '#1E3A8A', fontWeight: 600 }}>● {invoices.filter(i => i.status === 'UNPAID').length} Open Tax Invoices</div>
              </div>

              <div style={{ padding: '24px', backgroundColor: '#FFF', borderRadius: '16px', border: '1px solid #E2E8F0', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
                <span style={{ fontSize: '12px', color: '#64748B', fontWeight: 600, textTransform: 'uppercase' }}>Net SaaS Profit</span>
                <div style={{ fontSize: '24px', fontWeight: 800, color: netProfit >= 0 ? '#166534' : '#991B1B', margin: '8px 0' }}>₹{netProfit.toLocaleString()}</div>
                <div style={{ fontSize: '12px', color: '#475569', fontWeight: 600 }}>Revenue / Expense Ratio</div>
              </div>

            </div>

            {/* Financial Statements Grid */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '40px' }}>
              
              {/* Profit and Loss Statement */}
              <div style={{ padding: '32px', backgroundColor: '#FFF', borderRadius: '16px', border: '1px solid #E2E8F0' }}>
                <h3 style={{ margin: '0 0 24px 0', fontSize: '18px', fontWeight: 700, color: '#0F172A', borderBottom: '1px solid #F1F5F9', paddingBottom: '12px' }}>Profit & Loss Statement (FY-2026-27)</h3>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <h4 style={{ margin: '0 0 8px 0', fontSize: '14px', color: '#166534', textTransform: 'uppercase' }}>Operating Revenues</h4>
                    {accounts.filter(a => a.type === 'REVENUE').map(a => (
                      <div key={a.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', fontSize: '14px', borderBottom: '1px dotted #E2E8F0' }}>
                        <span>{a.name} ({a.code})</span>
                        <span style={{ fontWeight: 600 }}>₹{a.balance.toLocaleString()}</span>
                      </div>
                    ))}
                    <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 0', fontWeight: 'bold', fontSize: '14px', color: '#166534' }}>
                      <span>Total Operating Revenue</span>
                      <span>₹{totalRevenue.toLocaleString()}</span>
                    </div>
                  </div>

                  <div>
                    <h4 style={{ margin: '16px 0 8px 0', fontSize: '14px', color: '#991B1B', textTransform: 'uppercase' }}>Operating Expenses</h4>
                    {accounts.filter(a => a.type === 'EXPENSE').map(a => (
                      <div key={a.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', fontSize: '14px', borderBottom: '1px dotted #E2E8F0' }}>
                        <span>{a.name} ({a.code})</span>
                        <span style={{ fontWeight: 600 }}>₹{a.balance.toLocaleString()}</span>
                      </div>
                    ))}
                    <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 0', fontWeight: 'bold', fontSize: '14px', color: '#991B1B' }}>
                      <span>Total Operating Expenses</span>
                      <span>₹{totalExpense.toLocaleString()}</span>
                    </div>
                  </div>

                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '16px 0', fontWeight: 800, fontSize: '16px', borderTop: '2px solid #0F172A', color: '#0F172A' }}>
                    <span>Consolidated Net Profit / Loss</span>
                    <span>₹{netProfit.toLocaleString()}</span>
                  </div>
                </div>
              </div>

              {/* Balance Sheet */}
              <div style={{ padding: '32px', backgroundColor: '#FFF', borderRadius: '16px', border: '1px solid #E2E8F0' }}>
                <h3 style={{ margin: '0 0 24px 0', fontSize: '18px', fontWeight: 700, color: '#0F172A', borderBottom: '1px solid #F1F5F9', paddingBottom: '12px' }}>Double Entry Balance Sheet (FY-2026-27)</h3>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <h4 style={{ margin: '0 0 8px 0', fontSize: '14px', color: '#1E3A8A', textTransform: 'uppercase' }}>Assets</h4>
                    {accounts.filter(a => a.type === 'ASSET').map(a => (
                      <div key={a.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', fontSize: '14px', borderBottom: '1px dotted #E2E8F0' }}>
                        <span>{a.name} ({a.code})</span>
                        <span style={{ fontWeight: 600 }}>₹{a.balance.toLocaleString()}</span>
                      </div>
                    ))}
                    <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 0', fontWeight: 'bold', fontSize: '14px', color: '#1E3A8A' }}>
                      <span>Total Assets</span>
                      <span>₹{totalAssets.toLocaleString()}</span>
                    </div>
                  </div>

                  <div>
                    <h4 style={{ margin: '16px 0 8px 0', fontSize: '14px', color: '#7C3AED', textTransform: 'uppercase' }}>Liabilities & Equity</h4>
                    
                    {/* Liabilities */}
                    {accounts.filter(a => a.type === 'LIABILITY').map(a => (
                      <div key={a.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', fontSize: '14px', borderBottom: '1px dotted #E2E8F0' }}>
                        <span>{a.name} ({a.code})</span>
                        <span style={{ fontWeight: 600 }}>₹{a.balance.toLocaleString()}</span>
                      </div>
                    ))}
                    
                    {/* Equity */}
                    {accounts.filter(a => a.type === 'EQUITY').map(a => (
                      <div key={a.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', fontSize: '14px', borderBottom: '1px dotted #E2E8F0' }}>
                        <span>{a.name} ({a.code})</span>
                        <span style={{ fontWeight: 600 }}>₹{a.balance.toLocaleString()}</span>
                      </div>
                    ))}
                    <div style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', fontSize: '14px', borderBottom: '1px dotted #E2E8F0' }}>
                      <span>Retained Earnings (from Profit & Loss)</span>
                      <span style={{ fontWeight: 600 }}>₹{netProfit.toLocaleString()}</span>
                    </div>

                    <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 0', fontWeight: 'bold', fontSize: '14px', color: '#7C3AED' }}>
                      <span>Total Liabilities & Equity</span>
                      <span>₹{(totalLiabilities + totalEquity).toLocaleString()}</span>
                    </div>
                  </div>

                  <div style={{
                    padding: '12px 16px', backgroundColor: totalAssets === (totalLiabilities + totalEquity) ? '#DCFCE7' : '#FEE2E2',
                    borderRadius: '8px', color: totalAssets === (totalLiabilities + totalEquity) ? '#15803D' : '#B91C1C',
                    display: 'flex', justifyContent: 'space-between', fontWeight: 'bold', fontSize: '13px'
                  }}>
                    <span>Double Entry Integrity Audit:</span>
                    <span>{totalAssets === (totalLiabilities + totalEquity) ? '✓ Ledgers Balanced & Verifiable (IndAS compliant)' : '❌ Balance Mismatch Detected'}</span>
                  </div>
                </div>
              </div>

            </div>
          </div>
        )}

        {/* 2. CHART OF ACCOUNTS PANEL */}
        {activeTab === 'chart_accounts' && (
          <div>
            <h1 style={{ fontSize: '24px', fontWeight: 800, color: '#0F172A', marginBottom: '8px' }}>Chart of Accounts (COA)</h1>
            <p style={{ color: '#64748B', marginBottom: '32px' }}>Maintain high-volume general ledger parameters. Double-entry system maps Assets, Liabilities, Equities, Revenues, and Expenses.</p>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 300px', gap: '32px' }}>
              <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '16px', border: '1px solid #E2E8F0' }}>
                <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', fontWeight: 700 }}>Active General Ledgers</h3>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid #E2E8F0', paddingBottom: '8px', fontSize: '12px', color: '#64748B', textTransform: 'uppercase' }}>
                      <th style={{ padding: '12px 8px' }}>Account Code</th>
                      <th>Account Name</th>
                      <th>Account Type</th>
                      <th style={{ textAlign: 'right', paddingRight: '12px' }}>Balance (INR)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {accounts.map(acc => (
                      <tr key={acc.id} style={{ borderBottom: '1px solid #F1F5F9', fontSize: '14px' }}>
                        <td style={{ padding: '12px 8px', fontFamily: 'monospace', fontWeight: 'bold' }}>{acc.code}</td>
                        <td style={{ fontWeight: 500 }}>{acc.name}</td>
                        <td>
                          <span style={{
                            padding: '4px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: 'bold',
                            backgroundColor: acc.type === 'ASSET' ? '#DBEAFE' : (acc.type === 'LIABILITY' ? '#FEE2E2' : (acc.type === 'EQUITY' ? '#F3E8FF' : (acc.type === 'REVENUE' ? '#D1FAE5' : '#FEF3C7'))),
                            color: acc.type === 'ASSET' ? '#1E40AF' : (acc.type === 'LIABILITY' ? '#991B1B' : (acc.type === 'EQUITY' ? '#6B21A8' : (acc.type === 'REVENUE' ? '#065F46' : '#92400E'))),
                          }}>{acc.type}</span>
                        </td>
                        <td style={{ textAlign: 'right', paddingRight: '12px', fontWeight: 'bold' }}>₹{acc.balance.toLocaleString()}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Add Custom COA Account */}
              <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '16px', border: '1px solid #E2E8F0', height: 'fit-content' }}>
                <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', fontWeight: 700 }}>Create New Ledger</h3>
                <form onSubmit={handleAddAccount} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Ledger Code (Unique)</label>
                    <input 
                      type="text" placeholder="e.g. 5040" value={newAccCode} onChange={e => setNewAccCode(e.target.value)}
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Account Name</label>
                    <input 
                      type="text" placeholder="e.g. Server hosting costs" value={newAccName} onChange={e => setNewAccName(e.target.value)}
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Type Category</label>
                    <select 
                      value={newAccType} onChange={e => setNewAccType(e.target.value)}
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}>
                      <option value="ASSET">ASSET</option>
                      <option value="LIABILITY">LIABILITY</option>
                      <option value="EQUITY">EQUITY</option>
                      <option value="REVENUE">REVENUE</option>
                      <option value="EXPENSE">EXPENSE</option>
                    </select>
                  </div>
                  <button type="submit" style={{ width: '100%', padding: '12px', backgroundColor: '#0F172A', color: '#FFF', border: 'none', borderRadius: '8px', fontWeight: 'bold', cursor: 'pointer' }}>
                    + Register Account
                  </button>
                </form>
              </div>
            </div>
          </div>
        )}

        {/* 3. DOUBLE ENTRY JOURNAL WRITER & MAKER-CHECKER WORKFLOWS */}
        {activeTab === 'journals' && (
          <div>
            <h1 style={{ fontSize: '24px', fontWeight: 800, color: '#0F172A', marginBottom: '8px' }}>Double Entry Journals (Maker-Checker Approval)</h1>
            <p style={{ color: '#64748B', marginBottom: '32px' }}>Audit compliance requires maker-checker dual authorization. General Ledger adjustments are drafted, verified, and audited before balance updates.</p>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 380px', gap: '32px' }}>
              
              {/* Journal List */}
              <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '16px', border: '1px solid #E2E8F0' }}>
                <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', fontWeight: 700 }}>Ledger Journal Log</h3>
                {journalEntries.map(je => (
                  <div key={je.id} style={{ border: '1px solid #E2E8F0', borderRadius: '12px', padding: '20px', marginBottom: '20px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <div>
                        <span style={{ fontFamily: 'monospace', fontWeight: 'bold', color: '#0F172A', fontSize: '15px' }}>{je.entryNumber}</span>
                        <span style={{ fontSize: '12px', color: '#64748B', marginLeft: '12px' }}>📅 {je.postingDate}</span>
                      </div>
                      <span style={{
                        padding: '4px 10px', borderRadius: '20px', fontSize: '11px', fontWeight: 'bold',
                        backgroundColor: je.status === 'POSTED' ? '#DCFCE7' : '#FEF3C7',
                        color: je.status === 'POSTED' ? '#15803D' : '#D97706'
                      }}>{je.status}</span>
                    </div>

                    <div style={{ fontSize: '14px', color: '#334155', fontWeight: 'bold', marginBottom: '12px' }}>{je.description}</div>
                    
                    <div style={{ fontSize: '12px', color: '#64748B', display: 'flex', gap: '16px', marginBottom: '12px' }}>
                      <span>👤 Created: {je.createdBy}</span>
                      {je.approvedBy && <span style={{ color: '#15803D' }}>✓ Checker approved: {je.approvedBy}</span>}
                    </div>

                    {/* Debit/Credit details */}
                    <div style={{ backgroundColor: '#F8FAFC', padding: '12px', borderRadius: '8px' }}>
                      <table style={{ width: '100%', fontSize: '12px', borderCollapse: 'collapse' }}>
                        <thead>
                          <tr style={{ color: '#64748B', borderBottom: '1px solid #E2E8F0' }}>
                            <th style={{ textAlign: 'left', paddingBottom: '4px' }}>Account</th>
                            <th style={{ textAlign: 'right', paddingBottom: '4px' }}>Debit (INR)</th>
                            <th style={{ textAlign: 'right', paddingBottom: '4px' }}>Credit (INR)</th>
                          </tr>
                        </thead>
                        <tbody>
                          {je.items.map((i, idx) => (
                            <tr key={idx} style={{ height: '24px' }}>
                              <td>[{i.accountCode}] {i.accountName}</td>
                              <td style={{ textAlign: 'right', color: '#166534', fontWeight: i.debit > 0 ? 'bold' : 'normal' }}>{i.debit > 0 ? `₹${i.debit.toLocaleString()}` : '-'}</td>
                              <td style={{ textAlign: 'right', color: '#991B1B', fontWeight: i.credit > 0 ? 'bold' : 'normal' }}>{i.credit > 0 ? `₹${i.credit.toLocaleString()}` : '-'}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>

                    {je.status === 'DRAFT' && (
                      <button 
                        onClick={() => handleApproveJournal(je.id)}
                        style={{
                          marginTop: '16px', width: '100%', padding: '10px', backgroundColor: '#15803D', color: '#FFF',
                          border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold', fontSize: '13px'
                        }}>
                        ✓ Approve and Post (Checker Authority)
                      </button>
                    )}
                  </div>
                ))}
              </div>

              {/* Journal Entry Writer Form */}
              <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '16px', border: '1px solid #E2E8F0', height: 'fit-content' }}>
                <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', fontWeight: 700 }}>Record Journal Entry</h3>
                <form onSubmit={submitJournalEntry} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Description</label>
                    <input 
                      type="text" placeholder="e.g. Accrue monthly software fee" value={jeDescription} onChange={e => setJeDescription(e.target.value)} required
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Reference</label>
                    <input 
                      type="text" placeholder="e.g. Bank slip, invoice ref" value={jeReference} onChange={e => setJeReference(e.target.value)}
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                    />
                  </div>

                  <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                      <label style={{ fontSize: '12px', color: '#475569', fontWeight: 600 }}>Transaction Rows</label>
                      <button type="button" onClick={handleAddJeRow} style={{ border: 'none', background: 'transparent', color: '#38BDF8', fontWeight: 'bold', cursor: 'pointer', fontSize: '12px' }}>
                        + Add Row
                      </button>
                    </div>

                    {jeRows.map((row, idx) => (
                      <div key={idx} style={{ display: 'flex', gap: '8px', marginBottom: '12px', alignItems: 'center' }}>
                        <select 
                          value={row.accountId} onChange={e => handleJeRowChange(idx, 'accountId', e.target.value)}
                          style={{ flex: 2, padding: '8px', border: '1px solid #CBD5E1', borderRadius: '6px', fontSize: '12px' }}>
                          <option value="">Select Account</option>
                          {accounts.map(a => <option key={a.id} value={a.id}>[{a.code}] {a.name}</option>)}
                        </select>
                        <input 
                          type="number" placeholder="Debit" value={row.debit} onChange={e => handleJeRowChange(idx, 'debit', e.target.value)}
                          style={{ flex: 1, padding: '8px', border: '1px solid #CBD5E1', borderRadius: '6px', fontSize: '12px', width: '50px' }}
                        />
                        <input 
                          type="number" placeholder="Credit" value={row.credit} onChange={e => handleJeRowChange(idx, 'credit', e.target.value)}
                          style={{ flex: 1, padding: '8px', border: '1px solid #CBD5E1', borderRadius: '6px', fontSize: '12px', width: '50px' }}
                        />
                        {jeRows.length > 2 && (
                          <button type="button" onClick={() => handleRemoveJeRow(idx)} style={{ background: 'transparent', border: 'none', color: '#EF4444', fontWeight: 'bold', cursor: 'pointer' }}>✕</button>
                        )}
                      </div>
                    ))}
                  </div>

                  <button type="submit" style={{ width: '100%', padding: '12px', backgroundColor: '#0F172A', color: '#FFF', border: 'none', borderRadius: '8px', fontWeight: 'bold', cursor: 'pointer' }}>
                    Draft Journal Entry
                  </button>
                </form>
              </div>

            </div>
          </div>
        )}

        {/* 4. TAX INVOICE CENTER */}
        {activeTab === 'invoice_center' && (
          <div>
            <h1 style={{ fontSize: '24px', fontWeight: 800, color: '#0F172A', marginBottom: '8px' }}>GST Tax Invoices (IndAS / CGST / SGST / IGST compliance)</h1>
            <p style={{ color: '#64748B', marginBottom: '32px' }}>Issue professional invoices. Real-time dynamic tax routing handles Intra-State CGST/SGST vs Inter-State IGST based on the place of supply.</p>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 380px', gap: '32px' }}>
              
              {/* Invoice List */}
              <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '16px', border: '1px solid #E2E8F0' }}>
                <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', fontWeight: 700 }}>Invoiced Register</h3>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  {invoices.map(inv => (
                    <div key={inv.id} style={{ border: '1px solid #E2E8F0', borderRadius: '12px', padding: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <div>
                        <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginBottom: '8px' }}>
                          <span style={{ fontWeight: 'bold', fontSize: '15px' }}>{inv.invoiceNumber}</span>
                          <span style={{ fontSize: '11px', padding: '3px 8px', backgroundColor: '#E2E8F0', borderRadius: '12px', color: '#475569', fontWeight: 'bold' }}>{inv.invoiceType}</span>
                        </div>
                        <div style={{ fontSize: '14px', fontWeight: 600, color: '#0F172A' }}>{inv.customerName}</div>
                        <div style={{ fontSize: '12px', color: '#64748B', marginTop: '4px' }}>GSTIN: {inv.customerGstin} | Place: {inv.placeOfSupply}</div>
                        
                        {/* Tax breakdown */}
                        <div style={{ display: 'flex', gap: '16px', fontSize: '11px', color: '#475569', marginTop: '10px', backgroundColor: '#F8FAFC', padding: '6px 12px', borderRadius: '6px' }}>
                          <span>Taxable: ₹{inv.totalTaxableVal.toLocaleString()}</span>
                          {inv.igst > 0 ? (
                            <span style={{ fontWeight: 'bold', color: '#4338CA' }}>IGST: ₹{inv.igst.toLocaleString()}</span>
                          ) : (
                            <span style={{ fontWeight: 'bold', color: '#0369A1' }}>CGST/SGST: ₹{(inv.cgst + inv.sgst).toLocaleString()}</span>
                          )}
                        </div>
                      </div>

                      <div style={{ textAlign: 'right' }}>
                        <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#0F172A' }}>₹{inv.grandTotal.toLocaleString()}</div>
                        <span style={{
                          display: 'inline-block', padding: '4px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: 'bold', marginTop: '8px',
                          backgroundColor: inv.status === 'PAID' ? '#D1FAE5' : '#FEF3C7',
                          color: inv.status === 'PAID' ? '#065F46' : '#D97706'
                        }}>{inv.status}</span>

                        {inv.status === 'UNPAID' && (
                          <button 
                            onClick={() => handlePayInvoice(inv.id)}
                            style={{
                              display: 'block', marginTop: '12px', padding: '6px 12px', backgroundColor: '#0F172A', color: '#FFF',
                              border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '11px', fontWeight: 'bold'
                            }}>
                            Capture Payment
                          </button>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Invoice Maker Form */}
              <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '16px', border: '1px solid #E2E8F0', height: 'fit-content' }}>
                <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', fontWeight: 700 }}>Issue Dynamic Invoice</h3>
                <form onSubmit={handleIssueInvoice} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Invoice Type</label>
                    <select value={invoiceType} onChange={e => setInvoiceType(e.target.value)}
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}>
                      <option value="TAX_INVOICE">TAX INVOICE</option>
                      <option value="RETAIL_INVOICE">RETAIL INVOICE</option>
                      <option value="PROFORMA_INVOICE">PROFORMA INVOICE</option>
                    </select>
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Customer Name</label>
                    <input type="text" placeholder="e.g. Balaji Communications" value={customerName} onChange={e => setCustomerName(e.target.value)} required
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Customer GSTIN (Optional)</label>
                    <input type="text" placeholder="e.g. 29AAECS45..." value={customerGstin} onChange={e => setCustomerGstin(e.target.value)}
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Place of Supply (State)</label>
                    <select value={placeOfSupply} onChange={e => setPlaceOfSupply(e.target.value)}
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}>
                      <option value="Karnataka">Karnataka (Intra-state CGST/SGST)</option>
                      <option value="Maharashtra">Maharashtra (Inter-state IGST)</option>
                      <option value="Tamil Nadu">Tamil Nadu (Inter-state IGST)</option>
                      <option value="Delhi">Delhi (Inter-state IGST)</option>
                    </select>
                  </div>

                  <hr style={{ border: 'none', borderTop: '1px solid #F1F5F9', margin: '8px 0' }} />

                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Service Item Description</label>
                    <input type="text" value={invoiceDescription} onChange={e => setInvoiceDescription(e.target.value)} required
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                    />
                  </div>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <div style={{ flex: 1 }}>
                      <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>HSN/SAC Code</label>
                      <input type="text" value={invoiceHsn} onChange={e => setInvoiceHsn(e.target.value)} required
                        style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                      />
                    </div>
                    <div style={{ flex: 1 }}>
                      <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Qty</label>
                      <input type="number" value={invoiceQty} onChange={e => setInvoiceQty(e.target.value)} required
                        style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                      />
                    </div>
                  </div>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <div style={{ flex: 1 }}>
                      <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Rate / Price</label>
                      <input type="number" value={invoicePrice} onChange={e => setInvoicePrice(e.target.value)} required
                        style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                      />
                    </div>
                    <div style={{ flex: 1 }}>
                      <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>GST Slab</label>
                      <select value={invoiceTaxPercent} onChange={e => setInvoiceTaxPercent(Number(e.target.value))}
                        style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}>
                        <option value="0">0% Exemption</option>
                        <option value="5">5% UT/GST</option>
                        <option value="12">12% Standard</option>
                        <option value="18">18% Standard SaaS</option>
                        <option value="28">28% High Luxury</option>
                      </select>
                    </div>
                  </div>

                  <button type="submit" style={{ width: '100%', padding: '12px', backgroundColor: '#0F172A', color: '#FFF', border: 'none', borderRadius: '8px', fontWeight: 'bold', cursor: 'pointer' }}>
                    Generate & Post Invoice
                  </button>
                </form>
              </div>

            </div>
          </div>
        )}

        {/* 5. EXPENSE & BUDGET MANAGER */}
        {activeTab === 'expense_manager' && (
          <div>
            <h1 style={{ fontSize: '24px', fontWeight: 800, color: '#0F172A', marginBottom: '8px' }}>Corporate Budget & Expense Registry</h1>
            <p style={{ color: '#64748B', marginBottom: '32px' }}>Verify compliance with dynamic category budget caps. Warn on over-run and approve claims instantly.</p>

            <div style={{ display: 'grid', gridTemplateColumns: '300px 1fr', gap: '32px' }}>
              
              {/* Budgets Tracker Card */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '16px', border: '1px solid #E2E8F0' }}>
                  <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', fontWeight: 700 }}>Fiscal Budgets (FY-2026-27)</h3>
                  {budgets.map(b => (
                    <div key={b.id} style={{ marginBottom: '20px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', fontWeight: 'bold', color: '#334155', marginBottom: '6px' }}>
                        <span>📁 {b.category}</span>
                        <span>₹{b.spent.toLocaleString()} / ₹{b.allocated.toLocaleString()}</span>
                      </div>
                      <div style={{ width: '100%', backgroundColor: '#E2E8F0', borderRadius: '4px', height: '8px', overflow: 'hidden' }}>
                        <div style={{
                          width: `${Math.min((b.spent / b.allocated) * 100, 100)}%`,
                          backgroundColor: (b.spent / b.allocated) > 0.9 ? '#EF4444' : ((b.spent / b.allocated) > 0.6 ? '#F59E0B' : '#10B981'),
                          height: '100%'
                        }} />
                      </div>
                    </div>
                  ))}
                </div>

                {/* Expense claims logger form */}
                <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '16px', border: '1px solid #E2E8F0' }}>
                  <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', fontWeight: 700 }}>File Expense Claim</h3>
                  <form onSubmit={handleAddExpenseClaim} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                    <div>
                      <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Claimed By (Email)</label>
                      <input type="email" placeholder="e.g. employee@suryacredit.com" value={expClaimedBy} onChange={e => setExpClaimedBy(e.target.value)} required
                        style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                      />
                    </div>
                    <div>
                      <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Description</label>
                      <input type="text" placeholder="e.g. Flight to Mumbai" value={expDesc} onChange={e => setExpDesc(e.target.value)} required
                        style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                      />
                    </div>
                    <div>
                      <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Category</label>
                      <select value={expCat} onChange={e => setExpCat(e.target.value)}
                        style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}>
                        <option value="TECH_INFRA">TECH INFRASTRUCTURE</option>
                        <option value="MARKETING">MARKETING & SALES</option>
                        <option value="OFFICE_RENT">OFFICE RENT & UTILITIES</option>
                      </select>
                    </div>
                    <div>
                      <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Amount (INR)</label>
                      <input type="number" value={expAmount} onChange={e => setExpAmount(e.target.value)} required
                        style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                      />
                    </div>
                    <button type="submit" style={{ width: '100%', padding: '12px', backgroundColor: '#0F172A', color: '#FFF', border: 'none', borderRadius: '8px', fontWeight: 'bold', cursor: 'pointer' }}>
                      Submit Claim
                    </button>
                  </form>
                </div>
              </div>

              {/* Expense Claims Table list */}
              <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '16px', border: '1px solid #E2E8F0' }}>
                <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', fontWeight: 700 }}>Expense Audit Registry</h3>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid #E2E8F0', fontSize: '12px', color: '#64748B', textTransform: 'uppercase' }}>
                      <th style={{ padding: '12px 8px' }}>Date</th>
                      <th>Claim Description</th>
                      <th>Category</th>
                      <th>Claimant</th>
                      <th>Amount</th>
                      <th>Status</th>
                      <th style={{ textAlign: 'center' }}>Approval Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {expenses.map(e => (
                      <tr key={e.id} style={{ borderBottom: '1px solid #F1F5F9', fontSize: '14px' }}>
                        <td style={{ padding: '12px 8px', fontFamily: 'monospace' }}>{e.date}</td>
                        <td style={{ fontWeight: 600 }}>{e.description}</td>
                        <td><span style={{ fontSize: '11px', padding: '3px 8px', backgroundColor: '#F1F5F9', borderRadius: '12px', fontWeight: 'bold' }}>{e.category}</span></td>
                        <td>{e.claimedBy}</td>
                        <td style={{ fontWeight: 'bold' }}>₹{e.amount.toLocaleString()}</td>
                        <td>
                          <span style={{
                            padding: '4px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: 'bold',
                            backgroundColor: e.status === 'APPROVED' ? '#DCFCE7' : '#FEF3C7',
                            color: e.status === 'APPROVED' ? '#15803D' : '#D97706'
                          }}>{e.status}</span>
                        </td>
                        <td style={{ textAlign: 'center' }}>
                          {e.status === 'PENDING' ? (
                            <button 
                              onClick={() => handleApproveExpense(e.id)}
                              style={{ padding: '6px 12px', backgroundColor: '#15803D', color: '#FFF', border: 'none', borderRadius: '6px', fontWeight: 'bold', fontSize: '11px', cursor: 'pointer' }}>
                              Approve Pay
                            </button>
                          ) : (
                            <span style={{ fontSize: '11px', color: '#64748B' }}>Settled</span>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

            </div>
          </div>
        )}

        {/* 6. COMPLIANCE & AML WORKFLOWS */}
        {activeTab === 'aml_compliance' && (
          <div>
            <h1 style={{ fontSize: '24px', fontWeight: 800, color: '#0F172A', marginBottom: '8px' }}>AML (Anti-Money Laundering) & KYC Compliance Hub</h1>
            <p style={{ color: '#64748B', marginBottom: '32px' }}>Real-time KYC record keeping, risk grading dashboards, and transaction compliance rules for regulatory auditing.</p>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 380px', gap: '32px' }}>
              
              {/* Compliance logs */}
              <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '16px', border: '1px solid #E2E8F0' }}>
                <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', fontWeight: 700 }}>Security Compliance Log</h3>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  {complianceLogs.map(log => (
                    <div key={log.id} style={{ border: '1px solid #E2E8F0', padding: '20px', borderRadius: '12px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                        <div>
                          <span style={{
                            padding: '4px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: 'bold',
                            backgroundColor: log.type === 'AML_ALERT' ? '#FEE2E2' : '#DBEAFE',
                            color: log.type === 'AML_ALERT' ? '#991B1B' : '#1E40AF'
                          }}>{log.type}</span>
                          <span style={{ fontSize: '12px', color: '#64748B', marginLeft: '12px' }}>{log.date}</span>
                        </div>
                        <span style={{
                          padding: '4px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: 'bold',
                          backgroundColor: log.status === 'APPROVED' || log.status === 'CLEAR' ? '#DCFCE7' : '#FEF3C7',
                          color: log.status === 'APPROVED' || log.status === 'CLEAR' ? '#15803D' : '#D97706'
                        }}>{log.status}</span>
                      </div>

                      <div style={{ fontSize: '14px', fontWeight: 'bold', color: '#0F172A', marginBottom: '6px' }}>Target entity: {log.entity}</div>
                      <p style={{ margin: 0, fontSize: '13px', color: '#475569' }}>{log.desc}</p>
                      <div style={{ marginTop: '12px', fontSize: '11px', color: '#64748B' }}>Investigator Assigned: <b>{log.investigator}</b></div>
                    </div>
                  ))}
                </div>
              </div>

              {/* AML Sandbox Checker */}
              <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '16px', border: '1px solid #E2E8F0', height: 'fit-content' }}>
                <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', fontWeight: 700 }}>AML Pattern Analyzer (Testbed)</h3>
                <p style={{ fontSize: '12px', color: '#64748B', marginBottom: '16px' }}>Pre-screen distributor/retailer credit or wallet volume jumps instantly using GAAP transaction analysis criteria.</p>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Entity Name</label>
                    <input type="text" value={amlSandboxCust} onChange={e => setAmlSandboxCust(e.target.value)} required
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '12px', color: '#475569', marginBottom: '6px', fontWeight: 600 }}>Proposed Transaction Volume (INR)</label>
                    <input type="number" value={amlSandboxAmount} onChange={e => setAmlSandboxAmount(e.target.value)} required
                      style={{ width: '100%', padding: '10px', border: '1px solid #CBD5E1', borderRadius: '8px', boxSizing: 'border-box' }}
                    />
                  </div>
                  <button type="button" onClick={runAmlAnalysis} style={{ width: '100%', padding: '12px', backgroundColor: '#0F172A', color: '#FFF', border: 'none', borderRadius: '8px', fontWeight: 'bold', cursor: 'pointer' }}>
                    🔍 Run Compliance Check
                  </button>

                  {amlResult && (
                    <div style={{
                      marginTop: '20px', padding: '16px', borderRadius: '12px', border: '1px solid #E2E8F0',
                      backgroundColor: amlResult.riskLevel === 'SUSPICIOUS' ? '#FEF2F2' : '#F0FDF4'
                    }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                        <span style={{ fontSize: '13px', fontWeight: 'bold' }}>Risk Analysis Result</span>
                        <span style={{
                          padding: '4px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: 'bold',
                          backgroundColor: amlResult.riskLevel === 'SUSPICIOUS' ? '#FEE2E2' : '#DCFCE7',
                          color: amlResult.riskLevel === 'SUSPICIOUS' ? '#991B1B' : '#15803D'
                        }}>{amlResult.riskLevel}</span>
                      </div>
                      <div style={{ fontSize: '24px', fontWeight: 'bold', margin: '12px 0' }}>{amlResult.riskScore}% Risk Score</div>
                      
                      <div style={{ fontSize: '11px', color: '#475569' }}>
                        <b>Flags raised:</b>
                        <ul style={{ margin: '4px 0 0 0', paddingLeft: '16px' }}>
                          {amlResult.factors.map((f, idx) => <li key={idx}>{f}</li>)}
                        </ul>
                      </div>
                    </div>
                  )}
                </div>
              </div>

            </div>
          </div>
        )}

        {/* 7. DISASTER BACKUP & RESTORE */}
        {activeTab === 'backup_disaster' && (
          <div style={{ maxWidth: '600px', margin: '0 auto' }}>
            <h1 style={{ fontSize: '24px', fontWeight: 800, color: '#0F172A', marginBottom: '8px' }}>Disaster Recovery & Financial Data Backups</h1>
            <p style={{ color: '#64748B', marginBottom: '32px' }}>Download signed system ledger files and restore snapshots dynamically in case of system failures.</p>

            <div style={{ backgroundColor: '#FFF', padding: '32px', borderRadius: '16px', border: '1px solid #E2E8F0', display: 'flex', flexDirection: 'column', gap: '32px' }}>
              
              <div style={{ borderBottom: '1px solid #E2E8F0', paddingBottom: '32px' }}>
                <h3 style={{ margin: '0 0 12px 0', fontSize: '16px', fontWeight: 700 }}>Generate Tenant Backup File</h3>
                <p style={{ fontSize: '14px', color: '#64748B', marginBottom: '24px' }}>Export a cryptographically signed, complete offline snapshot containing all ledgers, accounting tables, tax invoices, and compliance profiles.</p>
                <button 
                  onClick={handleDownloadBackup}
                  style={{ padding: '14px 28px', backgroundColor: '#0F172A', color: '#FFF', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold', fontSize: '14px' }}>
                  📥 Download Corporate Backup JSON
                </button>
              </div>

              <div>
                <h3 style={{ margin: '0 0 12px 0', fontSize: '16px', fontWeight: 700 }}>Restore Tenant Snapshot</h3>
                <p style={{ fontSize: '14px', color: '#64748B', marginBottom: '24px' }}>Upload a valid corporate ledger backup file to restore transaction databases. <b>Warning:</b> This operation overwrites the current tenant financial state.</p>
                <div style={{ border: '2px dashed #CBD5E1', padding: '32px', borderRadius: '12px', textAlign: 'center', backgroundColor: '#F8FAFC' }}>
                  <input type="file" accept=".json" onChange={handleUploadBackup} style={{ cursor: 'pointer', fontSize: '14px' }} />
                  <p style={{ margin: '12px 0 0 0', fontSize: '12px', color: '#64748B' }}>Only *.json backup configurations created by Surya FinTech platform are accepted.</p>
                </div>
              </div>

            </div>
          </div>
        )}

      </div>
    </div>
  );
}
