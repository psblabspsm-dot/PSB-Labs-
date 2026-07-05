import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/fintech_services_provider.dart';
import '../providers/auth_provider.dart';

/// 1. AEPS DASHBOARD
class AepsDashboard extends ConsumerStatefulWidget {
  const AepsDashboard({Key? key}) : super(key: key);

  @override
  ConsumerState<AepsDashboard> createState() => _AepsDashboardState();
}

class _AepsDashboardState extends ConsumerState<AepsDashboard> {
  final _aadhaarController = TextEditingController();
  final _amountController = TextEditingController();
  String _selectedBank = 'State Bank of India';
  String _transactionType = 'Cash Withdrawal'; // Cash Withdrawal, Balance Enquiry, Mini Statement
  bool _isScanningBiometric = false;
  String? _successMessage;

  @override
  void dispose() {
    _aadhaarController.dispose();
    _amountController.dispose();
    super.dispose();
  }

  void _triggerBiometricScan() async {
    if (_aadhaarController.text.length != 12) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Aadhaar number must be exactly 12 digits')),
      );
      return;
    }

    setState(() {
      _isScanningBiometric = true;
      _successMessage = null;
    });

    // Simulate Mantra/Morpho RD Service biometric finger scan delay
    await Future.delayed(const Duration(milliseconds: 1500));

    final user = ref.read(authProvider).user.value;
    final userId = user?.id ?? 'usr-ret-03';
    final amt = double.tryParse(_amountController.text) ?? 0.0;

    final success = await ref.read(fintechProvider.notifier).executeTxn(
          userId: userId,
          service: 'AEPS',
          amount: _transactionType == 'Cash Withdrawal' ? amt : 0.0,
          description: 'AEPS $_transactionType via $_selectedBank',
        );

    setState(() {
      _isScanningBiometric = false;
      if (success) {
        _successMessage = 'AEPS $_transactionType of ₹$amt completed successfully!';
      } else {
        _successMessage = 'Biometric matches but wallet limit check failed';
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final fintechState = ref.watch(fintechProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Aadhaar Enabled Payment (AEPS)'),
        backgroundColor: Colors.green.shade700,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Card(
              elevation: 2,
              shape: RoundedCornerShape(12),
              color: Colors.green.shade50,
              child: const Padding(
                padding: EdgeInsets.all(12.0),
                child: Row(
                  children: [
                    Icon(Icons.fingerprint, size: 40, color: Colors.green),
                    SizedBox(width: 12),
                    Expanded(
                      child: Text(
                        'RD Service integration is active. Ensure Mantra MFS100 or Morpho device is attached via OTG.',
                        style: TextStyle(fontSize: 12, color: Colors.green),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            DropdownButtonFormField<String>(
              value: _transactionType,
              decoration: const InputDecoration(
                labelText: 'Transaction Operation',
                border: OutlineInputBorder(),
              ),
              items: ['Cash Withdrawal', 'Balance Enquiry', 'Mini Statement']
                  .map((t) => DropdownMenuItem(value: t, child: Text(t)))
                  .toList(),
              onChanged: (val) {
                setState(() {
                  _transactionType = val!;
                });
              },
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _aadhaarController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(
                labelText: 'Applicant Aadhaar Number (12-Digit)',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.credit_card),
              ),
            ),
            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              value: _selectedBank,
              decoration: const InputDecoration(
                labelText: 'Linked Bank Account',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.account_balance),
              ),
              items: ['State Bank of India', 'HDFC Bank', 'ICICI Bank', 'Punjab National Bank']
                  .map((b) => DropdownMenuItem(value: b, child: Text(b)))
                  .toList(),
              onChanged: (val) {
                setState(() {
                  _selectedBank = val!;
                });
              },
            ),
            if (_transactionType == 'Cash Withdrawal') ...[
              const SizedBox(height: 12),
              TextFormField(
                controller: _amountController,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(
                  labelText: 'Withdrawal Amount (₹)',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.currency_rupee),
                ),
              ),
            ],
            const SizedBox(height: 20),
            if (_isScanningBiometric)
              const Center(
                child: Column(
                  children: [
                    CircularProgressIndicator(color: Colors.green),
                    SizedBox(height: 8),
                    Text('Place finger on biometric scanner...'),
                  ],
                ),
              )
            else
              ElevatedButton.icon(
                onPressed: _triggerBiometricScan,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.green.shade700,
                  foregroundColor: Colors.white,
                  minimumSize: const Size.fromHeight(50),
                ),
                icon: const Icon(Icons.fingerprint),
                label: Text('Initiate Biometric $_transactionType'),
              ),
            if (_successMessage != null) ...[
              const SizedBox(height: 20),
              Card(
                color: Colors.grey.shade100,
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('TRANSACTION RECEIPT', style: TextStyle(fontWeight: FontWeight.bold)),
                      const Divider(),
                      Text('Status: SUCCESS'),
                      Text('Type: $_transactionType'),
                      Text('Aadhaar: XXXXXXXXX${_aadhaarController.text.padRight(12).substring(8)}'),
                      Text('Bank: $_selectedBank'),
                      Text('Ref ID: AEPS${DateTime.now().millisecondsSinceEpoch.toString().substring(6)}'),
                    ],
                  ),
                ),
              ),
            ]
          ],
        ),
      ),
    );
  }
}

/// 2. DMT DASHBOARD (DOMESTIC MONEY TRANSFER)
class DmtDashboard extends ConsumerStatefulWidget {
  const DmtDashboard({Key? key}) : super(key: key);

  @override
  ConsumerState<DmtDashboard> createState() => _DmtDashboardState();
}

class _DmtDashboardState extends ConsumerState<DmtDashboard> {
  final _senderPhoneController = TextEditingController();
  final _beneficiaryNameController = TextEditingController();
  final _beneficiaryAccountController = TextEditingController();
  final _ifscController = TextEditingController();
  final _amountController = TextEditingController();
  String _transferMode = 'IMPS'; // IMPS, NEFT
  bool _isProcessing = false;

  @override
  void dispose() {
    _senderPhoneController.dispose();
    _beneficiaryNameController.dispose();
    _beneficiaryAccountController.dispose();
    _ifscController.dispose();
    _amountController.dispose();
    super.dispose();
  }

  void _executeTransfer() async {
    if (_beneficiaryAccountController.text.isEmpty || _amountController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please supply full transfer credentials')),
      );
      return;
    }

    setState(() => _isProcessing = true);
    final user = ref.read(authProvider).user.value;
    final userId = user?.id ?? 'usr-ret-03';
    final amt = double.tryParse(_amountController.text) ?? 0.0;

    final success = await ref.read(fintechProvider.notifier).executeTxn(
          userId: userId,
          service: 'DMT',
          amount: amt,
          description: 'DMT $_transferMode to ${_beneficiaryNameController.text}',
        );

    setState(() => _isProcessing = false);

    if (success) {
      showDialog(
        context: context,
        builder: (ctx) => AlertDialog(
          title: const Text('Transfer Confirmed'),
          content: Text('₹$amt successfully remitted to ${_beneficiaryNameController.text} via IMPS Real-time switch.'),
          actions: [
            TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('OK')),
          ],
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Domestic Money Transfer (DMT)'),
        backgroundColor: Colors.purple.shade700,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextFormField(
              controller: _senderPhoneController,
              keyboardType: TextInputType.phone,
              decoration: const InputDecoration(
                labelText: 'Sender Mobile Number (OTP Verified)',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.phone),
              ),
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _beneficiaryNameController,
              decoration: const InputDecoration(
                labelText: 'Beneficiary Full Name',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.person),
              ),
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _beneficiaryAccountController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(
                labelText: 'Bank Account Number',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.wallet),
              ),
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _ifscController,
              decoration: const InputDecoration(
                labelText: 'Bank IFSC Code',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.key),
              ),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: RadioListTile<String>(
                    title: const Text('IMPS'),
                    value: 'IMPS',
                    groupValue: _transferMode,
                    onChanged: (val) => setState(() => _transferMode = val!),
                  ),
                ),
                Expanded(
                  child: RadioListTile<String>(
                    title: const Text('NEFT'),
                    value: 'NEFT',
                    groupValue: _transferMode,
                    onChanged: (val) => setState(() => _transferMode = val!),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _amountController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(
                labelText: 'Transfer Amount (₹)',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.currency_rupee),
              ),
            ),
            const SizedBox(height: 20),
            _isProcessing
                ? const CircularProgressIndicator()
                : ElevatedButton(
                    onPressed: _executeTransfer,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.purple.shade700,
                      foregroundColor: Colors.white,
                      minimumSize: const Size.fromHeight(50),
                    ),
                    child: const Text('Proceed with Money Remittance'),
                  ),
          ],
        ),
      ),
    );
  }
}

/// 3. BBPS DASHBOARD
class BbpsDashboard extends StatefulWidget {
  const BbpsDashboard({Key? key}) : super(key: key);

  @override
  State<BbpsDashboard> createState() => _BbpsDashboardState();
}

class _BbpsDashboardState extends State<BbpsDashboard> {
  String _category = 'Electricity';
  final _consumerNo = TextEditingController();
  final _billerName = TextEditingController();
  final _amount = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Bharat Bill Payment System (BBPS)'),
        backgroundColor: Colors.cyan.shade800,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            DropdownButtonFormField<String>(
              value: _category,
              decoration: const InputDecoration(border: OutlineInputBorder(), labelText: 'Bill Category'),
              items: ['Electricity', 'Water', 'LPG Gas', 'Broadband', 'FASTag', 'Loan EMI']
                  .map((c) => DropdownMenuItem(value: c, child: Text(c)))
                  .toList(),
              onChanged: (val) => setState(() => _category = val!),
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _billerName,
              decoration: const InputDecoration(border: OutlineInputBorder(), labelText: 'Select Biller operator'),
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _consumerNo,
              decoration: const InputDecoration(border: OutlineInputBorder(), labelText: 'Consumer / Account ID'),
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _amount,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(border: OutlineInputBorder(), labelText: 'Bill Amount (₹)'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('BBPS transaction of ₹${_amount.text} completed!')),
                );
              },
              style: ElevatedButton.styleFrom(backgroundColor: Colors.cyan.shade800, foregroundColor: Colors.white, minimumSize: const Size.fromHeight(50)),
              child: const Text('Fetch & Pay Utility Bill'),
            ),
          ],
        ),
      ),
    );
  }
}

/// Helper RoundedCornerShape for older Flutter SDK compatibility in mock environments
ShapeBorder RoundedCornerShape(double radius) {
  return RoundedRectangleBorder(borderRadius: BorderRadius.circular(radius));
}
