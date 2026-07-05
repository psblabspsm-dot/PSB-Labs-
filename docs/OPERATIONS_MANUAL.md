# SURYA CREDIT SOLUTIONS: ECOSYSTEM OPERATIONS MANUAL

This document establishes standard operating procedures (SOPs), clearance guides, and daily playbooks for every primary stakeholder and participant inside the **Surya Credit Solutions** ecosystem.

---

## 1. SUPER ADMINISTRATOR PLAYBOOK

### 1.1 Scope of Authority
The Super Admin is the ultimate security authority of the platform. Their clearance permits global multi-tenant workspace provisioning, systemic emergency lockdowns, key rotations, and administrative role assignments.

### 1.2 Step-by-Step Onboarding of a New White-Label Tenant
1. **Step 1: Domain Mapping**
   - Point the tenant's custom CNAME record to the central app-gateway cluster.
   - Run secure HTTPS certificate generation:
     ```bash
     sudo certbot certonly --nginx -d portal.partnerbrand.com
     ```
2. **Step 2: Database Provisioning**
   - Log into the Admin panel (`/admin`).
   - Navigate to the **Tenants Console** and click **Add New Workspace**.
   - Input Tenant Name, Subdomain, Contact Admin Details, and click **Create**.
3. **Step 3: Theme Branding Customization**
   - In **Tenant Edit**, configure custom primary/secondary color schemes, brand logos, custom favicon icons, and transactional commission rates.
   - Click **Deploy Dynamic Styles** to populate the Redis cache.

### 1.3 Emergency Platform Lockdown SOP
If a severe cryptographic breach or systemic attack is detected inside the Security Operations Center (SOC):
- Navigate to the **SOC Controls Console**.
- Click the red **Emergency Platform Freeze** button. This instantly alters a Redis state key, blocking all non-admin write routes and wallet cash-out requests with a secure message: `"Platform is undergoing temporary maintenance."`

---

## 2. FINANCE & ACCOUNTING AUDITOR PLAYBOOK

### 2.1 Double-Entry Bookkeeping Verification
Every transaction must maintain debit-credit ledger equations. The auditor is responsible for verifying ledger integrity.
- **SOP**: Log into the **Finance & Accounting Hub** on React Admin.
- **Action**: Check the **Trial Balance Dashboard**. Ensure the absolute difference between Debits and Credits is exactly `0.00`. If any difference exists, click **Highlight Discrepant Logs** to run an automated audit across all `JournalItem` tables.

### 2.2 Manual Settlement Escalations
If a payment gateway webhook handshaking cycle is disrupted (e.g. Razorpay fails during dynamic status checks):
1. Locate the stuck transaction on the **Settlements Ledger**.
2. Click **Reconcile with Gateway Provider** to automatically poll the provider API logs.
3. If the payment is confirmed on the provider's dashboard but stuck as `INITIATED` locally, select **Force Reconcile Settlement**. This triggers the state promotion flow, crediting the merchant's trade ledger wallet and generating the corresponding double-entry accounting records.

---

## 3. DISTRICT DISTRIBUTOR PLAYBOOK

### 3.1 Managing Regional Retailers (Agents)
Distributors manage trade networks and route physical inventory to regional shops.
- **Action 1: Adjusting Credit Lines**
  - Navigate to the **Merchant Network Panel** inside the Flutter Client.
  - Select the target Retailer profile.
  - Locate **Credit Limit Slabs**. Click **Extend Credit Line**, input the new amount, set the repayment duration (e.g. 15 days), and authorize via biometrics/MPIN.
- **Action 2: Repayments Ledger Auditing**
  - On the retailer summary card, review utilized credit. If a retailer fails to repay within terms, click **Temporarily Suspend Credit Account** to freeze outbound procurements.

---

## 4. ON-FIELD RETAILER (AGENT) DAILY PLAYBOOK

### 4.1 Daily Launch and Device Binding Verification
- Open the **Surya Credit Mobile App**.
- Enter your mobile number. If logging in from a new smartphone, the app prompts: `"New Device Binding Detected"`.
- Complete the dual verification: Input the OTP sent to your registered mobile and verify with your biometric scan.
- Enter your 4-digit **MPIN** to access the dashboard.

### 4.2 Domestic Money Transfer (DMT) Workflow
To send cash on behalf of a walk-in customer:
1. Navigate to the **DMT Money Transfer Card**.
2. Input the Beneficiary's Name, Bank Name, Account Number, and IFSC code.
3. Input the Send Amount (the system calculates dynamic transaction fees and commissions).
4. Tap **Confirm Transfer** and enter your **MPIN**. The wallet balance is instantly debited, routing payment via UPI/IMPS networks.
5. Tap **Generate Thermal Receipt** to print or share the transaction summary.

---

## 5. VENDOR (MARKETPLACE SUPPLIER) PLAYBOOK

### 5.1 Catalog and Order Fulfillment
Vendors publish wholesale products for purchase by regional retailers.
- **SOP 1: Catalog Uploads**
  - Log into the **Vendor Marketplace Portal**.
  - Navigate to **Inventory Manager** -> Click **Add Bulk Products** (or upload CSV).
  - Define name, price, tax rate (GST Slab: 5%, 12%, or 18%), and stock levels.
- **SOP 2: Shipping Orders**
  - Navigate to **Incoming Orders**.
  - Once order payment is validated as `SUCCESSFUL` by the system, select **Mark as Shipped** and input tracking details. This triggers dynamic client-side shipping notifications.

---

## 6. PLATFORM EMPLOYEES & SUPPORT AGENT PLAYBOOK

### 6.1 Multi-Tenant Ticketing Management
Support agents keep platform service levels high.
- **Action 1: Dispatching Support Tickets**
  - Open the **Support Agent Hub** on React Admin.
  - Sort by **SLA Priority** (Tickets past a 2-hour response window auto-escalate to `CRITICAL`).
- **Action 2: Resolving Disputes**
  - Click on a ticket to review history. If the issue is related to a failed wallet transfer, review the attached transaction ID via the built-in **Transaction Trace Panel**.
  - Add resolution comments, select **Mark as Resolved**, and click **Submit Response**.
