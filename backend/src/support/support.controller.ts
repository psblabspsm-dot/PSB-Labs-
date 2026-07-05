import { Controller, Get, Post, Body, Param, Query, Patch, HttpCode, HttpStatus } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiBody, ApiQuery } from '@nestjs/swagger';
import { SupportService, SupportTicketRecord, ChatSessionRecord, KnowledgeArticleRecord, BroadcastReport } from './support.service';
import { CreateTicketDto, AddReplyDto, CreateChatSessionDto, SendChatMessageDto, BroadcastCampaignDto, SubmitFeedbackDto } from './support.dto';

@ApiTags('Omni-Channel Customer Support & CRM')
@Controller('api/v1/support')
export class SupportController {
  constructor(private readonly supportService: SupportService) {}

  @Get('dashboard/stats')
  @ApiOperation({ summary: 'Fetch unified helpdesk SLA, CSAT, response metrics, and agent KPIs' })
  async getDashboardStats(@Query('tenantId') tenantId: string = 'tenant-default'): Promise<any> {
    return this.supportService.getSupportDashboardStats(tenantId);
  }

  // ----------------- TICKET MANAGEMENT -----------------

  @Get('tickets')
  @ApiOperation({ summary: 'List all customer tickets filed under tenant context' })
  async getTickets(@Query('tenantId') tenantId: string = 'tenant-default'): Promise<SupportTicketRecord[]> {
    return this.supportService.getAllTickets(tenantId);
  }

  @Get('tickets/:id')
  @ApiOperation({ summary: 'Fetch full trace log of individual ticket replies' })
  async getTicketById(@Param('id') id: string): Promise<SupportTicketRecord> {
    return this.supportService.getTicketById(id);
  }

  @Post('tickets')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Submit a new customer/merchant help ticket' })
  async createTicket(
    @Query('tenantId') tenantId: string = 'tenant-default',
    @Body() dto: CreateTicketDto
  ): Promise<SupportTicketRecord> {
    return this.supportService.createTicket(tenantId, dto);
  }

  @Post('tickets/:id/replies')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Submit ticket conversation reply (or internal notes annotation)' })
  async addReply(@Param('id') id: string, @Body() dto: AddReplyDto): Promise<SupportTicketRecord> {
    return this.supportService.addReply(id, dto);
  }

  @Patch('tickets/:id/status')
  @ApiOperation({ summary: 'Update ticket resolution lifecycle states (RESOLVED, CLOSED, ESCALATED)' })
  async updateStatus(
    @Param('id') id: string,
    @Body('status') status: 'OPEN' | 'PENDING' | 'ESCALATED' | 'RESOLVED' | 'CLOSED',
    @Body('notes') notes?: string
  ): Promise<SupportTicketRecord> {
    return this.supportService.updateTicketStatus(id, status, notes);
  }

  @Post('tickets/merge')
  @ApiOperation({ summary: 'Merge duplicate duplicate requests under a single main tracking ticket' })
  async mergeTickets(
    @Body('sourceId') sourceId: string,
    @Body('destinationId') destinationId: string
  ): Promise<any> {
    return this.supportService.mergeTickets(sourceId, destinationId);
  }

  // ----------------- LIVE CHAT CONSOLE -----------------

  @Get('chats')
  @ApiOperation({ summary: 'List active client web-chat sessions for the dashboard agent console' })
  async getChats(@Query('tenantId') tenantId: string = 'tenant-default'): Promise<ChatSessionRecord[]> {
    return this.supportService.getAllChatSessions(tenantId);
  }

  @Post('chats')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Initialize a real-time live chat room session' })
  async createChat(@Query('tenantId') tenantId: string = 'tenant-default', @Body() dto: CreateChatSessionDto): Promise<ChatSessionRecord> {
    return this.supportService.createChatSession(tenantId, dto);
  }

  @Post('chats/:id/messages')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Send message into live session stream' })
  async sendChatMessage(@Param('id') sessionId: string, @Body() dto: SendChatMessageDto): Promise<ChatSessionRecord> {
    return this.supportService.sendChatMessage(sessionId, dto);
  }

  @Post('chats/:id/end')
  @ApiOperation({ summary: 'Mark live session chat as resolved and closed' })
  async endChat(@Param('id') id: string): Promise<ChatSessionRecord> {
    return this.supportService.endChatSession(id);
  }

  // ----------------- KNOWLEDGE BASE GUIDES -----------------

  @Get('kb/articles')
  @ApiOperation({ summary: 'List interactive FAQ guides, self-help articles, and walkthrough tutorials' })
  @ApiQuery({ name: 'category', required: false })
  @ApiQuery({ name: 'search', required: false })
  async getArticles(
    @Query('tenantId') tenantId: string = 'tenant-default',
    @Query('category') category?: string,
    @Query('search') search?: string
  ): Promise<KnowledgeArticleRecord[]> {
    return this.supportService.getArticles(tenantId, category, search);
  }

  @Post('kb/articles/:id/rate')
  @ApiOperation({ summary: 'Collect user feedback analytics (helpful/unhelpful rating) on document quality' })
  async rateArticle(@Param('id') id: string, @Body('helpful') helpful: boolean): Promise<any> {
    return this.supportService.rateArticle(id, helpful);
  }

  // ----------------- CRM PORTAL LINK -----------------

  @Get('crm/customer/:id')
  @ApiOperation({ summary: 'Fetch customer details, transaction history, KYC status, credit line and related support history' })
  async getCrmCustomer(
    @Param('id') customerId: string,
    @Query('tenantId') tenantId: string = 'tenant-default'
  ): Promise<any> {
    return this.supportService.getCrmCustomerProfile(tenantId, customerId);
  }

  // ----------------- CAMPAIGNS BROADCASTER -----------------

  @Get('campaigns')
  @ApiOperation({ summary: 'Fetch recent campaign broadcast metrics logs' })
  async getCampaigns(@Query('tenantId') tenantId: string = 'tenant-default'): Promise<BroadcastReport[]> {
    return this.supportService.getCampaignReports(tenantId);
  }

  @Post('campaigns/broadcast')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Dispatch bulk push/SMS/email broadcast marketing campaigns' })
  async triggerBroadcast(
    @Query('tenantId') tenantId: string = 'tenant-default',
    @Body() dto: BroadcastCampaignDto
  ): Promise<BroadcastReport> {
    return this.supportService.triggerBroadcast(tenantId, dto);
  }

  // ----------------- SATISFACTION FEEDBACK -----------------

  @Post('feedback')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Collect CSAT ratings metrics' })
  async submitFeedback(@Body() dto: SubmitFeedbackDto): Promise<any> {
    return this.supportService.submitFeedback(dto);
  }
}
