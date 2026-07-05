import 'package:flutter/material.dart';

// --- DATA MODELS FOR THE SECURITY CENTER ---
class SocAlert {
  final String id;
  final String eventType; // THREAT, INTRUSION, ANOMALY, FAILED_LOGIN, WALLET_FRAUD, CREDIT_ABUSE
  final String severity; // LOW, MEDIUM, HIGH, CRITICAL
  String status; // OPEN, UNDER_INVESTIGATION, RESOLVED
  final String sourceIp;
  final String description;
  final String deviceName;
  final DateTime createdAt;
  String? resolution;

  SocAlert({
    required this.id,
    required this.eventType,
    required this.severity,
    required this.status,
    required this.sourceIp,
    required this.description,
    required this.deviceName,
    required this.createdAt,
    this.resolution,
  });
}

class UserSession {
  final String id;
  final String email;
  final String ipAddress;
  final String location;
  final String deviceName;
  final bool mfaVerified;
  bool isActive;
  final DateTime createdAt;

  UserSession({
    required this.id,
    required this.email,
    required this.ipAddress,
    required this.location,
    required this.deviceName,
    required this.mfaVerified,
    required this.isActive,
    required this.createdAt,
  });
}

class RegisteredDevice {
  final String id;
  final String deviceName;
  final String deviceType; // MOBILE, TABLET, DESKTOP
  final String osVersion;
  final String fingerprint;
  String status; // ACTIVE, REVOKED, BLOCKED
  bool isTrusted;
  final DateTime registeredAt;

  RegisteredDevice({
    required this.id,
    required this.deviceName,
    required this.deviceType,
    required this.osVersion,
    required this.fingerprint,
    required this.status,
    required this.isTrusted,
    required this.registeredAt,
  });
}

class ApiKeyModel {
  final String id;
  final String name;
  final String keyPrefix;
  final String scopes;
  final String? ipWhitelist;
  final int rateLimitRps;
  bool isActive;
  final DateTime createdAt;

  ApiKeyModel({
    required this.id,
    required this.name,
    required this.keyPrefix,
    required this.scopes,
    this.ipWhitelist,
    required this.rateLimitRps,
    required this.isActive,
    required this.createdAt,
  });
}

class SystemMetric {
  final String name;
  final double value;
  final String unit;
  final Color color;

  SystemMetric({
    required this.name,
    required this.value,
    required this.unit,
    required this.color,
  });
}

class BackupRecord {
  final String id;
  final String backupType; // FULL, INCREMENTAL
  String status; // IN_PROGRESS, COMPLETED, FAILED
  final int fileSizeMb;
  final bool verified;
  final String storagePath;
  final DateTime createdAt;

  BackupRecord({
    required this.id,
    required this.backupType,
    required this.status,
    required this.fileSizeMb,
    required this.verified,
    required this.storagePath,
    required this.createdAt,
  });
}

class DrDrillResult {
  final String scenario;
  final double rtoSeconds; // Recovery Time Objective achieved
  final double rpoSeconds; // Recovery Point Objective achieved
  final String status;
  final String tester;
  final DateTime testedAt;

  DrDrillResult({
    required this.scenario,
    required this.rtoSeconds,
    required this.rpoSeconds,
    required this.status,
    required this.tester,
    required this.testedAt,
  });
}

// --- MAIN WIDGET ---
class SecurityOpsScreens extends StatefulWidget {
  const SecurityOpsScreens({Key? key}) : super(key: key);

  @override
  State<SecurityOpsScreens> createState() => _SecurityOpsScreensState();
}

class _SecurityOpsScreensState extends State<SecurityOpsScreens> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  // --- COMPREHENSIVE INITIAL SEED DATA ---
  final List<SocAlert> _alerts = [
    SocAlert(
      id: 'AL-1001',
      eventType: 'WALLET_FRAUD',
      severity: 'CRITICAL',
      status: 'OPEN',
      sourceIp: '185.220.101.4',
      description: 'Suspicious immediate wallet cash out following high-value AePS credit transaction.',
      deviceName: 'Redmi Note 12',
      createdAt: DateTime.now().subtract(const Duration(minutes: 15)),
    ),
    SocAlert(
      id: 'AL-1002',
      eventType: 'FAILED_LOGIN',
      severity: 'LOW',
      status: 'RESOLVED',
      sourceIp: '103.45.201.12',
      description: '3 consecutive failed login attempts on retailer subramanyampaipuri14@gmail.com',
      deviceName: 'Chrome / Windows',
      createdAt: DateTime.now().subtract(const Duration(hours: 2)),
      resolution: 'IP temporary lockout applied automatically for 15 mins. Session re-verified.',
    ),
    SocAlert(
      id: 'AL-1003',
      eventType: 'API_ABUSE',
      severity: 'HIGH',
      status: 'UNDER_INVESTIGATION',
      sourceIp: '49.206.12.89',
      description: 'Rate limit tripped on DMT transaction API. 450 requests/sec detected.',
      deviceName: 'External Client Core',
      createdAt: DateTime.now().subtract(const Duration(hours: 4)),
    ),
  ];

  final List<UserSession> _sessions = [
    UserSession(
      id: 'SESS-901',
      email: 'subramanyampaipuri14@gmail.com',
      ipAddress: '157.48.91.22',
      location: 'Bengaluru, Karnataka',
      deviceName: 'OnePlus 11 Pro',
      mfaVerified: true,
      isActive: true,
      createdAt: DateTime.now().subtract(const Duration(hours: 1)),
    ),
    UserSession(
      id: 'SESS-902',
      email: 'sales-admin@suryacredit.com',
      ipAddress: '103.20.144.18',
      location: 'Mumbai, Maharashtra',
      deviceName: 'ThinkPad X1 Carbon',
      mfaVerified: true,
      isActive: true,
      createdAt: DateTime.now().subtract(const Duration(hours: 3)),
    ),
  ];

  final List<RegisteredDevice> _devices = [
    RegisteredDevice(
      id: 'DEV-001',
      deviceName: 'OnePlus 11 Pro',
      deviceType: 'MOBILE',
      osVersion: 'Android 13',
      fingerprint: 'fg_oneplus_11_829a',
      status: 'ACTIVE',
      isTrusted: true,
      registeredAt: DateTime.now().subtract(const Duration(days: 45)),
    ),
    RegisteredDevice(
      id: 'DEV-002',
      deviceName: 'ThinkPad X1 Carbon',
      deviceType: 'DESKTOP',
      osVersion: 'Windows 11 Pro',
      fingerprint: 'fg_thinkpad_x1_992c',
      status: 'ACTIVE',
      isTrusted: true,
      registeredAt: DateTime.now().subtract(const Duration(days: 90)),
    ),
    RegisteredDevice(
      id: 'DEV-003',
      deviceName: 'Unknown Emulator ID',
      deviceType: 'MOBILE',
      osVersion: 'Android 9',
      fingerprint: 'fg_emu_suspicious_8b',
      status: 'REVOKED',
      isTrusted: false,
      registeredAt: DateTime.now().subtract(const Duration(days: 5)),
    ),
  ];

  final List<ApiKeyModel> _apiKeys = [
    ApiKeyModel(
      id: 'KEY-001',
      name: 'Retail Gateway Live Integration',
      keyPrefix: 'sc_live_f82',
      scopes: 'wallet:read,wallet:write,transaction:init',
      ipWhitelist: '13.125.10.41, 52.78.20.89',
      rateLimitRps: 50,
      isActive: true,
      createdAt: DateTime.now().subtract(const Duration(days: 30)),
    ),
    ApiKeyModel(
      id: 'KEY-002',
      name: 'Staging Integration Key',
      keyPrefix: 'sc_test_b12',
      scopes: 'wallet:read,transaction:init',
      rateLimitRps: 10,
      isActive: true,
      createdAt: DateTime.now().subtract(const Duration(days: 10)),
    ),
  ];

  final List<BackupRecord> _backups = [
    BackupRecord(
      id: 'BAK-1001',
      backupType: 'FULL',
      status: 'COMPLETED',
      fileSizeMb: 437,
      verified: true,
      storagePath: 's3://surya-backups/fy2026/full_170321.enc',
      createdAt: DateTime.now().subtract(const Duration(days: 1)),
    ),
    BackupRecord(
      id: 'BAK-1002',
      backupType: 'INCREMENTAL',
      status: 'COMPLETED',
      fileSizeMb: 12,
      verified: true,
      storagePath: 's3://surya-backups/fy2026/inc_170325.enc',
      createdAt: DateTime.now().subtract(const Duration(hours: 6)),
    ),
  ];

  final List<DrDrillResult> _drDrills = [
    DrDrillResult(
      scenario: 'DATABASE_FAILOVER',
      rtoSeconds: 4.2, // 4.2s Recovery Time Objective achieved
      rpoSeconds: 0, // 0s data loss (Active-Active replication)
      status: 'SUCCESS',
      tester: 'devops-chief@suryacredit.com',
      testedAt: DateTime.now().subtract(const Duration(days: 30)),
    ),
    DrDrillResult(
      scenario: 'REDIS_CACHE_RECOVERY',
      rtoSeconds: 1.8,
      rpoSeconds: 0,
      status: 'SUCCESS',
      tester: 'devops-chief@suryacredit.com',
      testedAt: DateTime.now().subtract(const Duration(days: 12)),
    ),
  ];

  // --- CONTROLLERS ---
  final _keyNameCtrl = TextEditingController();
  final _keyWhitelistCtrl = TextEditingController();
  final _keyScopesCtrl = TextEditingController(text: 'wallet:read,transaction:init');
  final _keyRpsCtrl = TextEditingController(text: '20');

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 5, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    _keyNameCtrl.dispose();
    _keyWhitelistCtrl.dispose();
    _keyScopesCtrl.dispose();
    _keyRpsCtrl.dispose();
    super.dispose();
  }

  // --- SECURITY CENTER ACTIONS ---
  void _resolveAlert(SocAlert alert, String resolution) {
    setState(() {
      alert.status = 'RESOLVED';
      alert.resolution = resolution;
    });
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('Security Incident ${alert.id} resolved successfully.'),
        backgroundColor: Colors.green.shade800,
      ),
    );
  }

  void _triggerBackup(String type) {
    final newId = 'BAK-${1000 + _backups.length + 1}';
    final newBackup = BackupRecord(
      id: newId,
      backupType: type,
      status: 'IN_PROGRESS',
      fileSizeMb: 0,
      verified: false,
      storagePath: 'Processing AWS KMS symmetric cipher...',
      createdAt: DateTime.now(),
    );

    setState(() {
      _backups.insert(0, newBackup);
    });

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('manual $type Backup initialized inside secure backup bucket.'),
        backgroundColor: const Color(0xFF0F172A),
      ),
    );

    // Simulate completion
    Future.delayed(const Duration(seconds: 3), () {
      if (mounted) {
        setState(() {
          final idx = _backups.indexWhere((b) => b.id == newId);
          if (idx >= 0) {
            _backups[idx] = BackupRecord(
              id: newId,
              backupType: type,
              status: 'COMPLETED',
              fileSizeMb: type == 'FULL' ? 442 : 15,
              verified: true,
              storagePath: 's3://surya-backups/fy2026/man_${type.toLowerCase()}_${DateTime.now().millisecondsSinceEpoch}.enc',
              createdAt: DateTime.now(),
            );
          }
        });
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Symmetric AES-256 backup $newId verified & written successfully.'),
            backgroundColor: Colors.green.shade800,
          ),
        );
      }
    });
  }

  void _triggerDrDrill(String scenario) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('Simulated DR failover drill initiated: $scenario'),
        backgroundColor: Colors.blue.shade900,
      ),
    );

    Future.delayed(const Duration(seconds: 4), () {
      if (mounted) {
        setState(() {
          _drDrills.insert(
            0,
            DrDrillResult(
              scenario: scenario,
              rtoSeconds: scenario == 'DATABASE_FAILOVER' ? 3.9 : 1.2,
              rpoSeconds: 0,
              status: 'SUCCESS',
              tester: 'subramanyampaipuri14@gmail.com',
              testedAt: DateTime.now(),
            ),
          );
        });
        showDialog(
          context: context,
          builder: (context) => AlertDialog(
            title: const Text('DR Failover Test Successful'),
            content: Text(
              'Continuous replication failover simulated successfully.\n\n'
              '• achieved RTO: ${scenario == 'DATABASE_FAILOVER' ? "3.9 seconds" : "1.2 seconds"}\n'
              '• achieved RPO: 0 seconds (No data loss)\n'
              '• Quorum state verification: OK\n\n'
              'Result written to security compliance ledger.',
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('Close'),
              ),
            ],
          ),
        );
      }
    });
  }

  void _issueApiKey() {
    final name = _keyNameCtrl.text;
    final rps = int.tryParse(_keyRpsCtrl.text) ?? 20;
    if (name.isEmpty) return;

    final newKey = ApiKeyModel(
      id: 'KEY-00${_apiKeys.length + 1}',
      name: name,
      keyPrefix: 'sc_live_${crypto.randomUUID().substring(0, 3)}',
      scopes: _keyScopesCtrl.text,
      ipWhitelist: _keyWhitelistCtrl.text.isEmpty ? null : _keyWhitelistCtrl.text,
      rateLimitRps: rps,
      isActive: true,
      createdAt: DateTime.now(),
    );

    setState(() {
      _apiKeys.insert(0, newKey);
    });

    Navigator.pop(context);
    _keyNameCtrl.clear();
    _keyWhitelistCtrl.clear();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('API Credentials Issued'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Issuer completed signature safely. Symmetric key:'),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(12),
              color: const Color(0xFFF1F5F9),
              child: Text(
                '${newKey.keyPrefix}_${crypto.randomUUID().replaceAll("-", "")}',
                style: const TextStyle(fontFamily: 'monospace', fontWeight: FontWeight.bold, fontSize: 12),
              ),
            ),
            const SizedBox(height: 8),
            const Text('⚠️ WARNING: Copy the key now. It will not be shown again.', style: TextStyle(color: Colors.red, fontSize: 11, fontWeight: FontWeight.bold)),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('I have stored it safely'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Surya SOC & Security Operations',
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
            Tab(text: 'SOC Alerts', icon: Icon(Icons.shield_outlined)),
            Tab(text: 'Sessions & Devices', icon: Icon(Icons.devices_outlined)),
            Tab(text: 'B2B API Keys', icon: Icon(Icons.vpn_key_outlined)),
            Tab(text: 'Observability', icon: Icon(Icons.monitor_heart_outlined)),
            Tab(text: 'Backup & DR', icon: Icon(Icons.cloud_sync_outlined)),
          ],
        ),
      ),
      body: Container(
        color: const Color(0xFFF8FAFC),
        child: TabBarView(
          controller: _tabController,
          children: [
            _buildSocAlertsTab(),
            _buildDevicesTab(),
            _buildApiKeysTab(),
            _buildObservabilityTab(),
            _buildBackupDrTab(),
          ],
        ),
      ),
    );
  }

  // 1. SOC ALERTS TAB
  Widget _buildSocAlertsTab() {
    return ListView(
      padding: const EdgeInsets.all(24.0),
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: const [
            Text(
              'Security Operations Center (SOC) Alerts',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
            ),
            Chip(
              label: Text('ACTIVE AUDITING'),
              backgroundColor: Color(0xFFFEE2E2),
              labelStyle: TextStyle(color: Color(0xFF991B1B), fontWeight: FontWeight.bold, fontSize: 11),
            ),
          ],
        ),
        const SizedBox(height: 16),
        ..._alerts.map((al) {
          final isCritical = al.severity == 'CRITICAL';
          final isHigh = al.severity == 'HIGH';
          final isOpen = al.status == 'OPEN';
          
          Color severityColor = Colors.blue;
          if (isCritical) severityColor = Colors.red.shade900;
          else if (isHigh) severityColor = Colors.orange.shade800;

          return Card(
            elevation: 0,
            margin: const EdgeInsets.only(bottom: 16),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: BorderSide(color: Colors.grey.shade200)),
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
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                            decoration: BoxDecoration(color: severityColor.withOpacity(0.1), borderRadius: BorderRadius.circular(6)),
                            child: Text(
                              al.severity,
                              style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: severityColor),
                            ),
                          ),
                          const SizedBox(width: 8),
                          Text(al.id, style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.grey, fontSize: 12)),
                        ],
                      ),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(
                          color: al.status == 'RESOLVED' ? Colors.green.shade50 : (al.status == 'OPEN' ? Colors.red.shade50 : Colors.blue.shade50),
                          borderRadius: BorderRadius.circular(6),
                        ),
                        child: Text(
                          al.status,
                          style: TextStyle(
                            fontSize: 10, 
                            fontWeight: FontWeight.bold, 
                            color: al.status == 'RESOLVED' ? Colors.green.shade800 : (al.status == 'OPEN' ? Colors.red.shade800 : Colors.blue.shade800),
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  Text(
                    al.description,
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15, color: Color(0xFF0F172A)),
                  ),
                  const SizedBox(height: 8),
                  Text('Trigger Source IP: ${al.sourceIp} | Device/Entity: ${al.deviceName}', style: const TextStyle(fontSize: 12, color: Colors.grey)),
                  if (al.resolution != null) ...[
                    const Divider(height: 24),
                    Text('Resolution remediation report:', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: Colors.green.shade900)),
                    const SizedBox(height: 4),
                    Text(al.resolution!, style: const TextStyle(fontSize: 12, color: Colors.grey)),
                  ],
                  if (isOpen) ...[
                    const Divider(height: 24),
                    Row(
                      children: [
                        Expanded(
                          child: ElevatedButton(
                            onPressed: () {
                              _showRemediationDialog(al);
                            },
                            style: ElevatedButton.styleFrom(
                              backgroundColor: const Color(0xFF0F172A),
                              foregroundColor: Colors.white,
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                            ),
                            child: const Text('Resolve Incident'),
                          ),
                        ),
                      ],
                    ),
                  ],
                ],
              ),
            ),
          );
        }).toList(),
      ],
    );
  }

  // 2. SESSIONS & DEVICES TAB
  Widget _buildDevicesTab() {
    return ListView(
      padding: const EdgeInsets.all(24.0),
      children: [
        const Text(
          'Active Login Sessions (IAM Policy)',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
        ),
        const SizedBox(height: 12),
        ..._sessions.map((sess) => Card(
          elevation: 0,
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: BorderSide(color: Colors.grey.shade200)),
          child: ListTile(
            title: Text(sess.email, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
            subtitle: Text('${sess.deviceName} • ${sess.ipAddress} (${sess.location})', style: const TextStyle(fontSize: 12)),
            trailing: sess.isActive 
              ? ElevatedButton(
                  onPressed: () {
                    setState(() {
                      sess.isActive = false;
                    });
                    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Session revoked successfully.')));
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.red.shade50,
                    foregroundColor: Colors.red.shade900,
                    elevation: 0,
                  ),
                  child: const Text('Terminate', style: TextStyle(fontSize: 11)),
                )
              : const Text('Revoked', style: TextStyle(color: Colors.grey, fontSize: 12)),
          ),
        )).toList(),

        const SizedBox(height: 32),
        const Text(
          'Hardware Devices Register',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
        ),
        const SizedBox(height: 12),
        ..._devices.map((dev) => Card(
          elevation: 0,
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: BorderSide(color: Colors.grey.shade200)),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(dev.deviceName, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                    Text('${dev.deviceType} • ${dev.osVersion}', style: const TextStyle(fontSize: 12, color: Colors.grey)),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                          decoration: BoxDecoration(
                            color: dev.status == 'ACTIVE' ? Colors.green.shade50 : Colors.red.shade50,
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            dev.status,
                            style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: dev.status == 'ACTIVE' ? Colors.green : Colors.red),
                          ),
                        ),
                        const SizedBox(width: 8),
                        if (dev.isTrusted)
                          const Chip(
                            label: Text('TRUSTED'),
                            backgroundColor: Color(0xFFF0FDF4),
                            labelStyle: TextStyle(color: Colors.green, fontSize: 9, fontWeight: FontWeight.bold),
                          ),
                      ],
                    ),
                  ],
                ),
                Row(
                  children: [
                    Switch(
                      value: dev.isTrusted,
                      onChanged: dev.status == 'ACTIVE' ? (val) {
                        setState(() {
                          dev.isTrusted = val;
                        });
                      } : null,
                    ),
                    IconButton(
                      icon: const Icon(Icons.delete_outline, color: Colors.red),
                      onPressed: dev.status == 'ACTIVE' ? () {
                        setState(() {
                          dev.status = 'REVOKED';
                          dev.isTrusted = false;
                        });
                        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Device ${dev.deviceName} revoked and blocked.')));
                      } : null,
                    ),
                  ],
                ),
              ],
            ),
          ),
        )).toList(),
      ],
    );
  }

  // 3. API KEYS TAB
  Widget _buildApiKeysTab() {
    return ListView(
      padding: const EdgeInsets.all(24.0),
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'Developer API Access Keys',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
            ),
            ElevatedButton.icon(
              onPressed: _showAddKeyDialog,
              icon: const Icon(Icons.add),
              label: const Text('Issue Key'),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0F172A),
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        ..._apiKeys.map((key) => Card(
          elevation: 0,
          margin: const EdgeInsets.only(bottom: 16),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: BorderSide(color: Colors.grey.shade200)),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(key.name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                    Switch(
                      value: key.isActive,
                      onChanged: (val) {
                        setState(() {
                          key.isActive = val;
                        });
                      },
                    ),
                  ],
                ),
                Text('Prefix: ${key.keyPrefix}*****', style: const TextStyle(fontFamily: 'monospace', color: Colors.grey, fontSize: 13)),
                const SizedBox(height: 8),
                Wrap(
                  spacing: 6,
                  runSpacing: 4,
                  children: key.scopes.split(',').map((s) => Chip(
                    label: Text(s),
                    backgroundColor: const Color(0xFFF1F5F9),
                    labelStyle: const TextStyle(fontSize: 10, color: Color(0xFF475569)),
                  )).toList(),
                ),
                const SizedBox(height: 8),
                Text(
                  'IP Whitelist: ${key.ipWhitelist ?? "Any IP Address Allowed (External)"} | Rate Limit: ${key.rateLimitRps} RPS',
                  style: const TextStyle(fontSize: 11, color: Colors.grey),
                ),
              ],
            ),
          ),
        )).toList(),
      ],
    );
  }

  // 4. OBSERVABILITY TAB
  Widget _buildObservabilityTab() {
    return ListView(
      padding: const EdgeInsets.all(24.0),
      children: [
        const Text(
          'Real-time Gateway Telemetry Monitoring',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
        ),
        const SizedBox(height: 16),
        GridView.count(
          crossAxisCount: 2,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisSpacing: 16,
          mainAxisSpacing: 16,
          childAspectRatio: 1.4,
          children: [
            _buildMetricCard('CPU LOAD (API)', 32, '%', Colors.blue, 'Normal range (30-55%)'),
            _buildMetricCard('MEMORY USAGE', 68, '%', Colors.purple, 'Secure pool (60-75%)'),
            _buildMetricCard('GATEWAY LATENCY', 42, 'ms', Colors.green, 'Excellent speed (<100ms)'),
            _buildMetricCard('JOB QUEUE DEPTH', 2, 'jobs', Colors.orange, 'Zero pipeline lag'),
          ],
        ),
        const SizedBox(height: 32),
        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16), side: BorderSide(color: Colors.grey.shade200)),
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Gateway Heartbeat Health Auditing', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                const Divider(height: 24),
                _buildHeartbeatRow('Surya core B2B Gateway API', 'HEALTHY (99.98% up)', Colors.green),
                _buildHeartbeatRow('AePS (Aadhaar Payments Gateway)', 'HEALTHY', Colors.green),
                _buildHeartbeatRow('DMT Ledger IMPS pool', 'HEALTHY', Colors.green),
                _buildHeartbeatRow('Redis Shared Sessions Cluster', 'HEALTHY', Colors.green),
                _buildHeartbeatRow('KMS Encryption Storage server', 'HEALTHY', Colors.green),
              ],
            ),
          ),
        ),
      ],
    );
  }

  // 5. BACKUP & DR TAB
  Widget _buildBackupDrTab() {
    return ListView(
      padding: const EdgeInsets.all(24.0),
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'Automated Ledger Backups',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
            ),
            Row(
              children: [
                ElevatedButton(
                  onPressed: () => _triggerBackup('INCREMENTAL'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.blue.shade900,
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  ),
                  child: const Text('Incremental'),
                ),
                const SizedBox(width: 8),
                ElevatedButton(
                  onPressed: () => _triggerBackup('FULL'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF0F172A),
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  ),
                  child: const Text('Full Backup'),
                ),
              ],
            ),
          ],
        ),
        const SizedBox(height: 16),
        ..._backups.map((bak) => Card(
          elevation: 0,
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: BorderSide(color: Colors.grey.shade200)),
          child: ListTile(
            leading: Icon(Icons.cloud_done_outlined, color: bak.status == 'COMPLETED' ? Colors.green : Colors.grey),
            title: Text('${bak.backupType} Backup - ${bak.id}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
            subtitle: Text('Symmetric KMS encryption | size: ${bak.fileSizeMb}MB\n${bak.storagePath}', style: const TextStyle(fontSize: 11)),
            trailing: Container(
              padding: const EdgeInsets.all(6),
              decoration: BoxDecoration(
                color: bak.status == 'COMPLETED' ? Colors.green.shade50 : Colors.blue.shade50,
                borderRadius: BorderRadius.circular(6),
              ),
              child: Text(
                bak.status,
                style: TextStyle(
                  fontSize: 10, 
                  fontWeight: FontWeight.bold, 
                  color: bak.status == 'COMPLETED' ? Colors.green.shade900 : Colors.blue.shade900,
                ),
              ),
            ),
          ),
        )).toList(),

        const SizedBox(height: 32),
        const Text(
          'Continuity Testing & Disaster Recovery Drills',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: ElevatedButton.icon(
                onPressed: () => _triggerDrDrill('DATABASE_FAILOVER'),
                icon: const Icon(Icons.settings_backup_restore),
                label: const Text('PostgreSQL Failover drill'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.blue.shade900,
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
              ),
            ),
            const SizedBox(width: 8),
            Expanded(
              child: ElevatedButton.icon(
                onPressed: () => _triggerDrDrill('REDIS_CACHE_RECOVERY'),
                icon: const Icon(Icons.memory_outlined),
                label: const Text('Redis Sentinel drill'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.purple.shade900,
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        ..._drDrills.map((drill) => Card(
          elevation: 0,
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: BorderSide(color: Colors.grey.shade200)),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(drill.scenario, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    Chip(
                      label: Text(drill.status),
                      backgroundColor: Colors.green.shade50,
                      labelStyle: TextStyle(color: Colors.green.shade900, fontSize: 10, fontWeight: FontWeight.bold),
                    ),
                  ],
                ),
                Text('Tested by: ${drill.tester}', style: const TextStyle(fontSize: 11, color: Colors.grey)),
                const Divider(),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text('achieved RTO: ${drill.rtoSeconds} seconds', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                    Text('achieved RPO: ${drill.rpoSeconds} seconds', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.green)),
                  ],
                ),
              ],
            ),
          ),
        )).toList(),
      ],
    );
  }

  // --- REUSABLE BUILDERS ---
  Widget _buildMetricCard(String label, int value, String unit, Color color, String subtext) {
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: BorderSide(color: Colors.grey.shade200)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(label, style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.grey)),
            const SizedBox(height: 6),
            Row(
              children: [
                Text('$value', style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: Color(0xFF0F172A))),
                const SizedBox(width: 2),
                Text(unit, style: const TextStyle(fontSize: 12, color: Colors.grey)),
              ],
            ),
            const SizedBox(height: 4),
            Text(subtext, style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: color)),
          ],
        ),
      ),
    );
  }

  Widget _buildHeartbeatRow(String service, String status, Color color) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              Container(
                width: 8,
                height: 8,
                decoration: BoxDecoration(shape: BoxShape.circle, color: color),
              ),
              const SizedBox(width: 12),
              Text(service, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w500)),
            ],
          ),
          Text(status, style: TextStyle(color: color, fontWeight: FontWeight.bold, fontSize: 12)),
        ],
      ),
    );
  }

  void _showRemediationDialog(SocAlert alert) {
    final resCtrl = TextEditingController();
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Resolve Security Incident'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Incident: ${alert.description}', style: const TextStyle(fontSize: 13, color: Colors.grey)),
            const SizedBox(height: 12),
            TextField(
              controller: resCtrl,
              maxLines: 2,
              decoration: const InputDecoration(
                labelText: 'Remediation actions taken',
                border: OutlineInputBorder(),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          ElevatedButton(
            onPressed: () {
              if (resCtrl.text.isNotEmpty) {
                _resolveAlert(alert, resCtrl.text);
                Navigator.pop(context);
              }
            },
            child: const Text('Resolve'),
          ),
        ],
      ),
    );
  }

  void _showAddKeyDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Issue B2B API Access Key'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(controller: _keyNameCtrl, decoration: const InputDecoration(labelText: 'Client name / integration purpose')),
              TextField(controller: _keyWhitelistCtrl, decoration: const InputDecoration(labelText: 'IP Whitelist (Optional, comma-separated)')),
              TextField(controller: _keyScopesCtrl, decoration: const InputDecoration(labelText: 'Scopes')),
              TextField(controller: _keyRpsCtrl, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: 'Rate Limit (Requests per sec)')),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          ElevatedButton(onPressed: _issueApiKey, child: const Text('Issue Key')),
        ],
      ),
    );
  }
}
