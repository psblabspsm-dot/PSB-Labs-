import { Test, TestingModule } from '@nestjs/testing';
import { JwtService } from '@nestjs/jwt';
import { AuthService, Role } from './auth.service';
import { UnauthorizedException, ForbiddenException } from '@nestjs/common';

describe('AuthService (Surya B2B FinTech Core)', () => {
  let service: AuthService;
  let jwtService: JwtService;

  const mockJwtService = {
    sign: jest.fn().mockReturnValue('mock-jwt-token-signature-value-2026'),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,
        {
          provide: JwtService,
          useValue: mockJwtService,
        },
      ],
    }).compile();

    service = module.get<AuthService>(AuthService);
    jwtService = module.get<JwtService>(JwtService);
  });

  it('should be defined and initialized', () => {
    expect(service).toBeDefined();
  });

  describe('validateUserCredentials', () => {
    it('should successfully validate distributor credentials and return payload', async () => {
      const result = await service.validateUserCredentials('9876543210', '9281');
      expect(result).toBeDefined();
      expect(result.role).toBe(Role.DISTRIBUTOR);
      expect(result.email).toBe('distributor@suryacredit.com');
      expect(result.id).toBe('usr-dist-02');
    });

    it('should successfully validate retailer credentials and return parent distributor links', async () => {
      const result = await service.validateUserCredentials('9000110022', '9281');
      expect(result).toBeDefined();
      expect(result.role).toBe(Role.RETAILER);
      expect(result.parentId).toBe('usr-dist-02');
    });

    it('should throw UnauthorizedException for non-existent mobile numbers', async () => {
      await expect(
        service.validateUserCredentials('9999999999', '1234'),
      ).rejects.toThrow(UnauthorizedException);
    });
  });

  describe('handleSsoLogin', () => {
    it('should successfully map existing enterprise email users without duplication', async () => {
      const existingUser = {
        email: 'distributor@suryacredit.com',
        fullName: 'Ramesh Pai Distributors',
      };
      const result = await service.handleSsoLogin(existingUser, 'GOOGLE');
      expect(result.accessToken).toBe('mock-jwt-token-signature-value-2026');
      expect(result.user.role).toBe(Role.DISTRIBUTOR);
    });

    it('should auto-onboard unknown SSO profiles as Retailer partners mapped to direct distributor', async () => {
      const newUser = {
        email: 'new_kiosk_partner@gmail.com',
        firstName: 'Vijay',
        lastName: 'Mallya',
      };
      const result = await service.handleSsoLogin(newUser, 'MICROSOFT');
      expect(result.accessToken).toBe('mock-jwt-token-signature-value-2026');
      expect(result.user.role).toBe(Role.RETAILER);
    });
  });

  describe('generateToken', () => {
    it('should create high-fidelity signed JWT payloads with 8 hours expirations', async () => {
      const mockPayload = {
        id: 'usr-dist-02',
        email: 'distributor@suryacredit.com',
        phoneNumber: '9876543210',
        role: Role.DISTRIBUTOR,
      };
      const result = await service.generateToken(mockPayload);
      expect(result.accessToken).toBe('mock-jwt-token-signature-value-2026');
      expect(result.expiresIn).toBe('8h');
      expect(result.tokenType).toBe('Bearer');
    });
  });
});
