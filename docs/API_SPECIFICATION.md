# SURYA CREDIT SOLUTIONS: REST API & INTERFACE SPECIFICATION

This document provides the complete API specifications, validation rules, OpenAPI 3.0 blueprints, and standard Postman collection payload blocks for **Surya Credit Solutions** (Release RC1).

---

## 1. GLOBAL PLATFORM API CONVENTIONS

### 1.1 Base URL & Routing Context
- **Staging / Sandbox Endpoint**: `https://api-staging.suryacredit.in/api/v1`
- **Production Server Endpoint**: `https://api.suryacredit.in/api/v1`
- **Protocol**: Standard HTTPS forced via HSTS. All payload formats must be `application/json`.

### 1.2 Mandatory Request Headers
Every secure API request must submit the following header metadata:
```http
Authorization: Bearer <jwt_access_token>
Content-Type: application/json
X-Tenant-ID: tenant_partner_a
X-Device-ID: dev_90a827cfb
X-Correlation-ID: req_823a098ef1
```

---

## 2. API ENDPOINTS DICTIONARY

### 2.1 Domain Module: Authentication & IAM

#### A. Login Session Initiation
- **Endpoint**: `POST /auth/login`
- **Authentication**: `NONE`
- **Request Body**:
  ```json
  {
    "email": "agent.ram@suryacredit.in",
    "password": "SecurePassword123!",
    "deviceId": "dev_90a827cfb"
  }
  ```
- **Response Payload (HTTP 200)**:
  ```json
  {
    "success": true,
    "accessToken": "eyJhbGciOiJIUzI1NiIsIn...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5...",
    "expiresIn": 1800,
    "user": {
      "id": "usr_990182",
      "name": "Ram Prasad",
      "role": "RETAILER",
      "tenantId": "tenant_partner_a"
    }
  }
  ```

#### B. MPIN Security Validation
- **Endpoint**: `POST /auth/verify-mpin`
- **Authentication**: `Bearer Token`
- **Request Body**:
  ```json
  {
    "mpin": "9921",
    "transactionId": "txn_pay_891028"
  }
  ```
- **Response Payload (HTTP 200)**:
  ```json
  {
    "status": "APPROVED",
    "verificationToken": "mpin_verify_token_abc123",
    "validatedAt": "2026-07-03T20:30:11Z"
  }
  ```

---

### 2.2 Domain Module: Payments & Gateway Switching

#### A. Initiate Gateway Transaction
- **Endpoint**: `POST /payments/initiate`
- **Authentication**: `Bearer Token`
- **Request Body**:
  ```json
  {
    "orderId": "order_8921028",
    "amount": 15000.50,
    "gateway": "RAZORPAY",
    "callbackUrl": "https://callback.suryacredit.in/checkout"
  }
  ```
- **Response Payload (HTTP 200)**:
  ```json
  {
    "success": true,
    "txnId": "txn_pay_281029",
    "gateway": "RAZORPAY",
    "checkoutUrl": "https://api.razorpay.com/v1/checkout/pay?token=rzp_pay_902",
    "amount": 15000.50,
    "sdkConfig": {
      "key": "rzp_live_SuryaCreditLive2026",
      "order_id": "order_rzp_89201"
    }
  }
  ```

#### B. Process Inward Aggregator Webhook
- **Endpoint**: `POST /payments/webhook/:gateway` (e.g. `/payments/webhook/razorpay`)
- **Authentication**: `Signature Verified (Headers)`
- **Headers**:
  ```http
  x-razorpay-signature: hex_hmac_signature_value
  ```
- **Response Payload (HTTP 200)**:
  ```json
  {
    "webhookHandshakeStatus": "SUCCESSFUL",
    "processedAt": "2026-07-03T20:30:45.000Z",
    "auditLogged": true
  }
  ```

---

## 3. OPENAPI (SWAGGER) SPECIFICATION

The OpenAPI 3.0 blueprint modeling the critical billing and payment gateway module is represented below:

```yaml
openapi: 3.0.3
info:
  title: Surya Credit Solutions core Gateway API
  description: Enterprise B2B payment gateway aggregator and settlement switch interface.
  version: 1.0.0-rc1
paths:
  /payments/initiate:
    post:
      summary: Initiate dynamic gateway checkout session
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - orderId
                - amount
                - gateway
              properties:
                orderId:
                  type: string
                amount:
                  type: number
                gateway:
                  type: string
                  enum: [RAZORPAY, CASHFREE, PINELABS, CCAVENUE, PAYTM, ZAAKPAY]
      responses:
        '200':
          description: Session initiated successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  success:
                    type: boolean
                  txnId:
                    type: string
                  checkoutUrl:
                    type: string
components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```

---

## 4. POSTMAN COLLECTION V2.1 EXPORT STRUCTURE

Developers can import this raw JSON string block directly into Postman to load pre-configured endpoint variables, authentication, and integration tests.

```json
{
  "info": {
    "_postman_id": "76dfd28c-482a-4318-ae20-fc962a98f121",
    "name": "Surya Credit Solutions RC1 Suite",
    "description": "Enterprise API functional collection for integration audits, checkout testing, and sandbox verification.",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "IAM Auth",
      "item": [
        {
          "name": "User Session Login",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"agent.ram@suryacredit.in\",\n  \"password\": \"SecurePassword123!\",\n  \"deviceId\": \"dev_90a827cfb\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/auth/login",
              "host": ["{{base_url}}"],
              "path": ["auth", "login"]
            }
          }
        }
      ]
    },
    {
      "name": "Payments Routing Switch",
      "item": [
        {
          "name": "Initiate Checkout",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwt_token}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"orderId\": \"order_2901238\",\n  \"amount\": 8500.00,\n  \"gateway\": \"RAZORPAY\"\n}"
            },
            "url": {
              "raw": "{{base_url}}/payments/initiate",
              "host": ["{{base_url}}"],
              "path": ["payments", "initiate"]
            }
          }
        }
      ]
    }
  ],
  "event": [
    {
      "listen": "prerequest",
      "script": {
        "type": "text/javascript",
        "exec": [
          "pm.environment.set(\"base_url\", \"https://api-staging.suryacredit.in/api/v1\");"
        ]
      }
    }
  ]
}
```
