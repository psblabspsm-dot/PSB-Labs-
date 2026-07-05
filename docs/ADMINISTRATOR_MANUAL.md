# SURYA CREDIT SOLUTIONS: ADMINISTRATOR MANUAL

This manual provides instructions for corporate system administrators, compliance officers, and franchise supervisors to operate, audit, and customize the **Surya Credit Solutions** platform via the React Admin Portal.

---

## 1. ACCESSING THE RECONCILIATION & MANAGEMENT SUITE

Administrators can access the web console at: `https://admin.suryacredit.in`

### 1.1 Multi-Tenant Subdomain Isolation
If accessing as a white-label partner administrator, navigate directly to your assigned tenant portal subdomain (e.g. `https://portal.partnerbrand.com/admin`). The gateway automatically parses the HTTP host header to isolate and apply your custom styling, tenant data, and user databases.

---

## 2. WHITE-LABEL CONFIGURATION & BRANDING

Administrators can customize user interfaces dynamically without touching code or redeploying servers.

```
 [Open Tenant Config Panel] ──► [Select Target Tenant] ──► [Modify CSS Variables/Logo] ──► [Click Deploy Dynamic Styles]
```

### 2.1 Modifying Brand Styling Assets
1. Under **Tenant Management**, click on **Tenant List** and select your tenant.
2. In the **Tenant Edit** pane, adjust the styling variables:
   - **Primary Color Hex**: E.g. `#E53935` (Surya Crimson Red)
   - **Secondary Color Hex**: E.g. `#1E88E5` (Fintech Sky Blue)
   - **Brand Logo URL**: Upload your high-resolution vector PNG or SVG logo.
3. Set the **Default Commission Sharing Rule**:
   - Set distributor commission percentage splits (e.g. 10%).
   - Set retailer commission shares (e.g. 90%).
4. Click **Deploy Dynamic Styles**. This instantly flushes the Redis tenant cache, loading the updated visual parameters across all active user devices in real time.

---

## 3. AUDITING THE SECURITY OPERATIONS CENTER (SOC)

The SOC dashboard aggregates real-time metrics and security warnings to protect the system against fraud.

### 3.1 Security Threat Levels
- **Active Failure Spike**: Triggers if a client IP addresses fail more than 5 login attempts within 1 minute.
- **DMT Rate limit warnings**: Flags accounts making high-volume transfers to randomized accounts within a short period.
- **Database Parity Checks**: Automatically runs matching verification schedules to confirm that `SUM(debit) == SUM(credit)` across all active double-entry accounts.

### 3.2 Imposing Temporary Client Lockouts
1. In the **SOC Dashboard**, navigate to the **Active Threat Log**.
2. If an IP address displays high-volume API requests indicative of a denial-of-service attempt:
   - Click the **Ban IP Address** button next to the suspicious record.
   - Specify the ban duration (e.g., 2 hours, 24 hours, or permanent).
   - Enter a reason (e.g., `"Suspicious failed login loop"`).
3. The firewall rules instantly deploy to the NGINX reverse-proxy layers, blocking all traffic from the specified IP.

---

## 4. RESOLVING USER DISPUTES & CHARGEBACKS

When transaction failures occur, administrators can easily audit and resolve disputes manually using the **Support CRM Console**:

1. Under **Support Desk**, search for the ticket ID or customer email reported by the distributor/retailer.
2. Review the transaction parameters listed in the **Trace Panel**.
3. If a transaction is stuck with a status of `PENDING` but the customer's account has been verified as debited:
   - Click **Run Manual Reconciliation**. This queries the payment gateway provider's API directly to verify the real status.
   - If the payment was successful, click **Force Balance Allocation**. The wallet is credited, double-entry bookkeeping ledgers update, and the ticket is automatically marked as `RESOLVED`.
4. Submit comments explaining the resolution steps and close the ticket.
