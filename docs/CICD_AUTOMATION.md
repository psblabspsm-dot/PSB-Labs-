# SURYA CREDIT SOLUTIONS: CI/CD RELEASE AUTOMATION PIPELINES

This document details the automated continuous integration and continuous deployment (CI/CD) pipelines implemented via **GitHub Actions** to support zero-downtime, audited production releases.

---

## 1. INTEGRATED BUILD & RELEASE PIPELINE (`.github/workflows/pipeline.yml`)

The following YAML blueprint runs code quality audits, lints, integration tests, security scanning, containerizes components, deploys them to the EKS/GKE cluster, and performs automated rollbacks if post-deployment health probes fail.

```yaml
name: Production Release Pipeline (SCS Engine)

on:
  push:
    branches:
      - main
  pull_request:
    branches:
      - main

permissions:
  contents: read
  id-token: write

jobs:
  # ============================================================================
  # JOB 1: CODE LINT, TESTING, AND SECURITY COMPLIANCE AUDIT
  # ============================================================================
  audit_and_test:
    name: Code Audit & Unit Tests
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code Repository
        uses: actions/checkout@v3

      - name: Initialize Node.js Environment
        uses: actions/setup-node@v3
        with:
          node-version: 18
          cache: 'npm'
          cache-dependency-path: './backend/package-lock.json'

      - name: Install Dependencies
        working-directory: ./backend
        run: npm ci

      - name: Run Syntax & Linter Verification
        working-directory: ./backend
        run: npm run lint

      - name: Validate Prisma Database Models
        working-directory: ./backend
        run: npx prisma validate

      - name: Execute Backend Tests
        working-directory: ./backend
        run: npm run test

      - name: Execute Security Scan (Trivy)
        uses: aquasecurity/trivy-action@master
        with:
          scan-type: 'fs'
          ignore-unfixed: true
          format: 'table'
          exit-code: '1' # Fail pipeline if CRITICAL vulnerabilities exist
          severity: 'CRITICAL,HIGH'

  # ============================================================================
  # JOB 2: CONTAINERIZE AND PUSH SECURE IMAGES
  # ============================================================================
  build_and_publish:
    name: Build & Push Docker Images
    needs: audit_and_test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v3

      - name: Set up QEMU for Multi-Arch Builds
        uses: docker/setup-qemu-action@v2

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v2

      - name: Log in to Docker Hub Registry
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKER_HUB_USERNAME }}
          password: ${{ secrets.DOCKER_HUB_ACCESS_TOKEN }}

      - name: Build & Publish Backend Image
        uses: docker/build-push-action@v4
        with:
          context: ./backend
          file: ./backend/Dockerfile
          push: true
          tags: |
            suryacredit/backend:latest
            suryacredit/backend:${{ github.sha }}
          cache-from: type=registry,ref=suryacredit/backend:buildcache
          cache-to: type=registry,ref=suryacredit/backend:buildcache,mode=max

      - name: Build & Publish React Admin Image
        uses: docker/build-push-action@v4
        with:
          context: ./react-admin
          file: ./react-admin/Dockerfile
          push: true
          tags: |
            suryacredit/admin-panel:latest
            suryacredit/admin-panel:${{ github.sha }}
          cache-from: type=registry,ref=suryacredit/admin-panel:buildcache
          cache-to: type=registry,ref=suryacredit/admin-panel:buildcache,mode=max

  # ============================================================================
  # JOB 3: AUTOMATED ZERO-DOWNTIME KUBERNETES DEPLOYMENT & ROLLBACK
  # ============================================================================
  kubernetes_deployment:
    name: K8s Production Deploy
    needs: build_and_publish
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v3

      - name: Set up Kubernetes Cluster Context (kubeconfig)
        uses: azure/k8s-set-context@v2
        with:
          method: kubeconfig
          kubeconfig: ${{ secrets.KUBECONFIG_DATA }}

      - name: Dry-Run Helm Charts Verification
        run: |
          helm lint ./kubernetes/helm
          helm template surya-prod ./kubernetes/helm --values ./kubernetes/helm/values.yaml

      - name: Upgrade Platform via Helm Rollouts
        run: |
          helm upgrade --install surya-prod ./kubernetes/helm \
            --namespace surya-prod --create-namespace \
            --set image.tag=${{ github.sha }} \
            --set postgresql.auth.password=${{ secrets.DB_PROD_PASSWORD }} \
            --set redis.auth.password=${{ secrets.REDIS_PROD_TOKEN }} \
            --wait --timeout 10m0s

      - name: Run Automated Rollback on Rollout Failures
        if: failure()
        run: |
          echo "Deployment unsuccessful. Initiating automated revert to previous Helm revision..."
          helm rollback surya-prod -n surya-prod
          echo "Rollback sequence completed successfully."

      - name: Send Slack Release Notifications
        if: always()
        uses: rtCamp/action-slack-notify@v2
        env:
          SLACK_WEBHOOK: ${{ secrets.SLACK_RELEASE_WEBHOOK }}
          SLACK_COLOR: ${{ job.status == 'success' && 'good' || 'danger' }}
          SLACK_TITLE: "Release Deployment: ${{ job.status }}"
          SLACK_MESSAGE: "Surya Credit Solutions image tag: ${{ github.sha }} has been evaluated."
```

---

## 2. MANUAL RECOVERY RUN TRIGGER (`.github/workflows/rollback.yml`)

This simple workflow gives SREs the power to quickly trigger manual rollback routines to a target working image or Helm revision instantly using a parameter inputs interface.

```yaml
name: Manual Infrastructure Emergency Rollback

on:
  workflow_dispatch:
    inputs:
      target_revision:
        description: 'Target Helm revision version to restore (e.g. 52, or leave empty for previous)'
        required: false
        default: ''

jobs:
  emergency_revert:
    name: Emergency K8s Restoral
    runs-on: ubuntu-latest
    steps:
      - name: Setup Kubernetes Context
        uses: azure/k8s-set-context@v2
        with:
          method: kubeconfig
          kubeconfig: ${{ secrets.KUBECONFIG_DATA }}

      - name: Execute Reversion Commands
        run: |
          if [ -z "${{ github.event.inputs.target_revision }}" ]; then
            echo "Reverting immediately to the last stable release revision..."
            helm rollback surya-prod -n surya-prod
          else
            echo "Restoring system state to target revision: ${{ github.event.inputs.target_revision }}..."
            helm rollback surya-prod ${{ github.event.inputs.target_revision }} -n surya-prod
          fi
          kubectl get pods -n surya-prod
