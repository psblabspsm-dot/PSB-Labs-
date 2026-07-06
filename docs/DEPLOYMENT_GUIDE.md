# SURYA CREDIT SOLUTIONS: ENTERPRISE PRODUCTION DEPLOYMENT & OPERATION GUIDE

This deployment document outlines the enterprise-grade production-ready infrastructure blueprint, continuous integration/continuous delivery (CI/CD) pipelines, multi-cloud provisioning pathways, NGINX reverse-proxy configurations, disaster recovery scripts, environment parameters, and release procedures for the **Surya Credit Solutions** platform.

---

## 1. ECOSYSTEM TOPOLOGY & SYSTEM ARCHITECTURE

The Surya Credit Solutions ecosystem uses a decoupled, highly available, fault-tolerant B2B Fintech architecture. The service layer is mapped out below:

```
                            +-------------------------------------------+
                            |           Dynamic Client Apps             |
                            | (Kotlin/Compose Android, Flutter Mobile)  |
                            +-------------------------------------------+
                                                  |
                                                  | HTTPS (Port 443)
                                                  v
                            +-------------------------------------------+
                            |            Enterprise NGINX               |
                            |  Reverse Proxy & WAF (Rate Limiting, TLS) |
                            +-------------------------------------------+
                                                  |
                                                  | Internal HTTP (Port 3000)
                                                  v
                            +-------------------------------------------+
                            |           NestJS Core API Cluster         |
                            |   (Auto-scaled Pods, Liveness Probes)     |
                            +-------------------------------------------+
                                        /                  \
                        (Read/Write)   /                    \   (Sessions/Cache)
                                      v                      v
                +----------------------------+        +--------------------------+
                | PostgreSQL Master Database |        |   Redis Core Cluster     |
                |  (Audit Trails, Ledgers)   |        | (Rate limits, Job Queue) |
                +----------------------------+        +--------------------------+
                              |
                     (Nightly Backup)
                              v
                +----------------------------+
                | AWS S3 Encrypted Cold-Vault|
                | (30-Day Auto Retention)    |
                +----------------------------+
```

---

## 2. PRODUCTION ENVIRONMENT CONFIGURATION

For deployment, create a `.env` file containing these production configuration variables. Ensure this file is never committed to git repositories; inject these keys during CI/CD steps or Kubernetes Secrets mounting.

### Reference `.env` Configuration Template

```ini
# =========================================================================================
# SURYA CREDIT SOLUTIONS: CORE API ENGINE PRODUCTION ENVIRONMENT CONFIGURATION
# =========================================================================================

# Node / NestJS Engine Configuration
NODE_ENV=production
PORT=3000
API_GLOBAL_PREFIX=api/v1

# Sentry Telemetry Integration
SENTRY_DSN="https://e712a21045dbb5b9@o4507000.ingest.sentry.io/4507000"
SENTRY_TRACES_SAMPLE_RATE=1.0

# Relational Postgres Database Configuration (Targeted by Prisma ORM)
DATABASE_URL="postgresql://surya_admin:SuryaPassWord2026@postgres-service.surya-prod.svc.cluster.local:5432/surya_db?schema=public&sslmode=prefer"
POSTGRES_USER=surya_admin
POSTGRES_PASSWORD=SuryaPassWord2026
POSTGRES_DB=surya_db
POSTGRES_MAX_CONNECTION_POOL=20

# High-Availability Redis Ledger Cache & Rate Limiter Configuration
REDIS_URL="redis://redis-service.surya-prod.svc.cluster.local:6379"
REDIS_SESSION_STORE=true
REDIS_SESSION_TTL_SECONDS=28800

# Security Credentials (JWT Encryption Key)
JWT_SECRET="SURYA_CREDIT_SECURE_KEY_2026_CHANGE_THIS_IN_PROD_128BIT"
JWT_EXPIRATION_TIME="8h"

# Multi-Tenant Enterprise SSO Credentials (Google OAuth2)
GOOGLE_CLIENT_ID="your-google-oauth2-client-id.apps.googleusercontent.com"
GOOGLE_CLIENT_SECRET="your-google-oauth2-client-secret-key"
GOOGLE_CALLBACK_URL="https://api.suryacredit.com/api/v1/auth/google/callback"

# Multi-Tenant Enterprise SSO Credentials (Microsoft Azure Active Directory)
MICROSOFT_CLIENT_ID="your-microsoft-azure-client-id-uuid"
MICROSOFT_CLIENT_SECRET="your-microsoft-azure-client-secret-hash"
MICROSOFT_CALLBACK_URL="https://api.suryacredit.com/api/v1/auth/microsoft/callback"

# Third-Party SMS OTP Integration (Twilio / Msg91 API Key)
SMS_GATEWAY_PROVIDER=msg91
SMS_GATEWAY_API_KEY="your_msg91_working_auth_key"
SMS_GATEWAY_SENDER_ID="SURYAC"

# Third-Party SMTP / Email Integration (SendGrid / Nodemailer)
SMTP_HOST="smtp.sendgrid.net"
SMTP_PORT=587
SMTP_USER="apikey"
SMTP_PASS="your_sendgrid_secure_api_key"
SMTP_SENDER="no-reply@suryacredit.com"

# Google Maps API Key for spatial geotargeting of retailer kiosks
GOOGLE_MAPS_API_KEY="AIzaSyYourGoogleMapsApiKeyHere2026"

# Integrated Payment Gateways (Razorpay Credentials)
RAZORPAY_KEY_ID="rzp_live_SuryaCreditLive2026"
RAZORPAY_KEY_SECRET="your_razorpay_secret_key"
RAZORPAY_WEBHOOK_SECRET="your_razorpay_webhook_payload_signature"

# Integrated Payment Gateways (Cashfree Credentials)
CASHFREE_APP_ID="SURYA_CASHFREE_ID_PROD"
CASHFREE_SECRET_KEY="cashfree_prod_working_secret_key"

# Integrated Payment Gateways (Pine Labs Credentials)
PINELABS_MERCHANT_ID="PINE_MERCH_PROD"
PINELABS_ACCESS_CODE="your_pine_labs_access_code"
PINELABS_SECRET_KEY="your_pine_labs_secret_working_key"

# Integrated Payment Gateways (CCAvenue Credentials)
CCAVENUE_MERCHANT_ID="CC_MERCH_PROD"
CCAVENUE_ACCESS_CODE="AVCC2026LIVE"
CCAVENUE_WORKING_KEY="ccavenue_working_key_aes_128_cbc"

# Integrated Payment Gateways (Paytm Credentials)
PAYTM_MID="PAYTM_MERCH_PROD"
PAYTM_MERCHANT_KEY="paytm_merchant_working_key"

# Integrated Payment Gateways (Zaakpay Credentials)
ZAAKPAY_MERCHANT_ID="zaak_surya_prod"
ZAAKPAY_SECRET_KEY="zaakpay_working_secret_keys"

# Firebase Cloud Messaging & Service Admin Account Config (Base64 Encoded JSON)
FIREBASE_SERVICE_ACCOUNT_BASE64="eyJrZXkiOiAidmFsdWUiIH0="
```

---

## 3. DEVOPS & CONTAINERIZATION (LOCAL & STAGING CONFIGS)

To spin up the entire development stack locally or in single-node staging environments, we have supplied a complete `/docker-compose.yml` and `/nginx.conf` at the root.

### Stage 1: Build & Containerize using Multi-Stage Dockerfile

The core API engine utilizes a multi-stage production-optimized `Dockerfile` located at `/backend/Dockerfile`:

```dockerfile
# Stage 1: Build Source
FROM node:20-alpine AS builder
WORKDIR /usr/src/app
COPY package*.json ./
COPY prisma ./prisma/
RUN npm ci
COPY . .
RUN npm run build

# Stage 2: Production Execution
FROM node:20-alpine
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm ci --only=production
COPY --from=builder /usr/src/app/dist ./dist
COPY --from=builder /usr/src/app/prisma ./prisma
EXPOSE 3000
ENV NODE_ENV=production
ENV PORT=3000
CMD ["node", "dist/main"]
```

### Stage 2: Deploy Container Stack locally via Docker Compose

1. **Verify Prerequisites**: Ensure Docker Engine v24+ and Docker Compose v2.20+ are installed.
2. **Generate SSL/TLS Certificates** for local development (NGINX expects these in `./nginx-ssl/`):
   ```bash
   mkdir -p nginx-ssl
   openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
     -keyout nginx-ssl/surya_server.key \
     -out nginx-ssl/surya_server.crt \
     -subj "/C=IN/ST=Karnataka/L=Bangalore/O=SuryaCredit/CN=localhost"
   ```
3. **Launch the Service Mesh**:
   ```bash
   docker compose up -d --build
   ```
4. **Initialize Database Schema & Seeds**:
   ```bash
   docker compose exec api npx prisma db push
   docker compose exec api npx prisma db seed
   ```

---

## 4. KUBERNETES & HELM CLUSTER DEPLOYMENTS (PRODUCTION)

For highly resilient, multi-AZ, auto-scaled deployments, utilize the Kubernetes manifests in the `/kubernetes` directory or the template configurations inside Helm.

### Step 1: Create Namespace and Apply Secrets
Before applying deployment files, create the secrets context with base64 encoded strings:
```bash
kubectl create namespace surya-prod

# Apply configuration maps and security credentials
kubectl apply -f kubernetes/deployment.yml
```

### Step 2: Database PVC Provisioning
PostgreSQL uses a StatefulSet connected to `postgres-pvc` requesting 50Gi block storage:
- Mounting path inside container: `/var/lib/postgresql/data`
- Service endpoint within cluster DNS: `postgres-service.surya-prod.svc.cluster.local`

### Step 3: API Scaling & Auto-healing (HPA)
The NestJS core deployment is paired with a Horizontal Pod Autoscaler (HPA) that dynamically scales replicas based on active workloads:
- **Min Replicas**: 3 Pods (Active Multi-AZ redundancy)
- **Max Replicas**: 10 Pods
- **CPU Scaling Threshold**: 80% utilization

### Step 4: Helm Chart Deployments (Optional Templating)
Initialize, package, and deploy the application cluster onto an active Kubernetes cluster using our Helm files:

1. **Lint the Helm Chart**:
   ```bash
   helm lint kubernetes/helm
   ```
2. **Perform Dry-Run Installation** to verify manifest output:
   ```bash
   helm install surya-fintech kubernetes/helm --dry-run --debug -n surya-prod
   ```
3. **Execute Production Upgrade/Installation**:
   ```bash
   helm upgrade --install surya-fintech kubernetes/helm --namespace surya-prod --create-namespace
   ```
4. **Trigger Rolling Redeployments**:
   ```bash
   kubectl rollout restart deployment/surya-fintech-api -n surya-prod
   ```

---

## 5. MULTI-CLOUD DEPLOYMENT PATHS

The Surya Credit Solutions container and database workloads compile with platform-agnostic cloud setups. Follow these prescriptive configurations:

### 1. Amazon Web Services (AWS)
- **Container Registry**: Push Docker images to **Amazon ECR**.
- **Container Execution**: Run on **Amazon EKS (Kubernetes)** or **ECS with Fargate** behind an Application Load Balancer (ALB).
- **Relational Ledger**: Provision an **Amazon RDS for PostgreSQL** Multi-AZ DB Instance.
- **Cache**: Spin up an **Amazon ElastiCache for Redis** Cluster.
- **Object Storage**: Create an **Amazon S3** Bucket with AWS KMS encryption for storing nightly database dumps and merchant KYC documents.
- **DNS & CDN**: Route traffic through **Amazon Route 53** integrated with **AWS CloudFront** for global edge performance.

### 2. Google Cloud Platform (GCP)
- **Kubernetes**: Deploy to **Google Kubernetes Engine (GKE)**. Enforce Autopilot for managed scaling.
- **Relational Ledger**: Provision a **Cloud SQL for PostgreSQL** instance with automated High Availability.
- **Cache**: Setup **Cloud Memorystore for Redis**.
- **Object Storage**: Create a **Google Cloud Storage (GCS)** Bucket with customer-managed encryption keys.
- **CI/CD Integration**: Connect GitHub Actions to **Google Artifact Registry (GAR)**.

### 3. Microsoft Azure
- **Kubernetes**: Run on **Azure Kubernetes Service (AKS)**.
- **Relational Ledger**: Provision **Azure Database for PostgreSQL Flexible Server**.
- **Cache**: Deploy **Azure Cache for Redis** (Premium Tier).
- **Object Storage**: Store backups in **Azure Blob Storage** (Hot/Cold storage tier rules).

### 4. DigitalOcean
- **Kubernetes**: Provision a managed **DigitalOcean Kubernetes (DOKS)** cluster.
- **Relational Ledger**: Setup a **DigitalOcean Managed PostgreSQL Database** with a Standby Node for redundancy.
- **Load Balancer**: Deploy DO Managed Cloud Load Balancer matching traffic to Kubernetes worker nodes.

---

## 6. ENTERPRISE NGINX REVERSE PROXY HARDENING

Our reverse proxy (`/nginx.conf`) is hardened out-of-the-box using standard OWASP-10 protection guidelines:

1. **SSL/TLS Protocol Restriction**: Only TLS 1.2 and TLS 1.3 are enabled. Weak protocols (SSLv3, TLS 1.0, TLS 1.1) and broken ciphers are strictly disabled.
2. **Strict Transport Security (HSTS)**: Enforced via `Strict-Transport-Security "max-age=63072000; includeSubDomains; preload"` to prevent man-in-the-middle attacks.
3. **Clickjacking Prevention**: Configured with `X-Frame-Options "DENY"`.
4. **MIME Sniffing Block**: Active via `X-Content-Type-Options "nosniff"`.
5. **Cross-Site Scripting (XSS)**: Handled via `X-XSS-Protection "1; mode=block"` and a strict `Content-Security-Policy`.
6. **Rate Limiting**: Configured with `limit_req_zone` targeting incoming client IPs to restrict brute-force scanning to a sustainable 20 requests per second.
7. **Proactive SQL Injection Filter**: Queries containing malicious SQL keywords like `UNION SELECT` or `CONCAT(` are blocked with an immediate HTTP `403 Forbidden` response at the proxy layer.

---

## 7. DISASTER RECOVERY & NIGHTLY BACKUPS

The `/scripts/backup_strategy.sh` shell script manages point-in-time database and memory backups.

### How the Backup Sequence Works
- **PostgreSQL Exports**: Runs an encrypted, compressed `pg_dump` of `surya_db`.
- **Redis Snapshots**: Triggers an asynchronous Redis database save (`BGSAVE`), monitors execution status, and exports the compiled `dump.rdb` snapshot.
- **S3 Cold Storage Archival**: Uploads payloads to a secure, encrypted S3 Bucket (or cloud equivalent) with AWS KMS server-side encryption.
- **Automatic 30-Day Rotation Policy**: Runs local disk-sweep cleaning cycles to prune any files older than 30 days to save disk allocation.

### Standard Database Recovery Command
In the event of a cluster failure, restore the PostgreSQL database using:
```bash
# Decompress SQL export
gunzip -c /var/backups/surya/postgres_surya_YYYYMMDD_HHMMSS.sql.gz > restore.sql

# Pipe the clean SQL transactions back into the master DB
PGPASSWORD="YourDbPassword" psql -h localhost -U surya_admin -d surya_db -f restore.sql
```

---

## 8. MOBILE CLIENT RELEASE & STORES PACKAGING

To prepare the **Kotlin/Jetpack Compose Android Client** or Flutter Mobile App for Google Play Store / Apple App Store verification, adhere to this sequence:

### Step 1: App Signing Key Generation
Generate a unique, cryptographically secure production keystore:
```bash
keytool -genkey -v -keystore app/src/main/assets/surya_release_keystore.jks \
  -alias surya_signing_alias -keyalg RSA -keysize 2048 -validity 10000
```
*Keep this keystore file and its password secret. Secure it using GitHub Actions Secrets for pipeline signing.*

### Step 2: Configure Android Manifest Permissions
Ensure critical permissions are declared in `app/src/main/AndroidManifest.xml`:
- `android.permission.INTERNET`: Required for connecting to Surya Credit APIs.
- `android.permission.ACCESS_NETWORK_STATE`: Required for offline/connectivity state handling.
- `android.permission.USE_BIOMETRIC` & `android.permission.USE_PIN`: Essential for local App secure locking (MPIN protection).

### Step 3: Compile Release App Bundle (AAB)
Compile the production binary ready to be uploaded to Google Play Developer Console:
```bash
./gradlew bundleRelease
```
The output file `app-release.aab` will be generated in `/app/build/outputs/bundle/release/` and is fully ready for store listing.

---

## 9. CI/CD WORKFLOW PIPELINE & AUTOMATED ROLLBACKS

The automated deployment pipeline `.github/workflows/deploy.yml` triggers on every merge/push to `main`:

### GitHub Actions Workflow Structure

```yaml
name: Surya Credit Solutions Production CI/CD

on:
  push:
    branches: [ "main" ]

jobs:
  test-and-verify:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Source Code
        uses: actions/checkout@v4

      - name: Install Node.js
        uses: actions/setup-node@v4
        with:
          node-version: 20
          cache: 'npm'
          cache-dependency-path: 'backend/package-lock.json'

      - name: Install Backend Dependencies
        run: npm ci
        working-directory: ./backend

      - name: Run Backend Tests
        run: npm run test
        working-directory: ./backend

      - name: Run Android JVM Verification Tests
        run: ./gradlew testDebugUnitTest
        
  build-and-deploy:
    needs: test-and-verify
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Source Code
        uses: actions/checkout@v4

      - name: Set up QEMU
        uses: docker/setup-qemu-action@v3

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Log in to Google Artifact Registry
        uses: docker/login-action@v3
        with:
          registry: gcr.io
          username: _json_key
          password: ${{ secrets.GCP_SA_KEY }}

      - name: Build and Push Core API Image
        uses: docker/build-push-action@v5
        with:
          context: ./backend
          file: ./backend/Dockerfile
          push: true
          tags: |
            gcr.io/surya-fintech-prod/surya-core-api:latest
            gcr.io/surya-fintech-prod/surya-core-api:${{ github.sha }}

      - name: Set Kubernetes Context
        uses: azure/k8s-set-context@v3
        with:
          kubeconfig: ${{ secrets.KUBE_CONFIG }}

      - name: Deploy Kubernetes Manifests
        run: |
          kubectl apply -f kubernetes/deployment.yml
          kubectl set image deployment/surya-api surya-api=gcr.io/surya-fintech-prod/surya-core-api:${{ github.sha }} -n surya-prod
```

### Deployment Rollback Protocol
If any newly deployed image crashes or fails liveness probes during runtime:
```bash
# Inspect rollout status
kubectl rollout status deployment/surya-api -n surya-prod

# If failures are detected, instantly roll back to the last stable deployment revision
kubectl rollout undo deployment/surya-api -n surya-prod
```
The Kubernetes control plane will immediately reverse the traffic switch and bring back the preceding stable container pods instantly with zero downtime.
