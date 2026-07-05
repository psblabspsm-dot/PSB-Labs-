import 'package:flutter/material.dart';

// --- CUSTOM ENUM FOR SUPPORT ROUTING ---
enum TicketPriority { low, medium, high, urgent }
enum TicketStatus { open, pending, escalated, resolved, closed }

class SupportTicket {
  final String id;
  final String ticketNumber;
  final String subject;
  final String description;
  final String category;
  final TicketPriority priority;
  TicketStatus status;
  final DateTime createdAt;
  final String customerName;
  final List<TicketMessage> messages;
  final List<String> attachments;
  int? csatRating;
  String? resolutionNotes;

  SupportTicket({
    required this.id,
    required this.ticketNumber,
    required this.subject,
    required this.description,
    required this.category,
    required this.priority,
    required this.status,
    required this.createdAt,
    required this.customerName,
    required this.messages,
    required this.attachments,
    this.csatRating,
    this.resolutionNotes,
  });
}

class TicketMessage {
  final String senderName;
  final String senderRole; // 'CUSTOMER' or 'AGENT'
  final String message;
  final DateTime createdAt;
  final String? attachmentUrl;

  TicketMessage({
    required this.senderName,
    required this.senderRole,
    required this.message,
    required this.createdAt,
    this.attachmentUrl,
  });
}

class KbArticle {
  final String id;
  final String title;
  final String content;
  final String category;
  final bool popular;
  final String? videoUrl;
  int helpfulCount;
  int unhelpfulCount;

  KbArticle({
    required this.id,
    required this.title,
    required this.content,
    required this.category,
    required this.popular,
    this.videoUrl,
    required this.helpfulCount,
    required this.unhelpfulCount,
  });
}

// --- STATE MANAGEMENT ---
class SupportCenterScreens extends StatefulWidget {
  const SupportCenterScreens({Key? key}) : super(key: key);

  @override
  State<SupportCenterScreens> createState() => _SupportCenterScreensState();
}

class _SupportCenterScreensState extends State<SupportCenterScreens> {
  // In-Memory state mirroring NestJS server
  final List<SupportTicket> _myTickets = [
    SupportTicket(
      id: 'tkt_1001',
      ticketNumber: 'TKT-1001',
      subject: 'IMPS Settlement delayed for Rao Digital',
      description: 'The payout was initiated on June 29 but status remains processing. Merchant claims bank account is not credited.',
      category: 'TRANSACTION_SETTLEMENT',
      priority: TicketPriority.high,
      status: TicketStatus.open,
      createdAt: DateTime.now().subtract(const Duration(hours: 3)),
      customerName: 'Shankar Gowda',
      messages: [
        TicketMessage(
          senderName: 'Shankar Gowda',
          senderRole: 'CUSTOMER',
          message: 'Can someone please expedite? This is stalling morning trades.',
          createdAt: DateTime.now().subtract(const Duration(hours: 3)),
        ),
        TicketMessage(
          senderName: 'Surya Agent Vicky',
          senderRole: 'AGENT',
          message: 'Under investigation. We are verifying the IMPS settlement switch clearance report.',
          createdAt: DateTime.now().subtract(const Duration(hours: 2)),
        )
      ],
      attachments: ['receipt_69102.pdf'],
    ),
    SupportTicket(
      id: 'tkt_1002',
      ticketNumber: 'TKT-1002',
      subject: 'Biometric scanning error on MicroATM terminal',
      description: 'MicroATM biometric keeps outputting "Timeout 500". The scanner light blinks twice and then powers down.',
      category: 'HARDWARE_TECH',
      priority: TicketPriority.medium,
      status: TicketStatus.resolved,
      createdAt: DateTime.now().subtract(const Duration(days: 2)),
      customerName: 'Ramesh Hegde',
      messages: [
        TicketMessage(
          senderName: 'Ramesh Hegde',
          senderRole: 'CUSTOMER',
          message: 'Error code is 500-TIMEOUT-SWITCH.',
          createdAt: DateTime.now().subtract(const Duration(days: 2)),
        ),
        TicketMessage(
          senderName: 'Surya Agent Suresh',
          senderRole: 'AGENT',
          message: 'Driver update applied. Please reboot the terminal.',
          createdAt: DateTime.now().subtract(const Duration(days: 1)),
        )
      ],
      attachments: [],
      csatRating: 5,
      resolutionNotes: 'MicroATM firmware upgraded to build 1.42.01. Test transaction processed successfully.',
    )
  ];

  final List<KbArticle> _articles = [
    KbArticle(
      id: 'art_01',
      title: 'Resolving Bank Settlement Delay Exceptions',
      content: 'Payout pipelines typically settle within 10 minutes. If a settlement transaction takes more than 1 hour to clear, check the network route logs and ensure that your settlement account limit is not exceeded.',
      category: 'TRANSACTIONS',
      popular: true,
      videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4',
      helpfulCount: 42,
      unhelpfulCount: 1,
    ),
    KbArticle(
      id: 'art_02',
      title: 'MicroATM Device Configuration Guide',
      content: 'Ensure your microATM terminal is running version 1.42.0. Connect the biometric scanner strictly using high-throughput USB OTG cables. Install latest drivers via the settings dashboard.',
      category: 'HARDWARE',
      popular: true,
      helpfulCount: 29,
      unhelpfulCount: 3,
    ),
    KbArticle(
      id: 'art_03',
      title: 'Applying for Corporate Credit Allocations',
      content: 'Submit audit-certified transaction logs from your previous 3 quarters. Go to the Settings tab, select subscription limits, and click "Propose Limit Elevation". Upload the forms in PDF.',
      category: 'CREDIT',
      popular: false,
      helpfulCount: 12,
      unhelpfulCount: 0,
    ),
  ];

  final List<Map<String, dynamic>> _faqs = [
    {
      'question': 'What are the charges for IMPS settlements?',
      'answer': 'Flat ₹5 per transaction for amounts up to ₹1,00,000, and ₹10 for amounts higher up to ₹5,00,000.',
    },
    {
      'question': 'How long does biometric KYC approval take?',
      'answer': 'Aadhaar biometric matching processes in under 45 seconds through our secure integration pipeline.',
    },
    {
      'question': 'Can we run multi-tenant branding schemes?',
      'answer': 'Yes, go to Tenant Settings, set customized hex primary colors, and upload your enterprise logo instantly.',
    },
  ];

  final List<Map<String, dynamic>> _liveChatHistory = [
    {
      'sender': 'Vicky Support Agent',
      'text': 'Hello Shankar! Welcome to Surya Live Desk. How can I help you today?',
      'isMe': false,
      'time': '12:00 PM',
    },
    {
      'sender': 'Me',
      'text': 'Having biometric scanning failures in Bangalore North branch.',
      'isMe': true,
      'time': '12:02 PM',
    },
  ];

  final List<Map<String, dynamic>> _campaignMessages = [
    {
      'title': 'Monsoon Wallet Boost',
      'body': 'Get 1.5% additional buffer margin on corporate transactions.',
      'time': 'Just now',
      'read': false,
      'channel': 'PUSH'
    },
    {
      'title': 'SaaS System Maintenance Scheduled',
      'body': 'SaaS databases will perform automated backups on July 5, 02:00 AM UTC.',
      'time': 'Yesterday',
      'read': true,
      'channel': 'EMAIL'
    },
  ];

  final _chatInputController = TextEditingController();
  final _searchController = TextEditingController();
  bool _isTyping = false;

  @override
  void dispose() {
    _chatInputController.dispose();
    _searchController.dispose();
    super.dispose();
  }

  // Active view controller (simulates bottom navigation or top hierarchy views)
  int _currentTab = 0; // 0: Help Center, 1: My Tickets, 2: Knowledge Base, 3: Communication Hub

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0F172A), // Slate Dark Background
      appBar: AppBar(
        title: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(6),
              decoration: BoxDecoration(
                color: const Color(0xFF1E293B),
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Icon(Icons.support_agent, color: Color(0xFF38BDF8), size: 20),
            ),
            const SizedBox(width: 10),
            const Text(
              'Surya Omnichannel Support',
              style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Colors.white),
            ),
          ],
        ),
        backgroundColor: const Color(0xFF0F172A),
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications, color: Color(0xFF38BDF8)),
            onPressed: () {
              setState(() {
                _currentTab = 3; // Jump to Communications Campaign
              });
            },
          )
        ],
      ),
      body: Row(
        children: [
          // Adaptive Sidebar for large screens, integrated smoothly
          NavigationRail(
            backgroundColor: const Color(0xFF0F172A),
            selectedIndex: _currentTab,
            onDestinationSelected: (int index) {
              setState(() {
                _currentTab = index;
              });
            },
            labelType: NavigationRailLabelType.all,
            unselectedLabelTextStyle: const TextStyle(color: Colors.white54, fontSize: 10),
            selectedLabelTextStyle: const TextStyle(color: Color(0xFF38BDF8), fontSize: 10, fontWeight: FontWeight.bold),
            unselectedIconTheme: const IconThemeData(color: Colors.white54),
            selectedIconTheme: const IconThemeData(color: Color(0xFF38BDF8)),
            destinations: const [
              NavigationRailDestination(
                icon: Icon(Icons.dashboard_customize_outlined),
                selectedIcon: Icon(Icons.dashboard_customize),
                label: Text('Help Center'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.confirmation_number_outlined),
                selectedIcon: Icon(Icons.confirmation_number),
                label: Text('My Tickets'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.menu_book_outlined),
                selectedIcon: Icon(Icons.menu_book),
                label: Text('Knowledge'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.campaign_outlined),
                selectedIcon: Icon(Icons.campaign),
                label: Text('Campaigns'),
              ),
            ],
          ),
          const VerticalDivider(thickness: 1, width: 1, color: Color(0xFF1E293B)),
          Expanded(
            child: Container(
              color: const Color(0xFF090D16),
              child: _buildSelectedScreen(),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSelectedScreen() {
    switch (_currentTab) {
      case 0:
        return _buildHelpCenterDashboard();
      case 1:
        return _buildTicketListScreen();
      case 2:
        return _buildKnowledgeBaseScreen();
      case 3:
        return _buildCommunicationHubScreen();
      default:
        return _buildHelpCenterDashboard();
    }
  }

  // ==========================================
  // 1. HELP CENTER DASHBOARD SCREEN
  // ==========================================
  Widget _buildHelpCenterDashboard() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(20.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Hero Banner
          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF1E3A8A), Color(0xFF0F172A)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: const Color(0xFF38BDF8).withOpacity(0.3)),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      'Welcome to Surya Support Center',
                      style: TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.bold),
                    ),
                    Container(
                      padding: const EdgeInsets.all(8),
                      decoration: const BoxDecoration(color: Color(0xFF38BDF8), shape: BoxShape.circle),
                      child: const Icon(Icons.bolt, color: Color(0xFF0F172A), size: 18),
                    )
                  ],
                ),
                const SizedBox(height: 8),
                const Text(
                  'Explore our responsive multi-tenant self-help network, live agent consoles, or file a high-priority SLA ticket.',
                  style: TextStyle(color: Colors.white70, fontSize: 12),
                ),
                const SizedBox(height: 16),
                ElevatedButton.icon(
                  onPressed: () => _showCreateTicketDialog(),
                  icon: const Icon(Icons.add_circle_outline),
                  label: const Text('Open Fresh SLA Support Ticket'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF38BDF8),
                    foregroundColor: const Color(0xFF0F172A),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),

          const Text(
            'Channels for Enterprise Support',
            style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 12),

          GridView.count(
            crossAxisCount: 2,
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            mainAxisSpacing: 12,
            crossAxisSpacing: 12,
            childAspectRatio: 1.4,
            children: [
              _buildChannelCard(
                title: 'In-App Live Chat',
                desc: 'Chat directly with dedicated agent desk.',
                icon: Icons.chat_bubble,
                color: const Color(0xFF38BDF8),
                onTap: () => _navigateToLiveChat(),
              ),
              _buildChannelCard(
                title: 'WhatsApp Desk',
                desc: 'Direct corporate gateway route.',
                icon: Icons.phone_android,
                color: const Color(0xFF10B981),
                onTap: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Simulating redirection to +91 Surya WhatsApp Desk channel...'),
                      backgroundColor: Color(0xFF10B981),
                    ),
                  );
                },
              ),
              _buildChannelCard(
                title: 'Request Callback',
                desc: 'Schedule instant voice callback under SLA.',
                icon: Icons.phone_callback,
                color: const Color(0xFFF59E0B),
                onTap: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Instant Callback Scheduled! Agent will contact you in 15 mins.'),
                      backgroundColor: Color(0xFFF59E0B),
                    ),
                  );
                },
              ),
              _buildChannelCard(
                title: 'Browse User Guides',
                desc: 'Find self-help articles instantly.',
                icon: Icons.menu_book,
                color: const Color(0xFF8B5CF6),
                onTap: () {
                  setState(() => _currentTab = 2);
                },
              ),
            ],
          ),

          const SizedBox(height: 24),
          const Text(
            'Frequently Asked Questions',
            style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 12),

          ListView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemCount: _faqs.length,
            itemBuilder: (context, index) {
              final faq = _faqs[index];
              return Container(
                margin: const EdgeInsets.only(bottom: 10),
                decoration: BoxDecoration(
                  color: const Color(0xFF1E293B),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: ExpansionTile(
                  title: Text(
                    faq['question'],
                    style: const TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.bold),
                  ),
                  children: [
                    Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        faq['answer'],
                        style: const TextStyle(color: Colors.white70, fontSize: 12, height: 1.5),
                      ),
                    )
                  ],
                ),
              );
            },
          )
        ],
      ),
    );
  }

  Widget _buildChannelCard({
    required String title,
    required String desc,
    required IconData icon,
    required Color color,
    required VoidCallback onTap,
  }) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: const Color(0xFF1E293B),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.white.withOpacity(0.05)),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Icon(icon, color: color, size: 24),
                const Icon(Icons.chevron_right, color: Colors.white30, size: 16),
              ],
            ),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: const TextStyle(color: Colors.white, fontSize: 14, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 4),
                Text(
                  desc,
                  style: const TextStyle(color: Colors.white54, fontSize: 10),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  // ==========================================
  // 2. TICKET LIST SCREEN
  // ==========================================
  Widget _buildTicketListScreen() {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'My Active SLA Tickets',
                style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
              ),
              ElevatedButton(
                onPressed: () => _showCreateTicketDialog(),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF38BDF8),
                  foregroundColor: const Color(0xFF0F172A),
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  textStyle: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold),
                ),
                child: const Text('Add Ticket'),
              )
            ],
          ),
          const SizedBox(height: 12),
          Expanded(
            child: _myTickets.isEmpty
                ? const Center(
                    child: Text('No tickets raised yet. Click above to submit.', style: TextStyle(color: Colors.white54)),
                  )
                : ListView.builder(
                    itemCount: _myTickets.length,
                    itemBuilder: (context, index) {
                      final t = _myTickets[index];
                      return Container(
                        margin: const EdgeInsets.only(bottom: 12),
                        padding: const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          color: const Color(0xFF1E293B),
                          borderRadius: BorderRadius.circular(12),
                          border: Border.all(
                            color: t.priority == TicketPriority.high || t.priority == TicketPriority.urgent
                                ? Colors.red.withOpacity(0.3)
                                : Colors.transparent,
                          ),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                  decoration: BoxDecoration(
                                    color: const Color(0xFF0F172A),
                                    borderRadius: BorderRadius.circular(6),
                                  ),
                                  child: Text(
                                    t.ticketNumber,
                                    style: const TextStyle(color: Color(0xFF38BDF8), fontWeight: FontWeight.bold, fontSize: 11),
                                  ),
                                ),
                                Row(
                                  children: [
                                    _buildPriorityChip(t.priority),
                                    const SizedBox(width: 8),
                                    _buildStatusChip(t.status),
                                  ],
                                )
                              ],
                            ),
                            const SizedBox(height: 12),
                            Text(
                              t.subject,
                              style: const TextStyle(color: Colors.white, fontSize: 14, fontWeight: FontWeight.bold),
                            ),
                            const SizedBox(height: 6),
                            Text(
                              t.description,
                              style: const TextStyle(color: Colors.white70, fontSize: 12),
                              maxLines: 2,
                              overflow: TextOverflow.ellipsis,
                            ),
                            const Divider(color: Colors.white12, height: 24),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(
                                  'Raised: ${t.createdAt.toString().split(' ')[0]}',
                                  style: const TextStyle(color: Colors.white38, fontSize: 10),
                                ),
                                InkWell(
                                  onTap: () => _navigateToTicketDetail(t),
                                  child: Row(
                                    children: const [
                                      Text(
                                        'Inspect Conversations',
                                        style: TextStyle(color: Color(0xFF38BDF8), fontSize: 12, fontWeight: FontWeight.bold),
                                      ),
                                      SizedBox(width: 4),
                                      Icon(Icons.arrow_right, color: Color(0xFF38BDF8), size: 16),
                                    ],
                                  ),
                                )
                              ],
                            )
                          ],
                        ),
                      );
                    },
                  ),
          )
        ],
      ),
    );
  }

  Widget _buildPriorityChip(TicketPriority priority) {
    Color col = Colors.grey;
    if (priority == TicketPriority.high) col = Colors.orange;
    if (priority == TicketPriority.urgent) col = Colors.red;
    if (priority == TicketPriority.medium) col = Colors.blue;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(
        color: col.withOpacity(0.1),
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(
        priority.name.toUpperCase(),
        style: TextStyle(color: col, fontSize: 9, fontWeight: FontWeight.bold),
      ),
    );
  }

  Widget _buildStatusChip(TicketStatus status) {
    Color col = Colors.grey;
    if (status == TicketStatus.open) col = Colors.emerald;
    if (status == TicketStatus.escalated) col = Colors.red;
    if (status == TicketStatus.resolved) col = Colors.blue;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(
        color: col.withOpacity(0.1),
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(
        status.name.toUpperCase(),
        style: TextStyle(color: col, fontSize: 9, fontWeight: FontWeight.bold),
      ),
    );
  }

  // ==========================================
  // 3. TICKET DETAIL SCREEN
  // ==========================================
  void _navigateToTicketDetail(SupportTicket t) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => Scaffold(
          backgroundColor: const Color(0xFF0F172A),
          appBar: AppBar(
            title: Text('Ticket Details: ${t.ticketNumber}', style: const TextStyle(fontSize: 14)),
            backgroundColor: const Color(0xFF0F172A),
          ),
          body: Column(
            children: [
              Expanded(
                child: SingleChildScrollView(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(t.subject, style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                      const SizedBox(height: 8),
                      Text(t.description, style: const TextStyle(color: Colors.white70, fontSize: 13, height: 1.5)),
                      if (t.attachments.isNotEmpty) ...[
                        const SizedBox(height: 12),
                        Wrap(
                          children: t.attachments.map((file) => Container(
                            padding: const EdgeInsets.all(8),
                            decoration: BoxDecoration(color: const Color(0xFF1E293B), borderRadius: BorderRadius.circular(6)),
                            child: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                const Icon(Icons.attach_file, color: Color(0xFF38BDF8), size: 14),
                                const SizedBox(width: 4),
                                Text(file, style: const TextStyle(color: Colors.white, fontSize: 11)),
                              ],
                            ),
                          )).toList(),
                        )
                      ],
                      const Divider(color: Colors.white12, height: 32),
                      const Text('Replies & Activity Logs', style: TextStyle(color: Colors.white70, fontSize: 12, fontWeight: FontWeight.bold)),
                      const SizedBox(height: 12),
                      ...t.messages.map((m) => Container(
                        margin: const EdgeInsets.only(bottom: 12),
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: m.senderRole == 'CUSTOMER' ? const Color(0xFF1E293B) : const Color(0xFF0F172A),
                          borderRadius: BorderRadius.circular(8),
                          border: Border.all(color: m.senderRole == 'AGENT' ? const Color(0xFF38BDF8).withOpacity(0.3) : Colors.transparent),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(m.senderName, style: TextStyle(color: m.senderRole == 'AGENT' ? const Color(0xFF38BDF8) : Colors.white, fontWeight: FontWeight.bold, fontSize: 12)),
                                Text(m.createdAt.toString().split('.')[0].substring(11, 16), style: const TextStyle(color: Colors.white30, fontSize: 10)),
                              ],
                            ),
                            const SizedBox(height: 6),
                            Text(m.message, style: const TextStyle(color: Colors.white70, fontSize: 12, height: 1.4)),
                          ],
                        ),
                      )).toList(),
                    ],
                  ),
                ),
              ),
              // Send Reply Input Area
              Container(
                padding: const EdgeInsets.all(12),
                color: const Color(0xFF1E293B),
                child: Row(
                  children: [
                    Expanded(
                      child: TextField(
                        controller: _chatInputController,
                        style: const TextStyle(color: Colors.white, fontSize: 13),
                        decoration: const InputDecoration(
                          hintText: 'Type your support reply...',
                          hintStyle: TextStyle(color: Colors.white30),
                          border: InputBorder.none,
                        ),
                      ),
                    ),
                    IconButton(
                      icon: const Icon(Icons.send, color: Color(0xFF38BDF8)),
                      onPressed: () {
                        if (_chatInputController.text.isEmpty) return;
                        setState(() {
                          t.messages.add(TicketMessage(
                            senderName: 'Me',
                            senderRole: 'CUSTOMER',
                            message: _chatInputController.text,
                            createdAt: DateTime.now(),
                          ));
                          _chatInputController.clear();
                        });
                        Navigator.pop(context);
                        _navigateToTicketDetail(t);
                      },
                    )
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }

  // ==========================================
  // 4. CREATE TICKET DIALOG / FLOW
  // ==========================================
  void _showCreateTicketDialog() {
    final subController = TextEditingController();
    final descController = TextEditingController();
    String cat = 'TRANSACTION_SETTLEMENT';
    TicketPriority prio = TicketPriority.medium;

    showModalBottomSheet(
      context: context,
      backgroundColor: const Color(0xFF0F172A),
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(20))),
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setModalState) => Padding(
            padding: EdgeInsets.only(
              bottom: MediaQuery.of(context).viewInsets.bottom,
              top: 24,
              left: 20,
              right: 20,
            ),
            child: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('File Support Ticket under SLA', style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 16),
                  TextField(
                    controller: subController,
                    style: const TextStyle(color: Colors.white),
                    decoration: const InputDecoration(
                      labelText: 'Subject',
                      labelStyle: TextStyle(color: Colors.white54),
                      enabledBorder: OutlineInputBorder(borderSide: BorderSide(color: Color(0xFF334155))),
                    ),
                  ),
                  const SizedBox(height: 12),
                  DropdownButtonFormField<String>(
                    value: cat,
                    dropdownColor: const Color(0xFF0F172A),
                    style: const TextStyle(color: Colors.white),
                    decoration: const InputDecoration(
                      labelText: 'Category',
                      labelStyle: TextStyle(color: Colors.white54),
                      enabledBorder: OutlineInputBorder(borderSide: BorderSide(color: Color(0xFF334155))),
                    ),
                    items: const [
                      DropdownMenuItem(value: 'TRANSACTION_SETTLEMENT', child: Text('Transaction / Settlement Issue')),
                      DropdownMenuItem(value: 'HARDWARE_TECH', child: Text('MicroATM Hardware Technical Support')),
                      DropdownMenuItem(value: 'COMPLIANCE_KYC', child: Text('Regulatory KYC Audit Review')),
                    ],
                    onChanged: (val) {
                      setModalState(() {
                        cat = val!;
                      });
                    },
                  ),
                  const SizedBox(height: 12),
                  DropdownButtonFormField<TicketPriority>(
                    value: prio,
                    dropdownColor: const Color(0xFF0F172A),
                    style: const TextStyle(color: Colors.white),
                    decoration: const InputDecoration(
                      labelText: 'SLA Priority Threshold',
                      labelStyle: TextStyle(color: Colors.white54),
                      enabledBorder: OutlineInputBorder(borderSide: BorderSide(color: Color(0xFF334155))),
                    ),
                    items: TicketPriority.values.map((p) => DropdownMenuItem(
                      value: p,
                      child: Text(p.name.toUpperCase()),
                    )).toList(),
                    onChanged: (val) {
                      setModalState(() {
                        prio = val!;
                      });
                    },
                  ),
                  const SizedBox(height: 12),
                  TextField(
                    controller: descController,
                    style: const TextStyle(color: Colors.white),
                    maxLines: 4,
                    decoration: const InputDecoration(
                      labelText: 'Elaborate issue context...',
                      labelStyle: TextStyle(color: Colors.white54),
                      enabledBorder: OutlineInputBorder(borderSide: BorderSide(color: Color(0xFF334155))),
                    ),
                  ),
                  const SizedBox(height: 20),
                  ElevatedButton(
                    onPressed: () {
                      if (subController.text.isEmpty || descController.text.isEmpty) return;
                      setState(() {
                        _myTickets.insert(
                          0,
                          SupportTicket(
                            id: 'tkt_${Date.now()}',
                            ticketNumber: 'TKT-${1000 + _myTickets.length + 1}',
                            subject: subController.text,
                            description: descController.text,
                            category: cat,
                            priority: prio,
                            status: TicketStatus.open,
                            createdAt: DateTime.now(),
                            customerName: 'Me',
                            messages: [],
                            attachments: [],
                          ),
                        );
                      });
                      Navigator.pop(context);
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('Ticket registered successfully under active SLA monitoring!'), backgroundColor: Colors.emerald),
                      );
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF38BDF8),
                      foregroundColor: const Color(0xFF0F172A),
                      minimumSize: const Size.fromHeight(48),
                    ),
                    child: const Text('Broadcast Ticket to Agent Queues', style: TextStyle(fontWeight: FontWeight.bold)),
                  ),
                  const SizedBox(height: 12),
                ],
              ),
            ),
          ),
        );
      },
    );
  }

  // ==========================================
  // 5. LIVE CHAT MODAL SCREEN
  // ==========================================
  void _navigateToLiveChat() {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => Scaffold(
          backgroundColor: const Color(0xFF0F172A),
          appBar: AppBar(
            title: Row(
              children: [
                const CircleAvatar(
                  backgroundColor: Color(0xFF1E293B),
                  child: Icon(Icons.support_agent, color: Color(0xFF38BDF8)),
                ),
                const SizedBox(width: 8),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: const [
                    Text('Vicky Support Agent', style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: Colors.white)),
                    Text('Active Online • SLA 15m Response', style: TextStyle(fontSize: 10, color: Colors.emerald)),
                  ],
                )
              ],
            ),
            backgroundColor: const Color(0xFF0F172A),
          ),
          body: Column(
            children: [
              Expanded(
                child: ListView.builder(
                  padding: const EdgeInsets.all(16),
                  itemCount: _liveChatHistory.length,
                  itemBuilder: (context, index) {
                    final chat = _liveChatHistory[index];
                    final isMe = chat['isMe'];
                    return Align(
                      alignment: isMe ? Alignment.centerRight : Alignment.centerLeft,
                      child: Container(
                        margin: const EdgeInsets.only(bottom: 12),
                        padding: const EdgeInsets.all(12),
                        maxWidth: MediaQuery.of(context).size.width * 0.75,
                        decoration: BoxDecoration(
                          color: isMe ? const Color(0xFF38BDF8) : const Color(0xFF1E293B),
                          borderRadius: BorderRadius.only(
                            topLeft: const Radius.circular(12),
                            topRight: const Radius.circular(12),
                            bottomLeft: isMe ? const Radius.circular(12) : Radius.zero,
                            bottomRight: isMe ? Radius.zero : const Radius.circular(12),
                          ),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              chat['text'],
                              style: TextStyle(color: isMe ? const Color(0xFF0F172A) : Colors.white, fontSize: 13),
                            ),
                            const SizedBox(height: 4),
                            Align(
                              alignment: Alignment.bottomRight,
                              child: Text(
                                chat['time'],
                                style: TextStyle(color: isMe ? const Color(0xFF0F172A).withOpacity(0.5) : Colors.white30, fontSize: 9),
                              ),
                            )
                          ],
                        ),
                      ),
                    );
                  },
                ),
              ),
              // Chat input bar
              Container(
                padding: const EdgeInsets.all(12),
                color: const Color(0xFF1E293B),
                child: Row(
                  children: [
                    IconButton(
                      icon: const Icon(Icons.attach_file, color: Colors.white54),
                      onPressed: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('Simulated Secure File uploader: Select PDF/PNG')),
                        );
                      },
                    ),
                    Expanded(
                      child: TextField(
                        controller: _chatInputController,
                        style: const TextStyle(color: Colors.white, fontSize: 13),
                        decoration: const InputDecoration(
                          hintText: 'Type message...',
                          hintStyle: TextStyle(color: Colors.white30),
                          border: InputBorder.none,
                        ),
                      ),
                    ),
                    IconButton(
                      icon: const Icon(Icons.send, color: Color(0xFF38BDF8)),
                      onPressed: () {
                        if (_chatInputController.text.isEmpty) return;
                        setState(() {
                          _liveChatHistory.add({
                            'sender': 'Me',
                            'text': _chatInputController.text,
                            'isMe': true,
                            'time': 'Just now',
                          });
                          _chatInputController.clear();
                        });
                        Navigator.pop(context);
                        _navigateToLiveChat();
                      },
                    )
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }

  // ==========================================
  // 6. KNOWLEDGE BASE & SEARCH SCREEN
  // ==========================================
  Widget _buildKnowledgeBaseScreen() {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Search Document Library', style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          TextField(
            controller: _searchController,
            style: const TextStyle(color: Colors.white, fontSize: 13),
            decoration: InputDecoration(
              hintText: 'e.g. settlements, microATM, credit...',
              hintStyle: const TextStyle(color: Colors.white24),
              prefixIcon: const Icon(Icons.search, color: Color(0xFF38BDF8)),
              enabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
                borderSide: const BorderSide(color: Color(0xFF334155)),
              ),
              focusedBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
                borderSide: const BorderSide(color: Color(0xFF38BDF8)),
              ),
            ),
            onChanged: (val) {
              setState(() {}); // Redraw list on filter change
            },
          ),
          const SizedBox(height: 16),
          Expanded(
            child: ListView.builder(
              itemCount: _articles.length,
              itemBuilder: (context, index) {
                final art = _articles[index];
                if (_searchController.text.isNotEmpty &&
                    !art.title.toLowerCase().contains(_searchController.text.toLowerCase()) &&
                    !art.content.toLowerCase().contains(_searchController.text.toLowerCase())) {
                  return const SizedBox.shrink();
                }
                return Card(
                  color: const Color(0xFF1E293B),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                  margin: const EdgeInsets.only(bottom: 12),
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                              decoration: BoxDecoration(
                                color: const Color(0xFF0F172A),
                                borderRadius: BorderRadius.circular(4),
                              ),
                              child: Text(
                                art.category,
                                style: const TextStyle(color: Color(0xFF38BDF8), fontSize: 9, fontWeight: FontWeight.bold),
                              ),
                            ),
                            if (art.popular)
                              const Icon(Icons.star, color: Colors.amber, size: 16),
                          ],
                        ),
                        const SizedBox(height: 8),
                        Text(
                          art.title,
                          style: const TextStyle(color: Colors.white, fontSize: 14, fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 6),
                        Text(
                          art.content,
                          style: const TextStyle(color: Colors.white70, fontSize: 11, height: 1.4),
                        ),
                        const SizedBox(height: 12),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(
                              'Helpful: ${art.helpfulCount}  •  Unhelpful: ${art.unhelpfulCount}',
                              style: const TextStyle(color: Colors.white38, fontSize: 10),
                            ),
                            Row(
                              children: [
                                IconButton(
                                  icon: const Icon(Icons.thumb_up, color: Colors.emerald, size: 14),
                                  onPressed: () {
                                    setState(() {
                                      art.helpfulCount += 1;
                                    });
                                  },
                                ),
                                IconButton(
                                  icon: const Icon(Icons.thumb_down, color: Colors.red, size: 14),
                                  onPressed: () {
                                    setState(() {
                                      art.unhelpfulCount += 1;
                                    });
                                  },
                                )
                              ],
                            )
                          ],
                        )
                      ],
                    ),
                  ),
                );
              },
            ),
          )
        ],
      ),
    );
  }

  // ==========================================
  // 7. COMMUNICATION HUB SCREEN
  // ==========================================
  Widget _buildCommunicationHubScreen() {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Omni-Channel Live Bulletins', style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 4),
          const Text('Broadcast announcements delivered directly from multi-tenant CRM campaign suites.', style: TextStyle(color: Colors.white54, fontSize: 11)),
          const SizedBox(height: 16),
          Expanded(
            child: ListView.builder(
              itemCount: _campaignMessages.length,
              itemBuilder: (context, index) {
                final bulletin = _campaignMessages[index];
                return Container(
                  margin: const EdgeInsets.only(bottom: 12),
                  padding: const EdgeInsets.all(14),
                  decoration: BoxDecoration(
                    color: const Color(0xFF1E293B),
                    borderRadius: BorderRadius.circular(10),
                    border: Border.all(
                      color: bulletin['read'] ? Colors.transparent : const Color(0xFF38BDF8).withOpacity(0.3),
                    ),
                  ),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        padding: const EdgeInsets.all(8),
                        decoration: BoxDecoration(
                          color: const Color(0xFF0F172A),
                          shape: BoxShape.circle,
                        ),
                        child: Icon(
                          bulletin['channel'] == 'EMAIL' ? Icons.email : Icons.notifications_active,
                          color: const Color(0xFF38BDF8),
                          size: 16,
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(
                                  bulletin['title'],
                                  style: const TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.bold),
                                ),
                                Text(
                                  bulletin['time'],
                                  style: const TextStyle(color: Colors.white30, fontSize: 10),
                                )
                              ],
                            ),
                            const SizedBox(height: 4),
                            Text(
                              bulletin['body'],
                              style: const TextStyle(color: Colors.white70, fontSize: 11, height: 1.4),
                            ),
                          ],
                        ),
                      )
                    ],
                  ),
                );
              },
            ),
          )
        ],
      ),
    );
  }
}
