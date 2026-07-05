# SURYA CREDIT SOLUTIONS: TECHNICAL TROUBLESHOOTING GUIDE

This guide provides precise, step-by-step diagnostic and recovery protocols for engineers, system administrators, and site reliability engineers (SREs).

---

## 1. COMPREHENSIVE ERROR CODE MATRIX

The platform utilizes a structured error cataloging strategy. API exception objects contain a descriptive string identifier inside the `error` attribute.

| Error Code | Potential Root Cause | Recovery Protocol |
| :--- | :--- | :--- |
| `ERR_AUTH_MFA_FAILED` | User entered incorrect OTP or validation expired. | Re-prompt OTP submission; check MSG91/Twilio SMS logs if SMS did not deliver. |
| `ERR_MPIN_LOCKED` | 3 consecutive invalid MPIN entries on wallet transfer. | Temporary 15-minute lock. Advise agent to use "Forgot MPIN" workflow to reset with SMS OTP. |
| `ERR_LEDGER_PARITY_BREACH` | Double-entry balance failed (`debit != credit`). | Block transaction, rollback database state transaction, and write an urgent alert to `SecurityEvent`. |
| `ERR_GATEWAY_TIMEOUT` | Razorpay/Cashfree servers did not respond within 15 seconds. | Fall back to redundant aggregator switch. Queue transaction for dynamic status checks. |
| `ERR_INSUFFICIENT_CREDIT` | Retailer attempted order exceeding distributor credit ceiling. | Block checkout. Prompt agent to apply for credit line extensions or pay via standard wallet balance. |
| `ERR_CROSS_TENANT_ACCESS` | Subdomain mismatch or authorization key tampering. | Terminate request instantly, log action to `AuditLog` as a high-threat incident, and flag Client IP. |

---

## 2. PARSING COMPREHENSIVE PLATFORM LOGS

All system logs are written to central log aggregates (such as AWS CloudWatch or Elasticsearch) with a trace-critical `correlationId`.

### 2.1 Trace Example: Analyzing a Database Timeout
When diagnosing a transaction timeout:
1. Retrieve the `correlationId` from the error popup on the admin dashboard or the client network log (e.g. `err_corr_8a92f8`).
2. Search the logs index using the correlation ID:
   ```bash
   # CLI query for real-time tracking
   grep -rn "err_corr_8a92f8" /var/log/surya-api/
   ```
3. Locate the error footprint.
   - **Sample Log Trace**:
     ```json
     {
       "timestamp": "2026-07-03T20:41:12.002Z",
       "level": "error",
       "message": "Query execution timeout after 15000ms",
       "correlationId": "err_corr_8a92f8",
       "context": "PrismaClient",
       "sql": "SELECT SUM(debit) FROM \"JournalItem\" WHERE \"tenantId\" = 'tenant_partner_a'"
     }
     ```
   - **Resolution**: Apply index optimization as mapped in Section 3 of `PRODUCTION_VALIDATION_REPORT.md` to restore `< 5ms` execution times.

---

## 3. DATABASE RECOVERY & DISASTER RESTORATION

### 3.1 Reverting to a Safe Hot Database State
If a database migration goes wrong, execute the following commands to restore from a warm hourly snapshot:

```bash
# 1. Stop the application pod replicas to prevent further transactional writes
kubectl scale deployment/surya-api --replicas=0 -n surya-prod

# 2. Check the PostgreSQL DB state using standard tools
pg_isready -h prod-db-cluster.amazonaws.com

# 3. Restore snapshot (example using AWS RDS CLI)
aws rds restore-db-instance-to-point-in-time \
  --source-db-instance-identifier prod-db-cluster \
  --target-db-instance-identifier prod-db-cluster-restored \
  --restore-time 2026-07-03T13:00:00Z

# 4. Verify restored DB cluster connectivity and apply migrations up to target safe version
npx prisma migrate deploy

# 5. Restart application pods to accept user connections again
kubectl scale deployment/surya-api --replicas=5 -n surya-prod
```

---

## 4. RESOLVING PAYMENT FAILURE DISCREPANCIES

When a payment succeeds on the gateway side (user bank debited) but fails to reflect locally (ledger balance unmodified):

```
       [Client Reports Missing Balance]
                      │
                      ▼
   [Query Gateway Dashboard by Payment ID]
                      │
         ┌────────────┴────────────┐
         ▼ Successful?             ▼ Failed?
[Run Manual Reconciliation]    [Notify Customer & Bank]
         │                         │
         ▼                         ▼
 [Execute Balance Credit]      [Instruct User to Wait 48 Hours]
```

### 4.1 Manual Reconciliation Script
System administrators can trigger automated status checks on a single transaction using the following terminal API request:

```bash
curl -X POST https://api.suryacredit.in/api/v1/payments/reconcile \
  -H "Authorization: Bearer ADMIN_JWT_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": "txn_pay_281029",
    "gateway": "RAZORPAY"
  }'
```

The server response will confirm transaction updates:
```json
{
  "paymentId": "txn_pay_281029",
  "gateway": "RAZORPAY",
  "amount": 15000.50,
  "originalStatus": "INITIATED",
  "reconciledStatus": "SUCCESSFUL",
  "reconciled": true,
  "discrepancyDetected": false,
  "settlementBatchId": "settle_batch_8fb20a"
}
```
*Note: This command will automatically update the core database, credit the merchant's wallet balance, and update the double-entry accounting ledger.*
