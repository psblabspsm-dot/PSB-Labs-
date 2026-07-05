import React, { useState, useEffect } from 'react';

export default function SecurityOpsCenter() {
  const [activeTab, setActiveTab] = useState('soc_dashboard'); // soc_dashboard, iam_devices, api_security, observability_logs, backup_dr
  const [message, setMessage] = useState(null);
  const [tenantId, setTenantId] = useState('default-tenant-1');

  // --- COMPONENT NOTIFICATIONS (TOASTS) ---
  const triggerToast = (text) => {
    setMessage(text);
    setTimeout(() => setMessage(null), 4500);
  };

  // ---------------------------------------------------------------------------
  // 1. SOC SECURITY ALERTS & THREAT STATE
  // ---------------------------------------------------------------------------
  const [socAlerts, setSocAlerts] = useState([
    {
      id: 'AL-1001',
      eventType: 'WALLET_FRAUD',
      severity: 'CRITICAL',
      status: 'OPEN',
      sourceIp: '185.220.101.4',
      description: 'Suspicious immediate wallet cash out following high-value AePS credit transaction.',
      deviceName: 'Redmi Note 12 (Mumbai, India)',
      createdAt: '2026-07-03T12:45:00Z',
      details: 'AePS direct deposit amount: INR 2,50,000. Outgoing IMPS instant transfer: INR 2,45,000. Interval: 4.5s. Flagged by fraud threshold rule #8.',
      remediation: 'Temporary lockout of outward merchant transfer API. Alert pushed to risk supervisor.'
    },
    {
      id: 'AL-1002',
      eventType: 'FAILED_LOGIN',
      severity: 'LOW',
      status: 'RESOLVED',
      sourceIp: '103.45.201.12',
      description: '3 consecutive failed login attempts on retailer subramanyampaipuri14@gmail.com',
      deviceName: 'Chrome 120 / Windows 11 (Bengaluru)',
      createdAt: '2026-07-03T11:20:00Z',
      details: 'Failed passcode validations. User subsequently bypassed locked state via biometric MFA challenge.',
      remediation: 'IP temporary lockout released. Identity confirmed.'
    },
    {
      id: 'AL-1003',
      eventType: 'API_ABUSE',
      severity: 'HIGH',
      status: 'UNDER_INVESTIGATION',
      sourceIp: '49.206.12.89',
      description: 'Rate limit tripped on DMT transaction API. 450 requests/sec detected.',
      deviceName: 'DMT Integration Node B12',
      createdAt: '2026-07-03T09:15:00Z',
      details: 'Request volume exceeds maximum approved rate limit limit (50 requests/sec) for credential sc_live_f82.',
      remediation: 'Rate limit bucket throttling applied automatically. Requesting connection verify from retail client.'
    },
    {
      id: 'AL-1004',
      eventType: 'CREDIT_ABUSE',
      severity: 'MEDIUM',
      status: 'OPEN',
      sourceIp: '122.164.21.144',
      description: 'Multiple credit line utility drawdown applications matching shell-company patterns.',
      deviceName: 'Corporate Credit Portal',
      createdAt: '2026-07-03T08:02:00Z',
      details: 'Dummy Trade Corp applied for 3 separate credit allocations using identical MCA verification certificates.',
      remediation: 'Flagged for physical verification. Suspended automated early sanction flow.'
    }
  ]);

  const [resolveIncidentId, setResolveIncidentId] = useState('');
  const [resolutionText, setResolutionText] = useState('');

  const handleResolveAlert = (e) => {
    e.preventDefault();
    if (!resolveIncidentId || !resolutionText) return;

    setSocAlerts(socAlerts.map(alert => {
      if (alert.id === resolveIncidentId) {
        return { ...alert, status: 'RESOLVED', remediation: resolutionText };
      }
      return alert;
    }));

    triggerToast(`SOC Incident ${resolveIncidentId} resolved successfully with remediation report.`);
    setResolveIncidentId('');
    setResolutionText('');
  };

  // ---------------------------------------------------------------------------
  // 2. IAM SESSIONS & DEVICES STATE
  // ---------------------------------------------------------------------------
  const [userSessions, setUserSessions] = useState([
    {
      id: 'SESS-901',
      email: 'subramanyampaipuri14@gmail.com',
      ipAddress: '157.48.91.22',
      location: 'Bengaluru, Karnataka',
      deviceName: 'OnePlus 11 Pro',
      mfaVerified: true,
      isActive: true,
      createdAt: '2026-07-03T11:54:00Z'
    },
    {
      id: 'SESS-902',
      email: 'sales-admin@suryacredit.com',
      ipAddress: '103.20.144.18',
      location: 'Mumbai, Maharashtra',
      deviceName: 'ThinkPad X1 Carbon',
      mfaVerified: true,
      isActive: true,
      createdAt: '2026-07-03T10:12:00Z'
    },
    {
      id: 'SESS-903',
      email: 'guest-partner@surya.in',
      ipAddress: '185.220.101.4',
      location: 'Unknown Router',
      deviceName: 'Unknown Chrome Android',
      mfaVerified: false,
      isActive: false,
      createdAt: '2026-07-03T07:22:00Z'
    }
  ]);

  const [registeredDevices, setRegisteredDevices] = useState([
    {
      id: 'DEV-001',
      deviceName: 'OnePlus 11 Pro',
      deviceType: 'MOBILE',
      osVersion: 'Android 13',
      fingerprint: 'fg_oneplus_11_829a',
      status: 'ACTIVE',
      isTrusted: true,
      registeredAt: '2026-05-18'
    },
    {
      id: 'DEV-002',
      deviceName: 'ThinkPad X1 Carbon',
      deviceType: 'DESKTOP',
      osVersion: 'Windows 11 Pro',
      fingerprint: 'fg_thinkpad_x1_992c',
      status: 'ACTIVE',
      isTrusted: true,
      registeredAt: '2026-04-03'
    },
    {
      id: 'DEV-003',
      deviceName: 'Unknown Emulator ID',
      deviceType: 'MOBILE',
      osVersion: 'Android 9',
      fingerprint: 'fg_emu_suspicious_8b',
      status: 'REVOKED',
      isTrusted: false,
      registeredAt: '2026-06-28'
    }
  ]);

  const handleRevokeSession = (sessId) => {
    setUserSessions(userSessions.map(s => s.id === sessId ? { ...s, isActive: false } : s));
    triggerToast(`Session ${sessId} invalidated. Token revoked immediately on gateway.`);
  };

  const handleRevokeDevice = (devId) => {
    setRegisteredDevices(registeredDevices.map(d => d.id === devId ? { ...d, status: 'REVOKED', isTrusted: false } : d));
    triggerToast(`Hardware device ${devId} revoked. Blacklisted from API access.`);
  };

  const handleToggleTrustDevice = (devId) => {
    setRegisteredDevices(registeredDevices.map(d => {
      if (d.id === devId) {
        const nextTrust = !d.isTrusted;
        return { ...d, isTrusted: nextTrust };
      }
      return d;
    }));
  };

  // ---------------------------------------------------------------------------
  // 3. API SECURITY & B2B DEVELOPER KEYS STATE
  // ---------------------------------------------------------------------------
  const [apiKeys, setApiKeys] = useState([
    {
      id: 'KEY-001',
      name: 'Retail Gateway Live Integration',
      keyPrefix: 'sc_live_f82',
      scopes: 'wallet:read,wallet:write,transaction:init',
      ipWhitelist: '13.125.10.41, 52.78.20.89',
      rateLimitRps: 50,
      isActive: true,
      createdAt: '2026-06-03'
    },
    {
      id: 'KEY-002',
      name: 'Sandbox Test Key',
      keyPrefix: 'sc_test_b12',
      scopes: 'wallet:read,transaction:init',
      ipWhitelist: null,
      rateLimitRps: 10,
      isActive: true,
      createdAt: '2026-06-23'
    }
  ]);

  const [newKeyName, setNewKeyName] = useState('');
  const [newKeyScopes, setNewKeyScopes] = useState('wallet:read,transaction:init');
  const [newKeyWhitelist, setNewKeyWhitelist] = useState('');
  const [newKeyRps, setNewKeyRps] = useState(20);
  const [createdKeyReport, setCreatedKeyReport] = useState(null);

  const handleCreateApiKey = (e) => {
    e.preventDefault();
    if (!newKeyName) {
      triggerToast('Please provide a client name for the key.');
      return;
    }

    const uuid = Math.floor(Math.random() * 10000);
    const clearKey = `sc_live_${Math.random().toString(36).substring(2, 15)}${Math.random().toString(36).substring(2, 15)}`;
    
    const newKeyObj = {
      id: `KEY-00${apiKeys.length + 1}`,
      name: newKeyName,
      keyPrefix: clearKey.substring(0, 11),
      scopes: newKeyScopes,
      ipWhitelist: newKeyWhitelist ? newKeyWhitelist : null,
      rateLimitRps: parseInt(newKeyRps) || 20,
      isActive: true,
      createdAt: new Date().toISOString().substring(0, 10)
    };

    setApiKeys([newKeyObj, ...apiKeys]);
    setCreatedKeyReport({ name: newKeyName, clearKey });
    setNewKeyName('');
    setNewKeyWhitelist('');
    triggerToast(`Symmetric B2B key issued safely for ${newKeyName}.`);
  };

  const handleToggleApiKeyStatus = (keyId) => {
    setApiKeys(apiKeys.map(k => k.id === keyId ? { ...k, isActive: !k.isActive } : k));
  };

  const handleRevokeApiKey = (keyId) => {
    setApiKeys(apiKeys.filter(k => k.id !== keyId));
    triggerToast(`API key credential ${keyId} revoked permanently from gateway memory routers.`);
  };

  // ---------------------------------------------------------------------------
  // 4. METRICS & LOG AUDIT TRAIL STATE
  // ---------------------------------------------------------------------------
  const [telemetry, setTelemetry] = useState({
    cpu: 34,
    memory: 66,
    apiLatency: 48,
    activeJobs: 3
  });

  // Simulated live telemetry fluctuation
  useEffect(() => {
    const interval = setInterval(() => {
      setTelemetry({
        cpu: Math.floor(Math.random() * 20) + 20,
        memory: Math.floor(Math.random() * 8) + 60,
        apiLatency: Math.floor(Math.random() * 30) + 35,
        activeJobs: Math.floor(Math.random() * 5)
      });
    }, 4000);
    return () => clearInterval(interval);
  }, []);

  const [logType, setLogType] = useState('SYSTEM'); // SYSTEM, ACCESS, SECURITY
  const [logSearch, setLogSearch] = useState('');
  const [systemLogs, setSystemLogs] = useState([
    { id: 'LOG-01', level: 'INFO', module: 'GATEWAY', message: 'Successfully validated signed payload for transaction #DMT-98121.', time: '12:53:02' },
    { id: 'LOG-02', level: 'WARN', module: 'AUTH', message: 'MFA SMS verification delay of 4.5s on operator subramanyampaipuri14@gmail.com.', time: '12:51:14' },
    { id: 'LOG-03', level: 'INFO', module: 'COMPLIANCE', message: 'Compliance risk score calculated for retailer w-7182: 12.5 (CLEAR).', time: '12:48:30' },
    { id: 'LOG-04', level: 'ERROR', module: 'WALLET', message: 'Failed database pool acquire response for AEPS client balance inquiry.', time: '12:44:02' },
    { id: 'LOG-05', level: 'INFO', module: 'BACKUP', message: 'Continuous point-in-time recovery WAL journal archive verified: OK.', time: '12:30:00' }
  ]);

  const [accessLogs, setAccessLogs] = useState([
    { id: 'ACC-01', ip: '13.125.10.41', method: 'POST', path: '/api/v1/payments/initiate', code: 200, elapsed: 48, key: 'sc_live_f82' },
    { id: 'ACC-02', ip: '157.48.91.22', method: 'GET', path: '/api/v1/wallet/balance', code: 200, elapsed: 14, key: 'User-Token' },
    { id: 'ACC-03', ip: '185.220.101.4', method: 'POST', path: '/api/v1/dmt/transfer', code: 429, elapsed: 8, key: 'sc_live_f82' },
    { id: 'ACC-04', ip: '52.78.20.89', method: 'GET', path: '/api/v1/ledger/statement', code: 200, elapsed: 95, key: 'sc_live_f82' }
  ]);

  const filteredSystemLogs = systemLogs.filter(log => {
    if (!logSearch) return true;
    const s = logSearch.toLowerCase();
    return log.message.toLowerCase().includes(s) || log.module.toLowerCase().includes(s) || log.level.toLowerCase().includes(s);
  });

  const filteredAccessLogs = accessLogs.filter(log => {
    if (!logSearch) return true;
    const s = logSearch.toLowerCase();
    return log.path.toLowerCase().includes(s) || log.ip.includes(s) || log.method.toLowerCase().includes(s);
  });

  // ---------------------------------------------------------------------------
  // 5. BACKUP & DISASTER RECOVERY (DR) STATE
  // ---------------------------------------------------------------------------
  const [backups, setBackups] = useState([
    { id: 'BAK-1001', type: 'FULL', status: 'COMPLETED', size: '437 MB', verified: true, keyId: 'kms-key-aes256-01', time: '2026-07-02 23:00:00' },
    { id: 'BAK-1002', type: 'INCREMENTAL', status: 'COMPLETED', size: '12 MB', verified: true, keyId: 'kms-key-aes256-01', time: '2026-07-03 06:00:00' }
  ]);

  const [drDrills, setDrDrills] = useState([
    { scenario: 'DATABASE_FAILOVER', rto: '4.2 seconds', rpo: '0 seconds (Sync Replication)', status: 'SUCCESS', tester: 'devops-chief@suryacredit.com', time: '30 days ago' },
    { scenario: 'REDIS_CACHE_RECOVERY', rto: '1.8 seconds', rpo: '0 seconds', status: 'SUCCESS', tester: 'devops-chief@suryacredit.com', time: '12 days ago' }
  ]);

  const handleTriggerManualBackup = (type) => {
    const newId = `BAK-${1000 + backups.length + 1}`;
    const newB = {
      id: newId,
      type,
      status: 'IN_PROGRESS',
      size: '0 MB',
      verified: false,
      keyId: 'kms-key-aes256-01',
      time: new Date().toISOString().replace('T', ' ').substring(0, 19)
    };

    setBackups([newB, ...backups]);
    triggerToast(`manual symmetric ${type} backup initialized inside Route53 secure vault.`);

    setTimeout(() => {
      setBackups(currentBackups => currentBackups.map(b => {
        if (b.id === newId) {
          return { ...b, status: 'COMPLETED', size: type === 'FULL' ? '441 MB' : '15 MB', verified: true };
        }
        return b;
      }));
      triggerToast(`Backup job ${newId} verified and written successfully.`);
    }, 2500);
  };

  const handleTriggerDrDrill = (scenario) => {
    triggerToast(`Simulated active DR failover drill initiated: ${scenario}. Testing connection pipelines...`);

    setTimeout(() => {
      const achievedRto = scenario === 'DATABASE_FAILOVER' ? '3.9 seconds' : '1.2 seconds';
      const newDr = {
        scenario,
        rto: achievedRto,
        rpo: '0 seconds',
        status: 'SUCCESS',
        tester: 'subramanyampaipuri14@gmail.com',
        time: 'Just now'
      };
      setDrDrills([newDr, ...drDrills]);
      alert(`Disaster Recovery drill completed successfully!\n\nScenario: ${scenario}\nAchieved RTO: ${achievedRto}\nAchieved RPO: 0 seconds (No data loss)\nRunbook logs saved to audit registry.`);
    }, 3000);
  };

  return (
    <div className="bg-slate-50 min-h-screen">
      {/* HEADER SECTION */}
      <div className="bg-slate-900 text-white py-6 px-8 flex flex-col md:flex-row justify-between items-start md:items-center border-b border-slate-800 shadow-sm">
        <div>
          <div className="flex items-center space-x-3">
            <span className="p-2 bg-sky-500 rounded text-slate-900 font-black text-xs tracking-wider">SEC-OPS</span>
            <h1 className="text-2xl font-bold tracking-tight">Surya Admin Security Operations Center</h1>
          </div>
          <p className="text-sm text-slate-400 mt-1">Enterprise-grade SOC, Identity Vault (IAM), Real-time Logging, Telemetry and Business Continuity Management</p>
        </div>
        <div className="mt-4 md:mt-0 flex items-center space-x-3">
          <label className="text-xs text-slate-400 font-mono">MULTI-TENANT REALM:</label>
          <select 
            value={tenantId} 
            onChange={(e) => setTenantId(e.target.value)}
            className="bg-slate-800 text-white font-mono text-xs border border-slate-700 rounded px-3 py-1.5 focus:outline-none focus:ring-1 focus:ring-sky-500"
          >
            <option value="default-tenant-1">Tenant: Default (retail_node)</option>
            <option value="tenant-merchant-91">Tenant: Surya merchant Hub</option>
            <option value="tenant-fintech-co">Tenant: Apex FinTech API</option>
          </select>
        </div>
      </div>

      {/* COMPONENT TOAST */}
      {message && (
        <div className="bg-slate-900 text-white px-6 py-3 border-l-4 border-sky-400 fixed bottom-6 right-6 z-50 rounded shadow-2xl flex items-center space-x-2 transition-all">
          <span className="animate-ping h-2 w-2 rounded-full bg-sky-400"></span>
          <span className="text-sm font-semibold">{message}</span>
        </div>
      )}

      {/* PRIMARY TABULAR ENGINE */}
      <div className="max-w-7xl mx-auto py-8 px-6">
        <div className="flex space-x-2 overflow-x-auto pb-3 mb-8 border-b border-slate-200">
          {[
            { id: 'soc_dashboard', label: 'SOC Threat Queue', icon: '🛡️' },
            { id: 'iam_devices', label: 'Sessions & Device IAM', icon: '👤' },
            { id: 'api_security', label: 'B2B API Keys & Gateway', icon: '🔑' },
            { id: 'observability_logs', label: 'Auditing & System Logs', icon: '📊' },
            { id: 'backup_dr', label: 'Continuity & Disaster Recovery', icon: '☁️' }
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => { setActiveTab(tab.id); setLogSearch(''); }}
              className={`flex items-center space-x-2 px-5 py-3 rounded-lg font-semibold text-sm transition-all whitespace-nowrap ${
                activeTab === tab.id 
                  ? 'bg-slate-900 text-white shadow-md' 
                  : 'bg-white text-slate-600 hover:bg-slate-100 border border-slate-200'
              }`}
            >
              <span>{tab.icon}</span>
              <span>{tab.label}</span>
            </button>
          ))}
        </div>

        {/* 1. SOC ALERTS & FRAUD INVESTIGATION TAB */}
        {activeTab === 'soc_dashboard' && (
          <div className="space-y-6">
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
              <h2 className="text-lg font-bold text-slate-900 mb-2">Threat Detection Alerts</h2>
              <p className="text-xs text-slate-500 mb-6">Real-time alerts generated by login anomalies, suspicious wallet behaviors, API rate limit abuse, and credit pattern checkers.</p>

              <div className="grid grid-cols-1 gap-6">
                {socAlerts.map((alert) => {
                  let badgeColor = "bg-blue-100 text-blue-800";
                  if (alert.severity === 'CRITICAL') badgeColor = "bg-red-100 text-red-800 font-bold border border-red-200";
                  else if (alert.severity === 'HIGH') badgeColor = "bg-amber-100 text-amber-800 font-semibold";
                  
                  const isOpen = alert.status !== 'RESOLVED';

                  return (
                    <div key={alert.id} className="border border-slate-200 rounded-lg p-5 flex flex-col md:flex-row justify-between items-start md:items-center space-y-4 md:space-y-0 bg-slate-50 hover:bg-white transition-all">
                      <div className="space-y-2 max-w-3xl">
                        <div className="flex items-center space-x-3">
                          <span className={`text-xs px-2.5 py-0.5 rounded-full ${badgeColor}`}>{alert.severity}</span>
                          <span className="text-xs font-mono text-slate-500">{alert.id}</span>
                          <span className="text-xs font-mono text-slate-400">IP: {alert.sourceIp}</span>
                          <span className="text-xs text-slate-500">{new Date(alert.createdAt).toLocaleTimeString()}</span>
                        </div>
                        <h3 className="text-base font-bold text-slate-900">{alert.description}</h3>
                        <p className="text-sm text-slate-600">{alert.details}</p>
                        <p className="text-xs text-slate-500 font-medium">Device: {alert.deviceName}</p>
                        {alert.remediation && (
                          <div className="mt-3 p-3 bg-emerald-50 rounded border border-emerald-100 text-xs text-emerald-800 font-mono">
                            <strong>[Remediation Archive]:</strong> {alert.remediation}
                          </div>
                        )}
                      </div>
                      <div className="flex items-center space-x-3">
                        {isOpen ? (
                          <button
                            onClick={() => {
                              setResolveIncidentId(alert.id);
                              setResolutionText(`Blocked source IP ${alert.sourceIp}. Cleared security state.`);
                            }}
                            className="bg-slate-900 hover:bg-slate-800 text-white text-xs px-4 py-2 rounded-lg font-bold transition-all shadow-sm"
                          >
                            Remediate
                          </button>
                        ) : (
                          <span className="text-xs font-bold text-emerald-700 bg-emerald-100 px-3 py-1.5 rounded">RESOLVED</span>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {resolveIncidentId && (
              <form onSubmit={handleResolveAlert} className="bg-slate-900 text-white p-6 rounded-xl space-y-4 shadow-xl border border-slate-800">
                <h3 className="text-base font-bold text-sky-400">File Security Remediation: {resolveIncidentId}</h3>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
                  <div className="md:col-span-2">
                    <label className="block text-xs font-semibold text-slate-300 mb-2">Remediation Action Taken (Signed Ledger Record)</label>
                    <input
                      type="text"
                      required
                      value={resolutionText}
                      onChange={(e) => setResolutionText(e.target.value)}
                      className="w-full bg-slate-800 border border-slate-700 rounded px-4 py-2 text-sm text-white focus:outline-none focus:border-sky-500"
                    />
                  </div>
                  <div>
                    <button
                      type="submit"
                      className="w-full bg-sky-500 text-slate-900 font-bold hover:bg-sky-400 text-sm py-2.5 rounded transition-all"
                    >
                      Authorize Closeout
                    </button>
                  </div>
                </div>
              </form>
            )}
          </div>
        )}

        {/* 2. IAM SESSIONS & HARDWARE DEVICES TAB */}
        {activeTab === 'iam_devices' && (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Active Sessions */}
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4">
              <h2 className="text-lg font-bold text-slate-900">Active Tenant Login Sessions</h2>
              <p className="text-xs text-slate-500">System monitors all live JWT tokens. Invalidating a session permanently logs out the target browser instantly.</p>

              <div className="space-y-4">
                {userSessions.map((sess) => (
                  <div key={sess.id} className="p-4 border border-slate-100 rounded-lg flex justify-between items-center bg-slate-50">
                    <div>
                      <p className="text-sm font-bold text-slate-800">{sess.email}</p>
                      <p className="text-xs text-slate-500">{sess.deviceName} • {sess.location}</p>
                      <p className="text-xs text-slate-400 font-mono mt-1">IP: {sess.ipAddress} • {sess.id}</p>
                    </div>
                    {sess.isActive ? (
                      <button
                        onClick={() => handleRevokeSession(sess.id)}
                        className="bg-red-50 text-red-700 hover:bg-red-100 text-xs font-bold px-3 py-1.5 rounded transition-all"
                      >
                        Revoke
                      </button>
                    ) : (
                      <span className="text-xs text-slate-400 bg-slate-200 px-3 py-1.5 rounded font-medium">TERMINATED</span>
                    )}
                  </div>
                ))}
              </div>
            </div>

            {/* Hardware Devices */}
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4">
              <h2 className="text-lg font-bold text-slate-900">Authorized Device Registry</h2>
              <p className="text-xs text-slate-500">M3 biometric trusted bypass registers device hardware hashes to bypass MFA triggers on recurring operations.</p>

              <div className="space-y-4">
                {registeredDevices.map((dev) => {
                  const isActive = dev.status === 'ACTIVE';
                  return (
                    <div key={dev.id} className="p-4 border border-slate-100 rounded-lg flex justify-between items-center bg-slate-50">
                      <div>
                        <div className="flex items-center space-x-2">
                          <p className="text-sm font-bold text-slate-800">{dev.deviceName}</p>
                          <span className={`text-[10px] px-1.5 py-0.5 rounded ${isActive ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'}`}>
                            {dev.status}
                          </span>
                        </div>
                        <p className="text-xs text-slate-500">{dev.deviceType} • {dev.osVersion}</p>
                        <p className="text-xs text-slate-400 font-mono mt-1">Fingerprint: {dev.fingerprint}</p>
                      </div>
                      <div className="flex items-center space-x-2">
                        {isActive && (
                          <button
                            onClick={() => handleToggleTrustDevice(dev.id)}
                            className={`text-xs font-bold px-3 py-1.5 rounded transition-all ${
                              dev.isTrusted ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-slate-200 text-slate-600'
                            }`}
                          >
                            {dev.isTrusted ? '★ Trusted' : 'Un-trusted'}
                          </button>
                        )}
                        {isActive ? (
                          <button
                            onClick={() => handleRevokeDevice(dev.id)}
                            className="bg-red-50 text-red-600 hover:bg-red-100 p-1.5 rounded transition-all"
                            title="Block Device"
                          >
                            Block
                          </button>
                        ) : (
                          <span className="text-xs text-slate-400">Blocked</span>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        )}

        {/* 3. API SECURITY & B2B DEVELOPER KEYS TAB */}
        {activeTab === 'api_security' && (
          <div className="space-y-8">
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              {/* Key Issuance Form */}
              <form onSubmit={handleCreateApiKey} className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4 h-fit">
                <h2 className="text-lg font-bold text-slate-900">Issue Cryptographic B2B API Key</h2>
                <p className="text-xs text-slate-400">Issuing a key generates a unique symmetric SHA-256 secure hash used for high-availability DMT, payouts, and wallet integrations.</p>

                <div className="space-y-3">
                  <div>
                    <label className="block text-xs font-bold text-slate-700 mb-1">Integration Partner / Client Name</label>
                    <input
                      type="text"
                      required
                      placeholder="e.g. Apex FinTech India Ltd"
                      value={newKeyName}
                      onChange={(e) => setNewKeyName(e.target.value)}
                      className="w-full text-sm bg-slate-50 border border-slate-200 rounded p-2 focus:outline-none focus:border-slate-900"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 mb-1">IP Whitelist (Comma-separated IPs)</label>
                    <input
                      type="text"
                      placeholder="e.g. 13.125.10.41, 52.78.20.89"
                      value={newKeyWhitelist}
                      onChange={(e) => setNewKeyWhitelist(e.target.value)}
                      className="w-full text-sm bg-slate-50 border border-slate-200 rounded p-2 focus:outline-none focus:border-slate-900"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 mb-1">Scopes</label>
                    <input
                      type="text"
                      value={newKeyScopes}
                      onChange={(e) => setNewKeyScopes(e.target.value)}
                      className="w-full text-sm bg-slate-50 border border-slate-200 rounded p-2 focus:outline-none"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 mb-1">Rate Limit Cap (Requests/Sec)</label>
                    <input
                      type="number"
                      value={newKeyRps}
                      onChange={(e) => setNewKeyRps(e.target.value)}
                      className="w-full text-sm bg-slate-50 border border-slate-200 rounded p-2 focus:outline-none"
                    />
                  </div>
                </div>

                <button
                  type="submit"
                  className="w-full bg-slate-900 hover:bg-slate-800 text-white font-bold text-sm py-2.5 rounded transition-all"
                >
                  Generate Symmetric Key
                </button>
              </form>

              {/* Issued Key List */}
              <div className="lg:col-span-2 bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4">
                <h2 className="text-lg font-bold text-slate-900">Active API Key Credentials</h2>
                <div className="space-y-4">
                  {apiKeys.map((k) => (
                    <div key={k.id} className="p-4 border border-slate-100 rounded-lg space-y-3 bg-slate-50 hover:bg-white transition-all">
                      <div className="flex justify-between items-center">
                        <h3 className="text-sm font-bold text-slate-800">{k.name}</h3>
                        <div className="flex items-center space-x-2">
                          <label className="text-xs text-slate-500">Active:</label>
                          <input
                            type="checkbox"
                            checked={k.isActive}
                            onChange={() => handleToggleApiKeyStatus(k.id)}
                            className="rounded text-slate-900"
                          />
                          <button
                            onClick={() => handleRevokeApiKey(k.id)}
                            className="text-xs text-red-600 font-bold hover:underline ml-4"
                          >
                            Revoke
                          </button>
                        </div>
                      </div>
                      <div className="text-xs text-slate-500 space-y-1 font-mono">
                        <div>Key Prefix: <span className="text-slate-800 font-bold">{k.keyPrefix}*****</span></div>
                        <div>Whitelisted IP: {k.ipWhitelist ? k.ipWhitelist : 'Any IP allowed (Not secure)'}</div>
                        <div>Rate Limit Cap: {k.rateLimitRps} Requests/Sec</div>
                      </div>
                      <div className="flex flex-wrap gap-1.5">
                        {k.scopes.split(',').map(sc => (
                          <span key={sc} className="text-[10px] bg-slate-200 text-slate-700 px-2 py-0.5 rounded">{sc}</span>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {createdKeyReport && (
              <div className="bg-slate-950 text-white p-6 rounded-xl border border-slate-800 shadow-2xl relative space-y-4">
                <button 
                  onClick={() => setCreatedKeyReport(null)}
                  className="absolute top-4 right-4 text-slate-400 hover:text-white"
                >
                  ✕
                </button>
                <h3 className="text-emerald-400 font-bold text-base">⚠️ API CREDENTIAL GENERATED SUCCESSFULLY</h3>
                <p className="text-xs text-slate-400">Copy this raw token immediately. Due to our strict SHA-256 standard encryption, it is impossible to view this raw key again.</p>
                <div className="bg-slate-900 p-4 rounded border border-slate-800 flex justify-between items-center">
                  <code className="text-emerald-300 font-mono text-xs select-all">{createdKeyReport.clearKey}</code>
                  <button 
                    onClick={() => {
                      navigator.clipboard.writeText(createdKeyReport.clearKey);
                      triggerToast('Symmetric raw key copied to clipboard.');
                    }}
                    className="text-xs font-bold text-sky-400 hover:underline"
                  >
                    Copy Key
                  </button>
                </div>
              </div>
            )}
          </div>
        )}

        {/* 4. REALTIME OBSERVABILITY & LOGS AUDIT */}
        {activeTab === 'observability_logs' && (
          <div className="space-y-8">
            {/* Telemetry Dials */}
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-6">
              {[
                { label: 'CPU LOAD (API)', value: telemetry.cpu, unit: '%', text: 'Steady state range', color: 'text-blue-500' },
                { label: 'MEM POOL RESOURCE', value: telemetry.memory, unit: '%', text: 'Heap OK', color: 'text-purple-500' },
                { label: 'GATEWAY RESP TIME', value: telemetry.apiLatency, unit: 'ms', text: 'Latency < 50ms', color: 'text-emerald-500' },
                { label: 'PENDING JOBS QUEUE', value: telemetry.activeJobs, unit: 'jobs', text: 'Instant pipeline', color: 'text-amber-500' }
              ].map((m) => (
                <div key={m.label} className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm space-y-2">
                  <p className="text-[11px] font-bold text-slate-500">{m.label}</p>
                  <p className="text-3xl font-black text-slate-900">{m.value}<span className="text-sm font-semibold text-slate-400 ml-1">{m.unit}</span></p>
                  <p className={`text-[10px] font-bold ${m.color}`}>{m.text}</p>
                </div>
              ))}
            </div>

            {/* Log Auditing Engine */}
            <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-6">
              <div className="flex flex-col md:flex-row justify-between items-start md:items-center space-y-4 md:space-y-0">
                <div>
                  <h2 className="text-lg font-bold text-slate-900">Real-Time Security & Compliance Log Audit Center</h2>
                  <p className="text-xs text-slate-500">Live streams security, transaction API payloads, system worker activities, and database pools.</p>
                </div>

                <div className="flex space-x-2">
                  <button 
                    onClick={() => { setLogType('SYSTEM'); setLogSearch(''); }}
                    className={`text-xs font-bold px-4 py-2 rounded-lg transition-all border ${logType === 'SYSTEM' ? 'bg-slate-900 text-white border-slate-900' : 'bg-slate-50 text-slate-600 border-slate-200'}`}
                  >
                    System Logs
                  </button>
                  <button 
                    onClick={() => { setLogType('ACCESS'); setLogSearch(''); }}
                    className={`text-xs font-bold px-4 py-2 rounded-lg transition-all border ${logType === 'ACCESS' ? 'bg-slate-900 text-white border-slate-900' : 'bg-slate-50 text-slate-600 border-slate-200'}`}
                  >
                    Access Logs
                  </button>
                </div>
              </div>

              {/* Search Log Bar */}
              <div className="relative">
                <input
                  type="text"
                  placeholder={`Search audit payload by keywords, modules, IPs, or error states...`}
                  value={logSearch}
                  onChange={(e) => setLogSearch(e.target.value)}
                  className="w-full text-sm bg-slate-50 border border-slate-200 rounded px-4 py-2.5 pl-10 focus:outline-none focus:border-slate-900 focus:bg-white"
                />
                <span className="absolute left-3 top-3.5 text-slate-400 text-xs">🔍</span>
              </div>

              {/* Logs Output Panel */}
              <div className="bg-slate-950 rounded-xl p-5 border border-slate-900 text-slate-300 font-mono text-xs space-y-3 h-96 overflow-y-auto">
                {logType === 'SYSTEM' ? (
                  filteredSystemLogs.length > 0 ? (
                    filteredSystemLogs.map((log) => {
                      let levelColor = "text-sky-400";
                      if (log.level === 'ERROR') levelColor = "text-rose-500 font-bold";
                      else if (log.level === 'WARN') levelColor = "text-amber-400";
                      return (
                        <div key={log.id} className="border-b border-slate-900 pb-2 flex items-start space-x-3">
                          <span className="text-slate-500">[{log.time}]</span>
                          <span className={levelColor}>[{log.level}]</span>
                          <span className="text-sky-300 font-semibold">[{log.module}]</span>
                          <span className="text-slate-200">{log.message}</span>
                        </div>
                      );
                    })
                  ) : (
                    <div className="text-slate-500 text-center py-12">No active system audit trace found matching criteria.</div>
                  )
                ) : (
                  filteredAccessLogs.length > 0 ? (
                    filteredAccessLogs.map((log) => {
                      let codeColor = "text-emerald-400";
                      if (log.code >= 400) codeColor = "text-rose-500 font-bold animate-pulse";
                      return (
                        <div key={log.id} className="border-b border-slate-900 pb-2 flex items-start justify-between">
                          <div className="flex items-start space-x-3">
                            <span className="text-slate-500">[{log.ip}]</span>
                            <span className="text-purple-400">[{log.method}]</span>
                            <span className="text-slate-200 font-semibold">{log.path}</span>
                            <span className="text-slate-500">(API Cred: {log.key})</span>
                          </div>
                          <div className="flex items-center space-x-4">
                            <span className={codeColor}>{log.code}</span>
                            <span className="text-slate-400">{log.elapsed}ms</span>
                          </div>
                        </div>
                      );
                    })
                  ) : (
                    <div className="text-slate-500 text-center py-12">No matching access logs found.</div>
                  )
                )}
              </div>
            </div>
          </div>
        )}

        {/* 5. BACKUP & DISASTER RECOVERY CONTINUITY TAB */}
        {activeTab === 'backup_dr' && (
          <div className="space-y-8">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              {/* Ledger Backups */}
              <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4">
                <div className="flex justify-between items-center">
                  <h2 className="text-lg font-bold text-slate-900">Ledger Snapshot Backups</h2>
                  <div className="flex space-x-2">
                    <button
                      onClick={() => handleTriggerManualBackup('INCREMENTAL')}
                      className="bg-sky-50 text-sky-800 hover:bg-sky-100 text-xs font-bold px-3 py-1.5 rounded"
                    >
                      + Incremental
                    </button>
                    <button
                      onClick={() => handleTriggerManualBackup('FULL')}
                      className="bg-slate-900 hover:bg-slate-800 text-white text-xs font-bold px-3 py-1.5 rounded"
                    >
                      + Full Snapshot
                    </button>
                  </div>
                </div>
                <p className="text-xs text-slate-500">Continuous ledger WAL journals are packaged with symmetric KMS AES-256 ciphers and synchronized across multiple Route53 S3 buckets.</p>

                <div className="space-y-4">
                  {backups.map((b) => (
                    <div key={b.id} className="p-4 border border-slate-100 rounded-lg flex justify-between items-center bg-slate-50">
                      <div>
                        <p className="text-sm font-bold text-slate-800">{b.type} Ledger Backup - {b.id}</p>
                        <p className="text-xs text-slate-500 font-mono mt-1">KMS Key: {b.keyId} • size: {b.size}</p>
                        <p className="text-[10px] text-slate-400 font-mono">{b.time}</p>
                      </div>
                      <div className="flex items-center space-x-2">
                        {b.verified && (
                          <span className="text-[10px] font-bold text-emerald-800 bg-emerald-100 px-2 py-1.5 rounded">✓ INTEGRITY VERIFIED</span>
                        )}
                        <span className={`text-xs px-3 py-1.5 rounded font-bold ${
                          b.status === 'COMPLETED' ? 'bg-emerald-50 text-emerald-800' : 'bg-amber-50 text-amber-800 animate-pulse'
                        }`}>
                          {b.status}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* DR Drills */}
              <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4">
                <div className="flex justify-between items-center">
                  <h2 className="text-lg font-bold text-slate-900">Continuous Recovery & DR Drills</h2>
                  <div className="flex space-x-2">
                    <button
                      onClick={() => handleTriggerDrDrill('DATABASE_FAILOVER')}
                      className="bg-indigo-50 text-indigo-800 hover:bg-indigo-100 text-xs font-bold px-3 py-1.5 rounded"
                    >
                      Failover DB Drill
                    </button>
                    <button
                      onClick={() => handleTriggerDrDrill('REDIS_CACHE_RECOVERY')}
                      className="bg-purple-50 text-purple-800 hover:bg-purple-100 text-xs font-bold px-3 py-1.5 rounded"
                    >
                      Failover Redis Drill
                    </button>
                  </div>
                </div>
                <p className="text-xs text-slate-500">Simulates high-availability failover drills. Compliance audit verifies active RTO and RPO metrics inside strict B2B thresholds.</p>

                <div className="space-y-4">
                  {drDrills.map((drill, idx) => (
                    <div key={idx} className="p-4 border border-slate-100 rounded-lg space-y-3 bg-slate-50">
                      <div className="flex justify-between items-center">
                        <p className="text-sm font-bold text-slate-800">{drill.scenario}</p>
                        <span className="text-xs font-bold text-emerald-700 bg-emerald-100 px-2 py-0.5 rounded">✓ SUCCESS</span>
                      </div>
                      <div className="text-xs text-slate-500 space-y-1 font-mono">
                        <div>achieved RTO: <span className="font-bold text-slate-900">{drill.rto}</span> (Goal: &lt; 30s)</div>
                        <div>achieved RPO: <span className="font-bold text-slate-900">{drill.rpo}</span> (Goal: &lt; 10s)</div>
                        <div>Authorized Tester: {drill.tester}</div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
