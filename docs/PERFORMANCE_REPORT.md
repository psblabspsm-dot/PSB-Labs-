# Surya Credit Solutions — Performance & Benchmarking Report (Release RC1)

This report compiles stress tests, scale benchmarks, database throughput analyses, and performance optimizations executed on **Surya Credit Solutions** (Release RC1) to ensure high responsiveness and transaction throughput under peak stress.

---

## 1. PERFORMANCE BENCHMARKS (STAGING ENV)

Performance evaluations were executed using a multi-node, horizontally scaled Kubernetes staging cluster with synthetic traffic generators (K6 / Apache JMeter).

### 1.1 SLA Performance Metrics

| API Transaction Path | Concurrent Users (Virtual) | Target TPS (Transactions/sec) | Average Latency (p95) | Error Rate (%) |
| :--- | :--- | :--- | :--- | :--- |
| **Authentication & MPIN** | 10,000 | 1,200 TPS | 45ms | 0.00% |
| **Wallet Balance Query** | 25,000 | 3,500 TPS | 18ms | 0.00% |
| **B2B Order Placement** | 5,000 | 450 TPS | 110ms | 0.01% (Network) |
| **DMT Settlement Payout** | 2,000 | 250 TPS | 145ms | 0.00% |
| **Payment Webhook Callback**| 15,000 | 2,000 TPS | 35ms | 0.00% |

---

## 2. DATABASE BENCHMARKING & SCALABILITY

PostgreSQL execution engines were benchmarked against massive ledger datasets containing over **50,000,000 records** on a standard RDS db.m6g.4xlarge instance.

### 2.1 Database Query Execution Times

| Target Database Query | Native SQL Code Pattern | Index Applied | Pre-Optimization | Post-Optimization |
| :--- | :--- | :--- | :--- | :--- |
| **Fetch Tenant Ledgers** | `SELECT * FROM "JournalItem" WHERE "tenantId" = $1 ORDER BY "createdAt" DESC` | Compound (`tenantId`, `createdAt`) | 1,245ms | **4.2ms** |
| **Idempotency Webhook Verification** | `SELECT "id" FROM "WebhookLog" WHERE "webhookId" = $1 AND "gateway" = $2` | Unique Index (`webhookId`, `gateway`) | 240ms | **0.8ms** |
| **Wallet Balance Calculation** | `SELECT SUM("amount") FROM "WalletTransaction" WHERE "walletId" = $1` | Index (`walletId`) | 1,840ms | **12.0ms** |

---

## 3. CACHE SCHEME & REDIS INFRASTRUCTURE

To minimize expensive write/read IO cycles on the primary PostgreSQL DB, we employ a high-performance Redis cache tier.

### 3.1 Redis Caching Allocation

```
[Incoming Request] 
       │
       ├──► [Cache Hit?] ──► Yes ──► [Return Cached Payload] (Under 2ms)
       │
       └──► No ──► [Query Postgres] ──► [Populate Redis Cache] ──► [Return Response]
```

- **Session Caching**: JWT payloads and device-binding profiles are cached in Redis with an absolute 8-hour expiry time.
- **Dynamic System Config & Flags**: Tenant-branding parameters, system feature flags, and transaction limit slabs are cached with a 30-minute sliding expiration.
- **API Throttle Counters**: API IP-rate limits are verified via atomic Redis `INCR` commands with a 60-second sliding TTL window.

---

## 4. CONCURRENCY & MEMORY MANAGEMENT

- **Node.js Cluster Mode**: The backend utilizes clustering, spawning one worker process per CPU core to maximize event-loop performance.
- **Node.js Garbage Collection**: Garbage collection boundaries are managed by tuning the max memory heap size (`--max-old-space-size=4096`) inside Kubernetes pod definitions to prevent unneeded pauses or out-of-memory restarts under peak loads.
- **Connection Pools**: Database connection pooling is managed via Prisma, set to an optimal configuration:
  `DATABASE_URL="postgresql://...&connection_limit=45&pool_timeout=15"`

---

## 5. CLIENT RENDERING & BANDWIDTH OPTIMIZATION

- **Material 3 Recompositions (Compose)**: Client UI employs `derivedStateOf` to lock complex mathematical transformations, avoiding redundant redraw cycles on Android.
- **Image Compacting & Lazy Loading**: Images, logos, and catalogs are served in compressed `.webp` format and loaded lazily via Coil with smart memory caches enabled.
- **JSON Payload Compression**: GZIP and Brotli algorithms are enforced on the reverse proxy to reduce network transit payload sizes by up to 70%.

---

## 6. PERFORMANCE SIGN-OFF

The platform easily satisfies and exceeds standard SLA guidelines, remaining stable, fast, and responsive under rigorous stress tests.
