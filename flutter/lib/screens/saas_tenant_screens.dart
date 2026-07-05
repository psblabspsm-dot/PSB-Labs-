import 'package:flutter/material.dart';

/// 1. TENANT LOGIN SCREEN
class TenantLoginScreen extends StatefulWidget {
  const TenantLoginScreen({Key? key}) : super(key: key);

  @override
  State<TenantLoginScreen> createState() => _TenantLoginScreenState();
}

class _TenantLoginScreenState extends State<TenantLoginScreen> {
  final _subdomainController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isResolving = false;
  String? _resolvedTenantName;

  @override
  void dispose() {
    _subdomainController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  void _resolveTenantSubdomain() async {
    if (_subdomainController.text.isEmpty) return;
    setState(() => _isResolving = true);
    await Future.delayed(const Duration(milliseconds: 1000));
    setState(() {
      _isResolving = false;
      _resolvedTenantName = '${_subdomainController.text.toUpperCase()} White-Label Solutions';
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0F172A), // Premium Dark Slate
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 36.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                padding: const EdgeInsets.all(16),
                decoration: const BoxDecoration(
                  color: Color(0xFF1E293B),
                  shape: BoxShape.circle,
                ),
                child: const Icon(Icons.hub, size: 50, color: Color(0xFF38BDF8)),
              ),
              const SizedBox(height: 16),
              const Text(
                'Surya SaaS Portal',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 28,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 0.5,
                ),
              ),
              const SizedBox(height: 8),
              const Text(
                'Multi-Tenant White-Label FinTech Gateway',
                style: TextStyle(color: Color(0xFF94A3B8), fontSize: 13),
              ),
              const SizedBox(height: 32),
              Card(
                color: const Color(0xFF1E293B),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                child: Padding(
                  padding: const EdgeInsets.all(20.0),
                  child: Column(
                    children: [
                      TextFormField(
                        controller: _subdomainController,
                        style: const TextStyle(color: Colors.white),
                        decoration: InputDecoration(
                          labelText: 'Organization Subdomain',
                          labelStyle: const TextStyle(color: Color(0xFF94A3B8)),
                          hintText: 'e.g. alpha, beta, main',
                          hintStyle: const TextStyle(color: Colors.white30),
                          prefixIcon: const Icon(Icons.dns, color: Color(0xFF38BDF8)),
                          suffixIcon: IconButton(
                            icon: const Icon(Icons.arrow_forward_sharp, color: Colors.white),
                            onPressed: _resolveTenantSubdomain,
                          ),
                          enabledBorder: const OutlineInputBorder(
                            borderSide: BorderSide(color: Color(0xFF334155)),
                          ),
                          focusedBorder: const OutlineInputBorder(
                            borderSide: BorderSide(color: Color(0xFF38BDF8)),
                          ),
                        ),
                      ),
                      if (_isResolving) ...[
                        const SizedBox(height: 12),
                        const LinearProgressIndicator(color: Color(0xFF38BDF8)),
                      ],
                      if (_resolvedTenantName != null) ...[
                        const SizedBox(height: 12),
                        Container(
                          padding: const EdgeInsets.all(8),
                          width: double.infinity,
                          decoration: BoxDecoration(
                            color: const Color(0xFF0F172A),
                            borderRadius: BorderRadius.circular(8),
                            border: Border.all(color: const Color(0xFF10B981)),
                          ),
                          child: Row(
                            children: [
                              const Icon(Icons.check_circle, color: Color(0xFF10B981), size: 18),
                              const SizedBox(width: 8),
                              Expanded(
                                child: Text(
                                  'Resolved: $_resolvedTenantName',
                                  style: const TextStyle(color: Color(0xFF10B981), fontSize: 11, fontWeight: FontWeight.bold),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _emailController,
                        style: const TextStyle(color: Colors.white),
                        decoration: const InputDecoration(
                          labelText: 'Registered Corporate Email',
                          labelStyle: TextStyle(color: Color(0xFF94A3B8)),
                          prefixIcon: Icon(Icons.alternate_email, color: Color(0xFF38BDF8)),
                          enabledBorder: OutlineInputBorder(borderSide: BorderSide(color: Color(0xFF334155))),
                          focusedBorder: OutlineInputBorder(borderSide: BorderSide(color: Color(0xFF38BDF8))),
                        ),
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _passwordController,
                        obscureText: true,
                        style: const TextStyle(color: Colors.white),
                        decoration: const InputDecoration(
                          labelText: 'Secured Password',
                          labelStyle: TextStyle(color: Color(0xFF94A3B8)),
                          prefixIcon: Icon(Icons.lock, color: Color(0xFF38BDF8)),
                          enabledBorder: OutlineInputBorder(borderSide: BorderSide(color: Color(0xFF334155))),
                          focusedBorder: OutlineInputBorder(borderSide: BorderSide(color: Color(0xFF38BDF8))),
                        ),
                      ),
                      const SizedBox(height: 24),
                      ElevatedButton(
                        onPressed: () {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(
                              content: Text('Logging into ${_subdomainController.text}.suryacredit.com...'),
                              backgroundColor: const Color(0xFF10B981),
                            ),
                          );
                        },
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF38BDF8),
                          foregroundColor: const Color(0xFF0F172A),
                          minimumSize: const Size.fromHeight(50),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                        ),
                        child: const Text('Authenticate & Unlock', style: TextStyle(fontWeight: FontWeight.bold)),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// 2. FRANCHISE PORTAL DASHBOARD
class FranchiseDashboardScreen extends StatelessWidget {
  const FranchiseDashboardScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Franchise Management Hub'),
        backgroundColor: const Color(0xFF0F172A),
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Franchise Header Banner
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [Color(0xFF1E3A8A), Color(0xFF1D4ED8)],
                ),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                children: [
                  const Icon(Icons.account_balance, size: 48, color: Colors.white),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: const [
                        Text(
                          'Surya Regional Franchise North',
                          style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 18),
                        ),
                        SizedBox(height: 4),
                        Text(
                          'Primary node for retail territory coordination and ledger clearances.',
                          style: TextStyle(color: Colors.white70, fontSize: 11),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),
            const Text('Territory Operations Overview', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            const SizedBox(height: 12),
            // Stat Cards Grid
            GridView.count(
              crossAxisCount: 2,
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              mainAxisSpacing: 12,
              crossAxisSpacing: 12,
              childAspectRatio: 1.4,
              children: [
                _buildStatCard(
                  title: 'Franchise Wallet',
                  value: '₹8,50,000.00',
                  icon: Icons.account_balance_wallet,
                  color: Colors.emerald,
                ),
                _buildStatCard(
                  title: 'Allocated Credit',
                  value: '₹20,000.00',
                  icon: Icons.shield,
                  color: Colors.amber,
                ),
                _buildStatCard(
                  title: 'Active Kiosks',
                  value: '45 Retailers',
                  icon: Icons.storefront,
                  color: Colors.blue,
                ),
                _buildStatCard(
                  title: 'This Month Rev',
                  value: '₹1,24,500.00',
                  icon: Icons.trending_up,
                  color: Colors.indigo,
                ),
              ],
            ),
            const SizedBox(height: 24),
            const Text('Associated Retail Merchants', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            const SizedBox(height: 12),
            // Retailers list
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: 3,
              itemBuilder: (context, index) {
                final names = ['Rao Digital Hub', 'Kiosk Bangalore North', 'Pai Retail Agency'];
                final balances = ['₹42,500', '₹18,200', '₹89,400'];
                final sales = ['₹1,45,000', '₹84,000', '₹4,90,000'];
                return Card(
                  margin: const EdgeInsets.only(bottom: 8),
                  color: Colors.white,
                  child: ListTile(
                    leading: const CircleAvatar(
                      backgroundColor: Color(0xFFF1F5F9),
                      child: Icon(Icons.person, color: Color(0xFF475569)),
                    ),
                    title: Text(names[index], style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    subtitle: Text('Monthly Trade Volume: ${sales[index]}', style: const TextStyle(fontSize: 11)),
                    trailing: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        Text(balances[index], style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.emerald)),
                        const Text('Wallet Bal', style: TextStyle(fontSize: 9, color: Colors.grey)),
                      ],
                    ),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatCard({required String title, required String value, required IconData icon, required Color color}) {
    return Card(
      color: Colors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(12.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Icon(icon, color: color, size: 20),
                const Icon(Icons.arrow_right_alt, color: Colors.grey, size: 16),
              ],
            ),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(value, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                Text(title, style: const TextStyle(color: Colors.grey, fontSize: 10)),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

/// 3. BRANDING SETTINGS SCREEN
class BrandingSettingsScreen extends StatefulWidget {
  const BrandingSettingsScreen({Key? key}) : super(key: key);

  @override
  State<BrandingSettingsScreen> createState() => _BrandingSettingsScreenState();
}

class _BrandingSettingsScreenState extends State<BrandingSettingsScreen> {
  final _companyController = TextEditingController(text: 'Surya Credit Solutions');
  final _fontController = TextEditingController(text: 'Space Grotesk');
  final _termsController = TextEditingController(text: 'https://suryacredit.com/terms');
  final _supportMailController = TextEditingController(text: 'support@suryacredit.com');
  String _selectedPrimaryColor = '#0F172A';

  @override
  void dispose() {
    _companyController.dispose();
    _fontController.dispose();
    _termsController.dispose();
    _supportMailController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Tenant Branding Suite'),
        backgroundColor: const Color(0xFF0F172A),
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Corporate Custom Identity', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
            const SizedBox(height: 4),
            const Text('Personalize the multi-tenant look-and-feel of your white-labeled application.', style: TextStyle(fontSize: 11, color: Colors.grey)),
            const SizedBox(height: 20),
            TextFormField(
              controller: _companyController,
              decoration: const InputDecoration(
                labelText: 'Company Title (Splash/AppBar)',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.business),
              ),
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _fontController,
              decoration: const InputDecoration(
                labelText: 'Primary Font Family name',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.font_download),
              ),
            ),
            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              value: _selectedPrimaryColor,
              decoration: const InputDecoration(
                labelText: 'Brand Colors Theme Base',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.palette),
              ),
              items: const [
                DropdownMenuItem(value: '#0F172A', child: Text('Deep Slate Black (#0F172A)')),
                DropdownMenuItem(value: '#1E3A8A', child: Text('Corporate Blue (#1E3A8A)')),
                DropdownMenuItem(value: '#10B981', child: Text('Emerald Mint (#10B981)')),
                DropdownMenuItem(value: '#8B5CF6', child: Text('Royal Purple (#8B5CF6)')),
              ],
              onChanged: (val) {
                setState(() {
                  _selectedPrimaryColor = val!;
                });
              },
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _termsController,
              decoration: const InputDecoration(
                labelText: 'Terms & Conditions Hyperlink',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.gavel),
              ),
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _supportMailController,
              decoration: const InputDecoration(
                labelText: 'Support Contact Email Desk',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.contact_mail),
              ),
            ),
            const SizedBox(height: 24),
            ElevatedButton(
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('White-label branding layout compiled and saved successfully!')),
                );
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0F172A),
                foregroundColor: Colors.white,
                minimumSize: const Size.fromHeight(50),
              ),
              child: const Text('Apply Changes across ecosystem', style: TextStyle(fontWeight: FontWeight.bold)),
            ),
          ],
        ),
      ),
    );
  }
}

/// 4. SUBSCRIPTION MANAGEMENT SCREEN
class SubscriptionManagementScreen extends StatelessWidget {
  const SubscriptionManagementScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Subscription Quotas'),
        backgroundColor: const Color(0xFF0F172A),
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Current Plan Card
            Container(
              padding: const EdgeInsets.all(20),
              width: double.infinity,
              decoration: BoxDecoration(
                color: const Color(0xFF0F172A),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Active System Contract', style: TextStyle(color: Colors.white60, fontSize: 11)),
                  const SizedBox(height: 4),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: const [
                      Text('PROFESSIONAL PLAN', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 20)),
                      Chip(
                        label: Text('ACTIVE', style: TextStyle(fontSize: 10, color: Colors.emerald, fontWeight: FontWeight.bold)),
                        backgroundColor: Color(0xFF064E3B),
                      ),
                    ],
                  ),
                  const Divider(color: Colors.white12, height: 20),
                  const Text('Cost: ₹499.00 / month', style: TextStyle(color: Colors.white)),
                  const Text('Renews On: August 15, 2026 (Auto renewal ON)', style: TextStyle(color: Colors.white55, fontSize: 11)),
                ],
              ),
            ),
            const SizedBox(height: 24),
            const Text('SaaS Subscription Pricing Models', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            const SizedBox(height: 12),
            _buildPlanCard(
              title: 'Starter Package',
              price: '₹199 / month',
              features: ['Up to 5 Users', 'Max ₹1,00,000 Transacts/mo', 'Email-only supportdesk'],
              isSelected: false,
            ),
            _buildPlanCard(
              title: 'Professional Suite',
              price: '₹499 / month',
              features: ['Up to 25 Users', 'Max ₹10,00,000 Transacts/mo', 'Priority API routing support', 'Custom white label branding'],
              isSelected: true,
            ),
            _buildPlanCard(
              title: 'Enterprise Unlimited',
              price: '₹999 / month',
              features: ['Unlimited Users', 'Infinite throughput volume', '24/7 Dedicated account manager', 'AI Analytics predictive engine'],
              isSelected: false,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPlanCard({required String title, required String price, required List<String> features, required bool isSelected}) {
    return Card(
      color: Colors.white,
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(
          color: isSelected ? const Color(0xFF38BDF8) : Colors.transparent,
          width: 2,
        ),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                Text(price, style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF1E3A8A))),
              ],
            ),
            const Divider(height: 20),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: features.map((f) => Row(
                children: [
                  const Icon(Icons.check, size: 14, color: Colors.emerald),
                  const SizedBox(width: 8),
                  Text(f, style: const TextStyle(fontSize: 12, color: Colors.grey)),
                ],
              )).toList(),
            ),
            if (!isSelected) ...[
              const SizedBox(height: 12),
              ElevatedButton(
                onPressed: () {},
                style: ElevatedButton.styleFrom(
                  minimumSize: const Size.fromHeight(36),
                  backgroundColor: const Color(0xFFF1F5F9),
                  foregroundColor: const Color(0xFF0F172A),
                  elevation: 0,
                ),
                child: const Text('Upgrade To This Plan', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
              ),
            ]
          ],
        ),
      ),
    );
  }
}

/// 5. FEATURE MANAGEMENT SCREEN
class FeatureManagementScreen extends StatefulWidget {
  const FeatureManagementScreen({Key? key}) : super(key: key);

  @override
  State<FeatureManagementScreen> createState() => _FeatureManagementScreenState();
}

class _FeatureManagementScreenState extends State<FeatureManagementScreen> {
  // Feature flags toggles map
  final Map<String, bool> _features = {
    'B2B Wholesale Hardware Marketplace': true,
    'Merchant Ledger / Wallet Suite': true,
    'Allocated Credit Buffer Line': true,
    'Aadhaar Enabled Payments (AEPS)': true,
    'Domestic Money Transfer (DMT)': false,
    'Bharat Bill Payment System (BBPS)': true,
    'Recharge and Utilities Service': true,
    'AI-powered Fraud Predictive Engine': false,
  };

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Feature Flags Desk'),
        backgroundColor: const Color(0xFF0F172A),
        foregroundColor: Colors.white,
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Text('Workspace Module Controls', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
          const SizedBox(height: 4),
          const Text('Enable or disable service portals instantaneously for this white label tenant.', style: TextStyle(fontSize: 11, color: Colors.grey)),
          const SizedBox(height: 16),
          ..._features.keys.map((f) => Card(
            margin: const EdgeInsets.only(bottom: 8),
            color: Colors.white,
            child: SwitchListTile(
              title: Text(f, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
              value: _features[f]!,
              onChanged: (val) {
                setState(() {
                  _features[f] = val;
                });
              },
            ),
          )).toList(),
        ],
      ),
    );
  }
}

/// 6. TENANT PROFILE SCREEN
class TenantProfileScreen extends StatelessWidget {
  const TenantProfileScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Corporate Profile'),
        backgroundColor: const Color(0xFF0F172A),
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            const CircleAvatar(
              radius: 45,
              backgroundImage: NetworkImage('https://images.unsplash.com/photo-1599305445671-ac291c95aba9?q=80&w=300'),
            ),
            const SizedBox(height: 12),
            const Text('Surya Credit Solutions Primary', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
            const Text('Subdomain: main.suryacredit.com', style: TextStyle(color: Colors.grey, fontSize: 12)),
            const SizedBox(height: 20),
            _buildProfileRow('SaaS Identification', 'tenant-default'),
            _buildProfileRow('Domain Mapping', 'suryacredit.com'),
            _buildProfileRow('Corporate Structure', 'Central Parent Node'),
            _buildProfileRow('Active Users Accounted', '1,420 Users'),
            _buildProfileRow('Aggregator Channels Mapped', 'Razorpay, Cashfree, Pine Labs'),
            _buildProfileRow('Regulatory compliance status', 'Verified & Audit Compliant'),
          ],
        ),
      ),
    );
  }

  Widget _buildProfileRow(String label, String value) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      color: Colors.white,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(label, style: const TextStyle(color: Colors.grey, fontSize: 12)),
            Text(value, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
          ],
        ),
      ),
    );
  }
}

/// 7. COMPANY SYSTEM SETTINGS SCREEN
class CompanySettingsScreen extends StatefulWidget {
  const CompanySettingsScreen({Key? key}) : super(key: key);

  @override
  State<CompanySettingsScreen> createState() => _CompanySettingsScreenState();
}

class _CompanySettingsScreenState extends State<CompanySettingsScreen> {
  bool _kycAutoApprove = true;
  bool _creditAutoIncrease = true;
  bool _reportsAutoGenerate = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Tenant Configuration Settings'),
        backgroundColor: const Color(0xFF0F172A),
        foregroundColor: Colors.white,
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Text('Workflow Execution Rules', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
          const SizedBox(height: 4),
          const Text('Modify background operational flows and automation setups.', style: TextStyle(fontSize: 11, color: Colors.grey)),
          const SizedBox(height: 16),
          SwitchListTile(
            title: const Text('Instant KYC Auto-Verification', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
            subtitle: const Text('Verify merchant PAN and Aadhaar records automatically without manual compliance checks.', style: TextStyle(fontSize: 10)),
            value: _kycAutoApprove,
            onChanged: (val) => setState(() => _kycAutoApprove = val),
          ),
          SwitchListTile(
            title: const Text('Auto-Increase Pre-approved Credit', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
            subtitle: const Text('Upgrade credit limits by 20% on timely payment of outstanding dues.', style: TextStyle(fontSize: 10)),
            value: _creditAutoIncrease,
            onChanged: (val) => setState(() => _creditAutoIncrease = val),
          ),
          SwitchListTile(
            title: const Text('Daily BI Report Generation', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
            subtitle: const Text('Calculate financial volumes, profit logs, and distribute auto reports.', style: TextStyle(fontSize: 10)),
            value: _reportsAutoGenerate,
            onChanged: (val) => setState(() => _reportsAutoGenerate = val),
          ),
          const Divider(height: 40),
          const Text('Disaster Recovery Backup', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
          const SizedBox(height: 8),
          ElevatedButton.icon(
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('SaaS State Isolated Backup Completed and Synced.')),
              );
            },
            icon: const Icon(Icons.backup),
            label: const Text('Take Snapshot BackupNow'),
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF0F172A),
              foregroundColor: Colors.white,
            ),
          )
        ],
      ),
    );
  }
}
