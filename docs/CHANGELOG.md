# SURYA CREDIT SOLUTIONS: ARCHITECTURAL CHANGELOG

All notable changes, module integrations, and technical improvements made to the **Surya Credit Solutions** platform are documented in this changelog.

---

## [1.1.0-RC1] - 2026-07-04 (RELEASE CANDIDATE 1 - FINAL STABILIZATION)

This release marks the final stabilization cycle, completing all outstanding Medium-Priority improvements and preparing the enterprise suite for Release Candidate (RC1) sign-off.

### Added
- **Multi-Language Localization Dictionary**: Implemented high-fidelity localization support inside `Localization.kt` supporting major Indian regional languages (English, Hindi, Kannada, Tamil, Marathi, Telugu) reacting instantly to user language selections.
- **Offline Database Synchronization Controller**: Created a Room-database sync indicator and trigger system (`isOfflineSyncing`, `triggerOfflineSync`) inside `AppViewModel` to gracefully reconcile local kiosk transactions with the cloud ledger service.
- **AI-Assisted Product Recommendations**: Designed and integrated a horizontal B2B product recommendation carousel inside `MarketplaceScreen.kt` with dynamic match scores to optimize distributor procurement speeds.
- **Visual Theme Customization**: Fully configured runtime Theme option buttons in `AppSettingsScreen` to support seamless system, light, and dark-mode toggling across Jetpack Compose layouts.
- **Redis-Backed Distributed Idempotency**: Upgraded the webhook signature processing gateway (`GatewayService`) in NestJS with a distributed `ioredis` cache. Out-of-the-box support for AWS ElastiCache / Redis clustering, offering strict protection against double-credit settlement errors across horizontally scaled cloud pods.
- **High-Availability Graceful Fallback**: Integrated robust fail-safe error recovery blocks inside the caching service. If the Redis server cluster goes offline, the webhook engine automatically degrades to a localized high-availability Map cache to ensure zero transactions are lost.

### Fixed
- Fixed and verified unit tests in the JVM linter suite, ensuring 100% green test assertions on Robolectric.
- Refined typography sizing and accessibility bounds in `AppSettingsScreen` to adhere to Material Design 3 guidelines.

---

## [1.0.0] - 2026-07-04 (OFFICIAL PRODUCTION MILESTONE)

This release marks the final milestone of the **Surya Credit Solutions** core engine and administrative workspace suite.

### Added
- Created multi-stage optimized `Dockerfile` structures for both the NestJS backend API and the React Admin UI.
- Developed production-grade Terraform configurations for **AWS (Aurora/ElastiCache/VPC)**, **GCP (GKE Autopilot/Cloud SQL)**, and **Azure (AKS/Flexible PG)**.
- Integrated high-throughput billing and invoice generation tables, with support for automated CGST/SGST splitting.
- Implemented double-entry journal items to ensure ledger balances match perfectly.
- Created `OPERATIONS_MANUAL.md` playbooks covering workflows for retailers, distributors, super-admins, and finance teams.
- Implemented `TROUBLESHOOTING_GUIDE.md` diagnostics detailing SRE log parsing, DB recovery, and manual transaction reconciliation scripts.
- **Implemented Critical FinTech Concurrency Locking**: Added an async dynamic lock map per user in the NestJS Wallet Core (`WalletService`) to serialize wallet and commission ledger modifications, preventing balance leakage, race conditions, or double-spent vulnerabilities under concurrent API calls or gateway webhooks.
- **Implemented Sliding TTL Webhook Idempotency**: Upgraded payment gateway webhook processing (`GatewayService`) with a map-based idempotency registry with an auto-eviction sliding TTL mechanism (24-hour expiration) to secure against replay attacks and memory leaks.
- **Implemented Global Double-Entry Reconciliation**: Created a system-wide double-entry balance parity checker (`reconcileGlobalLedger`) inside `WalletService` and exposed it via a secure administrative REST API endpoint (`POST /api/v1/wallet/reconcile`) to mathematical verify accounting balancing metrics across all tenants.

### Fixed
- Fixed deep-nested Prisma module references inside support tickets and unified duplicate structures under `SimpleSupportTicket` and `SupportTicket`.
- Fixed legacy Java date APIs across automated unit tests using the modern Android SDK-safe date formatting strategies.
- Refined the NestJS logging interceptor to inject matching correlation IDs (`X-Correlation-ID`) across all request contexts.

### Security
- Locked down default admin and user endpoints behind forced JWT signatures.
- Implemented robust IP blocklist mechanisms within the Admin Console Security Operations Center (SOC).

---

## [0.5.0-RC1] - 2026-06-15

### Added
- Integrated the core wallet accounting engine supporting standard, credit, and commission wallets.
- Created the Flutter mobile client layouts, including dashboards for DMT and AEPS.
- Added support ticket CRM components and distributor credit-line management tools.
- Set up a Prometheus APM client inside NestJS to export metrics on `/metrics`.

### Fixed
- Handled SQL Injection risks globally by enforcing parameterized Prisma database queries.
- Corrected NestJS interceptors to handle CORS handshakes on whitelabeled domains.
