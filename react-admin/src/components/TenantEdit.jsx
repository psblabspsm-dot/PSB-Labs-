import React, { useState } from 'react';

export default function TenantEdit() {
  const [branding, setBranding] = useState({
    companyName: 'Alpha Finance Solutions',
    primaryColor: '#1E3A8A',
    secondaryColor: '#F59E0B',
    fontName: 'Inter',
    supportEmail: 'support@alpha.com',
    supportPhone: '+91 22 8493 0219',
    termsAndConditions: 'https://alphafintech.in/terms',
    privacyPolicy: 'https://alphafintech.in/privacy',
  });

  const [features, setFeatures] = useState({
    marketplace: true,
    wallet: true,
    credit: true,
    aeps: true,
    dmt: false,
    bbps: true,
    recharge: true,
    pan: false,
    insurance: false,
    travel: true,
    loans: false,
    crm: true,
    ai: false,
    analytics: true,
  });

  const [message, setMessage] = useState(null);

  const handleBrandingChange = (e) => {
    const { name, value } = e.target;
    setBranding(prev => ({ ...prev, [name]: value }));
  };

  const handleFeatureToggle = (key) => {
    setFeatures(prev => ({ ...prev, [key]: !prev[key] }));
  };

  const saveChanges = () => {
    setMessage('SaaS White-Label branding & Feature Flag configurations successfully synced across networks.');
  };

  return (
    <div style={{ fontFamily: 'Inter, sans-serif', padding: '24px', backgroundColor: '#F8FAFC', minHeight: '100vh' }}>
      <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
        <h1 style={{ fontSize: '24px', fontWeight: 'bold', color: '#0F172A', marginBottom: '4px' }}>White-Label Customizer</h1>
        <p style={{ fontSize: '13px', color: '#64748B', marginBottom: '24px' }}>Modify visual elements, color schemes, custom support lines, and feature flags for multi-tenant isolation.</p>

        {message && (
          <div style={{ backgroundColor: '#ECFDF5', border: '1px solid #10B981', color: '#065F46', padding: '12px 16px', borderRadius: '6px', marginBottom: '24px', fontSize: '13px' }}>
            {message}
          </div>
        )}

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
          {/* Branding Section */}
          <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
            <h2 style={{ fontSize: '16px', fontWeight: 'bold', color: '#1E293B', marginTop: 0, marginBottom: '16px' }}>Corporate Brand Identity</h2>
            
            <div style={{ marginBottom: '16px' }}>
              <label style={{ display: 'block', fontSize: '12px', fontWeight: '600', color: '#475569', marginBottom: '6px' }}>Company Legal Name</label>
              <input type="text" name="companyName" value={branding.companyName} onChange={handleBrandingChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #CBD5E1', boxSizing: 'border-box' }} />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '16px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: '600', color: '#475569', marginBottom: '6px' }}>Primary Theme Color</label>
                <input type="color" name="primaryColor" value={branding.primaryColor} onChange={handleBrandingChange} style={{ width: '100%', height: '40px', padding: '2px', border: '1px solid #CBD5E1', borderRadius: '6px', cursor: 'pointer' }} />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: '600', color: '#475569', marginBottom: '6px' }}>Secondary Color</label>
                <input type="color" name="secondaryColor" value={branding.secondaryColor} onChange={handleBrandingChange} style={{ width: '100%', height: '40px', padding: '2px', border: '1px solid #CBD5E1', borderRadius: '6px', cursor: 'pointer' }} />
              </div>
            </div>

            <div style={{ marginBottom: '16px' }}>
              <label style={{ display: 'block', fontSize: '12px', fontWeight: '600', color: '#475569', marginBottom: '6px' }}>Typography Font Family</label>
              <input type="text" name="fontName" value={branding.fontName} onChange={handleBrandingChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #CBD5E1', boxSizing: 'border-box' }} />
            </div>

            <div style={{ marginBottom: '16px' }}>
              <label style={{ display: 'block', fontSize: '12px', fontWeight: '600', color: '#475569', marginBottom: '6px' }}>Support Email Desk</label>
              <input type="email" name="supportEmail" value={branding.supportEmail} onChange={handleBrandingChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #CBD5E1', boxSizing: 'border-box' }} />
            </div>

            <div style={{ marginBottom: '16px' }}>
              <label style={{ display: 'block', fontSize: '12px', fontWeight: '600', color: '#475569', marginBottom: '6px' }}>Terms of Service Hyperlink</label>
              <input type="text" name="termsAndConditions" value={branding.termsAndConditions} onChange={handleBrandingChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #CBD5E1', boxSizing: 'border-box' }} />
            </div>
          </div>

          {/* Feature Flags Section */}
          <div style={{ backgroundColor: '#FFF', padding: '24px', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
            <h2 style={{ fontSize: '16px', fontWeight: 'bold', color: '#1E293B', marginTop: 0, marginBottom: '16px' }}>Feature Entitlements Desk</h2>
            <p style={{ fontSize: '12px', color: '#64748B', marginTop: '-12px', marginBottom: '16px' }}>Turn modules on or off instantaneously for this tenant with no build pipelines required.</p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {Object.keys(features).map(f => (
                <div key={f} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 14px', borderRadius: '8px', border: '1px solid #F1F5F9', backgroundColor: '#FAFAFA' }}>
                  <div>
                    <div style={{ fontSize: '13px', fontWeight: 'bold', textTransform: 'capitalize', color: '#0F172A' }}>{f}</div>
                    <div style={{ fontSize: '10px', color: '#64748B' }}>Allocated SaaS access controls.</div>
                  </div>
                  <button onClick={() => handleFeatureToggle(f)} style={{ backgroundColor: features[f] ? '#10B981' : '#CBD5E1', color: '#FFF', padding: '6px 12px', border: 'none', borderRadius: '20px', fontSize: '11px', fontWeight: '700', cursor: 'pointer', minWidth: '60px', textAlign: 'center' }}>
                    {features[f] ? 'ON' : 'OFF'}
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '24px' }}>
          <button onClick={saveChanges} style={{ backgroundColor: '#0F172A', color: '#FFF', padding: '12px 24px', border: 'none', borderRadius: '8px', fontSize: '14px', fontWeight: '600', cursor: 'pointer', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)' }}>
            Save & Broadcast Configurations
          </button>
        </div>
      </div>
    </div>
  );
}
