# SURYA CREDIT SOLUTIONS: CONTAINERIZATION & PRODUCTION DEPLOYMENT PACKAGE

This document defines the physical build configuration files, multi-stage production Dockerfiles, orchestration compose layouts, and Kubernetes Helm charts for deploying the **Surya Credit Solutions** platform.

---

## 1. DOCKERFILE DEFINITIONS (MULTI-STAGE OPTIMIZED)

Both configurations are optimized to prevent build layer leakages, run as non-root users, and ensure minimal final container image sizes.

### 1.1 NestJS Backend Engine (`/backend/Dockerfile`)
```dockerfile
# --- Stage 1: Build Module Dependencies & Assets ---
FROM node:18-alpine AS builder
WORKDIR /app

# Install system dependencies needed for native modules
RUN apk add --no-cache python3 make g++ openssl

# Install production dependencies
COPY package*.json ./
COPY prisma ./prisma/
RUN npm ci

# Generate type-safe Prisma client
RUN npx prisma generate

# Build NestJS app
COPY . .
RUN npm run build

# Prune dev dependencies to minimize package footprint
RUN npm prune --production

# --- Stage 2: Clean Production Environment ---
FROM node:18-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
RUN apk add --no-cache openssl

# Establish restricted running user profile
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nestjs

# Copy essential build outputs
COPY --from=builder --chown=nestjs:nodejs /app/dist ./dist
COPY --from=builder --chown=nestjs:nodejs /app/node_modules ./node_modules
COPY --from=builder --chown=nestjs:nodejs /app/package.json ./package.json
COPY --from=builder --chown=nestjs:nodejs /app/prisma ./prisma

USER nestjs
EXPOSE 3000

# Executing start command with DB migration auto-run
CMD ["sh", "-c", "npx prisma migrate deploy && node dist/main.js"]
```

### 1.2 React Admin Dashboard Panel (`/react-admin/Dockerfile`)
```dockerfile
# --- Stage 1: Static Bundle Compilation ---
FROM node:18-alpine AS compiler
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# --- Stage 2: NGINX Static Delivery Engine ---
FROM nginx:1.25-alpine AS runner

# Copy Custom NGINX Security configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy build artifacts to the static directory
COPY --from=compiler /app/build /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

---

## 2. PRODUCTION ORCHESTRATION (`docker-compose.prod.yml`)

The production compose environment configures memory/CPU resource caps, dependencies, health checks, and restart loop conditions.

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: surya-prod-postgres
    restart: always
    environment:
      POSTGRES_DB: surya_ledger
      POSTGRES_USER: surya_db_admin
      POSTGRES_PASSWORD: ${DATABASE_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    resources:
      limits:
        cpus: '2.0'
        memory: 4096M
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U surya_db_admin -d surya_ledger"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: surya-prod-redis
    restart: always
    command: redis-server --requirepass ${REDIS_AUTH_TOKEN}
    volumes:
      - redisdata:/data
    resources:
      limits:
        cpus: '1.0'
        memory: 2048M
    healthcheck:
      test: ["CMD", "redis-cli", "-a", "${REDIS_AUTH_TOKEN}", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    image: suryacredit/backend:1.0.0-rc1
    container_name: surya-prod-backend
    restart: always
    environment:
      - DATABASE_URL=postgresql://surya_db_admin:${DATABASE_PASSWORD}@postgres:5432/surya_ledger?schema=public
      - REDIS_HOST=redis
      - REDIS_PORT=6379
      - REDIS_PASSWORD=${REDIS_AUTH_TOKEN}
      - JWT_SECRET=${JWT_SECRET_KEY}
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    ports:
      - "3000:3000"
    resources:
      limits:
        cpus: '2.0'
        memory: 2048M

  frontend-admin:
    image: suryacredit/admin-panel:1.0.0-rc1
    container_name: surya-prod-admin-panel
    restart: always
    ports:
      - "8080:80"
    depends_on:
      - backend

volumes:
  pgdata:
  redisdata:
```

---

## 3. HELM DEPLOYMENT CONFIGURATIONS

The Kubernetes deployment utilizes standard packaging structures to streamline deployment and configurations inside EKS/GKE environments.

### 3.1 Helm Package Metadata (`/kubernetes/helm/Chart.yaml`)
```yaml
apiVersion: v2
name: surya-credit-solutions
description: Enterprise Helm configuration charting core Ledger Backend and SaaS Admin Portals.
type: application
version: 1.0.0
appVersion: "1.0.0-rc1"
dependencies:
  - name: postgresql
    version: 12.5.6
    repository: https://charts.bitnami.com/bitnami
    condition: postgresql.enabled
  - name: redis
    version: 17.11.3
    repository: https://charts.bitnami.com/bitnami
    condition: redis.enabled
```

### 3.2 Main Values File (`/kubernetes/helm/values.yaml`)
```yaml
replicaCount: 3

image:
  repository: suryacredit/backend
  pullPolicy: IfNotPresent
  tag: "1.0.0-rc1"

service:
  type: ClusterIP
  port: 3000

ingress:
  enabled: true
  className: nginx
  annotations:
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
  hosts:
    - host: api.suryacredit.in
      paths:
        - path: /
          pathType: Prefix

resources:
  limits:
    cpu: 2000m
    memory: 2Gi
  requests:
    cpu: 500m
    memory: 512Mi

autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 30
  targetCPUUtilizationPercentage: 70
  targetMemoryUtilizationPercentage: 75

# Bitnami Postgres Sub-Chart Parameters
postgresql:
  enabled: true
  auth:
    database: surya_ledger
    username: surya_db_admin

# Bitnami Redis Sub-Chart Parameters
redis:
  enabled: true
  auth:
    enabled: true
```
