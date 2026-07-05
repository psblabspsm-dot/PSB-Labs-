import React, { useState, useEffect } from 'react';

export default function SupportAgentHub() {
  const [activeTab, setActiveTab] = useState('agent_dashboard'); // agent_dashboard, ticket_console, chat_console, sla_dashboard, kb_manager, comm_center, theme_customizer
  const [message, setMessage] = useState(null);

  // --- CORE STATE MANAGERS ---
  const [stats, setStats] = useState({
    openTickets: 5,
    pendingTickets: 12,
    escalatedTickets: 2,
    slaCompliance: '97.2%',
    avgResponse: '14 mins',
    avgResolution: '2.1 hrs',
    csat: '4.85 / 5',
    activeChats: 3,
  });

  const [tickets, setTickets] = useState([
    {
      id: 'TKT-1001',
      customer: 'Shankar Gowda',
      role: 'RETAILER',
      category: 'TRANSACTION_SETTLEMENT',
      subject: 'AEPS Biometric Timeout on ICICI terminal',
      priority: 'HIGH',
      status: 'OPEN',
      assignedAgent: 'Vicky',
      department: 'GENERAL',
      createdAt: '2026-07-03 11:00 AM',
      dueDate: '2026-07-03 03:00 PM',
      messages: [
        { sender: 'Shankar Gowda', role: 'CUSTOMER', text: 'Scanned biometric but terminal output 500. Cash was debited.' }
      ]
    },
    {
      id: 'TKT-1002',
      customer: 'Ramesh Hegde',
      role: 'DISTRIBUTOR',
      category: 'CREDIT',
      subject: 'Monsoon Credit Extension raise request',
      priority: 'MEDIUM',
      status: 'PENDING',
      assignedAgent: 'Suresh',
      department: 'RECOVERY',
      createdAt: '2026-07-02 10:00 AM',
      dueDate: '2026-07-04 10:00 AM',
      messages: [
        { sender: 'Ramesh Hegde', role: 'CUSTOMER', text: 'Need additional ₹5,00,000 for high volume cashout seasonal trading.' }
      ]
    }
  ]);

  const [chatSessions, setChatSessions] = useState([
    {
      id: 'chat_9018',
      customer: 'Shankar Gowda',
      role: 'RETAILER',
      status: 'ACTIVE',
      department: 'GENERAL',
      typingStatus: 'Shankar is typing...',
      messages: [
        { sender: 'Shankar Gowda', role: 'CUSTOMER', text: 'Hey, terminal says biometric timeout. What to do?' },
        { sender: 'System Agent Auto-Reply', role: 'AGENT', text: 'Connecting you to Vicky...' }
      ]
    }
  ]);

  const [kbArticles, setKbArticles] = useState([
    { id: 'art_01', title: 'IMPS Settlement Latency Resolution', category: 'TRANSACTIONS', popular: true, helpful: 42 },
    { id: 'art_02', title: 'MicroATM Driver Configuration', category: 'HARDWARE', popular: false, helpful: 19 }
  ]);

  const [campaigns, setCampaigns] = useState([
    { id: 'cmp_01', name: 'July Wallet Boost Cashback Fest', channel: 'WHATSAPP', segment: 'RETAILERS', sent: 1420, readRate: '88%' }
  ]);

  const [selectedTicket, setSelectedTicket] = useState(null);
  const [selectedChat, setSelectedChat] = useState(null);
  const [replyText, setReplyText] = useState('');
  const [chatInput, setChatInput] = useState('');

  // --- THEME CUSTOMIZER STATE (REAL-TIME LIVE PREVIEW) ---
  const [brandingTheme, setBrandingTheme] = useState({
    companyName: 'Surya Credit Solutions Primary',
    primaryColor: '#0F172A',
    secondaryColor: '#38BDF8',
    logoUrl: 'https://images.unsplash.com/photo-1599305445671-ac291c95aba9?q=80&w=300',
    fontName: 'Space Grotesk',
    cornerRadius: '8px',
  });

  const triggerToast = (text) => {
    setMessage(text);
    setTimeout(() => setMessage(null), 4000);
  };

  // --- TICKETING ACTIONS ---
  const handleAddReply = (ticketId) => {
    if (!replyText.trim()) return;
    setTickets(prev => prev.map(t => {
      if (t.id === ticketId) {
        return {
          ...t,
          messages: [...t.messages, { sender: 'Support Agent (You)', role: 'AGENT', text: replyText }]
        };
      }
      return t;
    }));
    triggerToast(`Reply registered and broadcasted to Ticket: ${ticketId}`);
    setReplyText('');
  };

  const handleUpdateStatus = (ticketId, nextStatus) => {
    setTickets(prev => prev.map(t => t.id === ticketId ? { ...t, status: nextStatus } : t));
    triggerToast(`Ticket ${ticketId} status upgraded to ${nextStatus}`);
  };

  // --- LIVE CHAT ACTIONS ---
  const sendChatMessage = (sessionId) => {
    if (!chatInput.trim()) return;
    setChatSessions(prev => prev.map(c => {
      if (c.id === sessionId) {
        return {
          ...c,
          messages: [...c.messages, { sender: 'Support Agent Vicky (You)', role: 'AGENT', text: chatInput }]
        };
      }
      return c;
    }));
    setChatInput('');
    // Mock customer automatic response in 1 second
    setTimeout(() => {
      setChatSessions(prev => prev.map(c => {
        if (c.id === sessionId) {
          return {
            ...c,
            messages: [...c.messages, { sender: 'Shankar Gowda', role: 'CUSTOMER', text: 'Got it! It worked fine after reboot. Thanks.' }]
          };
        }
        return c;
      }));
    }, 1500);
  };

  return (
    <div style={{ fontFamily: 'Inter, sans-serif', backgroundColor: '#F8FAFC', minHeight: '100vh', display: 'flex' }}>
      
      {/* Dynamic Theme Banner / Sidebar Preview Indicator */}
      <div style={{ width: '240px', backgroundColor: '#0F172A', color: '#FFF', display: 'flex', flexDirection: 'column', borderRight: '1px solid #1E293B' }}>
        <div style={{ padding: '24px', borderBottom: '1px solid #1E293B' }}>
          {/* Logo instant preview */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <img src={brandingTheme.logoUrl} alt="Logo" style={{ width: '32px', height: '32px', borderRadius: '50%', objectFit: 'cover' }} />
            <div>
              <div style={{ fontWeight: 'bold', fontSize: '14px', color: brandingTheme.secondaryColor }}>{brandingTheme.companyName}</div>
              <div style={{ fontSize: '10px', color: '#64748B' }}>SaaS Enterprise Hub</div>
            </div>
          </div>
        </div>

        {/* Console Navigation */}
        <div style={{ flex: 1, padding: '16px 8px', display: 'flex', flexDirection: 'column', gap: '4px' }}>
          {[
            { id: 'agent_dashboard', label: 'Agent Dashboard', icon: '📊' },
            { id: 'ticket_console', label: 'Ticket Dashboard', icon: '🎫' },
            { id: 'chat_console', label: 'Live Chat Console', icon: '💬' },
            { id: 'sla_dashboard', label: 'SLA Dashboard', icon: '⏰' },
            { id: 'kb_manager', label: 'Knowledge Base', icon: '📚' },
            { id: 'comm_center', label: 'Campaign Center', icon: '📢' },
            { id: 'theme_customizer', label: 'Theme Customizer', icon: '🎨' },
          ].map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '10px',
                padding: '10px 14px',
                borderRadius: brandingTheme.cornerRadius,
                backgroundColor: activeTab === tab.id ? brandingTheme.secondaryColor : 'transparent',
                color: activeTab === tab.id ? '#0F172A' : '#94A3B8',
                border: 'none',
                textAlign: 'left',
                fontSize: '13px',
                fontWeight: '600',
                cursor: 'pointer',
                transition: 'all 0.2s',
              }}
            >
              <span>{tab.icon}</span>
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {/* Main Area */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        {/* Top bar */}
        <div style={{ height: '70px', backgroundColor: '#FFF', borderBottom: '1px solid #E2E8F0', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 24px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <span style={{ fontSize: '18px', fontWeight: 'bold', color: '#0F172A' }}>
              {activeTab.toUpperCase().replace('_', ' ')}
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <span style={{ fontSize: '12px', color: '#64748B' }}>Corporate Active Node: <strong>main.suryacredit.com</strong></span>
            <span style={{ backgroundColor: '#ECFDF5', color: '#10B981', padding: '4px 10px', borderRadius: '12px', fontSize: '11px', fontWeight: '700' }}>AGENT ACTIVE</span>
          </div>
        </div>

        {/* Toast Notification */}
        {message && (
          <div style={{ backgroundColor: '#0F172A', color: '#38BDF8', padding: '12px 24px', borderLeft: `4px solid ${brandingTheme.secondaryColor}`, fontSize: '13px', fontWeight: 'bold' }}>
            ⚡ {message}
          </div>
        )}

        {/* Render Views */}
        <div style={{ flex: 1, padding: '24px', overflowY: 'auto' }}>
          {activeTab === 'agent_dashboard' && _renderAgentDashboard()}
          {activeTab === 'ticket_console' && _renderTicketConsole()}
          {activeTab === 'chat_console' && _renderChatConsole()}
          {activeTab === 'sla_dashboard' && _renderSlaDashboard()}
          {activeTab === 'kb_manager' && _renderKbManager()}
          {activeTab === 'comm_center' && _renderCommCenter()}
          {activeTab === 'theme_customizer' && _renderThemeCustomizer()}
        </div>
      </div>
    </div>
  );

  // ==========================================
  // 1. AGENT & METRICS DASHBOARD
  // ==========================================
  function _renderAgentDashboard() {
    return (
      <div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '16px', marginBottom: '24px' }}>
          {[
            { label: 'Open Tickets', val: stats.openTickets, icon: '🎫', color: '#38BDF8' },
            { label: 'Pending SLA Tickets', val: stats.pendingTickets, icon: '⏳', color: '#F59E0B' },
            { label: 'Escalated Operations', val: stats.escalatedTickets, icon: '🚨', color: '#EF4444' },
            { label: 'SLA Compliance Rate', val: stats.slaCompliance, icon: '📈', color: '#10B981' },
          ].map((card, idx) => (
            <div key={idx} style={{ backgroundColor: '#FFF', padding: '20px', borderRadius: '12px', border: '1px solid #E2E8F0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <div style={{ fontSize: '12px', color: '#64748B' }}>{card.label}</div>
                <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#0F172A', marginTop: '6px' }}>{card.val}</div>
              </div>
              <span style={{ fontSize: '32px' }}>{card.icon}</span>
            </div>
          ))}
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1.5fr 1fr', gap: '24px' }}>
          {/* Chart Placeholder / Performance Logs */}
          <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
            <h3 style={{ fontSize: '15px', fontWeight: 'bold', color: '#0F172A', marginTop: 0, marginBottom: '16px' }}>SLA Peak Resolution Analytics</h3>
            <div style={{ height: '200px', display: 'flex', alignItems: 'flex-end', gap: '24px', borderBottom: '1px solid #E2E8F0', paddingBottom: '12px' }}>
              {[
                { day: 'Mon', h: '85%' },
                { day: 'Tue', h: '92%' },
                { day: 'Wed', h: '98%' },
                { day: 'Thu', h: '94%' },
                { day: 'Fri', h: '97%' },
                { day: 'Sat', h: '100%' },
              ].map((bar, idx) => (
                <div key={idx} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
                  <div style={{ width: '100%', height: bar.h, backgroundColor: brandingTheme.secondaryColor, borderRadius: '4px 4px 0 0', minHeight: '20px' }}></div>
                  <span style={{ fontSize: '11px', color: '#64748B' }}>{bar.day}</span>
                </div>
              ))}
            </div>
          </div>

          <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
            <h3 style={{ fontSize: '15px', fontWeight: 'bold', color: '#0F172A', marginTop: 0, marginBottom: '16px' }}>Client Satisfaction Matrix</h3>
            <div style={{ textAlign: 'center', padding: '20px 0' }}>
              <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#10B981' }}>{stats.csat}</div>
              <div style={{ fontSize: '12px', color: '#64748B', marginTop: '8px' }}>Average CSAT rating based on recent closed support tickets.</div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // ==========================================
  // 2. TICKET CONSOLE / DASHBOARD
  // ==========================================
  function _renderTicketConsole() {
    return (
      <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1.8fr', gap: '24px' }}>
        {/* Ticket List */}
        <div style={{ backgroundColor: '#FFF', borderRadius: '12px', border: '1px solid #E2E8F0', overflow: 'hidden' }}>
          <div style={{ padding: '16px', borderBottom: '1px solid #E2E8F0', backgroundColor: '#F8FAFC' }}>
            <h3 style={{ fontSize: '14px', fontWeight: 'bold', margin: 0 }}>Ticket Inbox</h3>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            {tickets.map(t => (
              <div
                key={t.id}
                onClick={() => setSelectedTicket(t)}
                style={{
                  padding: '16px',
                  borderBottom: '1px solid #E2E8F0',
                  cursor: 'pointer',
                  backgroundColor: selectedTicket?.id === t.id ? '#EFF6FF' : 'transparent',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                  <span style={{ fontWeight: 'bold', fontSize: '13px', color: brandingTheme.secondaryColor }}>{t.id}</span>
                  <span style={{
                    fontSize: '10px',
                    fontWeight: '700',
                    color: t.priority === 'HIGH' ? '#EF4444' : '#64748B',
                    backgroundColor: t.priority === 'HIGH' ? '#FEE2E2' : '#F1F5F9',
                    padding: '2px 8px',
                    borderRadius: '12px',
                  }}>{t.priority}</span>
                </div>
                <div style={{ fontSize: '13px', fontWeight: 'bold', color: '#0F172A', marginBottom: '4px' }}>{t.subject}</div>
                <div style={{ fontSize: '11px', color: '#64748B' }}>Customer: {t.customer} • {t.createdAt}</div>
              </div>
            ))}
          </div>
        </div>

        {/* Ticket Actions Console */}
        <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
          {selectedTicket ? (
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <h3 style={{ fontSize: '16px', fontWeight: 'bold', margin: 0 }}>{selectedTicket.id}: {selectedTicket.subject}</h3>
                <div>
                  <button onClick={() => handleUpdateStatus(selectedTicket.id, 'RESOLVED')} style={{ padding: '6px 12px', backgroundColor: '#10B981', color: '#FFF', border: 'none', borderRadius: '4px', marginRight: '8px', cursor: 'pointer', fontSize: '12px' }}>
                    Mark Resolved
                  </button>
                  <button onClick={() => handleUpdateStatus(selectedTicket.id, 'CLOSED')} style={{ padding: '6px 12px', backgroundColor: '#64748B', color: '#FFF', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' }}>
                    Close Ticket
                  </button>
                </div>
              </div>

              <div style={{ backgroundColor: '#F8FAFC', padding: '12px', borderRadius: '8px', marginBottom: '16px', fontSize: '13px' }}>
                <strong>Issue Description:</strong> {selectedTicket.description}
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '20px', maxHeight: '200px', overflowY: 'auto' }}>
                {selectedTicket.messages.map((m, idx) => (
                  <div key={idx} style={{ padding: '10px', borderRadius: '8px', backgroundColor: m.role === 'AGENT' ? '#EFF6FF' : '#F1F5F9' }}>
                    <div style={{ fontSize: '11px', fontWeight: 'bold', color: '#1E293B' }}>{m.sender}</div>
                    <div style={{ fontSize: '13px', marginTop: '4px' }}>{m.text}</div>
                  </div>
                ))}
              </div>

              <textarea
                value={replyText}
                onChange={(e) => setReplyText(e.target.value)}
                placeholder="Type your official support response..."
                rows={3}
                style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #CBD5E1', boxSizing: 'border-box', marginBottom: '12px' }}
              />

              <button onClick={() => handleAddReply(selectedTicket.id)} style={{ backgroundColor: brandingTheme.secondaryColor, color: '#0F172A', padding: '10px 20px', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}>
                Dispatch Conversation Reply
              </button>
            </div>
          ) : (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', color: '#64748B' }}>
              Select a support ticket from the list to start resolution workflows.
            </div>
          )}
        </div>
      </div>
    );
  }

  // ==========================================
  // 3. LIVE CHAT CONSOLE
  // ==========================================
  function _renderChatConsole() {
    return (
      <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1.8fr', gap: '24px' }}>
        {/* Active Chats List */}
        <div style={{ backgroundColor: '#FFF', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
          <div style={{ padding: '16px', borderBottom: '1px solid #E2E8F0', backgroundColor: '#F8FAFC' }}>
            <h3 style={{ fontSize: '14px', fontWeight: 'bold', margin: 0 }}>Active Channels</h3>
          </div>
          {chatSessions.map(c => (
            <div
              key={c.id}
              onClick={() => setSelectedChat(c)}
              style={{
                padding: '16px',
                borderBottom: '1px solid #E2E8F0',
                cursor: 'pointer',
                backgroundColor: selectedChat?.id === c.id ? '#EFF6FF' : 'transparent',
              }}
            >
              <div style={{ fontWeight: 'bold', fontSize: '13px' }}>{c.customer}</div>
              <div style={{ fontSize: '11px', color: '#64748B', marginTop: '4px' }}>Session: {c.id} • Dept: {c.department}</div>
              <div style={{ fontSize: '10px', color: '#10B981', marginTop: '6px', fontWeight: 'bold' }}>{c.typingStatus}</div>
            </div>
          ))}
        </div>

        {/* Live Chat Frame */}
        <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0', display: 'flex', flexDirection: 'column' }}>
          {selectedChat ? (
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', height: '350px' }}>
              <div style={{ borderBottom: '1px solid #E2E8F0', paddingBottom: '12px', marginBottom: '12px' }}>
                <span style={{ fontWeight: 'bold' }}>{selectedChat.customer}</span>
                <span style={{ marginLeft: '10px', backgroundColor: '#ECFDF5', color: '#10B981', padding: '2px 8px', borderRadius: '10px', fontSize: '10px', fontWeight: '700' }}>ONLINE</span>
              </div>

              <div style={{ flex: 1, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '16px' }}>
                {selectedChat.messages.map((msg, idx) => (
                  <div key={idx} style={{ alignSelf: msg.role === 'AGENT' ? 'flex-end' : 'flex-start', backgroundColor: msg.role === 'AGENT' ? brandingTheme.secondaryColor : '#F1F5F9', color: msg.role === 'AGENT' ? '#0F172A' : '#0F172A', padding: '10px 14px', borderRadius: '8px', maxWidth: '75%' }}>
                    <div style={{ fontSize: '10px', fontWeight: 'bold', opacity: 0.7 }}>{msg.sender}</div>
                    <div style={{ fontSize: '13px', marginTop: '4px' }}>{msg.text}</div>
                  </div>
                ))}
              </div>

              <div style={{ display: 'flex', gap: '10px' }}>
                <input
                  type="text"
                  value={chatInput}
                  onChange={(e) => setChatInput(e.target.value)}
                  placeholder="Type message directly..."
                  style={{ flex: 1, padding: '10px', borderRadius: '6px', border: '1px solid #CBD5E1' }}
                  onKeyDown={(e) => { if (e.key === 'Enter') sendChatMessage(selectedChat.id); }}
                />
                <button onClick={() => sendChatMessage(selectedChat.id)} style={{ backgroundColor: '#0F172A', color: '#FFF', padding: '0 20px', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>
                  Send
                </button>
              </div>
            </div>
          ) : (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', color: '#64748B' }}>
              Open an active chat line to respond to customer inquiries.
            </div>
          )}
        </div>
      </div>
    );
  }

  // ==========================================
  // 4. SLA DASHBOARD
  // ==========================================
  function _renderSlaDashboard() {
    return (
      <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
        <h3 style={{ fontSize: '15px', fontWeight: 'bold', margin: '0 0 16px 0' }}>Escalation Matrix Rules</h3>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '13px' }}>
          <thead>
            <tr style={{ backgroundColor: '#F8FAFC', borderBottom: '1px solid #E2E8F0' }}>
              <th style={{ padding: '12px' }}>SLA Tier Priority</th>
              <th style={{ padding: '12px' }}>Response Threshold</th>
              <th style={{ padding: '12px' }}>Resolution Hours allowed</th>
              <th style={{ padding: '12px' }}>Auto-Escalation Target Agent</th>
            </tr>
          </thead>
          <tbody>
            {[
              { prio: 'URGENT', resp: '15 mins', resol: '1 hour', target: 'escalation_manager_tony' },
              { prio: 'HIGH', resp: '1 hour', resol: '4 hours', target: 'support_lead_shankar' },
              { prio: 'MEDIUM', resp: '4 hours', resol: '24 hours', target: 'agent_vicky' },
              { prio: 'LOW', resp: '24 hours', resol: '48 hours', target: 'support_inbox' },
            ].map((rule, idx) => (
              <tr key={idx} style={{ borderBottom: '1px solid #F1F5F9' }}>
                <td style={{ padding: '12px', fontWeight: 'bold' }}>{rule.prio}</td>
                <td style={{ padding: '12px' }}>{rule.resp}</td>
                <td style={{ padding: '12px' }}>{rule.resol}</td>
                <td style={{ padding: '12px', color: brandingTheme.secondaryColor }}>{rule.target}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  }

  // ==========================================
  // 5. KNOWLEDGE BASE MANAGER
  // ==========================================
  function _renderKbManager() {
    return (
      <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h3 style={{ fontSize: '15px', fontWeight: 'bold', margin: 0 }}>Self-Help FAQ Articles</h3>
          <button onClick={() => triggerToast('Created a draft article template')} style={{ padding: '8px 16px', backgroundColor: '#0F172A', color: '#FFF', border: 'none', borderRadius: '6px', fontSize: '12px', cursor: 'pointer' }}>
            + Create New Article
          </button>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          {kbArticles.map(art => (
            <div key={art.id} style={{ border: '1px solid #E2E8F0', borderRadius: '8px', padding: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <span style={{ fontWeight: 'bold', fontSize: '14px' }}>{art.title}</span>
                <span style={{ fontSize: '11px', color: '#64748B', backgroundColor: '#F1F5F9', padding: '2px 8px', borderRadius: '12px' }}>{art.category}</span>
              </div>
              <div style={{ display: 'flex', gap: '16px', fontSize: '11px', color: '#64748B' }}>
                <span>ID: {art.id}</span>
                <span>Helpful Votes: <strong>{art.helpful}</strong></span>
                <span>Video Tutorial Linked: {art.popular ? 'Yes' : 'No'}</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  // ==========================================
  // 6. COMMUNICATION HUB
  // ==========================================
  function _renderCommCenter() {
    return (
      <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1.8fr', gap: '24px' }}>
        <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
          <h3 style={{ fontSize: '15px', fontWeight: 'bold', margin: '0 0 16px 0' }}>Dispatch Campaign</h3>
          
          <div style={{ marginBottom: '12px' }}>
            <label style={{ display: 'block', fontSize: '12px', fontWeight: 'bold', marginBottom: '6px' }}>Campaign Name</label>
            <input type="text" placeholder="Monsoon Recharge Promo" style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #CBD5E1' }} />
          </div>

          <div style={{ marginBottom: '12px' }}>
            <label style={{ display: 'block', fontSize: '12px', fontWeight: 'bold', marginBottom: '6px' }}>Channel</label>
            <select style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #CBD5E1' }}>
              <option value="WHATSAPP">WhatsApp Business API</option>
              <option value="EMAIL">Email SMTP Server</option>
              <option value="SMS">SMS OTP Gateway</option>
            </select>
          </div>

          <button onClick={() => triggerToast('Campaign dispatched to 1,420 targets successfully.')} style={{ width: '100%', backgroundColor: '#0F172A', color: '#FFF', padding: '12px', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
            ⚡ Broadcast Campaign Now
          </button>
        </div>

        <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
          <h3 style={{ fontSize: '15px', fontWeight: 'bold', margin: '0 0 16px 0' }}>Recent Bulletins</h3>
          {campaigns.map(c => (
            <div key={c.id} style={{ border: '1px solid #E2E8F0', padding: '16px', borderRadius: '8px' }}>
              <div style={{ fontWeight: 'bold', fontSize: '14px' }}>{c.name}</div>
              <div style={{ fontSize: '11px', color: '#64748B', marginTop: '6px' }}>Channel: {c.channel} • Target Segment: {c.segment} • Dispatched Targets: {c.sent} • Read Rate: {c.readRate}</div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  // ==========================================
  // 7. REAL-TIME THEME CUSTOMIZER (PREVIEW ENGINE)
  // ==========================================
  function _renderThemeCustomizer() {
    return (
      <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1.8fr', gap: '24px' }}>
        {/* Editor controls */}
        <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
          <h3 style={{ fontSize: '15px', fontWeight: 'bold', margin: '0 0 16px 0' }}>Theme Design Customizer</h3>
          
          <div style={{ marginBottom: '14px' }}>
            <label style={{ display: 'block', fontSize: '12px', fontWeight: 'bold', marginBottom: '6px' }}>Corporate Portal Name</label>
            <input
              type="text"
              value={brandingTheme.companyName}
              onChange={(e) => setBrandingTheme(prev => ({ ...prev, companyName: e.target.value }))}
              style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #CBD5E1', boxSizing: 'border-box' }}
            />
          </div>

          <div style={{ marginBottom: '14px' }}>
            <label style={{ display: 'block', fontSize: '12px', fontWeight: 'bold', marginBottom: '6px' }}>Brand Logo Image URL</label>
            <input
              type="text"
              value={brandingTheme.logoUrl}
              onChange={(e) => setBrandingTheme(prev => ({ ...prev, logoUrl: e.target.value }))}
              style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #CBD5E1', boxSizing: 'border-box' }}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '14px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '12px', fontWeight: 'bold', marginBottom: '6px' }}>Primary Theme Color</label>
              <input
                type="color"
                value={brandingTheme.primaryColor}
                onChange={(e) => setBrandingTheme(prev => ({ ...prev, primaryColor: e.target.value }))}
                style={{ width: '100%', height: '40px', border: '1px solid #CBD5E1', borderRadius: '6px', cursor: 'pointer' }}
              />
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '12px', fontWeight: 'bold', marginBottom: '6px' }}>Accent Highlight Color</label>
              <input
                type="color"
                value={brandingTheme.secondaryColor}
                onChange={(e) => setBrandingTheme(prev => ({ ...prev, secondaryColor: e.target.value }))}
                style={{ width: '100%', height: '40px', border: '1px solid #CBD5E1', borderRadius: '6px', cursor: 'pointer' }}
              />
            </div>
          </div>

          <div style={{ marginBottom: '14px' }}>
            <label style={{ display: 'block', fontSize: '12px', fontWeight: 'bold', marginBottom: '6px' }}>Layout Component Corner Radius</label>
            <select
              value={brandingTheme.cornerRadius}
              onChange={(e) => setBrandingTheme(prev => ({ ...prev, cornerRadius: e.target.value }))}
              style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #CBD5E1' }}
            >
              <option value="0px">Sharp (Brutalist Modernism)</option>
              <option value="6px">Medium Round (Soft Enterprise)</option>
              <option value="12px">Extra Round (Comfort Clean)</option>
            </select>
          </div>

          <button onClick={() => triggerToast('Corporate White-label theme assets generated and successfully synchronized across global content delivery networks!')} style={{ width: '100%', backgroundColor: '#0F172A', color: '#FFF', padding: '12px', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
            Save & Publish Custom Branding
          </button>
        </div>

        {/* Real-time mock preview wrapper */}
        <div style={{ backgroundColor: '#F1F5F9', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0', display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <h3 style={{ fontSize: '14px', fontWeight: 'bold', color: '#475569', margin: 0 }}>Instant Live Sandbox Preview</h3>
          
          <div style={{ backgroundColor: brandingTheme.primaryColor, color: '#FFF', padding: '20px', borderRadius: brandingTheme.cornerRadius, boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <img src={brandingTheme.logoUrl} alt="logo" style={{ width: '28px', height: '28px', borderRadius: '50%', objectFit: 'cover' }} />
                <span style={{ fontWeight: 'bold', fontSize: '14px' }}>{brandingTheme.companyName}</span>
              </div>
              <span style={{ backgroundColor: brandingTheme.secondaryColor, color: '#0F172A', padding: '2px 8px', borderRadius: '12px', fontSize: '10px', fontWeight: 'bold' }}>LIVE</span>
            </div>
            
            <div style={{ borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: '12px' }}>
              <div style={{ fontSize: '12px', opacity: 0.8 }}>Active Wallet Settlement Balance</div>
              <div style={{ fontSize: '20px', fontWeight: 'bold', marginTop: '4px', color: brandingTheme.secondaryColor }}>₹14,50,000.00</div>
            </div>
          </div>

          <button style={{ backgroundColor: brandingTheme.secondaryColor, color: '#0F172A', border: 'none', padding: '10px', borderRadius: brandingTheme.cornerRadius, fontWeight: 'bold', cursor: 'pointer' }}>
            Interactive Accent Action
          </button>
        </div>
      </div>
    );
  }
}
