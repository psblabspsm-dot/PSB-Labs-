# SURYA CREDIT SOLUTIONS: OPERATIONAL RUNBOOKS & SRE PLAYBOOKS

This document provides step-by-step recipes, command patterns, and emergency resolution trees for site reliability engineers (SREs) and platform operations staff.

---

## RUNBOOK 1: EMERGENCIES - CACHE PURGES (STAMPEDE PREVENTION)

When clearing the central Redis caching layers, running a raw `FLUSHALL` or `FLUSHDB` in a high-throughput production environment will cause **Cache Stampedes**. This floods the PostgreSQL primary database with thousands of concurrent queries, potentially crashing the DB tier.

```
       [Urgent Cache Purge Request]
                    │
                    ▼
 [Run Script to Purge Using SCAN & DEL Loop]
                    │
                    ▼
[Validate PostgreSQL Connection Pool Metrics]
                    │
                    ▼
     [Slowly Warm Up Hot Tenant Keys]
```

### Action Recipe: Safe Purging Protocol
Instead of running a global flush, SREs must purge keys incrementally using a scanning script:

```bash
#!/bin/sh
# Safe Key Eviction Script - Executed on the Redis Pod
REDIS_HOST="surya-prod-redis"
PORT="6379"

# 1. Verify DB Active Connection Status before purging
echo "Current Active Database Connection Count:"
kubectl exec -it deployment/surya-api -n surya-prod -- npx prisma client-status

# 2. Iteratively clear keys matching wildcard prefixes (e.g., non-critical tenant session profiles)
echo "Purging session caches incrementally..."
redis-cli -h $REDIS_HOST -p $PORT -a "$REDIS_AUTH_TOKEN" --scan --pattern "tenant:*:session*" | xargs -L 100 redis-cli -h $REDIS_HOST -p $PORT -a "$REDIS_AUTH_TOKEN" DEL

# 3. Monitor performance and latency to ensure no database spikes occur
echo "Incremental purge complete. Cache warmed up safely."
```

---

## RUNBOOK 2: DATABASE MIGRATION EXECUTION & ROLLBACK

All database migrations must run safely without locking tables or degrading performance for active users.

### 2.1 Execution Protocol
Run migrations during the lowest-traffic hours (e.g., 03:00 - 04:00 UTC).
1. Perform a manual schema backup before starting:
   ```bash
   pg_dump -h prod-db.amazonaws.com -U surya_db_admin -d surya_ledger -F c -b -v -f "/backups/pre-migrate-$(date +%F).dump"
   ```
2. Apply the migration using Prisma Migrate:
   ```bash
   kubectl run prisma-migration --rm -i --restart=Never \
     --image=suryacredit/backend:latest \
     --namespace=surya-prod \
     --env="DATABASE_URL=postgresql://surya_db_admin:PASSWORD@prod-db.amazonaws.com:5432/surya_ledger" \
     -- npx prisma migrate deploy
   ```

### 2.2 Rollback Protocol
If a migration fails or corrupts database state:
1. Re-apply the last stable schema baseline from your backup:
   ```bash
   pg_restore -h prod-db.amazonaws.com -U surya_db_admin -d surya_ledger -v "/backups/pre-migrate-TARGET-DATE.dump"
   ```
2. Reset Prisma schema verification hashes inside the database metadata table:
   ```bash
   npx prisma migrate resolve --rolled-back "202607031200_target_failed_migration"
   ```

---

## RUNBOOK 3: PAYMENT GATEWAY FAILOVER PROTOCOL

If a major payment partner (e.g., Razorpay) experiences high transaction failure rates or goes down:

```
                  [Gateway Failure Detected]
                              │
             ┌────────────────┴────────────────┐
             ▼ (Auto-Failover Active)          ▼ (Manual Override Required)
     [Aggregator Reroutes]               [Execute Manual Failover Script]
             │                                 │
             └────────────────┬────────────────┘
                              ▼
            [Verify Status of Flow on Alternative]
```

### Action Recipe: Manual Override Script
If the auto-failover mechanism needs to be overridden, SREs can force-route all transactions to an alternative gateway (e.g., Cashfree or Pine Labs) instantly:

```bash
# Force Route all transactions to CASHFREE gateway via Redis Config Update
kubectl exec -it deployment/surya-api -n surya-prod -- \
  redis-cli -h surya-prod-redis -a "$REDIS_AUTH_TOKEN" SET "config:global:payment_gateway" "CASHFREE"

echo "System Routing Parameter changed successfully. Traffic redirected."
```

---

## RUNBOOK 4: LOG LEVEL AND APM DEBUG RUNS

To debug active production issues without restarting containers or losing session logs, SREs can dynamically change the API logging verbosity.

### Action Recipe: Adjusting Logging Levels
```bash
# Enable high-verbosity debug logging dynamically on all active pods
kubectl exec -it deployment/surya-api -n surya-prod -- \
  curl -X PATCH http://localhost:3000/api/v1/admin/logging/level \
  -H "Authorization: Bearer ADMIN_JWT" \
  -H "Content-Type: application/json" \
  -d '{"level": "DEBUG"}'
```

Response payload confirmation:
```json
{
  "previousLogLevel": "INFO",
  "activeLogLevel": "DEBUG",
  "changedAt": "2026-07-03T20:55:00.000Z",
  "auditLogged": true
}
```
*Note: To prevent disk saturation, configure logging level resets back to `INFO` within 1 hour of completing investigations.*
