import { Injectable, NotFoundException, BadRequestException, Logger } from '@nestjs/common';
import { CreateTicketDto, AddReplyDto, CreateChatSessionDto, SendChatMessageDto, BroadcastCampaignDto, SubmitFeedbackDto } from './support.dto';

export interface SupportTicketRecord {
  id: string;
  ticketNumber: string;
  tenantId: string;
  customerId: string;
  customerName: string;
  customerRole: string;
  subject: string;
  description: string;
  category: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  status: 'OPEN' | 'PENDING' | 'ESCALATED' | 'RESOLVED' | 'CLOSED';
  assignedAgentId: string | null;
  department: string;
  slaDueDate: Date;
  satisfactionRating: number | null;
  resolutionNotes: string | null;
  mergedIntoId: string | null;
  createdAt: Date;
  updatedAt: Date;
  replies: Array<{
    id: string;
    senderId: string;
    senderName: string;
    senderRole: 'AGENT' | 'CUSTOMER';
    message: string;
    isInternalNote: boolean;
    createdAt: Date;
  }>;
  attachments: Array<{
    id: string;
    fileName: string;
    fileUrl: string;
    fileType: string;
    fileSize: number;
    createdAt: Date;
  }>;
}

export interface ChatSessionRecord {
  id: string;
  tenantId: string;
  customerId: string;
  customerName: string;
  customerRole: string;
  assignedAgentId: string | null;
  status: 'ACTIVE' | 'TRANSFERRED' | 'RESOLVED' | 'OFFLINE';
  department: string;
  createdAt: Date;
  updatedAt: Date;
  messages: Array<{
    id: string;
    senderId: string;
    senderName: string;
    senderRole: 'AGENT' | 'CUSTOMER';
    message: string;
    fileUrl: string | null;
    fileType: string | null;
    isRead: boolean;
    createdAt: Date;
  }>;
}

export interface KnowledgeArticleRecord {
  id: string;
  tenantId: string;
  title: string;
  content: string;
  category: string; // WALLET_GUIDE, SECURITY, TRANSACTION_TROUBLE, FAQ
  popular: boolean;
  videoUrl: string | null;
  helpfulCount: number;
  unhelpfulCount: number;
  createdAt: Date;
  updatedAt: Date;
}

export interface BroadcastReport {
  id: string;
  tenantId: string;
  campaignName: string;
  channel: 'PUSH' | 'SMS' | 'EMAIL' | 'WHATSAPP';
  subject: string | null;
  body: string;
  segment: string;
  totalSent: number;
  deliveryRate: number;
  readRate: number;
  createdAt: Date;
}

@Injectable()
export class SupportService {
  private readonly logger = new Logger('SuryaSupportCenter');

  // In-Memory database mimicking schema tables
  private tickets: SupportTicketRecord[] = [
    {
      id: 'tkt_10182',
      ticketNumber: 'TKT-1001',
      tenantId: 'tenant-default',
      customerId: 'cust_9018',
      customerName: 'Shankar Gowda',
      customerRole: 'RETAILER',
      subject: 'AEPS Biometric Timeout on ICICI terminal',
      description: 'Customer fingerprint scanned but txn failed with code 500. Cash debited from bank account.',
      category: 'AEPS_PAYMENTS',
      priority: 'HIGH',
      status: 'OPEN',
      assignedAgentId: 'agent_vicky',
      department: 'GENERAL',
      slaDueDate: new Date(Date.now() + 4 * 60 * 60 * 1000), // 4 Hours SLA
      satisfactionRating: null,
      resolutionNotes: null,
      mergedIntoId: null,
      createdAt: new Date('2026-07-03T11:00:00Z'),
      updatedAt: new Date('2026-07-03T11:00:00Z'),
      replies: [
        {
          id: 'rep_01',
          senderId: 'cust_9018',
          senderName: 'Shankar Gowda',
          senderRole: 'CUSTOMER',
          message: 'System dropped connection immediately after sensor scan.',
          isInternalNote: false,
          createdAt: new Date('2026-07-03T11:02:00Z'),
        }
      ],
      attachments: [
        {
          id: 'att_01',
          fileName: 'ae_biometric_err.png',
          fileUrl: 'https://images.unsplash.com/photo-1557683316-973673baf926?q=80&w=300',
          fileType: 'IMAGE',
          fileSize: 142012,
          createdAt: new Date('2026-07-03T11:00:00Z'),
        }
      ],
    },
    {
      id: 'tkt_8123',
      ticketNumber: 'TKT-1002',
      tenantId: 'tenant-default',
      customerId: 'cust_4124',
      customerName: 'Ramesh Hegde',
      customerRole: 'DISTRIBUTOR',
      subject: 'Corporate credit allocation increase proposal',
      description: 'Requesting buffer extension of ₹5,00,000 for monsoon high transactions bulk season.',
      category: 'CREDIT',
      priority: 'MEDIUM',
      status: 'RESOLVED',
      assignedAgentId: 'agent_suresh',
      department: 'RECOVERY',
      slaDueDate: new Date('2026-07-04T12:00:00Z'),
      satisfactionRating: 5,
      resolutionNotes: 'Credit history verified. Limit raised after standard collateral review.',
      mergedIntoId: null,
      createdAt: new Date('2026-07-02T10:00:00Z'),
      updatedAt: new Date('2026-07-02T16:00:00Z'),
      replies: [
        {
          id: 'rep_02',
          senderId: 'agent_suresh',
          senderName: 'Suresh Kumar',
          senderRole: 'AGENT',
          message: 'Proposal approved by central billing. Raising credit line now.',
          isInternalNote: false,
          createdAt: new Date('2026-07-02T15:30:00Z'),
        }
      ],
      attachments: [],
    },
  ];

  private chatSessions: ChatSessionRecord[] = [
    {
      id: 'chat_9281',
      tenantId: 'tenant-default',
      customerId: 'cust_9018',
      customerName: 'Shankar Gowda',
      customerRole: 'RETAILER',
      assignedAgentId: 'agent_vicky',
      status: 'ACTIVE',
      department: 'GENERAL',
      createdAt: new Date('2026-07-03T11:30:00Z'),
      updatedAt: new Date('2026-07-03T11:45:00Z'),
      messages: [
        {
          id: 'msg_01',
          senderId: 'cust_9018',
          senderName: 'Shankar Gowda',
          senderRole: 'CUSTOMER',
          message: 'Hey, my transaction RRN 29103982 failed but cash was debited.',
          fileUrl: null,
          fileType: null,
          isRead: true,
          createdAt: new Date('2026-07-03T11:30:00Z'),
        },
        {
          id: 'msg_02',
          senderId: 'agent_vicky',
          senderName: 'Vicky Support Agent',
          senderRole: 'AGENT',
          message: 'Checking with central NPCI switch. Please hold.',
          fileUrl: null,
          fileType: null,
          isRead: true,
          createdAt: new Date('2026-07-03T11:32:00Z'),
        },
      ],
    },
  ];

  private kbArticles: KnowledgeArticleRecord[] = [
    {
      id: 'art_01',
      tenantId: 'tenant-default',
      title: 'How to Resolve IMPS Bank Settlement Delays',
      content: 'Sometimes bank settlement routes experience technical failures. Wait 15 minutes for automated reconciliation before re-triggering payouts.',
      category: 'TRANSACTION_TROUBLE',
      popular: true,
      videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4',
      helpfulCount: 142,
      unhelpfulCount: 2,
      createdAt: new Date('2026-01-01'),
      updatedAt: new Date(),
    },
    {
      id: 'art_02',
      tenantId: 'tenant-default',
      title: 'Aadhaar Card Biometric Scanning Guidelines',
      content: 'Clean the finger scanning glass surface and adjust light contrast. Ensure the scanner driver status displays "ACTIVE" in system settings.',
      category: 'WALLET_GUIDE',
      popular: false,
      videoUrl: null,
      helpfulCount: 89,
      unhelpfulCount: 5,
      createdAt: new Date('2026-02-15'),
      updatedAt: new Date(),
    },
  ];

  private feedbackLogs: SubmitFeedbackDto[] = [];

  private campaignReports: BroadcastReport[] = [
    {
      id: 'cmp_july_promo',
      tenantId: 'tenant-default',
      campaignName: 'Recharge Cashback Fest',
      channel: 'WHATSAPP',
      subject: null,
      body: 'Get 5% instant cashback on BBPS utility payments today!',
      segment: 'RETAILERS',
      totalSent: 1420,
      deliveryRate: 98.4,
      readRate: 85.2,
      createdAt: new Date('2026-07-02T10:00:00Z'),
    },
  ];

  // SLA Configuration Rule lookup
  private slaRules: Record<string, number> = {
    LOW: 48,      // 48 hours
    MEDIUM: 24,   // 24 hours
    HIGH: 4,      // 4 hours
    URGENT: 1,    // 1 hour
  };

  // ----------------- TICKET MANAGEMENT OPERATIONS -----------------

  async getAllTickets(tenantId: string): Promise<SupportTicketRecord[]> {
    return this.tickets.filter(t => t.tenantId === tenantId);
  }

  async getTicketById(id: string): Promise<SupportTicketRecord> {
    const tkt = this.tickets.find(t => t.id === id);
    if (!tkt) {
      throw new NotFoundException(`Support Ticket ${id} not found.`);
    }
    return tkt;
  }

  async createTicket(tenantId: string, dto: CreateTicketDto): Promise<SupportTicketRecord> {
    const ticketSeq = 1000 + this.tickets.length + 1;
    const ticketNumber = `TKT-${ticketSeq}`;
    
    // SLA Calculation
    const slaHours = this.slaRules[dto.priority] || 24;
    const slaDueDate = new Date(Date.now() + slaHours * 60 * 60 * 1000);

    const newTicket: SupportTicketRecord = {
      id: `tkt_${Math.floor(100000 + Math.random() * 900000)}`,
      ticketNumber,
      tenantId,
      customerId: dto.customerId,
      customerName: 'SaaS Partner User',
      customerRole: dto.customerRole,
      subject: dto.subject,
      description: dto.description,
      category: dto.category,
      priority: dto.priority,
      status: 'OPEN',
      assignedAgentId: null, // Auto Assign simulation below
      department: dto.category === 'CREDIT' ? 'RECOVERY' : 'GENERAL',
      slaDueDate,
      satisfactionRating: null,
      resolutionNotes: null,
      mergedIntoId: null,
      createdAt: new Date(),
      updatedAt: new Date(),
      replies: [],
      attachments: [],
    };

    // Auto routing SLA logic: assign to first available support tier agent
    if (dto.priority === 'URGENT') {
      newTicket.assignedAgentId = 'escalation_manager_tony';
      newTicket.status = 'ESCALATED';
      this.logger.warn(`Urgent Ticket Raised! Escalated & Routed instantly to Tony.`);
    } else {
      newTicket.assignedAgentId = 'agent_vicky';
    }

    this.tickets.unshift(newTicket);
    return newTicket;
  }

  async addReply(ticketId: string, dto: AddReplyDto): Promise<SupportTicketRecord> {
    const ticket = await this.getTicketById(ticketId);
    
    const reply = {
      id: `rep_${Date.now()}`,
      senderId: dto.senderId,
      senderName: dto.senderName,
      senderRole: dto.senderRole,
      message: dto.message,
      isInternalNote: dto.isInternalNote || false,
      createdAt: new Date(),
    };

    ticket.replies.push(reply);
    ticket.updatedAt = new Date();

    // If customer replies, auto-reopen from resolved/pending states
    if (dto.senderRole === 'CUSTOMER' && (ticket.status === 'RESOLVED' || ticket.status === 'PENDING')) {
      ticket.status = 'OPEN';
    }

    return ticket;
  }

  async updateTicketStatus(id: string, status: 'OPEN' | 'PENDING' | 'ESCALATED' | 'RESOLVED' | 'CLOSED', notes?: string): Promise<SupportTicketRecord> {
    const ticket = await this.getTicketById(id);
    ticket.status = status;
    if (notes) {
      ticket.resolutionNotes = notes;
    }
    ticket.updatedAt = new Date();
    this.logger.log(`Ticket ${ticket.ticketNumber} marked as ${status}`);
    return ticket;
  }

  async mergeTickets(sourceId: string, destinationId: string): Promise<any> {
    const src = await this.getTicketById(sourceId);
    const dest = await this.getTicketById(destinationId);

    src.status = 'CLOSED';
    src.mergedIntoId = dest.id;
    src.updatedAt = new Date();

    this.logger.warn(`Ticket Merged: ${src.ticketNumber} into ${dest.ticketNumber}`);
    return {
      sourceTicketId: sourceId,
      destinationTicketId: destinationId,
      message: `System closed and nested all histories of ${src.ticketNumber} into ${dest.ticketNumber}.`,
    };
  }

  // ----------------- LIVE CHAT OPERATIONS -----------------

  async getAllChatSessions(tenantId: string): Promise<ChatSessionRecord[]> {
    return this.chatSessions.filter(c => c.tenantId === tenantId);
  }

  async getChatSessionById(id: string): Promise<ChatSessionRecord> {
    const session = this.chatSessions.find(c => c.id === id);
    if (!session) {
      throw new NotFoundException(`Chat session ${id} not found.`);
    }
    return session;
  }

  async createChatSession(tenantId: string, dto: CreateChatSessionDto): Promise<ChatSessionRecord> {
    const newSession: ChatSessionRecord = {
      id: `chat_${Math.floor(100000 + Math.random() * 900000)}`,
      tenantId,
      customerId: dto.customerId,
      customerName: dto.customerName,
      customerRole: dto.customerRole,
      assignedAgentId: 'agent_vicky',
      status: 'ACTIVE',
      department: dto.department || 'GENERAL',
      createdAt: new Date(),
      updatedAt: new Date(),
      messages: [],
    };

    this.chatSessions.unshift(newSession);
    return newSession;
  }

  async sendChatMessage(sessionId: string, dto: SendChatMessageDto): Promise<ChatSessionRecord> {
    const session = await this.getChatSessionById(sessionId);
    
    const message = {
      id: `msg_${Date.now()}`,
      senderId: dto.senderId,
      senderName: dto.senderName,
      senderRole: dto.senderRole,
      message: dto.message,
      fileUrl: dto.fileUrl || null,
      fileType: dto.fileType || null,
      isRead: false,
      createdAt: new Date(),
    };

    session.messages.push(message);
    session.updatedAt = new Date();
    
    return session;
  }

  async endChatSession(sessionId: string): Promise<ChatSessionRecord> {
    const session = await this.getChatSessionById(sessionId);
    session.status = 'RESOLVED';
    session.updatedAt = new Date();
    return session;
  }

  // ----------------- KNOWLEDGE BASE GUIDES -----------------

  async getArticles(tenantId: string, category?: string, search?: string): Promise<KnowledgeArticleRecord[]> {
    let list = this.kbArticles.filter(a => a.tenantId === tenantId);
    if (category) {
      list = list.filter(a => a.category === category);
    }
    if (search) {
      const query = search.toLowerCase();
      list = list.filter(a => a.title.toLowerCase().includes(query) || a.content.toLowerCase().includes(query));
    }
    return list;
  }

  async rateArticle(id: string, helpful: boolean): Promise<any> {
    const art = this.kbArticles.find(a => a.id === id);
    if (!art) throw new NotFoundException('Article not found.');
    if (helpful) {
      art.helpfulCount += 1;
    } else {
      art.unhelpfulCount += 1;
    }
    return art;
  }

  // ----------------- CRM INTEGRATION GATEWAY -----------------

  async getCrmCustomerProfile(tenantId: string, customerId: string): Promise<any> {
    // Simulated CRM telemetry fetch
    return {
      customerId,
      name: 'Shankar Gowda',
      role: 'RETAILER',
      company: 'Gowda Digital Services',
      kycStatus: 'APPROVED',
      creditStatus: 'ACTIVE',
      creditLimit: 250000,
      walletBalance: 84210.00,
      transactionSummary: {
        totalTxnCount: 891,
        totalVolume: 412980.00,
        successRate: 98.4,
      },
      communicationTimeline: [
        { event: 'KYC_VERIFICATION_PASSED', date: '2026-03-20', details: 'Aadhaar biometric validated automatically.' },
        { event: 'WALLET_FUND_ADDED', date: '2026-07-03', details: 'Added ₹20,000 via UPI portal gateway.' },
      ],
      associatedTickets: this.tickets.filter(t => t.customerId === customerId),
    };
  }

  // ----------------- OMNICHANNEL CRM CAMPAIGNS -----------------

  async triggerBroadcast(tenantId: string, dto: BroadcastCampaignDto): Promise<BroadcastReport> {
    const totalSim = dto.segment === 'RETAILERS' ? 1420 : dto.segment === 'FRANCHISES' ? 45 : 850;
    
    const freshReport: BroadcastReport = {
      id: `cmp_${Date.now()}`,
      tenantId,
      campaignName: dto.campaignName,
      channel: dto.channel,
      subject: dto.subject || null,
      body: dto.body,
      segment: dto.segment,
      totalSent: totalSim,
      deliveryRate: 99.1,
      readRate: dto.channel === 'SMS' ? 95.0 : dto.channel === 'WHATSAPP' ? 88.4 : 32.5,
      createdAt: new Date(),
    };

    this.campaignReports.unshift(freshReport);
    this.logger.log(`Broadcast campaign dispatched successfully. Channel: ${dto.channel}. Sent Count: ${totalSim}`);
    return freshReport;
  }

  async getCampaignReports(tenantId: string): Promise<BroadcastReport[]> {
    return this.campaignReports.filter(c => c.tenantId === tenantId);
  }

  // ----------------- FEEDBACK & CSAT METRICS -----------------

  async submitFeedback(dto: SubmitFeedbackDto): Promise<any> {
    this.feedbackLogs.push(dto);
    return {
      status: 'SUCCESS',
      message: 'Feedback rating collected for support telemetry dashboard optimization.',
    };
  }

  // ----------------- ANALYTICS STATS -----------------

  async getSupportDashboardStats(tenantId: string): Promise<any> {
    const list = this.tickets.filter(t => t.tenantId === tenantId);
    const chats = this.chatSessions.filter(c => c.tenantId === tenantId);

    const openCount = list.filter(t => t.status === 'OPEN').length;
    const pendingCount = list.filter(t => t.status === 'PENDING').length;
    const escalatedCount = list.filter(t => t.status === 'ESCALATED').length;
    const resolvedCount = list.filter(t => t.status === 'RESOLVED' || t.status === 'CLOSED').length;

    // Average rating
    const rated = list.filter(t => t.satisfactionRating !== null);
    const avgCsat = rated.length > 0 ? rated.reduce((acc, curr) => acc + (curr.satisfactionRating || 0), 0) / rated.length : 4.8;

    return {
      ticketMetrics: {
        total: list.length,
        open: openCount,
        pending: pendingCount,
        escalated: escalatedCount,
        resolved: resolvedCount,
      },
      slaSLAComplianceRate: 96.5,
      avgResponseTimeMinutes: 12.4,
      avgResolutionTimeHours: 1.8,
      customerSatisfactionRating: avgCsat,
      liveAgentAvailability: {
        activeChats: chats.filter(c => c.status === 'ACTIVE').length,
        agentsOnline: 8,
      },
    };
  }
}
