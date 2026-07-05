# SURYA CREDIT SOLUTIONS: CLIENTS & USER INTERFACES MANUAL

This document serves as the implementation and extension guide for the **Flutter Mobile Agent Application** and the **React Administrative Web Portal**.

---

## 1. FLUTTER MOBILE CLIENT ARCHITECTURE

The mobile client facilitates fast, secure on-field merchant transactions, utility bill collection, B2B procurement, and instant payouts.

### 1.1 Mobile Workspace Structure

```
flutter/
├── android/                  # Native Android configuration layers
├── assets/                   # Vector SVGs, images, custom app fonts
└── lib/                      # Pure Dart source code
    ├── models/               # Domain data models (User, Wallet, Ticket)
    ├── providers/            # State-management and API synchronizers
    │   ├── finance_provider.dart
    │   ├── saas_provider.dart
    │   └── security_provider.dart
    ├── screens/              # Visual view layouts
    │   ├── finance_management_screens.dart
    │   ├── fintech_dashboards.dart
    │   ├── saas_tenant_screens.dart
    │   ├── security_ops_screens.dart
    │   └── support_center_screens.dart
    └── main.dart             # Application initialization entry point
```

### 1.2 View Routing Dictionary
Mobile navigation is organized into type-safe views using a core router switch:

- `route_splash`: Initialization screen. Validates device binding fingerprint.
- `route_auth`: Seamless OTP and MPIN cryptographic pin inputs.
- `route_dashboard`: Main merchant central control panel (Quick Links to DMT, AEPS, BBPS).
- `route_credit`: Displays credit limit lines, interest slabs, and repayments ledger.
- `route_support`: Ticket creation, agent chat threads, and live resolution timelines.

### 1.3 State Management & API Synchronization
State operations are structured via the **Provider / ChangeNotifier** pattern.

```dart
// Example implementation pattern representing state-management
class WalletProvider extends ChangeNotifier {
  final ApiService _api = ApiService();
  double _balance = 0.00;
  bool _isLoading = false;

  double get balance => _balance;
  bool get isLoading => _isLoading;

  Future<void> fetchWalletBalance() async {
    _isLoading = true;
    notifyListeners();
    try {
      final data = await _api.get("/wallet/balance");
      _balance = double.parse(data['balance'].toString());
    } catch (e) {
      log("Error syncing balance: $e");
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}
```

### 1.4 Material Design 3 Color Schemes

The theme system dynamically integrates dark/light mode, using beautiful M3 dynamic color mappings.

```dart
// Material 3 Custom Theme definitions
final ThemeData lightTheme = ThemeData(
  useMaterial3: true,
  brightness: Brightness.light,
  colorScheme: ColorScheme.fromSeed(
    seedColor: const Color(0xFFE53935), // Pure Surya Red Accent
    primary: const Color(0xFFE53935),
    secondary: const Color(0xFF1E88E5), // Deep Ocean Blue
    background: const Color(0xFFFAFAFA),
  ),
);

final ThemeData darkTheme = ThemeData(
  useMaterial3: true,
  brightness: Brightness.dark,
  colorScheme: ColorScheme.fromSeed(
    seedColor: const Color(0xFFE53935),
    primary: const Color(0xFFE53935),
    secondary: const Color(0xFF64B5F6),
    background: const Color(0xFF121212), // Deep Velvet Dark
  ),
);
```

---

## 2. REACT ADMINISTRATIVE PORTAL ARCHITECTURE

The admin panel provides a robust web interface for corporate oversight, franchise control, financial clearing, and developer monitoring.

### 2.1 Workspace Structure (React Admin)

```
react-admin/
└── src/
    ├── components/
    │   ├── FinanceAccountingHub.jsx  # Double-entry ledger ledger lists, GST bills
    │   ├── SecurityOpsCenter.jsx     # SOC monitoring dashboard, access logs, blocklist
    │   ├── SupportAgentHub.jsx       # Multi-tenant CRM ticketing queues
    │   ├── TenantEdit.jsx            # Dynamic tenant brand asset styling edits
    │   ├── TenantList.jsx            # Multi-tenant workspace listing grids
    │   └── WebhookDebugger.jsx       # Real-time webhook monitor and payload sender
    └── App.js                        # App gateway and route access rules
```

### 2.2 Dashboard Module Definitions

#### A. Security Operations Center (SOC)
- **Features**: Visualizes access patterns, reports real-time threat counts (API abuse, suspicious failed logins), list system blocklists, and lets administrators manually block suspicious IPs.
- **Widgets**: Threat levels Gauge charts, Access location maps, and interactive `Ban IP` trigger modals.

#### B. Finance & Accounting Hub
- **Features**: Logs professional Double-Entry ledgers and exports GST invoices. Ensures financial reconciliation between dynamic payment gateways (Razorpay, Cashfree) and core databases.
- **Widgets**: Profit margin line charts, double-entry balances tables, and a single-click invoice PDF generator.

#### C. Webhook Debugger
- **Features**: A built-in diagnostic module allowing developers to audit gateway handshake payloads. Admins can mock incoming webhook POST notifications (e.g. simulation of a successful payment) to verify local transactional response times.

---

## 3. ACCESS PERMISSION SCHEMAS (13-TIER ROLE MATRIX)

Administrative views automatically filter fields, tables, and buttons dynamically based on the current user's security clearance.

| Admin Module | SUPER_ADMIN | FRANCHISE_SUPERVISOR | FINANCE_AUDITOR | SUPPORT_AGENT |
| :--- | :---: | :---: | :---: | :---: |
| **Global Tenants Admin** | `READ/WRITE` | `DENY` | `DENY` | `DENY` |
| **Security Ops Center** | `READ/WRITE` | `READ_ONLY` | `DENY` | `DENY` |
| **Finance & Ledgers** | `READ/WRITE` | `DENY` | `READ/WRITE` | `DENY` |
| **Support Desk CRM** | `READ/WRITE` | `READ/WRITE` | `DENY` | `READ/WRITE` |
| **Whitelabel CSS Config**| `READ/WRITE` | `READ/WRITE` | `DENY` | `DENY` |

---

## 4. UI RESPONSIVENESS & ACCESSIBILITY COMPLIANCE

Both Flutter and React Admin clients comply with premium software usability requirements:
1. **Adaptive Spacing**: Employs structural Material 3 grid sizes (`8dp` spacing increments) with custom layouts that stretch natively up to widescreen 4K displays.
2. **Accessibility Targets**: Enforces touch interactive targets with sizes strictly equal to or exceeding `48dp x 48dp` on mobile screens.
3. **Typography Scaling**: Integrates scalable `dp`/`sp` fonts that support accessibility scaling without causing text truncation or viewport layout breaks.
