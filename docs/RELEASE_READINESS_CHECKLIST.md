# Surya Credit Solutions — Release Readiness Checklist (Release RC1)

This checklist outlines the final technical and administrative requirements to sign off on **Surya Credit Solutions** Release Candidate (RC1) for secure, high-availability production staging.

---

## 1. PRE-FLIGHT PRODUCTION MATRIX

Ensure all validation checks are completed and verified by core stakeholders.

| Category | Readiness Validation Action | Owner | Verification Tool | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Database** | Generate compilable client, verify migrations, verify indexes. | Lead DB Architect | `npx prisma migrate status` | **READY** |
| **Core Client** | Native Android application compilation check. | Lead Mobile Dev | `compile_applet` | **PASSED** |
| **Code Linter** | Check code structures, styles, and SDK restrictions. | QC Engineer | `lint_applet` | **PASSED** |
| **Unit Tests** | Run Robolectric and JVM unit verification checks. | Testing Lead | `gradle :app:testDebugUnitTest`| **PASSED** |
| **Gateways** | Verify Razorpay, Cashfree, Paytm, etc. signature APIs. | Integrations Dev | Automated spec suite | **READY** |
| **Security** | Secrets rotation, HTTPS certificates, proxy headers. | DevSecOps Lead | OWASP scanner / SSL Labs | **READY** |

---

## 2. PRODUCTION ENVIRONMENT VARIABLES CHECKLIST

Ensure the production environment contains the following keys, fully populated inside Kubernetes Secrets or Docker Compose environments (never committed to repository).

```bash
# ==========================================
# 1. SYSTEM ENVIRONMENT & PORT
# ==========================================
NODE_ENV=production
PORT=3000
API_VERSION=v1

# ==========================================
# 2. DATABASE CREDENTIALS (POSTGRES & REDIS)
# ==========================================
DATABASE_URL="postgresql://surya_prod_db_user:SUPER_SECURE_PASSWORD@prod-db-cluster.amazonaws.com:5432/surya_ledger?schema=public&connection_limit=50&pool_timeout=15"
REDIS_URL="redis://:SECURE_REDIS_PASSWORD@prod-redis-cluster.cache.amazonaws.com:6379/0"

# ==========================================
# 3. JWT & CRYPTOGRAPHIC KEYS
# ==========================================
JWT_ACCESS_SECRET="surya_prod_access_secret_hash_9802834018240"
JWT_REFRESH_SECRET="surya_prod_refresh_secret_hash_1820384102830"
ENCRYPTION_KEY_32_BYTES="surya_crypt_32_byte_aes_prod_key_2026"

# ==========================================
# 4. THIRD-PARTY PAYMENT GATEWAY SECRETS
# ==========================================
RAZORPAY_KEY_ID="rzp_live_SuryaCreditLive2026"
RAZORPAY_KEY_SECRET="rzp_live_secret_prod_hash_8028"
RAZORPAY_WEBHOOK_SECRET="rzp_webhook_secret_key_1029"

CASHFREE_APP_ID="SURYA_CASHFREE_ID"
CASHFREE_CLIENT_SECRET="cf_live_secret_prod_hash_9182"
CASHFREE_WEBHOOK_SECRET="cf_webhook_secret_key_3841"

PINELABS_MERCHANT_ID="PINE_MERCH_9028"
PINELABS_SECURE_SECRET="pine_live_secret_prod_hash_0281"
PINELABS_WEBHOOK_SECRET="pine_webhook_secret_key_5629"

CCAVENUE_MERCHANT_ID="CC_MERCH_281"
CCAVENUE_WORKING_KEY="cc_live_secret_working_key_0192"
CCAVENUE_ACCESS_CODE="AVCC2026LIVE"

PAYTM_MERCHANT_ID="PAYTM_MERCH_890283"
PAYTM_MERCHANT_KEY="paytm_live_secret_key_02819"
PAYTM_WEBHOOK_SECRET="paytm_webhook_secret_key_9021"

ZAAKPAY_MERCHANT_ID="zaak_surya_102"
ZAAKPAY_SECRET_KEY="zaak_live_secret_key_38102"
ZAAKPAY_WEBHOOK_SECRET="zaakpay_webhook_secret_key_4432"

# ==========================================
# 5. COMMUNICATIONS INTEGRATIONS
# ==========================================
TWILIO_ACCOUNT_SID="AC_twilio_prod_sid_8291"
TWILIO_AUTH_TOKEN="tw_prod_auth_token_9028190"
MSG91_AUTH_KEY="msg91_prod_key_3810"

SENDGRID_API_KEY="SG.sendgrid_prod_api_key_829103"
FCM_SERVER_KEY="fcm_firebase_cloud_messaging_prod_secret_key"
```

---

## 3. ZERO-DOWNTIME CI/CD DEPLOYMENT PIPELINE

Staging releases are orchestrated automatically via Github Actions and Kubernetes.

```
 [GitHub Push / Tag] 
         │
         ▼
 [Lint & Test Validation] ──► Fail ──► [Halt Build & Notify]
         │
         ▼ Success
 [Build Production Docker Image] 
         │
         ▼
 [Push Image to Private Registry (ECR/GCR)]
         │
         ▼
 [Prisma DB Migration Hook] ──► Fail ──► [Block Container Release]
         │
         ▼ Success
 [Kubernetes Rolling Update (Canary Release)]
```

### 3.1 Step-by-Step Production Deployment Command Sequence

1. **Step 1: Check Database Migration Status**
   Ensure database schemas are aligned with the deployment container layers:
   ```bash
   npx prisma migrate deploy
   ```

2. **Step 2: Authenticate and Build Docker Containers**
   Build the immutable microservice backend docker container layer:
   ```bash
   docker build -t gcr.io/surya-credit-solutions/api:v1.0.0-rc1 ./backend
   docker push gcr.io/surya-credit-solutions/api:v1.0.0-rc1
   ```

3. **Step 3: Trigger Rolling Deployment in Kubernetes Cluster**
   Instruct the production cluster to execute a progressive canary rolling update:
   ```bash
   kubectl set image deployment/surya-api api=gcr.io/surya-credit-solutions/api:v1.0.0-rc1 -n surya-prod
   ```

4. **Step 4: Verify Live Liveness Status**
   Monitor pod initialization states and traffic routing health metrics:
   ```bash
   kubectl rollout status deployment/surya-api -n surya-prod
   ```

---

## 4. INSTANT DISASTER RECOVERY & ROLLBACK PLAN

In the event of a critical failure during staging or production activation (e.g., persistent liveness failure or a database migration lockout), DevOps engineers must execute this immediate rollback checklist:

### 4.1 Step 1: Instant Kubernetes Rollback
Revert the active deployment image stream instantly to the preceding stable deployment layer:
```bash
kubectl rollout undo deployment/surya-api -n surya-prod
```
*Expected Outcome: Kubernetes shifts ingress traffic to the healthy, cached v1.0.0 pod layer within 10 seconds.*

### 4.2 Step 2: Database Safe State Reversion
If the migration sequence corrupted data or introduced breaking anomalies, restore the database state from the latest automated hourly warm snapshot:
```bash
# Halt incoming transactional writes at NGINX gateway level
kubectl scale deployment/surya-api --replicas=0 -n surya-prod

# Execute rollback database snapshot restore script
./scripts/db_restore.sh --snapshot-id=snap_hourly_2026-07-03-1300

# Re-engage backend API pod replicas to traffic
kubectl scale deployment/surya-api --replicas=5 -n surya-prod
```

### 4.3 Step 3: Clear Redis State Cache
Ensure stale schemas or session structures are flushed from Redis to avoid state collisions:
```bash
redis-cli -u $REDIS_URL FLUSHALL
```

---

## 5. RELEASIBILITY SIGN-OFF SUMMARY

The Release Candidate (RC1) satisfies all staging validation parameters, performance SLA thresholds, secure header compliance, and structural tests. It is hereby **100% Signed Off** for deployment.
