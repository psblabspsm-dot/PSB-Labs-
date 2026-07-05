import { Injectable, Logger } from '@nestjs/common';
import * as crypto from 'crypto';

@Injectable()
export class IntegrationService {
  private readonly logger = new Logger('SuryaFinTechEnterpriseIntegrations');

  // SMS OTP local ledger store
  private activeOtps = new Map<string, string>();

  // 1. SMS OTP Integration (Twilio/Msg91 Node REST SDK model)
  async sendSmsOtp(phoneNumber: string): Promise<any> {
    const otp = Math.floor(100000 + Math.random() * 900000).toString();
    this.activeOtps.set(phoneNumber, otp);
    
    this.logger.log(`[SMS OTP] Dispatching 6-digit OTP passcode to +91${phoneNumber}`);
    // Simulate real SMS provider outbound webhook API call (e.g. Msg91 / Twilio REST)
    // await axios.post('https://api.msg91.com/api/v5/otp', { template_id: 'SURYA_OTP', mobile: `91${phoneNumber}`, otp });

    return {
      status: 'DISPATCHED',
      recipient: `+91******${phoneNumber.slice(-4)}`,
      gatewayVendor: 'TELE_MESSAGING_IMPS_NODE',
      expiresInSeconds: 300,
      debugOtpCode: otp, // Returned for sandbox testing ease in front-end
    };
  }

  async verifySmsOtp(phoneNumber: string, code: string): Promise<boolean> {
    const cached = this.activeOtps.get(phoneNumber);
    if (cached && cached === code) {
      this.activeOtps.delete(phoneNumber);
      return true;
    }
    return false;
  }

  // 2. Email Notification (Nodemailer / SendGrid SDK model)
  async sendEmail(to: string, subject: string, htmlContent: string): Promise<any> {
    this.logger.log(`[SMTP EMAIL] Routing transactional email to ${to} subject: "${subject}"`);
    // Simulated SMTP server TLS transport relay
    return {
      messageId: `smtp-${crypto.randomBytes(8).toString('hex')}@suryacredit.com`,
      gateway: 'SENDGRID_SECURE_RELAY',
      accepted: [to],
      timestamp: new Date().toISOString(),
    };
  }

  // 3. Firebase Cloud Messaging (FCM admin SDK client module)
  async pushFirebaseNotification(deviceToken: string, title: string, body: string, dataPayload?: any): Promise<any> {
    this.logger.log(`[FIREBASE FCM] Dispatching high-priority push payload to device token: ${deviceToken.slice(0, 10)}...`);
    // Simulated Firebase Admin SDK cloud messaging dispatch
    // const response = await admin.messaging().send({ token: deviceToken, notification: { title, body } });

    return {
      multicastId: `fcm-${Math.floor(1000000000 + Math.random() * 9000000000)}`,
      successCount: 1,
      failureCount: 0,
      results: [{ messageId: `msg_fcm_${crypto.randomBytes(6).toString('hex')}` }],
    };
  }

  // 4. Google Maps Platform Geolocation & Distance Matrix API
  async getKioskGeorouting(retailerCoords: { lat: number; lng: number }, distributorCoords: { lat: number; lng: number }): Promise<any> {
    this.logger.log(`[GOOGLE MAPS API] Geocoding Distance Matrix route between Retailer & Distributor`);
    
    // Simulate Google Maps Distance Matrix API REST Payload response
    // const url = `https://maps.googleapis.com/maps/api/distancematrix/json?origins=${retailerCoords.lat},${retailerCoords.lng}&destinations=${distributorCoords.lat},${distributorCoords.lng}&key=${process.env.GOOGLE_MAPS_API_KEY}`;
    
    // Calculated mock distance
    const distKm = (Math.random() * 25 + 5).toFixed(2);
    const durationMins = Math.floor(parseFloat(distKm) * 2.2);

    return {
      status: 'OK',
      originAddresses: [`Kiosk Latitude: ${retailerCoords.lat}, Longitude: ${retailerCoords.lng}`],
      destinationAddresses: [`Distributor Warehouse Latitude: ${distributorCoords.lat}, Longitude: ${distributorCoords.lng}`],
      rows: [
        {
          elements: [
            {
              status: 'OK',
              distance: {
                text: `${distKm} km`,
                value: Math.floor(parseFloat(distKm) * 1000),
              },
              duration: {
                text: `${durationMins} mins`,
                value: durationMins * 60,
              },
            },
          ],
        },
      ],
      apiGatewayQuotaCost: '0.005 USD',
    };
  }
}
