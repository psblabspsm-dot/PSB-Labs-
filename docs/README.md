# SURYA CREDIT SOLUTIONS: MASTER ENTERPRISE DOCUMENTATION HUB

This directory serves as the official integration, architecture, development, and operational repository for the **Surya Credit Solutions** platform (Release Candidate RC1).

---

## 📖 DOCUMENTATION DIRECTORY INDEX

Please refer to the dedicated, comprehensive guides below for each specific functional domain:

### 1. [System Architecture Document](./SYSTEM_ARCHITECTURE.md)
- Contains **High-Level System Context Diagrams**, Low-Level NestJS Microservice maps, Flutter mobile client state models, Whitelabel domains parsing topology, and Kubernetes clustering configurations.

### 2. [Database & Schema Documentation](./DATABASE_DOCUMENTATION.md)
- A complete data dictionary of all relational PostgreSQL tables mapped inside Prisma (`schema.prisma`). Includes detailed column specifications, data types, unique indexes, cascading delete rules, referential integrity blocks, and a detailed physical **Entity Relationship (ER) Diagram**.

### 3. [REST API & Interface Specification](./API_SPECIFICATION.md)
- Explains global routing conventions, mandatory HTTP headers, core JSON request payloads, and verification rules. Includes a copy-pasteable **OpenAPI 3.0 specification** (YAML format) and a fully loaded **Postman Collection v2.1 export payload**.

### 4. [Clients & User Interfaces Manual](./FRONTEND_ADMIN_GUIDE.md)
- Guides Flutter application developers and React Administrative web developers on UI routing, Material 3 theme configurations, custom widget libraries, role-based access grids, and viewport responsiveness standards.

### 5. [Ecosystem Operations Manual](./OPERATIONS_MANUAL.md)
- Actionable, step-by-step Standard Operating Procedures (SOPs) and playbooks written specifically for **Super Administrators**, **Finance & Accounting Auditors**, **District Distributors**, **On-Field Retailers (Agents)**, and **Support CRM Desk Agents**.

### 6. [Technical Troubleshooting Guide](./TROUBLESHOOTING_GUIDE.md)
- Practical SRE manual listing comprehensive platform error codes, automated trace queries using Correlation ID tracking, hot database snapshot restoration scripts, and payment gateway dispute resolution playbooks.

### 7. [Platform Release Candidate Documentation](./RELEASE_CANDIDATE_RC1.md)
- Full sign-off manual detailing integration audit modules, resolved lint fixes (NewApi old-SDK safe date formattings), and JUnit/Robolectric verified test configurations.

### 8. [Production Integration & Gateway Validation](./PRODUCTION_VALIDATION_REPORT.md)
- Summarizes Razorpay, Cashfree, Pine Labs, Paytm, CCAvenue, and Zaakpay onboarding guides, webhook cryptographic signature verification formulas, duplicate-spend filters, and auto-reconciliation polling services.

### 9. [Security Hardening & Penetration Audit](./SECURITY_REPORT.md)
- Detailed OWASP Top 10 mitigation mapping, environment secret management, forced HTTPS reverse-proxy NGINX configurations, secure HTTP response headers, and Security Operations Center (SOC) lockout rules.

### 10. [Performance & Benchmarking Report](./PERFORMANCE_REPORT.md)
- Staging SLA load test results, high-volume query indexing optimization SQL statements, Redis key caching layouts, Node.js cluster modes, and database connection pools.

### 11. [Deployment & Release Readiness Checklist](./RELEASE_READINESS_CHECKLIST.md)
- Pre-flight variables lists, rolling CI/CD deployment charts, zero-downtime rolling update commands, and emergency revert plans.

### 12. [Infrastructure as Code (IaC) Blueprints](./INFRASTRUCTURE_IAC.md)
- Production-grade Terraform configurations for AWS, GCP, and Azure including VPCs, Load Balancers, GKE/AKS clusters, Aurora/Cloud SQL, and ElastiCache.

### 13. [Containerization & Production Deployment Package](./CONTAINER_RELEASE_PACKAGE.md)
- Multi-stage Dockerfiles for NestJS backend and React Admin, Docker Compose production stack, and custom Helm deployment charts.

### 14. [CI/CD Release Automation Pipelines](./CICD_AUTOMATION.md)
- Automated deployment, syntax and validation check workflows via GitHub Actions, integrating security scans, container build-and-push steps, and failover rolling rollback loops.

### 15. [Monitoring & Alerting Engine](./MONITORING_ALERTING.md)
- Prometheus alerts and telemetry configurations alongside ready-to-import Grafana visual telemetry board JSON scripts.

### 16. [Operational Runbooks & SRE Playbooks](./OPERATIONAL_RUNBOOKS.md)
- Playbooks covering hot-cache eviction routines, database migrations, automated rollbacks, and instant manual payment gateway routing overrides.

### 17. [Production Readiness & Sign-Off Report](./PRODUCTION_READINESS_REPORT.md)
- High-level compliance sign-offs, production benchmarks, pre-launch verification checklist matrices, and official CTO release authorizations.
