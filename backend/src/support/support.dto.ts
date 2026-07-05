import { IsNotEmpty, IsString, IsOptional, IsEnum, IsInt, Min, Max, IsBoolean, IsArray } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class CreateTicketDto {
  @ApiProperty({ example: 'WALLET', description: 'Category of ticket query' })
  @IsString()
  @IsNotEmpty()
  category: string;

  @ApiProperty({ example: 'Urgent wallet reconciliation delay', description: 'Subject' })
  @IsString()
  @IsNotEmpty()
  subject: string;

  @ApiProperty({ example: 'Initiated IMPS transfer but credit not mapped yet.', description: 'Brief description' })
  @IsString()
  @IsNotEmpty()
  description: string;

  @ApiProperty({ example: 'HIGH', description: 'Priority level of ticket' })
  @IsEnum(['LOW', 'MEDIUM', 'HIGH', 'URGENT'])
  @IsNotEmpty()
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

  @ApiProperty({ example: 'RETAILER', description: 'Customer portal segment identity' })
  @IsString()
  @IsNotEmpty()
  customerRole: string;

  @ApiProperty({ example: 'cust_9018', description: 'Customer reference id' })
  @IsString()
  @IsNotEmpty()
  customerId: string;
}

export class AddReplyDto {
  @ApiProperty({ example: 'Please provide bank ref RRN code.', description: 'Message body' })
  @IsString()
  @IsNotEmpty()
  message: string;

  @ApiProperty({ example: false, description: 'Is internal memo note restricted to agents' })
  @IsBoolean()
  @IsOptional()
  isInternalNote?: boolean;

  @ApiProperty({ example: 'agent_007', description: 'Sender ID' })
  @IsString()
  @IsNotEmpty()
  senderId: string;

  @ApiProperty({ example: 'Support Agent Vicky', description: 'Sender Display Name' })
  @IsString()
  @IsNotEmpty()
  senderName: string;

  @ApiProperty({ example: 'AGENT', description: 'Sender Identity Role (AGENT/CUSTOMER)' })
  @IsEnum(['AGENT', 'CUSTOMER'])
  @IsNotEmpty()
  senderRole: 'AGENT' | 'CUSTOMER';
}

export class CreateChatSessionDto {
  @ApiProperty({ example: 'cust_9124', description: 'Customer user reference' })
  @IsString()
  @IsNotEmpty()
  customerId: string;

  @ApiProperty({ example: 'Ramesh Kumar', description: 'Customer Name' })
  @IsString()
  @IsNotEmpty()
  customerName: string;

  @ApiProperty({ example: 'RETAILER', description: 'Role' })
  @IsString()
  @IsNotEmpty()
  customerRole: string;

  @ApiProperty({ example: 'GENERAL', description: 'Associated routing department' })
  @IsString()
  @IsOptional()
  department?: string;
}

export class SendChatMessageDto {
  @ApiProperty({ example: 'Hello, my active session details.', description: 'Content text' })
  @IsString()
  @IsNotEmpty()
  message: string;

  @ApiProperty({ example: 'cust_9124', description: 'Sender reference' })
  @IsString()
  @IsNotEmpty()
  senderId: string;

  @ApiProperty({ example: 'Ramesh Kumar', description: 'Sender Name' })
  @IsString()
  @IsNotEmpty()
  senderName: string;

  @ApiProperty({ example: 'CUSTOMER', description: 'Sender Identity' })
  @IsEnum(['AGENT', 'CUSTOMER'])
  @IsNotEmpty()
  senderRole: 'AGENT' | 'CUSTOMER';

  @ApiProperty({ example: 'https://images.unsplash.com/photo-1599305445671', description: 'Optional Attachment file URL', required: false })
  @IsString()
  @IsOptional()
  fileUrl?: string;

  @ApiProperty({ example: 'IMAGE', description: 'Optional file type', required: false })
  @IsString()
  @IsOptional()
  fileType?: string;
}

export class BroadcastCampaignDto {
  @ApiProperty({ example: 'recharge_promo_july', description: 'Branded campaign slug' })
  @IsString()
  @IsNotEmpty()
  campaignName: string;

  @ApiProperty({ example: 'EMAIL', description: 'Omni-channel routing type' })
  @IsEnum(['PUSH', 'SMS', 'EMAIL', 'WHATSAPP'])
  @IsNotEmpty()
  channel: 'PUSH' | 'SMS' | 'EMAIL' | 'WHATSAPP';

  @ApiProperty({ example: 'Special Monsoon Cashback Wallet Boost!', description: 'Title' })
  @IsString()
  @IsOptional()
  subject?: string;

  @ApiProperty({ example: 'Hi {{name}}, get 5% instant cashback on BBPS utility recharges today!', description: 'Branded notification template' })
  @IsString()
  @IsNotEmpty()
  body: string;

  @ApiProperty({ example: 'RETAILERS', description: 'Campaign segmentation target' })
  @IsString()
  @IsNotEmpty()
  segment: string;
}

export class SubmitFeedbackDto {
  @ApiProperty({ example: 'cust_9012', description: 'Customer reference' })
  @IsString()
  @IsNotEmpty()
  customerId: string;

  @ApiProperty({ example: 'TICKET', description: 'Target element evaluated' })
  @IsString()
  @IsNotEmpty()
  entityType: 'TICKET' | 'CHAT' | 'TRANSACTION';

  @ApiProperty({ example: 'TKT-1002', description: 'UUID reference code' })
  @IsString()
  @IsNotEmpty()
  entityId: string;

  @ApiProperty({ example: 5, description: 'SaaS Customer satisfaction stars (1 to 5)' })
  @IsInt()
  @Min(1)
  @Max(5)
  rating: number;

  @ApiProperty({ example: 'Excellent prompt reconciliation resolution.', description: 'Feedback comments' })
  @IsString()
  @IsOptional()
  comments?: string;
}
