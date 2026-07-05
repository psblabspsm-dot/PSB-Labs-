import { Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { Strategy } from 'passport-microsoft';

@Injectable()
export class MicrosoftStrategy extends PassportStrategy(Strategy, 'microsoft') {
  constructor() {
    super({
      clientID: process.env.MICROSOFT_CLIENT_ID || 'mock-microsoft-client-id-2026',
      clientSecret: process.env.MICROSOFT_CLIENT_SECRET || 'mock-microsoft-client-secret-1234',
      callbackURL: 'http://localhost:3000/api/v1/auth/microsoft/callback',
      scope: ['user.read'],
      tenant: 'common', // Supports multi-tenant enterprise sign-in
    });
  }

  async validate(
    accessToken: string,
    refreshToken: string,
    profile: any,
    done: any,
  ): Promise<any> {
    const { displayName, emails, id } = profile;
    const user = {
      microsoftId: id,
      email: emails && emails[0] ? emails[0].value : `${id}@microsoft-enterprise.com`,
      fullName: displayName,
      accessToken,
    };
    done(null, user);
  }
}
