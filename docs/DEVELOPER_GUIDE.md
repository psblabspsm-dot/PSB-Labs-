# SURYA CREDIT SOLUTIONS: MASTER DEVELOPER'S GUIDE

This document serves as the primary technical onboarding guide and architectural handbook for software engineers maintaining, testing, and expanding the **Surya Credit Solutions** codebase.

---

## 1. LOCAL WORKSPACE BOOTSTRAP

To set up a local development sandbox, follow these steps to initialize the environment:

### 1.1 Prerequisites
Ensure the following runtimes and services are installed locally:
- **Node.js**: v18.16.0 or higher
- **Docker & Docker Compose**: v2.20.0+
- **Flutter SDK**: v3.13.0+ (Dart SDK 3.1.0+)
- **PostgreSQL**: v15 (if running natively outside Docker)
- **Redis**: v7 (if running natively outside Docker)

### 1.2 Local Setup Sequence
1. Clone the repository and navigate to the project root:
   ```bash
   git clone https://github.com/suryacredit/platform.git
   cd platform
   ```
2. Configure local environment variables:
   ```bash
   cp .env.example .env
   ```
3. Boot local databases and caching containers:
   ```bash
   docker compose -f docker-compose.yml up -d
   ```
4. Initialize the NestJS backend workspace:
   ```bash
   cd backend
   npm ci
   npx prisma generate
   npx prisma migrate dev --name init_local_schema
   npm run start:dev
   ```
5. Spin up the React administrative dashboard:
   ```bash
   cd ../react-admin
   npm install
   npm start
   ```
6. Run the Flutter mobile simulator client:
   ```bash
   cd ../flutter
   flutter pub get
   flutter run
   ```

---

## 2. PROJECT ARCHITECTURAL PRINCIPLES

The system follows a strict, domain-driven MVVM (Model-View-ViewModel) architecture on the clients and clean multi-layered services on the backend.

```
       [Client Views / UI Screen]
                  │
                  ▼
         [State ViewModels]
                  │
                  ▼
  [Services (HTTP / Repository Clients)]
                  │
                  ▼
       [Backend Entry Controllers]
                  │
                  ▼
    [Business Domain Modules / DTOs]
                  │
                  ▼
      [Prisma ORM Database Tier]
```

### 2.1 Backend Coding Standards (NestJS & Prisma)
- **DTO Validation**: Every incoming request body MUST be strictly mapped to a class decorated with `class-validator` operators (e.g. `@IsEmail()`, `@IsDecimal()`, `@IsUUID()`).
- **Atomic Database Operations**: Wrap database writes involving ledger balances or wallet adjustments inside an interactive transaction block (`prisma.$transaction`) to prevent dirty-read concurrency races.
- **Dependency Injection**: Leverage NestJS constructor injection exclusively. Avoid manual instantiation of singleton services.

---

## 3. WRITING COMPREHENSIVE PLATFORM UNIT TESTS

All business logic must maintain test coverage. Unit tests reside alongside files under `.spec.ts` naming patterns.

### 3.1 Executing Backend Unit Tests
```bash
cd backend
# Run all unit tests
npm run test

# Run unit tests with coverage reporting
npm run test:cov
```

### 3.2 Mocking Prisma inside Services
Use the following pattern to mock database queries safely inside NestJS unit tests:

```typescript
import { Test, TestingModule } from '@nestjs/testing';
import { WalletService } from './wallet.service';
import { PrismaService } from '../prisma/prisma.service';

describe('WalletService', () => {
  let service: WalletService;
  let prismaMock: any;

  beforeEach(async () => {
    prismaMock = {
      wallet: {
        findUnique: jest.fn(),
        update: jest.fn(),
      },
    };

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        WalletService,
        { provide: PrismaService, useValue: prismaMock },
      ],
    }).compile();

    service = module.get<WalletService>(WalletService);
  });

  it('should prevent debiting beyond wallet balance', async () => {
    prismaMock.wallet.findUnique.mockResolvedValue({ id: 'w1', balance: 100.00 });
    
    await expect(service.debitWallet('w1', 150.00)).rejects.toThrow(
      'Insufficient balance in wallet.'
    );
  });
});
```

---

## 4. DEVELOPER GENERAL BEST PRACTICES & GUIDELINES

1. **No Hardcoded Secrets**: Secrets, keys, and webhook endpoints must be accessed purely through `ConfigService` / `process.env`.
2. **Correlation Header Mapping**: Ensure every outgoing HTTP request propagates the inbound `X-Correlation-ID` header to allow centralized log tracing.
3. **Double-Entry Principle Preservation**: Never run a direct `update` operation modifying user wallet balances without concurrently executing a transactional write to `JournalItem` tables logging the respective balance offsets.
