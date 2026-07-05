# SURYA CREDIT SOLUTIONS: ENTERPRISE DEPLOYMENT & OPERATION GUIDE

This deployment document outlines the production-ready infrastructure blueprint, continuous integration pipelines, multi-cloud provisioning pathways, NGINX reverse-proxy configurations, disaster recovery scripts, and app store release protocols for the **Surya Credit Solutions** platform.

---

## 1. ECOSYSTEM ARCHITECTURE ARCHITECTURE

The Surya Credit Solutions ecosystem utilizes an industry-standard B2B Fintech design. Below is the operational layer map of the platform:

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

## 2. DEVOPS & LOCAL ORCHESTRATION

To spin up the entire development stack locally or in single-node staging environments, we have supplied a complete `/docker-compose.yml` and `/nginx.conf` at the root.

### Running via Docker Compose

1. **Prerequisites**: Ensure Docker Engine v24+ and Docker Compose v2.20+ are installed.
2. **Generate TLS Certificates** for local development (NGINX expects these in `./nginx-ssl/`):
   ```bash
   mkdir -p nginx-ssl
   openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
     -keyout nginx-ssl/surya_server.key \
     -out nginx-ssl/surya_server.crt \
     -subj "/C=IN/ST=Karnataka/L=Bangalore/O=SuryaCredit/CN=localhost"
   ```
3. **Launch the Container Stack**:
   ```bash
   docker compose up -d --build
   ```
4. **Initialize Database Schema & Migrations**:
   ```bash
   docker compose exec api npx prisma db push
   docker compose exec api npx prisma db seed
   ```

### Operational Container Services
- **NGINX Reverse Proxy**: Accessible on `https://localhost` (wraps traffic with SSL/TLS and enforces custom secure headers).
- **NestJS Core Engine**: Accessible internally inside the docker network on `http://api:3000`.
- **PostgreSQL relational database**: Accessible on port `5432` with username `surya_admin`.
- **Redis Memory cache**: Accessible on port `6379`.

---

## 3. KUBERNETES & HELM DEPLOYMENTS (PRODUCTION)

For highly resilient, multi-AZ, auto-scaled deployments, utilize the Kubernetes manifests in the `/kubernetes` directory or the provided Helm chart.

### Option A: Raw Kubernetes Deployment
Deploy the full enterprise stack (StatefulSet Postgres, Redis Deployment, Auto-scaled NestJS Deployment, Services, and NGINX Ingress rules) by executing:
```bash
kubectl create namespace surya-prod
kubectl apply -f kubernetes/deployment.yml
```

### Option B: Helm Chart Management (Enables Templating)
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
4. **Trigger Rolling Redeployments** (e.g., after updating code images):
   ```bash
   kubectl rollout restart deployment/surya-fintech-api -n surya-prod
   ```

---

## 4. MULTI-CLOUD DEPLOYMENT PATHS

Surya Credit Solutions can be deployed seamlessly across major cloud providers. Follow these prescriptive configurations:

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

## 5. ENTERPRISE SECURITY & NGINX HARDENING

Our reverse proxy (`/nginx.conf`) is hardened out-of-the-box using standard OWASP-10 protection guidelines:

1. **SSL/TLS Protocol Restriction**: Only TLS 1.2 and TLS 1.3 are enabled. Weak protocols (SSLv3, TLS 1.0, TLS 1.1) and broken ciphers are strictly disabled.
2. **Strict Transport Security (HSTS)**: Enforced via `Strict-Transport-Security "max-age=63072000; includeSubDomains; preload"` to prevent man-in-the-middle attacks.
3. **Clickjacking Prevention**: Configured with `X-Frame-Options "DENY"`.
4. **MIME Sniffing Block**: Active via `X-Content-Type-Options "nosniff"`.
5. **Cross-Site Scripting (XSS)**: Handled via `X-XSS-Protection "1; mode=block"` and a strict `Content-Security-Policy`.
6. **Rate Limiting**: Configured with `limit_req_zone` targeting incoming client IPs to restrict brute-force scanning to a sustainable 20 requests per second.
7. **Proactive SQL Injection Filter**: Queries containing malicious SQL keywords like `UNION SELECT` or `CONCAT(` are blocked with an immediate HTTP `403 Forbidden` response at the proxy layer.

---

## 6. DISASTER RECOVERY & NIGHTLY BACKUPS

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

## 7. MOBILE APP RELEASE & PLAY STORE PACKAGING

To prepare the **Kotlin/Jetpack Compose Android Client** for Google Play Store verification, adhere to this sequence:

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

## 8. CI/CD INTEGRATION & AUTOMATIC ROLLBACKS

The automated deployment pipeline `.github/workflows/deploy.yml` triggers on every merge/push to `main`:

1. **Automated Testing & Linter Verification**: Validates formatting, checks syntax, and compiles Jest/Mocha tests on NestJS backend.
2. **Robolectric JVM Testing**: Runs Android suite code audits and local widget unit checks in Compose.
3. **Multi-Stage Docker Packaging**: Compiles the source files, discards heavy dev dependencies, and generates an optimized production image using `/backend/Dockerfile`.
4. **Push & Tagging**: Tags the image with the unique commit SHA and `latest`, pushing it securely to GCP Artifact Registry / ECR.
5. **Zero-Downtime Rolling Update**: Instructs the GKE/EKS cluster to perform a rolling update on `surya-api`, spawning new pods with the new container version before terminating old ones.

### Deployment Rollback Protocol
If any newly deployed image crashes or fails liveness probes during runtime:
```bash
# Inspect rollout status
kubectl rollout status deployment/surya-api -n surya-prod

# If failures are detected, instantly roll back to the last stable deployment revision
kubectl rollout undo deployment/surya-api -n surya-prod
```
The Kubernetes control plane will immediately reverse the traffic switch and bring back the preceding stable container pods instantly with zero downtime.
