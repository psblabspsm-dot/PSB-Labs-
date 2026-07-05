# SURYA CREDIT SOLUTIONS: MONITORING & ALERTING ENGINE

This document establishes the telemetry, logging schemas, Prometheus alerting rules, and Grafana visualization frameworks required to monitor the platform's reliability.

---

## 1. APM METRICS CONFIGURATION

The NestJS core gateway integrates with a Prometheus metrics exporter, writing structured time-series metrics under the `/metrics` path.

```
                  ┌───────────────────────────────┐
                  │    Core NestJS API Service    │
                  └───────────────┬───────────────┘
                                  ▼ (Publishes Metrics)
                  ┌───────────────────────────────┐
                  │      /metrics Endpoint        │
                  └───────────────┬───────────────┘
                                  ▼ (Scrapes Every 15s)
                  ┌───────────────────────────────┐
                  │     Prometheus Server Node    │
                  └───────────────┬───────────────┘
                                  ▼ (Triggers Alert)
                  ┌───────────────────────────────┐
                  │      Alertmanager Engine      │
                  └───────────────┬───────────────┘
                                  ▼ (Alert Routing)
             ┌────────────────────┴────────────────────┐
             ▼ (SRE Team)                              ▼ (Business Admins)
      [PagerDuty Alert]                         [Slack Incident Alert]
```

---

## 2. PROMETHEUS PRODUCTION ALERT RULES (`alerts.yml`)

The rules engine evaluates metrics every 10 seconds. Alerts are routed to PagerDuty or Slack based on severity.

```yaml
groups:
  - name: surya-infrastructure-alerts
    rules:
      # ========================================================================
      # HOST RESOURCE ALERTS
      # ========================================================================
      - alert: ContainerHighCpuUsage
        expr: sum(rate(container_cpu_usage_seconds_total{namespace="surya-prod"}[3m])) by (pod) > 0.85
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "Pod {{ $labels.pod }} CPU usage is extremely high (> 85%)"
          description: "Scale replicas or inspect memory leak configurations on the affected pod."

      - alert: HostDiskSpaceRunningLow
        expr: node_filesystem_free_bytes{mountpoint="/"} / node_filesystem_size_bytes{mountpoint="/"} < 0.15
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "System Disk running critically low (< 15% remaining)"
          description: "Initiate log rotation sweeps or scale persistent storage disks."

      # ========================================================================
      # NETWORK & API SLA ALERTS
      # ========================================================================
      - alert: ApiHighResponseLatency
        expr: histogram_quantile(0.95, sum(rate(http_request_duration_seconds_bucket{job="surya-api"}[5m])) by (le)) > 2.5
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "95th percentile API response latency exceeds 2.5 seconds"
          description: "Inspect slow SQL query execution or check downstream payment gateway response times."

      - alert: ApiSpikeInHTTP5xxErrors
        expr: (sum(rate(http_requests_total{status=~"5.."}[3m])) / sum(rate(http_requests_total[3m]))) * 100 > 3.0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "API 5xx Server Error Rate exceeds 3% threshold"
          description: "Database connection pools may be exhausted. Run telemetry audits immediately."

      # ========================================================================
      # DATABASE & CACHE TIER ALERTS
      # ========================================================================
      - alert: DatabaseConnectionPoolExhausted
        expr: prisma_client_active_connections > 95
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Database connection pool near capacity (> 95 active connections)"
          description: "Increase db_connection_pool limits in the environment variables or scale read replicas."

      - alert: RedisMemoryLimitReachingMax
        expr: redis_memory_used_bytes / redis_memory_max_bytes > 0.90
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "Redis Cache memory utilization exceeds 90%"
          description: "Audit keys without TTL configurations or change the eviction policy to allkeys-lru."

      # ========================================================================
      # TRANSACTIONAL BUSINESS ANOMALY ALERTS
      # ========================================================================
      - alert: SystemicWalletTransactionFailures
        expr: sum(rate(wallet_transaction_failures_total[5m])) > 10
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "High volume of Wallet ledger transaction failures detected"
          description: "Review double-entry parity checks and transactional lock timeout conditions."

      - alert: PaymentGatewayReconciliationBreak
        expr: gateway_reconciliation_errors_total > 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Cryptographic signature or status mismatch on checkout webhooks"
          description: "Verify webhook endpoint SSL certificates and gateway secret key rotation parameters."
```

---

## 3. GRAFANA PRODUCTION DASHBOARD DESCRIPTORS

The central dashboard aggregates infrastructure performance and business transaction summaries onto a unified visual grid. SREs can import the configuration using the JSON template blueprint below.

### 3.1 Dashboard Layout Blueprint Structure (`dashboard.json`)
```json
{
  "annotations": {
    "list": []
  },
  "editable": true,
  "fiscalYearStartMonth": 0,
  "graphTooltip": 1,
  "id": 1028,
  "links": [],
  "liveNow": false,
  "panels": [
    {
      "collapsed": false,
      "gridPos": { "h": 8, "w": 8, "x": 0, "y": 0 },
      "id": 1,
      "title": "API Request Throughput (RPS)",
      "type": "timeseries",
      "targets": [
        {
          "datasource": { "type": "prometheus", "uid": "prometheus" },
          "editorMode": "code",
          "expr": "sum(rate(http_requests_total[1m])) by (status)",
          "legendFormat": "HTTP {{status}}",
          "range": true
        }
      ]
    },
    {
      "collapsed": false,
      "gridPos": { "h": 8, "w": 8, "x": 8, "y": 0 },
      "id": 2,
      "title": "95th Percentile Latency",
      "type": "timeseries",
      "targets": [
        {
          "datasource": { "type": "prometheus", "uid": "prometheus" },
          "editorMode": "code",
          "expr": "histogram_quantile(0.95, sum(rate(http_request_duration_seconds_bucket[5m])) by (le))",
          "legendFormat": "p95 Latency",
          "range": true
        }
      ]
    },
    {
      "collapsed": false,
      "gridPos": { "h": 8, "w": 8, "x": 16, "y": 0 },
      "id": 3,
      "title": "Active Database Connection Count",
      "type": "gauge",
      "targets": [
        {
          "datasource": { "type": "prometheus", "uid": "prometheus" },
          "editorMode": "code",
          "expr": "prisma_client_active_connections",
          "legendFormat": "Active Connections",
          "range": true
        }
      ]
    }
  ],
  "refresh": "5s",
  "schemaVersion": 38,
  "style": "dark",
  "tags": ["production", "surya"],
  "time": {
    "from": "now-1h",
    "to": "now"
  },
  "timepicker": {
    "refresh_intervals": ["5s", "10s", "30s", "1m"]
  },
  "timezone": "utc",
  "title": "Surya Credit Solutions Production Telemetry",
  "version": 1
}
```
