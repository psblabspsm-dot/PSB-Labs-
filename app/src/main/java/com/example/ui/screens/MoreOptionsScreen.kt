package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.data.ChatMessage
import com.example.ui.AppViewModel
import com.example.ui.AuthState
import com.example.ui.AiState
import com.example.ui.AppNotification
import com.example.ui.SupportTicket
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class MoreSubScreen {
    MENU,
    PROFILE,
    INVOICES,
    REPORTS,
    TRANSACTIONS,
    NOTIFICATIONS,
    SETTINGS,
    SUPPORT,
    KYC,
    ARCHITECTURE,
    SUPER_ADMIN,
    CRM,
    AI_ANALYTICS,
    NOTIFICATIONS_CAMPAIGN,
    REPORTS_CENTER,
    AUDIT_COMPLIANCE,
    PARTNER_API_HUB,
    PARTNER_BUSINESS_NETWORK,
    EMPLOYEE_HRMS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    var activeSubScreen by remember { mutableStateOf(MoreSubScreen.MENU) }

    AnimatedContent(
        targetState = activeSubScreen,
        transitionSpec = {
            slideInHorizontally { width -> width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> -width } + fadeOut()
        },
        label = "MoreSubScreenTransition"
    ) { screen ->
        when (screen) {
            MoreSubScreen.MENU -> MoreMenuGrid(viewModel, onNavigate = { activeSubScreen = it })
            MoreSubScreen.PROFILE -> MerchantProfileScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.INVOICES -> InvoicesHubScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.REPORTS -> ReportsAndCommissionsScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.TRANSACTIONS -> FullLedgerScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.NOTIFICATIONS -> NotificationsHubScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.SETTINGS -> AppSettingsScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.SUPPORT -> HelpdeskSupportScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.KYC -> CompactKycScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.ARCHITECTURE -> ArchitectureInsightsScreen(onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.SUPER_ADMIN -> SuperAdminDashboardScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.CRM -> CrmHubScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.AI_ANALYTICS -> AiAnalyticsScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.NOTIFICATIONS_CAMPAIGN -> CampaignManagerScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.REPORTS_CENTER -> FintechReportsDeskScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.AUDIT_COMPLIANCE -> AuditLogsComplianceScreen(onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.PARTNER_API_HUB -> PartnerApiHubScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.PARTNER_BUSINESS_NETWORK -> BusinessNetworkManagementScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
            MoreSubScreen.EMPLOYEE_HRMS -> EmployeeHrmsScreen(viewModel, onBack = { activeSubScreen = MoreSubScreen.MENU })
        }
    }
}

@Composable
fun MoreHeader(title: String, onBack: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun MoreMenuGrid(viewModel: AppViewModel, onNavigate: (MoreSubScreen) -> Unit) {
    val currentRole by viewModel.currentRole.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFF8C00), Color(0xFFFFB300))
                    )
                )
                .padding(top = 24.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Store, "merchant", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Surya Merchant Hub",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Role: ${currentRole.label}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Options Grid Item
            item {
                Text(
                    text = "Administrative Console",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MenuGridCard(
                        title = "Profile & QR",
                        desc = "Virtual accounts & UPI",
                        icon = Icons.Default.QrCode,
                        color = Color(0xFF1E88E5),
                        onClick = { onNavigate(MoreSubScreen.PROFILE) },
                        modifier = Modifier.weight(1f)
                    )
                    MenuGridCard(
                        title = "Tax Invoices",
                        desc = "Itemized GST bills",
                        icon = Icons.Default.ReceiptLong,
                        color = Color(0xFF8E24AA),
                        onClick = { onNavigate(MoreSubScreen.INVOICES) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MenuGridCard(
                        title = "Reports",
                        desc = "Slabs & Commissions",
                        icon = Icons.Default.BarChart,
                        color = Color(0xFF00C853),
                        onClick = { onNavigate(MoreSubScreen.REPORTS) },
                        modifier = Modifier.weight(1f)
                    )
                    MenuGridCard(
                        title = "Ledger Audit",
                        desc = "Double-entry books",
                        icon = Icons.Default.History,
                        color = Color(0xFFE53935),
                        onClick = { onNavigate(MoreSubScreen.TRANSACTIONS) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MenuGridCard(
                        title = "Alerts Center",
                        desc = "NPCI & settlement",
                        icon = Icons.Default.Notifications,
                        color = Color(0xFFFFB300),
                        onClick = { onNavigate(MoreSubScreen.NOTIFICATIONS) },
                        modifier = Modifier.weight(1f)
                    )
                    MenuGridCard(
                        title = "Onboarding KYC",
                        desc = "Update compliance",
                        icon = Icons.Default.AssignmentInd,
                        color = Color(0xFF00ACC1),
                        onClick = { onNavigate(MoreSubScreen.KYC) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MenuGridCard(
                        title = "Help Support",
                        desc = "Raise support tickets",
                        icon = Icons.Default.SupportAgent,
                        color = Color(0xFF43A047),
                        onClick = { onNavigate(MoreSubScreen.SUPPORT) },
                        modifier = Modifier.weight(1f)
                    )
                    MenuGridCard(
                        title = "App Settings",
                        desc = "Theme & PIN codes",
                        icon = Icons.Default.Settings,
                        color = Color(0xFF757575),
                        onClick = { onNavigate(MoreSubScreen.SETTINGS) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Enterprise Console Section
            item {
                Text(
                    text = "Enterprise Suite (CRM, AI, Super Admin)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MenuGridCard(
                        title = "Super Admin",
                        desc = "Ecosystem live stats",
                        icon = Icons.Default.SupervisorAccount,
                        color = Color(0xFFE65100),
                        onClick = { onNavigate(MoreSubScreen.SUPER_ADMIN) },
                        modifier = Modifier.weight(1f)
                    )
                    MenuGridCard(
                        title = "CRM Workspace",
                        desc = "Lead & pipeline tracking",
                        icon = Icons.Default.ContactPhone,
                        color = Color(0xFF1565C0),
                        onClick = { onNavigate(MoreSubScreen.CRM) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MenuGridCard(
                        title = "AI Analytics",
                        desc = "Forecasting & growth",
                        icon = Icons.Default.Psychology,
                        color = Color(0xFF6A1B9A),
                        onClick = { onNavigate(MoreSubScreen.AI_ANALYTICS) },
                        modifier = Modifier.weight(1f)
                    )
                    MenuGridCard(
                        title = "Campaigns",
                        desc = "Promo broadcast logs",
                        icon = Icons.Default.Campaign,
                        color = Color(0xFF2E7D32),
                        onClick = { onNavigate(MoreSubScreen.NOTIFICATIONS_CAMPAIGN) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MenuGridCard(
                        title = "Reports Hub",
                        desc = "Download Tax & Txn sheets",
                        icon = Icons.Default.Assessment,
                        color = Color(0xFFC2185B),
                        onClick = { onNavigate(MoreSubScreen.REPORTS_CENTER) },
                        modifier = Modifier.weight(1f)
                    )
                    MenuGridCard(
                        title = "Audits Desk",
                        desc = "Security API & activity logs",
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF37474F),
                        onClick = { onNavigate(MoreSubScreen.AUDIT_COMPLIANCE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MenuGridCard(
                        title = "Partner API Hub",
                        desc = "Switch, failover & route APIs",
                        icon = Icons.Default.Shuffle,
                        color = Color(0xFF00ACC1),
                        onClick = { onNavigate(MoreSubScreen.PARTNER_API_HUB) },
                        modifier = Modifier.weight(1f)
                    )
                    MenuGridCard(
                        title = "Business Network",
                        desc = "Franchise, Distr. & Commission",
                        icon = Icons.Default.Hub,
                        color = Color(0xFF3F51B5),
                        onClick = { onNavigate(MoreSubScreen.PARTNER_BUSINESS_NETWORK) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MenuGridCard(
                        title = "Employee HRMS",
                        desc = "Digital ID, Payslips & Payroll",
                        icon = Icons.Default.Badge,
                        color = Color(0xFF673AB7),
                        onClick = { onNavigate(MoreSubScreen.EMPLOYEE_HRMS) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Architecture Insight Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(MoreSubScreen.ARCHITECTURE) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schema,
                            contentDescription = null,
                            tint = Color(0xFFFF8C00),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Technical Database Schema",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Review PostgreSQL Prisma multi-tenant architecture details.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                    }
                }
            }

            // Power brand logo
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Surya FinTech Core v2.4.1",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "ISO 27001 Certified • Bank Settle Enforced",
                        fontSize = 9.sp,
                        color = Color.Gray.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun MenuGridCard(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = desc,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ---------------- SUB SCREENS IMPLEMENTATIONS ----------------

@Composable
fun MerchantProfileScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        MoreHeader(title = "Merchant Profile & QR", onBack = onBack)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dynamic UPI QR Code Card
            Card(
                modifier = Modifier.fillMaxWidth(0.85f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SURYA BHIM UPI QR",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E88E5)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Simulated QR Code Graphic using Compose shapes
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .border(3.dp, Color(0xFF1E88E5), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // QR corners mock
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Box(modifier = Modifier.size(35.dp).background(Color.Black))
                                Box(modifier = Modifier.size(35.dp).background(Color.Black))
                            }
                            // Center symbol logo
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF8C00)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WbSunny, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Box(modifier = Modifier.size(35.dp).background(Color.Black))
                                // Custom barcode stripes
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Box(modifier = Modifier.size(width = 35.dp, height = 6.dp).background(Color.Black))
                                    Box(modifier = Modifier.size(width = 35.dp, height = 6.dp).background(Color.Black))
                                    Box(modifier = Modifier.size(width = 35.dp, height = 6.dp).background(Color.Black))
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Proprietor: Ramesh Pai",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "VPA: suryacredit.ramesh@icici",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            // Virtual Settlements Bank Nodes
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dynamic Settlements Bank Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Load funds into this account via IMPS/NEFT/RTGS for instant wallet load balance credit.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileDetailRow("Beneficiary Name", "SURYA CREDIT - RAMESH PAI")
                    ProfileDetailRow("Virtual Bank Name", "YES BANK LTD (FinTech branch)")
                    ProfileDetailRow("Virtual Account No", "SURYAPAI9281038")
                    ProfileDetailRow("IFSC Code", "YESB0CMSNOC")
                    ProfileDetailRow("Settlement cycle", "T+0 Real-time (Instant)")
                }
            }
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun InvoicesHubScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var selectedTxn by remember { mutableStateOf<Transaction?>(null) }
    val transactions by viewModel.transactions.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        MoreHeader(title = "Tax Invoices Hub", onBack = {
            if (selectedTxn != null) selectedTxn = null else onBack()
        })

        if (selectedTxn == null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "B2B Digital Tax Invoices",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Select any completed service transaction to view/generate standard GST itemized CGST/SGST invoices.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(transactions) { txn ->
                    Card(
                        onClick = { selectedTxn = txn },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = txn.description, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "Ref: ${txn.referenceId} • ${txn.status}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "₹${txn.amount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        } else {
            // Detailed PDF-style Digital Invoice
            val txn = selectedTxn!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Invoice Title Block
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("SURYA FinTech Pvt Ltd", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.Black)
                                Text("GSTIN: 29AAECS1234B1Z2", fontSize = 10.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TAX INVOICE", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.Red)
                                Text("No: SURYA/2026/${txn.referenceId.takeLast(5)}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Parties Block
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Billed From:", fontSize = 10.sp, color = Color.Gray)
                                Text("Surya Credit Solutions", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                                Text("Solar Towers, Tech Zone 4", fontSize = 10.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Billed To (Merchant):", fontSize = 10.sp, color = Color.Gray)
                                Text("Surya Digital World", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                                Text("GSTIN: 29MMKPR1028P1Z8", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Itemized calculations
                        val serviceCost = txn.amount / 1.18
                        val gstAmt = txn.amount - serviceCost
                        val halfGst = gstAmt / 2

                        Text("Itemized Tax Breakdown:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        InvoiceCalculationRow("Service Cost (Taxable)", "₹${String.format("%,.2f", serviceCost)}")
                        InvoiceCalculationRow("Central CGST (9.0%)", "₹${String.format("%,.2f", halfGst)}")
                        InvoiceCalculationRow("State SGST (9.0%)", "₹${String.format("%,.2f", halfGst)}")
                        InvoiceCalculationRow("Service Surcharges", "₹0.00")
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Invoice Amount", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.Black)
                            Text("₹${String.format("%,.2f", txn.amount)}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF00C853))
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Note: This is a computer-generated tax invoice verified under Surya risk protocols. No physical signature is required.",
                            fontSize = 8.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Print Trigger action
                Button(
                    onClick = { viewModel.showNotification("Invoice dispatched to printer node successfully.") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Print, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PRINT DIGITAL INVOICE", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InvoiceCalculationRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun ReportsAndCommissionsScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var rechargeSlab by remember { mutableStateOf(3.2f) } // Default 3.2% commission
    var aepsWithdrawSlab by remember { mutableStateOf(12.0f) } // Flat ₹12 commission

    Column(modifier = Modifier.fillMaxSize()) {
        MoreHeader(title = "Reports & Commissions Slabs", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Revenue Progress charts
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "My Business Revenue Volumes (T+30 Days)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated Visual Progress Graph
                    ReportBarMetric("Domestic Money Transfer (DMT)", 0.85f, "₹18,50,000", Color(0xFF1E88E5))
                    ReportBarMetric("Aadhaar Cash Withdraw (AEPS)", 0.65f, "₹12,10,000", Color(0xFF8E24AA))
                    ReportBarMetric("BBPS Utility Bill Payments", 0.40f, "₹4,20,000", Color(0xFF00C853))
                    ReportBarMetric("B2B Hardware Procurement", 0.25f, "₹2,50,000", Color(0xFFFF8C00))
                }
            }

            // Interactive Commission Matrix Calculators
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive Commission Slabs Calculator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Simulate and visualize your commission margin yields based on trade volume routing.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Slab 1 recharge
                    Text(
                        text = "Recharge Agent Slab: ${String.format("%.1f", rechargeSlab)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = rechargeSlab,
                        onValueChange = { rechargeSlab = it },
                        valueRange = 1.0f..6.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF8C00),
                            activeTrackColor = Color(0xFFFF8C00)
                        )
                    )
                    Text(
                        text = "Expected daily earnings on ₹50K volume: ₹${String.format("%,.0f", 50000 * (rechargeSlab / 100))}",
                        fontSize = 11.sp,
                        color = Color(0xFF00C853),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Slab 2 AEPS
                    Text(
                        text = "AEPS Cash Withdrawal Flat: ₹${String.format("%.0f", aepsWithdrawSlab)} per txn",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = aepsWithdrawSlab,
                        onValueChange = { aepsWithdrawSlab = it },
                        valueRange = 5.0f..25.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF8E24AA),
                            activeTrackColor = Color(0xFF8E24AA)
                        )
                    )
                    Text(
                        text = "Expected yield on 150 daily cashouts: ₹${String.format("%,.0f", 150 * aepsWithdrawSlab)}",
                        fontSize = 11.sp,
                        color = Color(0xFF00C853),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ReportBarMetric(title: String, fraction: Float, valueStr: String, barColor: Color) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = valueStr, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = barColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = fraction,
            color = barColor,
            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun FullLedgerScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, CREDIT, DEBIT, COMMISSION
    val transactions by viewModel.transactions.collectAsState()

    val filteredList = transactions.filter { txn ->
        val matchesQuery = txn.description.contains(searchQuery, ignoreCase = true) || txn.referenceId.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilter == "ALL" || txn.type == selectedFilter
        matchesQuery && matchesFilter
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MoreHeader(title = "Full Ledger Audit Logs", onBack = onBack)

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search transactions / reference IDs") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "DEBIT", "CREDIT", "COMMISSION").forEach { filter ->
                    val isSel = selectedFilter == filter
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ledger dynamic logs
            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No transactions match the selected filters.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList) { txn ->
                        LedgerItemRow(txn)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsHubScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val notifications by viewModel.notifications.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        MoreHeader(title = "Notifications Alerts", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { item ->
                val cardBorder = when (item.type) {
                    "SUCCESS" -> BorderStroke(1.dp, Color(0xFF00C853).copy(alpha = 0.5f))
                    "WARNING" -> BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.5f))
                    "ALERT" -> BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f))
                    else -> BorderStroke(1.dp, Color.LightGray)
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = cardBorder
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = when (item.type) {
                                "SUCCESS" -> Icons.Default.CheckCircle
                                "WARNING" -> Icons.Default.Warning
                                "ALERT" -> Icons.Default.Error
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = when (item.type) {
                                "SUCCESS" -> Color(0xFF00C853)
                                "WARNING" -> Color(0xFFFFB300)
                                "ALERT" -> Color(0xFFE53935)
                                else -> Color(0xFF1E88E5)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = item.timestamp, fontSize = 10.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.message, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppSettingsScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val isDarkModePref by viewModel.isDarkMode.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isOfflineSyncing by viewModel.isOfflineSyncing.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        MoreHeader(title = localized("app_settings", viewModel), onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme selector card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = localized("visual_theme", viewModel), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionButton(
                            label = localized("system", viewModel),
                            selected = isDarkModePref == null,
                            onClick = { viewModel.toggleDarkMode(null) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionButton(
                            label = localized("light", viewModel),
                            selected = isDarkModePref == false,
                            onClick = { viewModel.toggleDarkMode(false) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionButton(
                            label = localized("dark", viewModel),
                            selected = isDarkModePref == true,
                            onClick = { viewModel.toggleDarkMode(true) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Language selector card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = localized("language_selection", viewModel), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    val langOptions = listOf(
                        Triple("en", "English", "English"),
                        Triple("hi", "Hindi", "हिंदी"),
                        Triple("kn", "Kannada", "ಕನ್ನಡ"),
                        Triple("ta", "Tamil", "தமிழ்"),
                        Triple("mr", "Marathi", "मराठी"),
                        Triple("te", "Telugu", "తెలుగు")
                    )

                    // 2-column layout for language buttons
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in langOptions.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (i < langOptions.size) {
                                    val (code, _, label) = langOptions[i]
                                    ThemeOptionButton(
                                        label = label,
                                        selected = currentLang == code,
                                        onClick = { viewModel.setLanguage(code) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (i + 1 < langOptions.size) {
                                    val (code, _, label) = langOptions[i + 1]
                                    ThemeOptionButton(
                                        label = label,
                                        selected = currentLang == code,
                                        onClick = { viewModel.setLanguage(code) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Offline synchronization status card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = localized("offline_status", viewModel), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Icon(
                            imageVector = if (isOfflineSyncing) Icons.Default.Sync else Icons.Default.CloudQueue,
                            contentDescription = null,
                            tint = if (isOfflineSyncing) Color(0xFFFF8C00) else Color(0xFF00C853),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isOfflineSyncing) "Synchronizing local Room database states with the cloud ledger service..." else localized("all_synced", viewModel),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isOfflineSyncing) {
                        LinearProgressIndicator(
                            color = Color(0xFFFF8C00),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Button(
                            onClick = { viewModel.triggerOfflineSync() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(localized("sync_now", viewModel))
                        }
                    }
                }
            }

            // Reset/Clear application database
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = localized("security_admin", viewModel), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Reset all offline transactional database entries and re-initialize systems.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.loadWallet(100000.0)
                            viewModel.showNotification("Simulated Master Wallet Reload Completed!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(localized("reload_wallet", viewModel))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { viewModel.setAuthState(AuthState.LOGIN) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(localized("sign_out", viewModel))
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeOptionButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (selected) Color(0xFFFF8C00) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (selected) Color(0xFFFF8C00) else MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.height(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun HelpdeskSupportScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var supportSubject by remember { mutableStateOf("") }
    var prioritySelected by remember { mutableStateOf("MEDIUM") }
    var categorySelected by remember { mutableStateOf("AEPS Service") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val tickets by viewModel.supportTickets.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        MoreHeader(title = "Helpdesk Support Centre", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick direct calls
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { viewModel.showNotification("Dialing Surya Toll-free Support: 1800-SUR-CREDIT...") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Phone, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Toll Free Direct", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.showNotification("Launching secure WhatsApp Help Chat room...") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Chat, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp Live", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Raise a Ticket form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Raise a Technical Support Ticket", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    
                    OutlinedTextField(
                        value = supportSubject,
                        onValueChange = { supportSubject = it },
                        label = { Text("Describe Issue Subject") },
                        placeholder = { Text("e.g. Transaction Ref pending credit") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Ticket Category
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = categorySelected,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Service Category") },
                            trailingIcon = {
                                IconButton(onClick = { categoryExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            listOf("AEPS Service", "DMT Cash Transfer", "BBPS Utility Bill", "Micro ATM Devices", "General Onboarding").forEach { cat ->
                                DropdownMenuItem(text = { Text(cat) }, onClick = {
                                    categorySelected = cat
                                    categoryExpanded = false
                                })
                            }
                        }
                    }

                    // Priority Row
                    Column {
                        Text(text = "Issue Severity Priority", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                                val selected = prioritySelected == p
                                Surface(
                                    onClick = { prioritySelected = p },
                                    color = if (selected) Color(0xFFFF8C00) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (selected) Color(0xFFFF8C00) else MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Text(text = p, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }

                    // Submit ticket
                    Button(
                        onClick = {
                            if (supportSubject.isNotBlank()) {
                                viewModel.raiseSupportTicket(supportSubject, categorySelected, prioritySelected)
                                supportSubject = ""
                            } else {
                                viewModel.showNotification("Please describe the issue subject first.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SUBMIT COMPLAINT TICKET")
                    }
                }
            }

            // Active raised tickets logs
            Text(text = "My Active Support Tickets", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            tickets.forEach { ticket ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = ticket.id, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (ticket.priority == "HIGH") Color(0xFFFFEBEE) else Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = ticket.priority,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ticket.priority == "HIGH") Color.Red else Color(0xFFEF6C00),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = ticket.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "${ticket.category} • ${ticket.timestamp}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Surface(
                            color = if (ticket.status == "RESOLVED") Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = ticket.status,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (ticket.status == "RESOLVED") Color(0xFF2E7D32) else Color(0xFFEF6C00),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactKycScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        MoreHeader(title = "KYC Merchant Compliance", onBack = onBack)
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            KycScreen(viewModel)
        }
    }
}

@Composable
fun ArchitectureInsightsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        MoreHeader(title = "Database Schema & Architecture", onBack = onBack)
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            ArchitectureScreen()
        }
    }
}

// ==========================================
// 1. SUPER ADMIN DASHBOARD
// ==========================================
@Composable
fun SuperAdminDashboardScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var reconciliationActive by remember { mutableStateOf(false) }
    var showReportDownloadDialog by remember { mutableStateOf(false) }
    var selectedStateFilter by remember { mutableStateOf("ALL") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MoreHeader(title = "Super Admin Live Command Desk", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status Header Alert
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, "status", tint = Color(0xFF2E7D32), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("SYSTEM HEURISTICS: STABLE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1B5E20))
                        Text("All 8 regional core payment switches running at 100% uptime.", fontSize = 11.sp, color = Color(0xFF2E7D32))
                    }
                }
            }

            // Super Admin KPI Cards Grid
            Text("Ecosystem Live Financial KPI Desk", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SuperAdminKpiCard("TOTAL USERS", "1,45,210", "+14.2% MoM", Color(0xFF1565C0), modifier = Modifier.weight(1f))
                SuperAdminKpiCard("ACTIVE WALLETS", "98,420", "67.8% Daily Ratio", Color(0xFF00ACC1), modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SuperAdminKpiCard("OUTSTANDING CREDIT", "₹12.54 Cr", "B2B Limit Allocation", Color(0xFFC2185B), modifier = Modifier.weight(1f))
                SuperAdminKpiCard("TOTAL BALANCE", "₹28.45 Cr", "Nodal Accounts Sum", Color(0xFF2E7D32), modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SuperAdminKpiCard("TODAY REVENUE", "₹14,50,230", "Net margin 1.25%", Color(0xFFE65100), modifier = Modifier.weight(1f))
                SuperAdminKpiCard("MONTHLY MARGIN", "₹4.18 Cr", "Commission Slabs Net", Color(0xFF7B1FA2), modifier = Modifier.weight(1f))
            }

            // Transaction Type distribution
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dynamic Transaction Volumes Distribution", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    VolumeBarMetric("Aadhaar Pay (AEPS)", "₹8,45,210", 0.45f, Color(0xFF2E7D32))
                    VolumeBarMetric("Money Transfer (DMT)", "₹5,10,400", 0.32f, Color(0xFF1565C0))
                    VolumeBarMetric("Bharat Bill Pay (BBPS)", "₹2,40,290", 0.15f, Color(0xFF7B1FA2))
                    VolumeBarMetric("Mobile Recharges", "₹98,400", 0.08f, Color(0xFF00ACC1))
                }
            }

            // State wise Business Volume
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Regional State-wise Volumes", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row {
                            listOf("ALL", "SOUTH", "WEST").forEach { filter ->
                                val active = selectedStateFilter == filter
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (active) Color(0xFFFF8C00) else Color.Transparent)
                                        .clickable { selectedStateFilter = filter }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(filter, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (active) Color.White else Color.Gray)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (selectedStateFilter == "ALL" || selectedStateFilter == "SOUTH") {
                        VolumeBarMetric("Karnataka (South Hub)", "₹5.24 Cr Volume", 0.42f, Color(0xFFFF8C00))
                        VolumeBarMetric("Tamil Nadu (East Hub)", "₹2.20 Cr Volume", 0.18f, Color(0xFF2E7D32))
                        VolumeBarMetric("Andhra Pradesh", "₹1.50 Cr Volume", 0.12f, Color(0xFF7B1FA2))
                    }
                    if (selectedStateFilter == "ALL" || selectedStateFilter == "WEST") {
                        VolumeBarMetric("Maharashtra (West Hub)", "₹3.50 Cr Volume", 0.28f, Color(0xFF1565C0))
                    }
                }
            }

            // Top performing distributors list
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Top Network Distributors Portfolio", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    DistributorPerformRow("1", "Ramesh Pai Fin-Agency", "Tumkur Dist", "₹85.4 Lakhs", "98.2% Active")
                    DistributorPerformRow("2", "Suryatech Digital Point", "Hubli Rural", "₹62.1 Lakhs", "97.4% Active")
                    DistributorPerformRow("3", "Balaji Telecom Switch", "Bangalore North", "₹54.9 Lakhs", "95.1% Active")
                }
            }

            // Super Admin Quick System Actions Desk
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Super-User Automated Cron Controls", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Execute critical automated settlement pipelines and gateway health audits instantly.", fontSize = 11.sp, color = Color.Gray)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                reconciliationActive = true
                                viewModel.showNotification("Auto-reconciling all 12 gateways... Completed.")
                                reconciliationActive = false
                            },
                            enabled = !reconciliationActive,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("RUN SWEEP RECON", fontSize = 11.sp)
                        }
                        
                        Button(
                            onClick = {
                                showReportDownloadDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("EXTRACT MASTER LEDGER", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }

    if (showReportDownloadDialog) {
        AlertDialog(
            onDismissRequest = { showReportDownloadDialog = false },
            title = { Text("Compile & Download Master Audit Ledger") },
            text = { Text("Generate full ledger containing all merchant wallets balance, cash-outs, micro-ATM volumes, and credit limits. Format: Excel Binary Sheet (.xlsx)") },
            confirmButton = {
                TextButton(onClick = {
                    showReportDownloadDialog = false
                    viewModel.showNotification("Master Ledger download initiated. Check notification bar.")
                }) {
                    Text("DOWNLOAD")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDownloadDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun SuperAdminKpiCard(title: String, valStr: String, trend: String, accentColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(valStr, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(trend, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
        }
    }
}

@Composable
fun VolumeBarMetric(label: String, volume: String, pct: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
            Text(volume, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.1f))) {
            Box(modifier = Modifier.fillMaxWidth(pct).fillMaxHeight().clip(CircleShape).background(color))
        }
    }
}

@Composable
fun DistributorPerformRow(rank: String, name: String, location: String, volume: String, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF8C00).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(rank, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8C00))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("$location • $status", fontSize = 10.sp, color = Color.Gray)
            }
        }
        Text(volume, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
    }
}

// ==========================================
// 2. CRM MODULE SCREEN
// ==========================================
@Composable
fun CrmHubScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var crmTabSelected by remember { mutableStateOf("RETAILERS") } // RETAILERS, LEADS, TICKETS
    var searchCrmText by remember { mutableStateOf("") }
    
    // Customer Notes Persistence Simulation
    var noteInputText by remember { mutableStateOf("") }
    val merchantNotes = remember {
        mutableStateListOf(
            "Note on Ramesh Pai (Tumkur): Requested localized commission slab hike on AEPS from 0.4% to 0.5% due to high volume kiosk load.",
            "Note on Suryatech (Hubli): Needs Micro ATM replacement unit shipped by Friday."
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MoreHeader(title = "Surya Enterprise CRM Hub", onBack = onBack)

        // Custom CRM Tabs Segment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("RETAILERS", "LEADS", "TICKETS").forEach { tab ->
                val active = crmTabSelected == tab
                Button(
                    onClick = { crmTabSelected = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (active) Color(0xFFFF8C00) else Color.Transparent
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        tab, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 11.sp, 
                        color = if (active) Color.White else Color(0xFFFF8C00)
                    )
                }
            }
        }

        // Search bar
        OutlinedTextField(
            value = searchCrmText,
            onValueChange = { searchCrmText = it },
            placeholder = { Text("Search CRM files, leads & active tickets...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (crmTabSelected == "RETAILERS") {
                item {
                    Text("High Volume Retail Merchants Directory", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                
                item {
                    CrmMerchantCard(
                        "Ramesh Pai Proprietorship",
                        "Kiosk #839, Tumkur Bus Terminal, KA",
                        "RETAILER",
                        "Last txn: 2 mins ago • Wallet: ₹42,100",
                        "High Volume"
                    )
                }
                item {
                    CrmMerchantCard(
                        "Suryatech Digital Point",
                        "Subhash Marg, Hubli Rural, KA",
                        "MASTER DISTRIBUTOR",
                        "Last txn: 12 mins ago • Wallet: ₹3,14,500",
                        "Strategic Network"
                    )
                }
                item {
                    CrmMerchantCard(
                        "Anusha Telecom Center",
                        "Main Road Market, Nelamangala, KA",
                        "RETAILER",
                        "Last txn: 1 hr ago • Wallet: ₹8,400",
                        "Regular Shop"
                    )
                }

                // Add follow up notes section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Corporate Merchant Notes Log", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            merchantNotes.forEach { note ->
                                Text("• $note", fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = noteInputText,
                                onValueChange = { noteInputText = it },
                                label = { Text("Append custom merchant follow-up note") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    if (noteInputText.isNotBlank()) {
                                        merchantNotes.add(noteInputText)
                                        noteInputText = ""
                                        viewModel.showNotification("Follow-up note appended successfully.")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("APPEND NOTE", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            if (crmTabSelected == "LEADS") {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Sales pipeline (KYC Leads)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Surface(color = Color(0xFFFFECE0), shape = RoundedCornerShape(4.dp)) {
                            Text("Auto-routed Leads", color = Color(0xFFFF8C00), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }
                }

                item {
                    CrmLeadCard("Basavaraj FinSolutions", "Lead Source: Organic Web", "QUALIFIED", "Requested ₹15 Lakhs credit threshold. Documents verified.")
                }
                item {
                    CrmLeadCard("Karthik Kiosk Shop", "Lead Source: Field Agent", "CONTACTED", "Scheduled for biometric scanner device shipment follow-up on Friday.")
                }
                item {
                    CrmLeadCard("Malnad Distributing Ltd", "Lead Source: Referral Desk", "NEW LEAD", "Requires bulk onboarding API suite for 42 regional sub-retailers.")
                }
            }

            if (crmTabSelected == "TICKETS") {
                item {
                    Text("System-wide Complaints Tracker", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                item {
                    CrmTicketCard("TCK-9281", "AEPS Cash Withdrawal Settle Dispute", "IN_PROGRESS", "HIGH", "Ramesh Pai (Tumkur)")
                }
                item {
                    CrmTicketCard("TCK-8722", "Micro ATM Lease Shipping Transit", "RESOLVED", "MEDIUM", "Suryatech (Hubli)")
                }
                item {
                    CrmTicketCard("TCK-7219", "PAN Card Application UTIDelay", "RESOLVED", "LOW", "Anusha Telecom")
                }
            }
        }
    }
}

@Composable
fun CrmMerchantCard(name: String, addr: String, role: String, meta: String, segment: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(4.dp)) {
                    Text(segment, color = Color(0xFF0D47A1), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Text(addr, fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Divider()
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Role: $role", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8C00))
                Text(meta, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun CrmLeadCard(title: String, source: String, stage: String, desc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Surface(
                    color = when (stage) {
                        "QUALIFIED" -> Color(0xFFE8F5E9)
                        "CONTACTED" -> Color(0xFFFFF3E0)
                        else -> Color(0xFFECEFF1)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        stage, 
                        color = when (stage) {
                            "QUALIFIED" -> Color(0xFF2E7D32)
                            "CONTACTED" -> Color(0xFFEF6C00)
                            else -> Color(0xFF37474F)
                        }, 
                        fontSize = 8.sp, 
                        fontWeight = FontWeight.Bold, 
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(source, fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, fontSize = 11.sp, color = Color.Black)
        }
    }
}

@Composable
fun CrmTicketCard(id: String, subject: String, status: String, priority: String, merchant: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(id, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(color = if (priority == "HIGH") Color(0xFFFFEBEE) else Color(0xFFFFF3E0), shape = RoundedCornerShape(4.dp)) {
                        Text(priority, color = if (priority == "HIGH") Color.Red else Color(0xFFEF6C00), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Merchant: $merchant", fontSize = 11.sp, color = Color.Gray)
            }
            Surface(
                color = if (status == "RESOLVED") Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    status, 
                    color = if (status == "RESOLVED") Color(0xFF2E7D32) else Color(0xFFEF6C00), 
                    fontSize = 8.sp, 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}

// ==========================================
// 3. AI ANALYTICS & PREDICTIONS
// ==========================================
data class RuleToggleItem(
    val title: String,
    val subtitle: String,
    val state: Boolean,
    val onToggle: (Boolean) -> Unit
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AiAnalyticsScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "AI Assistant",
        "BI Analytics",
        "Fraud Alerts",
        "Credit Risk",
        "Automation",
        "Secure Vault",
        "Customization"
    )

    // Global Notification Helper
    val snackbarHostState = remember { SnackbarHostState() }

    // ----------------- STATE 1: BUSINESS INTELLIGENCE -----------------
    var selectedState by remember { mutableStateOf("Karnataka") }
    val stateDistricts = remember(selectedState) {
        when (selectedState) {
            "Karnataka" -> listOf(
                Pair("Bangalore Urban", 4850000.0),
                Pair("Mysore Node", 1250000.0),
                Pair("Tumkur Kiosk", 850000.0),
                Pair("Hubli Center", 950000.0)
            )
            "Maharashtra" -> listOf(
                Pair("Mumbai City", 8950000.0),
                Pair("Pune Hub", 3420000.0),
                Pair("Nagpur Node", 1120000.0),
                Pair("Nashik Kiosk", 780000.0)
            )
            "Tamil Nadu" -> listOf(
                Pair("Chennai Urban", 5620000.0),
                Pair("Coimbatore Node", 2410000.0),
                Pair("Madurai Hub", 1340000.0),
                Pair("Trichy Kiosk", 680000.0)
            )
            else -> listOf(
                Pair("Central Delhi", 6200000.0),
                Pair("South Delhi Node", 4120000.0),
                Pair("Noida Sector 62", 1850000.0)
            )
        }
    }

    var biExporting by remember { mutableStateOf(false) }
    var biExportProgress by remember { mutableStateOf(0f) }
    var exportFormat by remember { mutableStateOf("PDF") }

    // ----------------- STATE 2: FRAUD DETECTION -----------------
    var fraudAlertsList by remember {
        mutableStateOf(
            listOf(
                mapOf("id" to "FA-912", "type" to "Suspicious Login", "detail" to "IP 192.168.12.98 from unknown terminal in Noida", "severity" to "HIGH", "status" to "PENDING"),
                mapOf("id" to "FA-804", "type" to "Abnormal Wallet Sweep", "detail" to "₹4.5L debited within 12 seconds via dynamic UPI node", "severity" to "HIGH", "status" to "PENDING"),
                mapOf("id" to "FA-501", "type" to "Device Fingerprint Conflict", "detail" to "Retailer BALAJI logged into 4 devices concurrently", "severity" to "MEDIUM", "status" to "PENDING"),
                mapOf("id" to "FA-220", "type" to "Geo-Location Mismatch", "detail" to "DMT transfer trigger in Tumkur while device GPS shows Bangalore", "severity" to "LOW", "status" to "RESOLVED")
            )
        )
    }

    var ipBlacklist by remember { mutableStateOf(listOf("185.220.101.4", "192.168.88.200", "43.212.12.5")) }
    var ipWhitelist by remember { mutableStateOf(listOf("203.111.90.1", "103.45.201.12", "122.180.14.92")) }
    var showAddIpDialog by remember { mutableStateOf(false) }
    var newIpValue by remember { mutableStateOf("") }
    var targetListIsWhitelist by remember { mutableStateOf(true) }

    // ----------------- STATE 3: CREDIT RISK -----------------
    var dynamicCreditScore by remember { mutableStateOf(785f) } // Credit Score out of 900
    val riskRatingClass = remember(dynamicCreditScore) {
        when {
            dynamicCreditScore >= 800 -> "Class A (Elite Low Risk)"
            dynamicCreditScore >= 720 -> "Class B (Standard Low Risk)"
            dynamicCreditScore >= 640 -> "Class C (Moderate Boundary Risk)"
            else -> "Class D (High Default Risk)"
        }
    }
    val defaultPredictionPercent = remember(dynamicCreditScore) {
        String.format("%.2f", (900f - dynamicCreditScore) / 10f * 0.15f)
    }

    // ----------------- STATE 4: WORKFLOWS & AUTOMATION -----------------
    var kycAutoApprove by remember { mutableStateOf(true) }
    var creditAutoIncrease by remember { mutableStateOf(false) }
    var reportsAutoGenerate by remember { mutableStateOf(true) }
    var commissionAutoSettle by remember { mutableStateOf(true) }
    var lowCreditSmsAlert by remember { mutableStateOf(false) }
    var invoiceAutoMail by remember { mutableStateOf(true) }

    var automationLogs by remember {
        mutableStateOf(
            listOf(
                "12:05:01 PM - [KYC Engine] Kiosk BALAJI doc verification passed. Account status updated to APPROVED.",
                "11:32:15 AM - [Invoice Builder] Corporate Tax invoice PDF successfully generated for Maharashtra Apex.",
                "11:00:00 AM - [Nodal Sweep] Sweep operation processed. ₹12,45,000.00 routed via ICICI bank node.",
                "09:15:33 AM - [Reminder Dispatch] WhatsApp due alerts sent to 14 retailers. Estimated due total ₹3.8L."
            )
        )
    }

    // ----------------- STATE 5: SECURE DOCUMENT VAULT -----------------
    var vaultDocuments by remember {
        mutableStateOf(
            listOf(
                mapOf("name" to "Aadhaar Card Copy", "type" to "Identity Proof", "status" to "VERIFIED", "date" to "July 01, 2026"),
                mapOf("name" to "PAN Card Ledger", "type" to "Tax Identification", "status" to "VERIFIED", "date" to "July 01, 2026"),
                mapOf("name" to "GST Registration Certificate", "type" to "Business Proof", "status" to "VERIFIED", "date" to "July 02, 2026"),
                mapOf("name" to "Bank Nodal Passbook Copy", "type" to "Financial Proof", "status" to "PENDING", "date" to "July 03, 2026")
            )
        )
    }

    var uploadFileType by remember { mutableStateOf("GST Certificate") }
    var docUploading by remember { mutableStateOf(false) }
    var docUploadProgress by remember { mutableStateOf(0f) }

    // ----------------- STATE 6: DASHBOARD CUSTOMIZATION -----------------
    var widgetBiKpisEnabled by remember { mutableStateOf(true) }
    var widgetFraudMeterEnabled by remember { mutableStateOf(true) }
    var widgetAiChatWidgetEnabled by remember { mutableStateOf(true) }
    var widgetCreditScoringEnabled by remember { mutableStateOf(true) }
    var widgetRecentLogsEnabled by remember { mutableStateOf(true) }

    var selectedShorcuts by remember { mutableStateOf(setOf("Fast Payout", "GST Audit", "Risk Profiler")) }
    var pushChannelEnabled by remember { mutableStateOf(true) }
    var whatsappChannelEnabled by remember { mutableStateOf(true) }
    var smsChannelEnabled by remember { mutableStateOf(false) }
    var emailChannelEnabled by remember { mutableStateOf(true) }

    // ----------------- STATE 0: AI CO-PILOT -----------------
    val chatHistory by viewModel.chatHistory.collectAsState(initial = emptyList())
    val aiState by viewModel.aiState.collectAsState(initial = AiState.Idle)
    var currentAiPrompt by remember { mutableStateOf("") }
    var mockVoiceInputActive by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                MoreHeader(title = "Enterprise Control & Intelligence Desk", onBack = onBack)
                ScrollableTabRow(
                    selectedTabIndex = activeTab,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = activeTab == index,
                            onClick = { activeTab = index },
                            text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("tab_button_$index")
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (activeTab) {
                0 -> {
                    // =========================================================================
                    // TAB 0: AI CO-PILOT (AI ASSISTANT)
                    // =========================================================================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "AI Intel",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Surya Cognitive Engine v3.5 (LIVE)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Text("Superpowered with deep reasoning. Formulate complex queries directly.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Chat History Panel
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            if (chatHistory.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubbleOutline,
                                        contentDescription = "no conversation",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No Conversation Logs", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Ask business performance, credit guidelines, or wallet audits below.", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(chatHistory) { msg: com.example.data.ChatMessage ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp)
                                        ) {
                                            val isUser = msg.sender == "USER"
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(0.85f)
                                                        .clip(
                                                            RoundedCornerShape(
                                                                topStart = 12.dp,
                                                                topEnd = 12.dp,
                                                                bottomStart = if (isUser) 12.dp else 0.dp,
                                                                bottomEnd = if (isUser) 0.dp else 12.dp
                                                            )
                                                        )
                                                        .background(
                                                            if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                                        )
                                                        .padding(12.dp)
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = if (isUser) "You" else "Surya Enterprise AI",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = msg.content,
                                                            fontSize = 12.sp,
                                                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }

                                            // Display Thinking processes if stored
                                            if (msg.thinkingProcess != null) {
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth(0.85f)
                                                        .padding(top = 4.dp),
                                                    shape = RoundedCornerShape(6.dp),
                                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                ) {
                                                    Column(modifier = Modifier.padding(8.dp)) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.HourglassEmpty, "thinking", tint = Color.Gray, modifier = Modifier.size(12.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("Reasoning Process:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                                        }
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(msg.thinkingProcess, fontSize = 9.sp, color = Color.Gray, lineHeight = 12.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Thinking Status Indicator
                        when (val state = aiState) {
                            is com.example.ui.AiState.Thinking -> {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(state.process, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                            is com.example.ui.AiState.Error -> {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Error, "error", tint = MaterialTheme.colorScheme.error)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(state.message, fontSize = 11.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                                    }
                                }
                            }
                            else -> {}
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Smart Query Shortcut Chips
                        Text("Suggested Queries:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val prompts = listOf(
                                "💳 Credit Slabs Check",
                                "📊 Daily Performance Audit",
                                "📦 B2B Logistics Track",
                                "🏦 Auto-GST Settlement Inquiry",
                                "⚠️ Risk Profile Report"
                            )
                            prompts.forEach { p ->
                                SuggestionChip(
                                    onClick = {
                                        viewModel.sendAiMessage(p.substring(2))
                                    },
                                    label = { Text(p, fontSize = 10.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Live Speech Sound Wave Simulation Popup
                        if (mockVoiceInputActive) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Mic, "recording", tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Listening... (Audio Soundwaves active)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        // Simple simulated visual sound wave bars
                                        repeat(6) { i ->
                                            Box(
                                                modifier = Modifier
                                                    .width(3.dp)
                                                    .height((12 + (i % 3) * 8).dp)
                                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                            )
                                        }
                                    }
                                    TextButton(onClick = {
                                        mockVoiceInputActive = false
                                        viewModel.sendAiMessage("Identify suspicious wallet withdrawals in Karnataka area and suggest counter-risk score calibrations.")
                                    }) {
                                        Text("Send Voice", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        // Prompt Inputs Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { mockVoiceInputActive = !mockVoiceInputActive },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(
                                    imageVector = if (mockVoiceInputActive) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Voice search"
                                )
                            }

                            OutlinedTextField(
                                value = currentAiPrompt,
                                onValueChange = { currentAiPrompt = it },
                                placeholder = { Text("Ask anything to Surya Enterprise Co-Pilot...", fontSize = 12.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ai_assistant_input"),
                                shape = RoundedCornerShape(24.dp),
                                maxLines = 2,
                                trailingIcon = {
                                    if (currentAiPrompt.isNotEmpty()) {
                                        IconButton(onClick = { currentAiPrompt = "" }) {
                                            Icon(Icons.Default.Clear, "clear text")
                                        }
                                    }
                                }
                            )

                            Button(
                                onClick = {
                                    if (currentAiPrompt.isNotBlank()) {
                                        viewModel.sendAiMessage(currentAiPrompt)
                                        currentAiPrompt = ""
                                    }
                                },
                                shape = RoundedCornerShape(24.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("ai_assistant_send_button")
                            ) {
                                Icon(Icons.Default.Send, "send", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                1 -> {
                    // =========================================================================
                    // TAB 1: BUSINESS INTELLIGENCE (BI ANALYTICS)
                    // =========================================================================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Regional State Selector row
                        Text("Interactive State Selector (Heat Map Drilldown):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Karnataka", "Maharashtra", "Tamil Nadu", "Delhi").forEach { sName ->
                                FilterChip(
                                    selected = selectedState == sName,
                                    onClick = { selectedState = sName },
                                    label = { Text(sName, fontSize = 11.sp) }
                                )
                            }
                        }

                        // BI KPIs Grid
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrendingUp, "sales", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("REVENUE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    }
                                    Text("₹24.85 Cr", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                    Text("+14.2% YoY growth", fontSize = 9.sp, color = Color(0xFF2E7D32))
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccountBalanceWallet, "profit", tint = Color(0xFF1565C0), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("NET MARGINS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    }
                                    Text("₹3.42 Cr", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                    Text("8.9% overall margin", fontSize = 9.sp, color = Color(0xFF1565C0))
                                }
                            }
                        }

                        // Interactive Charting Canvas
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Quarterly Regional Sales Flow (₹ In Millions)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(14.dp))

                                // Draw high-fidelity Canvas Bar Chart
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                ) {
                                    val width = size.width
                                    val height = size.height
                                    val gridCount = 4
                                    val barWidth = 45f
                                    val spacing = (width - (barWidth * 4)) / 5f

                                    // Draw grid lines
                                    for (i in 0..gridCount) {
                                        val y = (height / gridCount) * i
                                        drawLine(
                                            color = Color.LightGray.copy(alpha = 0.3f),
                                            start = androidx.compose.ui.geometry.Offset(0f, y),
                                            end = androidx.compose.ui.geometry.Offset(width, y),
                                            strokeWidth = 2f
                                        )
                                    }

                                    // Render 4 bars (Q1, Q2, Q3, Q4)
                                    val dataPoints = listOf(40f, 65f, 50f, 95f)
                                    val colors = listOf(Color(0xFF3F51B5), Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFFE91E63))

                                    dataPoints.forEachIndexed { idx, value ->
                                        val barHeight = (value / 100f) * height
                                        val x = spacing + idx * (barWidth + spacing)
                                        val y = height - barHeight

                                        drawRect(
                                            color = colors[idx],
                                            topLeft = androidx.compose.ui.geometry.Offset(x, y),
                                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    listOf("Q1 Apex", "Q2 Peak", "Q3 Slump", "Q4 Monsoon").forEachIndexed { idx, label ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(
                                                        when (idx) {
                                                            0 -> Color(0xFF3F51B5)
                                                            1 -> Color(0xFF4CAF50)
                                                            2 -> Color(0xFFFF9800)
                                                            else -> Color(0xFFE91E63)
                                                        },
                                                        CircleShape
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(label, fontSize = 9.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }

                        // State & District-wise Drill-down Report
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("$selectedState Local District Trade Ledger", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(10.dp))

                                stateDistricts.forEach { (dist, amount) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 5.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(dist, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        Text("₹${String.format("%,.2f", amount)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                }
                            }
                        }

                        // Export Center Section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("BI Export & Dispatch Hub", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Format:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    listOf("PDF", "EXCEL", "CSV").forEach { format ->
                                        ElevatedFilterChip(
                                            selected = exportFormat == format,
                                            onClick = { exportFormat = format },
                                            label = { Text(format, fontSize = 10.sp) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (biExporting) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Generating reports...", fontSize = 10.sp)
                                            Text("${(biExportProgress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        LinearProgressIndicator(
                                            progress = { biExportProgress },
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            biExporting = true
                                            biExportProgress = 0f
                                            coroutineScope.launch {
                                                while (biExportProgress < 1f) {
                                                    kotlinx.coroutines.delay(150)
                                                    biExportProgress += 0.1f
                                                }
                                                biExporting = false
                                                snackbarHostState.showSnackbar("Surya_BI_${selectedState}_Trade_Report.$exportFormat successfully exported.")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Download, "download", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Export $selectedState BI data to $exportFormat", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // =========================================================================
                    // TAB 2: FRAUD ALERTS & INVESTIGATION DESK
                    // =========================================================================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Global Risk gauge
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFC62828)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("8%", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text("Platform Security Risk index", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFC62828))
                                    Text("Anomaly rate is within optimal boundaries. Velocity thresholds operating normally.", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }

                        // Investigation queue
                        Text("Active Investigation Queue:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        fraudAlertsList.filter { it["status"] == "PENDING" }.forEach { alert ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(alert["type"] ?: "", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFC62828))
                                        Surface(
                                            color = Color(0xFFFFEBEE),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(alert["severity"] ?: "", color = Color(0xFFC62828), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(alert["detail"] ?: "", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = {
                                                fraudAlertsList = fraudAlertsList.map {
                                                    if (it["id"] == alert["id"]) it + ("status" to "RESOLVED") else it
                                                }
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Alert ${alert["id"]} Approved & Settled safely.")
                                                }
                                            }
                                        ) {
                                            Text("Approve & Settle", fontSize = 11.sp, color = Color(0xFF2E7D32))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = {
                                                fraudAlertsList = fraudAlertsList.map {
                                                    if (it["id"] == alert["id"]) it + ("status" to "RESOLVED") else it
                                                }
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Suspicious terminal blocked and flagged.")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                            shape = RoundedCornerShape(4.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Flag & Block", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }

                        // Whitelist & Blacklist Dual management
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Blacklist IP Manager
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Blacklist IPs", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFC62828))
                                        IconButton(onClick = {
                                            targetListIsWhitelist = false
                                            showAddIpDialog = true
                                        }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Add, "add black IP", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ipBlacklist.forEach { ip ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(ip, fontSize = 10.sp)
                                            IconButton(onClick = {
                                                ipBlacklist = ipBlacklist - ip
                                            }, modifier = Modifier.size(18.dp)) {
                                                Icon(Icons.Default.Delete, "remove", tint = Color.Gray, modifier = Modifier.size(12.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            // Whitelist IP Manager
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Whitelist IPs", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF2E7D32))
                                        IconButton(onClick = {
                                            targetListIsWhitelist = true
                                            showAddIpDialog = true
                                        }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Add, "add white IP", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ipWhitelist.forEach { ip ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(ip, fontSize = 10.sp)
                                            IconButton(onClick = {
                                                ipWhitelist = ipWhitelist - ip
                                            }, modifier = Modifier.size(18.dp)) {
                                                Icon(Icons.Default.Delete, "remove", tint = Color.Gray, modifier = Modifier.size(12.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // =========================================================================
                    // TAB 3: CREDIT RISK SCORING ENGINE
                    // =========================================================================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Interactive Risk Profiler Score", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier.size(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        progress = { dynamicCreditScore / 900f },
                                        modifier = Modifier.fillMaxSize(),
                                        strokeWidth = 8.dp,
                                        color = if (dynamicCreditScore >= 720) Color(0xFF2E7D32) else Color(0xFFFFB300)
                                    )
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${dynamicCreditScore.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                        Text("/ 900 max", fontSize = 9.sp, color = Color.Gray)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(riskRatingClass, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = if (dynamicCreditScore >= 720) Color(0xFF2E7D32) else Color(0xFFFFB300))

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Adjust Score to Calibrate Risk Policy:", fontSize = 11.sp, color = Color.Gray)
                                Slider(
                                    value = dynamicCreditScore,
                                    onValueChange = { dynamicCreditScore = it },
                                    valueRange = 450f..900f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }

                        // Detailed Risk Analytics Indicators
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Automated Risk Metrics Forecast", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Probability of Default (PD)", fontSize = 11.sp)
                                    Text("$defaultPredictionPercent%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                }
                                LinearProgressIndicator(
                                    progress = { defaultPredictionPercent.toFloatOrNull()?.div(10f) ?: 0.05f },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFFC62828)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Active Credit Utilization Rate", fontSize = 11.sp)
                                    Text("44.8% (Optimal)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                                LinearProgressIndicator(
                                    progress = { 0.448f },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFF2E7D32)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Repayment Behavior Analytics", fontSize = 11.sp)
                                    Text("Excellent (0 Delayed Cycles)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                            }
                        }

                        // Credit Slabs Recommendation Action Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Suggested Credit Policy Adjustment", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                val recommendLimit = remember(dynamicCreditScore) {
                                    when {
                                        dynamicCreditScore >= 800 -> "₹5.0 Lakhs (Unconditional)"
                                        dynamicCreditScore >= 720 -> "₹3.5 Lakhs (Standard)"
                                        dynamicCreditScore >= 640 -> "₹1.5 Lakhs (Restricted)"
                                        else -> "₹0.0 (Zero Slab Hold)"
                                    }
                                }
                                Text(
                                    text = "Based on $riskRatingClass rating, the automated system recommends a pre-approved B2B credit slab of $recommendLimit.",
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        viewModel.addNotification(
                                            "Credit Policy Applied",
                                            "Dynamic credit scoring engine calibrated pre-approved slab to $recommendLimit based on score ${dynamicCreditScore.toInt()}",
                                            "SUCCESS"
                                        )
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Dynamic Credit Slab updated to $recommendLimit.")
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Apply Recommended Slabs", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // =========================================================================
                    // TAB 4: AUTOMATION & WORKFLOW ENGINE
                    // =========================================================================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Active Workflow Rules Toggles:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)

                        // Rule Toggles List
                        listOf(
                            RuleToggleItem("Instant KYC Auto-Verification", "Aadhaar & PAN are instantly verified via national databases.", kycAutoApprove) { kycAutoApprove = it },
                            RuleToggleItem("Auto-Increase Pre-approved Credit", "Automatically increase limit by 20% on timely Repayment.", creditAutoIncrease) { creditAutoIncrease = it },
                            RuleToggleItem("Daily BI Report Generation", "Automatically calculate daily sales flows and send to State Heads.", reportsAutoGenerate) { reportsAutoGenerate = it },
                            RuleToggleItem("Instant Commission Nodal Sweeps", "Transfer retail transaction payouts within 24hr block windows.", commissionAutoSettle) { commissionAutoSettle = it },
                            RuleToggleItem("Low-Credit Balance SMS Alerts", "Notify retailers as dynamic credit limit approaches 90% utilization.", lowCreditSmsAlert) { lowCreditSmsAlert = it },
                            RuleToggleItem("Automated Business Tax Invoicing", "Generate monthly business GST invoices and dispatch via email.", invoiceAutoMail) { invoiceAutoMail = it }
                        ).forEach { rule ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(rule.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(rule.subtitle, fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Switch(
                                        checked = rule.state,
                                        onCheckedChange = {
                                            rule.onToggle(it)
                                            val status = if (it) "ENABLED" else "DISABLED"
                                            automationLogs = listOf("Just now - [Workflow Config] ${rule.title} rule set to $status") + automationLogs
                                        }
                                    )
                                }
                            }
                        }

                        // Live Automation logs
                        Text("Live Automation Engine Log Trail:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.Black)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                automationLogs.forEach { log ->
                                    Text(
                                        text = log,
                                        color = Color(0xFF4CAF50),
                                        fontSize = 9.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                5 -> {
                    // =========================================================================
                    // TAB 5: SECURE DOCUMENT VAULT
                    // =========================================================================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Secure Document Upload Terminal", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Doc Type:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    listOf("GST License", "PAN Card", "Cancel Cheque").forEach { type ->
                                        ElevatedFilterChip(
                                            selected = uploadFileType == type,
                                            onClick = { uploadFileType = type },
                                            label = { Text(type, fontSize = 10.sp) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (docUploading) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Uploading secure payload...", fontSize = 10.sp)
                                            Text("${(docUploadProgress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        LinearProgressIndicator(
                                            progress = { docUploadProgress },
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            docUploading = true
                                            docUploadProgress = 0f
                                            coroutineScope.launch {
                                                while (docUploadProgress < 1f) {
                                                    kotlinx.coroutines.delay(100)
                                                    docUploadProgress += 0.1f
                                                }
                                                docUploading = false
                                                vaultDocuments = vaultDocuments + mapOf(
                                                    "name" to "$uploadFileType Secure Doc",
                                                    "type" to "Merchant Upload",
                                                    "status" to "PENDING",
                                                    "date" to "Just now"
                                                )
                                                automationLogs = listOf("Just now - [Secure Vault] $uploadFileType uploaded and encrypted with SHA-256.") + automationLogs
                                                snackbarHostState.showSnackbar("Secure $uploadFileType uploaded successfully.")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.CloudUpload, "upload", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Choose File & Upload Securely", fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        // Encrypted Document Storage List
                        Text("Encrypted Document Records:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        vaultDocuments.forEach { doc ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "encrypted",
                                            tint = if (doc["status"] == "VERIFIED") Color(0xFF2E7D32) else Color(0xFFFFB300),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(doc["name"] ?: "", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("${doc["type"]} • ${doc["date"]}", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }

                                    Surface(
                                        color = if (doc["status"] == "VERIFIED") Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = doc["status"] ?: "",
                                            color = if (doc["status"] == "VERIFIED") Color(0xFF2E7D32) else Color(0xFFFFB300),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                6 -> {
                    // =========================================================================
                    // TAB 6: CUSTOMIZATION & DASHBOARD CONFIG
                    // =========================================================================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Rearrange Dashboard Layout Widgets:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)

                        listOf(
                            RuleToggleItem("Business Intelligence Quick KPIs", "Displays quick revenue and profit metrics at home screen.", widgetBiKpisEnabled) { widgetBiKpisEnabled = it },
                            RuleToggleItem("Security Risk Fraud Meter", "Displays live platform alert status gauge.", widgetFraudMeterEnabled) { widgetFraudMeterEnabled = it },
                            RuleToggleItem("AI Live Chat Co-Pilot Desk", "Displays quick-access natural language assistant widget.", widgetAiChatWidgetEnabled) { widgetAiChatWidgetEnabled = it },
                            RuleToggleItem("Dynamic Credit Score Tracker", "Displays real-time credit score and repayment behaviors.", widgetCreditScoringEnabled) { widgetCreditScoringEnabled = it },
                            RuleToggleItem("Live Automation log tail logs", "Displays active background events on home monitor.", widgetRecentLogsEnabled) { widgetRecentLogsEnabled = it }
                        ).forEach { rule ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(rule.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(rule.subtitle, fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Switch(checked = rule.state, onCheckedChange = rule.onToggle)
                                }
                            }
                        }

                        // Favorite Shortcut buttons
                        Text("Configure Platform Shortcut Nodes:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Fast Payout", "GST Audit", "Risk Profiler", "KYC Check", "Nodal Transfer", "API Setup").forEach { shortcut ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedShorcuts = if (selectedShorcuts.contains(shortcut)) selectedShorcuts - shortcut else selectedShorcuts + shortcut
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = selectedShorcuts.contains(shortcut),
                                            onCheckedChange = { checked ->
                                                selectedShorcuts = if (checked == true) selectedShorcuts + shortcut else selectedShorcuts - shortcut
                                            }
                                        )
                                        Text(shortcut, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        // Notification channels config
                        Text("Enable Notification Alert Channels:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(
                                    RuleToggleItem("Mobile Push Notification", "Instant transaction sweep confirmation.", pushChannelEnabled) { pushChannelEnabled = it },
                                    RuleToggleItem("WhatsApp Bot Alerts", "Verification status, credit limit updates.", whatsappChannelEnabled) { whatsappChannelEnabled = it },
                                    RuleToggleItem("SMS Transaction Texts", "Emergency terminal anomaly warnings.", smsChannelEnabled) { smsChannelEnabled = it },
                                    RuleToggleItem("Direct Email PDF Invoices", "Monthly GST and transaction logs.", emailChannelEnabled) { emailChannelEnabled = it }
                                ).forEach { rule ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(rule.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(rule.subtitle, fontSize = 10.sp, color = Color.Gray)
                                        }
                                        Switch(checked = rule.state, onCheckedChange = rule.onToggle)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Dashboard Preferences Saved successfully!")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Save Custom Preferences", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Modal dialogs
    if (showAddIpDialog) {
        AlertDialog(
            onDismissRequest = { showAddIpDialog = false },
            title = { Text(if (targetListIsWhitelist) "Add Whitelist IP" else "Add Blacklist IP", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter dynamic terminal IPv4 address:", fontSize = 11.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = newIpValue,
                        onValueChange = { newIpValue = it },
                        placeholder = { Text("e.g. 192.168.1.1", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("add_ip_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newIpValue.isNotBlank()) {
                            if (targetListIsWhitelist) {
                                ipWhitelist = ipWhitelist + newIpValue
                            } else {
                                ipBlacklist = ipBlacklist + newIpValue
                            }
                        }
                        showAddIpDialog = false
                        newIpValue = ""
                    }
                ) {
                    Text("Add IP")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddIpDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ==========================================
// 4. NOTIFICATION SYSTEM & CAMPAIGNS
// ==========================================
@Composable
fun CampaignManagerScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var promoTitle by remember { mutableStateOf("") }
    var promoBody by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf("WHATSAPP") } // WHATSAPP, SMS, PUSH, EMAIL
    var scheduledTime by remember { mutableStateOf("Instant Broadcast") }

    val activeCampaigns = remember {
        mutableStateListOf(
            CampaignLog("C-893", "Pre-approved Loan alert", "WHATSAPP", "Scheduled", "98.2% Delivered"),
            CampaignLog("C-204", "Monsoon BBPS cashback offer", "PUSH", "Sent", "94.5% Open rate")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MoreHeader(title = "Promotional Campaigns Desk", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Compose Campaign Widget
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Compose Broadcast Campaign", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    
                    // Channel picker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("WHATSAPP", "SMS", "PUSH", "EMAIL").forEach { channel ->
                            val active = selectedChannel == channel
                            Button(
                                onClick = { selectedChannel = channel },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (active) Color(0xFFFF8C00) else Color(0xFFFF8C00).copy(alpha = 0.15f)
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(channel, color = if (active) Color.White else Color(0xFFFF8C00), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = promoTitle,
                        onValueChange = { promoTitle = it },
                        label = { Text("Campaign Subject / Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = promoBody,
                        onValueChange = { promoBody = it },
                        label = { Text("Message Body Text (WhatsApp Template)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Schedule Picker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Instant Broadcast", "Schedule for Friday").forEach { sched ->
                            val active = scheduledTime == sched
                            Button(
                                onClick = { scheduledTime = sched },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (active) Color(0xFF37474F) else Color.Transparent
                                ),
                                border = BorderStroke(1.dp, Color(0xFF37474F))
                            ) {
                                Text(sched, color = if (active) Color.White else Color(0xFF37474F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (promoTitle.isNotBlank() && promoBody.isNotBlank()) {
                                activeCampaigns.add(
                                    CampaignLog(
                                        id = "C-${Random.nextInt(100, 999)}",
                                        title = promoTitle,
                                        channel = selectedChannel,
                                        status = if (scheduledTime == "Instant Broadcast") "Sent" else "Scheduled",
                                        delivery = "Pending dispatch"
                                    )
                                )
                                promoTitle = ""
                                promoBody = ""
                                viewModel.showNotification("Campaign broadcast successfully queued.")
                            } else {
                                viewModel.showNotification("Please specify both Title & Body.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("LAUNCH CAMPAIGN BROADCAST")
                    }
                }
            }

            // Historical Campaigns list
            Text("Active & Dispatched Campaigns", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            activeCampaigns.forEach { camp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(camp.id, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(color = Color(0xFFE0F7FA), shape = RoundedCornerShape(4.dp)) {
                                    Text(camp.channel, color = Color(0xFF006064), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(camp.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Delivery Ratio: ${camp.delivery}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Surface(
                            color = if (camp.status == "Sent") Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                camp.status, 
                                color = if (camp.status == "Sent") Color(0xFF2E7D32) else Color(0xFFEF6C00), 
                                fontSize = 8.sp, 
                                fontWeight = FontWeight.Bold, 
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class CampaignLog(
    val id: String,
    val title: String,
    val channel: String,
    val status: String,
    val delivery: String
)

// ==========================================
// 5. FINTECH REPORTS DESK SCREEN
// ==========================================
@Composable
fun FintechReportsDeskScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var activeReportType by remember { mutableStateOf("GST_TDS_LEDGER") } // GST_TDS_LEDGER, COMM_LEDGER, AEPS_LEDGER
    var showExportProgress by remember { mutableStateOf(false) }
    var exportProgressValue by remember { mutableStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MoreHeader(title = "Fintech Ledger Reports Center", onBack = onBack)

        // Segmented selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("GST & TDS", "Slabs & Comm", "AEPS Txns").forEach { label ->
                val type = when (label) {
                    "GST & TDS" -> "GST_TDS_LEDGER"
                    "Slabs & Comm" -> "COMM_LEDGER"
                    else -> "AEPS_LEDGER"
                }
                val active = activeReportType == type
                Button(
                    onClick = { activeReportType = type },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (active) Color(0xFFFF8C00) else Color.Transparent
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (active) Color.White else Color(0xFFFF8C00))
                }
            }
        }

        // Export Controls
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Compile & Export Options", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Download compliance ledger in multiple secure formats.", fontSize = 11.sp, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                showExportProgress = true
                                exportProgressValue = 0f
                                while (exportProgressValue < 1f) {
                                    kotlinx.coroutines.delay(200)
                                    exportProgressValue += 0.2f
                                }
                                showExportProgress = false
                                viewModel.showNotification("Excel ledger exported successfully. File saved to /downloads")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Text("Export Excel", fontSize = 10.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.showNotification("PDF invoice ledger compilation triggered.")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2185B)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("PDF Report", fontSize = 10.sp)
                    }
                }
            }
        }

        if (showExportProgress) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.15f))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Compiling massive database rows... ${String.format("%.0f", exportProgressValue * 100)}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(progress = exportProgressValue, color = Color(0xFFFF8C00), modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Ledger Record logs
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (activeReportType == "GST_TDS_LEDGER") {
                item { Text("GST and TDS Deductions (Current Month)", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                item { LedgerReportRow("GST-8902", "Tax for ATM Kiosk purchase", "₹4,230.00 GST (18%)", "TDS ₹423.00 (1%)", "Approved") }
                item { LedgerReportRow("GST-7621", "CGST / SGST fee AEPS service", "₹850.00 (9%+9%)", "TDS ₹85.00 (1%)", "Approved") }
                item { LedgerReportRow("GST-1249", "Remittance IMPS Switch GST", "₹120.00 Flat SGST", "TDS ₹12.00 (1%)", "Approved") }
            }
            if (activeReportType == "COMM_LEDGER") {
                item { Text("Commission Slab Disbursements", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                item { LedgerReportRow("COM-102", "DMT commission distribution", "Earned: ₹3,450.00", "State slab: 0.12%", "Paid") }
                item { LedgerReportRow("COM-103", "AEPS Flat commission release", "Earned: ₹12.00 Flat", "Retailer slab: ₹12.0", "Paid") }
            }
            if (activeReportType == "AEPS_LEDGER") {
                item { Text("AEPS National Switch Transaction Log", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                item { LedgerReportRow("TXN-3901", "AEPS Cash Withdrawal HDFC Bank", "₹10,000.00", "Ref: AP8391023", "SUCCESS") }
                item { LedgerReportRow("TXN-1290", "AEPS Balance Inquiry SBI Node", "₹0.00", "Ref: AP1203984", "SUCCESS") }
                item { LedgerReportRow("TXN-7128", "AEPS Cash Withdrawal ICICI", "₹2,500.00", "Ref: AP1283921", "FAILED") }
            }
        }
    }
}

@Composable
fun LedgerReportRow(id: String, desc: String, val1: String, val2: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(id, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                Surface(
                    color = if (status == "Approved" || status == "Paid" || status == "SUCCESS") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        status, 
                        color = if (status == "Approved" || status == "Paid" || status == "SUCCESS") Color(0xFF2E7D32) else Color.Red, 
                        fontSize = 8.sp, 
                        fontWeight = FontWeight.Bold, 
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(val1, fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                Text(val2, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

// ==========================================
// 6. AUDIT LOGS & COMPLIANCE
// ==========================================
@Composable
fun AuditLogsComplianceScreen(onBack: () -> Unit) {
    var selectedAuditTab by remember { mutableStateOf("USER_ACTIVITY") } // USER_ACTIVITY, API_LOGS, SYSTEM_HEALTH

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MoreHeader(title = "Audit & Compliance Command", onBack = onBack)

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("User Activity", "API Logs", "System Health").forEach { label ->
                val tab = when (label) {
                    "User Activity" -> "USER_ACTIVITY"
                    "API Logs" -> "API_LOGS"
                    else -> "SYSTEM_HEALTH"
                }
                val active = selectedAuditTab == tab
                Button(
                    onClick = { selectedAuditTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (active) Color(0xFFFF8C00) else Color.Transparent
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (active) Color.White else Color(0xFFFF8C00))
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedAuditTab == "USER_ACTIVITY") {
                item { Text("Security Compliance - User Activity Logs", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                item { AuditActivityCard("02 Jul 2026 13:05", "Ramesh Pai (SUPER_ADMIN)", "Set AEPS service gateway route to Yes Bank engine v2.") }
                item { AuditActivityCard("02 Jul 2026 12:44", "Suryatech (RETAILER)", "Applied for pre-approved credit threshold extension to ₹1,50,000.") }
                item { AuditActivityCard("02 Jul 2026 11:20", "Admin Desk (SYSTEM)", "Auto-released nodal settlement pipeline of ₹24.5 Lakhs.") }
            }
            if (selectedAuditTab == "API_LOGS") {
                item { Text("Immutable API Microservice Routing Headers", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                item { AuditApiCard("POST", "/api/v1/wallet/usr-ret-03/transaction", "200 SUCCESS", "124ms", "Mantra biometric OTG device validation wrapper") }
                item { AuditApiCard("GET", "/api/v1/wallet/usr-ret-03/balance", "200 SUCCESS", "42ms", "Dynamic balance lookup & multi-wallet caches") }
                item { AuditApiCard("POST", "/api/v1/compliance/kyc/upload", "400 BAD_REQUEST", "210ms", "Missing photo identifier file on Aadhaar check") }
            }
            if (selectedAuditTab == "SYSTEM_HEALTH") {
                item { Text("Active regional payment switches status", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                item { AuditHealthRow("Yes Bank AEPS core node", "ONLINE", "99.98% SLA", "12ms Latency") }
                item { AuditHealthRow("Mantra RD biometric validation switch", "ONLINE", "100.0% SLA", "88ms Latency") }
                item { AuditHealthRow("Surya IMPS Remittance core channel", "ONLINE", "99.92% SLA", "142ms Latency") }
                item { AuditHealthRow("NSDL PAN verification gateway", "ONLINE", "99.4% SLA", "602ms Latency") }
            }
        }
    }
}

@Composable
fun AuditActivityCard(time: String, user: String, action: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(user, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFFF8C00))
                Text(time, fontSize = 10.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(action, fontSize = 12.sp, color = Color.Black)
        }
    }
}

@Composable
fun AuditApiCard(method: String, endpoint: String, code: String, latency: String, details: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (method == "POST") Color(0xFFE3F2FD) else Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(method, color = if (method == "POST") Color(0xFF0D47A1) else Color(0xFF2E7D32), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(endpoint, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Text(latency, fontSize = 10.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(details, fontSize = 11.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(code, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (code.contains("SUCCESS")) Color(0xFF2E7D32) else Color.Red)
        }
    }
}

@Composable
fun AuditHealthRow(title: String, status: String, sla: String, latency: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("SLA Index: $sla • Latency: $latency", fontSize = 10.sp, color = Color.Gray)
            }
            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                Text(status, color = Color(0xFF2E7D32), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }
    }
}

// =========================================================================================
// PARTNER API GATEWAY HUB: DYNAMIC CLIENT MANAGEMENT LAYER
// =========================================================================================

data class ApiHubProvider(
    val id: String,
    val name: String,
    val category: String,
    val isEnabled: Boolean = true,
    val isProduction: Boolean = false,
    val priority: Int,
    val successRate: Double,
    val latencyMs: Int,
    val apiEndpoint: String,
    val apiKey: String,
    val apiSecret: String,
    val ipWhitelist: String = "10.120.2.14, 10.120.2.15",
    val lastHealthStatus: String = "HEALTHY", // HEALTHY, DEGRADED, OFFLINE
    val circuitBreaker: String = "CLOSED" // CLOSED, OPEN, HALF_OPEN
)

data class ApiConsoleLog(
    val timestamp: String,
    val provider: String,
    val action: String,
    val type: String, // "REQUEST", "RESPONSE", "FAILOVER", "WEBHOOK"
    val status: String // "SUCCESS", "FAILED", "WARNING"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerApiHubScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    // 1. Core Provider State
    var providers by remember {
        mutableStateOf(
            listOf(
                // Payment Gateways
                ApiHubProvider("razorpay", "Razorpay Payment Gateway", "PAYMENTS", true, true, 1, 99.8, 112, "https://api.razorpay.com/v1", "rzp_live_8912hjas", "sec_live_9201asj"),
                ApiHubProvider("cashfree", "Cashfree Core Checkout", "PAYMENTS", true, true, 2, 99.5, 128, "https://api.cashfree.com/v2", "cf_live_3901jasd", "cf_sec_0912hjv"),
                ApiHubProvider("pinelabs", "Pine Labs Plural Gateway", "PAYMENTS", true, false, 3, 98.9, 145, "https://api.pinelabs.com/plural", "pine_sb_23091hja", "pine_sec_901a"),
                ApiHubProvider("ccavenue", "CCAvenue AvenuePay", "PAYMENTS", false, true, 4, 97.4, 185, "https://api.ccavenue.com/txn", "cca_live_7821gsa", "cca_sec_1092a"),
                ApiHubProvider("paytm", "Paytm Merchant Business", "PAYMENTS", true, true, 5, 99.2, 119, "https://securegw.paytm.in/v3", "paytm_live_0912g", "paytm_sec_891b"),
                ApiHubProvider("zaakpay", "Zaakpay Mobikwik Gateway", "PAYMENTS", false, false, 6, 96.1, 198, "https://api.zaakpay.com/charge", "zaak_sb_901hba", "zaak_sec_334"),
                
                // BBPS Billers
                ApiHubProvider("npci_bbps", "Bharat BillPay (NPCI Core)", "BBPS", true, true, 1, 99.9, 82, "https://api.npci.org.in/bbps/v2", "npci_live_89211", "npci_sec_78a"),
                ApiHubProvider("billavenue", "BillAvenue BBPS Bridge", "BBPS", true, true, 2, 98.8, 138, "https://api.billavenue.com/biller", "bill_live_901hj", "bill_sec_99a"),
                
                // AEPS Gateways
                ApiHubProvider("icici_aeps", "ICICI Bank AEPS Core", "AEPS", true, true, 1, 99.6, 120, "https://api.icicibank.com/aeps", "icici_live_390a", "icici_sec_991c"),
                ApiHubProvider("yes_aeps", "Yes Bank AEPS Network", "AEPS", true, true, 2, 99.1, 134, "https://api.yesbank.in/aeps/v1", "yes_live_901a", "yes_sec_882b"),
                
                // Domestic Money Transfer (DMT)
                ApiHubProvider("instantpay_dmt", "InstantPay Money Transfer", "DMT", true, true, 1, 99.7, 95, "https://api.instantpay.in/dmt", "inst_live_88a", "inst_sec_33c"),
                ApiHubProvider("eko_dmt", "Eko Money Transfer Engine", "DMT", true, true, 2, 98.9, 142, "https://api.eko.in/v2/dmt", "eko_live_9921b", "eko_sec_91"),

                // KYC & Verifications
                ApiHubProvider("protean_pan", "Protean PAN Verification API", "KYC", true, true, 1, 99.9, 78, "https://api.proteantech.in/pan", "prot_live_882a", "prot_sec_99a"),
                ApiHubProvider("uidai_aadhaar", "UIDAI Aadhaar OTP Gateway", "KYC", true, true, 2, 98.4, 280, "https://api.uidai.gov.in/otp", "uidai_live_8892h", "uidai_sec_881"),

                // Communication (Comms)
                ApiHubProvider("twilio_sms", "Twilio Global Comms", "COMMS", true, true, 1, 99.9, 90, "https://api.twilio.com/2010", "tw_live_3901a", "tw_sec_99b"),
                ApiHubProvider("sendgrid_mail", "SendGrid Transactional Mail", "COMMS", true, true, 2, 99.8, 85, "https://api.sendgrid.com/v3", "sg_live_7718h", "sg_sec_00a")
            )
        )
    }

    // 2. Real-time Logs Console State
    var consoleLogs by remember {
        mutableStateOf(
            listOf(
                ApiConsoleLog("13:20:15", "Razorpay", "Payment authorization payload parsed", "REQUEST", "SUCCESS"),
                ApiConsoleLog("13:20:16", "Razorpay", "Received webhook transaction_captured", "WEBHOOK", "SUCCESS"),
                ApiConsoleLog("13:21:05", "ICICI AEPS", "AEPS withdrawal query processed successfully", "REQUEST", "SUCCESS"),
                ApiConsoleLog("13:22:40", "UIDAI Aadhaar", "OTP transmission response timeout (280ms)", "REQUEST", "WARNING"),
                ApiConsoleLog("13:24:12", "Eko DMT", "Bulk payout queued inside Redis Worker", "REQUEST", "SUCCESS")
            )
        )
    }

    // 3. Navigation and Configuration states
    var activeCategoryFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    
    // Webhook validator tool states
    var testWebhookPayload by remember { mutableStateOf("{\"event\":\"payment.authorized\",\"amount\":50000}") }
    var testWebhookSignature by remember { mutableStateOf("sha256=a8f93cd8b01e38df20a1") }
    var webhookValidationResult by remember { mutableStateOf<String?>(null) }

    // Active diagnostic simulator dialog state
    var selectedDiagnosticProvider by remember { mutableStateOf<ApiHubProvider?>(null) }
    var diagnosticProgress by remember { mutableStateOf(0f) }
    var diagnosticConsoleOutput by remember { mutableStateOf(listOf<String>()) }
    var isDiagnosticRunning by remember { mutableStateOf(false) }

    // Edit Credential Modal
    var editingProviderCreds by remember { mutableStateOf<ApiHubProvider?>(null) }
    var tempEndpoint by remember { mutableStateOf("") }
    var tempApiKey by remember { mutableStateOf("") }
    var tempApiSecret by remember { mutableStateOf("") }
    var tempIpWhitelist by remember { mutableStateOf("") }

    // 4. Global SLA stats computed dynamically from provider states
    val activeCount = providers.count { it.isEnabled }
    val avgLatency = if (activeCount > 0) providers.filter { it.isEnabled }.map { it.latencyMs }.average().toInt() else 0
    val systemSla = if (activeCount > 0) providers.filter { it.isEnabled }.map { it.successRate }.average() else 100.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Partner API Gateway Hub", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Text("Multi-Provider Failover & Routing Engine", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00ACC1))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // MODULE 1: SERVICE OVERVIEW METRICS PANEL
            // ==========================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF37474F)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Real-Time Router Performance", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Text(
                                    text = "GATEWAY SECURE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Registered APIS", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                                Text("${providers.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column {
                                Text("Active Trunks", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                                Text("$activeCount Online", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4DD0E1))
                            }
                            Column {
                                Text("Router SLA Index", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                                Text(String.format("%.2f%%", systemSla), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                            }
                            Column {
                                Text("Avg Gateway Latency", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                                Text("${avgLatency}ms", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB74D))
                            }
                        }
                    }
                }
            }

            // ==========================================
            // MODULE 2: CATEGORY SELECTOR & SEARCH
            // ==========================================
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().testTag("api_hub_search_input"),
                        placeholder = { Text("Search provider name or endpoint...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    // Scrolling Category Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = listOf("ALL", "PAYMENTS", "BBPS", "AEPS", "DMT", "KYC", "COMMS")
                        categories.forEach { category ->
                            val isSelected = activeCategoryFilter == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { activeCategoryFilter = category },
                                label = { Text(category, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00ACC1),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // ==========================================
            // MODULE 3: ROUTING PROVIDERS LIST
            // ==========================================
            val filteredProviders = providers.filter {
                (activeCategoryFilter == "ALL" || it.category == activeCategoryFilter) &&
                        (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true) || it.apiEndpoint.contains(searchQuery, ignoreCase = true))
            }.sortedBy { it.priority }

            if (filteredProviders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No providers match your search query", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(filteredProviders) { provider ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header: Name & Priority badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(provider.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFE0F7FA),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Priority ${provider.priority}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00838F),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(provider.apiEndpoint, fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }

                                // Interactive State Toggles
                                Switch(
                                    checked = provider.isEnabled,
                                    onCheckedChange = { checked ->
                                        providers = providers.map {
                                            if (it.id == provider.id) it.copy(isEnabled = checked) else it
                                        }
                                        val statusText = if (checked) "enabled" else "disabled"
                                        consoleLogs = listOf(
                                            ApiConsoleLog(
                                                timestamp = "13:25:00",
                                                provider = provider.name,
                                                action = "Admin manually $statusText the trunk line.",
                                                type = "REQUEST",
                                                status = if (checked) "SUCCESS" else "WARNING"
                                            )
                                        ) + consoleLogs
                                    },
                                    modifier = Modifier.scale(0.8f).testTag("switch_${provider.id}")
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Stats section: Latency, Success, Environment
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(
                                                    color = when (provider.lastHealthStatus) {
                                                        "HEALTHY" -> Color(0xFF4CAF50)
                                                        "DEGRADED" -> Color(0xFFFF9800)
                                                        else -> Color(0xFFF44336)
                                                    },
                                                    shape = CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("SLA: ${provider.successRate}%", fontSize = 11.sp, color = Color.DarkGray)
                                    }
                                    Text("Latency: ${provider.latencyMs}ms", fontSize = 10.sp, color = Color.Gray)
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Breaker: ", fontSize = 10.sp, color = Color.Gray)
                                        Surface(
                                            color = when (provider.circuitBreaker) {
                                                "CLOSED" -> Color(0xFFE8F5E9)
                                                "OPEN" -> Color(0xFFFFEBEE)
                                                else -> Color(0xFFFFF3E0)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = provider.circuitBreaker,
                                                color = when (provider.circuitBreaker) {
                                                    "CLOSED" -> Color(0xFF2E7D32)
                                                    "OPEN" -> Color(0xFFC62828)
                                                    else -> Color(0xFFEF6C00)
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                // Production/Sandbox Switch
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (provider.isProduction) "PROD" else "SANDBOX",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (provider.isProduction) Color(0xFFE65100) else Color(0xFF3949AB)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = {
                                            providers = providers.map {
                                                if (it.id == provider.id) it.copy(isProduction = !it.isProduction) else it
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Toggle Env",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEEE))

                            // Actions buttons: Check Health, Credentials, Reorder Priority
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Health Diagnostics Button
                                    OutlinedButton(
                                        onClick = {
                                            selectedDiagnosticProvider = provider
                                            isDiagnosticRunning = true
                                            diagnosticProgress = 0f
                                            diagnosticConsoleOutput = listOf(
                                                "Initializing diagnostic suite to ${provider.name}...",
                                                "Opening WebSocket tunnel and validating SSL certificate structure...",
                                                "Enforcing Strict-Transport-Security verification..."
                                            )
                                            coroutineScope.launch {
                                                for (i in 1..4) {
                                                    kotlinx.coroutines.delay(400)
                                                    diagnosticProgress = i * 0.25f
                                                    diagnosticConsoleOutput = diagnosticConsoleOutput + when (i) {
                                                        1 -> listOf("SSL Handshake completed with TLSv1.3 cryptographic parameters.", "Local IP whitelist check: PASSED (${provider.ipWhitelist})")
                                                        2 -> listOf("Sending encrypted dummy load payload to gateway end...", "Awaiting authorization acknowledgement...")
                                                        3 -> listOf("Transaction simulation success. Status 200 OK received in ${provider.latencyMs + (Random.nextInt(10) - 5)}ms.")
                                                        else -> listOf("Circuit breaker telemetry confirmed: ACTIVE-STABLE.", "Provider health diagnostic fully: HEALTHY")
                                                    }
                                                }
                                                isDiagnosticRunning = false
                                                // Update health state in provider list
                                                providers = providers.map {
                                                    if (it.id == provider.id) it.copy(lastHealthStatus = "HEALTHY") else it
                                                }
                                                consoleLogs = listOf(
                                                    ApiConsoleLog(
                                                        timestamp = "13:26:01",
                                                        provider = provider.name,
                                                        action = "Manual gateway audit check returned status HEALTHY in ${provider.latencyMs}ms.",
                                                        type = "RESPONSE",
                                                        status = "SUCCESS"
                                                    )
                                                ) + consoleLogs
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Audit Ping", fontSize = 10.sp)
                                    }

                                    // Credential Edit Button
                                    OutlinedButton(
                                        onClick = {
                                            editingProviderCreds = provider
                                            tempEndpoint = provider.apiEndpoint
                                            tempApiKey = provider.apiKey
                                            tempApiSecret = provider.apiSecret
                                            tempIpWhitelist = provider.ipWhitelist
                                        },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit Credentials", fontSize = 10.sp)
                                    }
                                }

                                // Priority adjustments
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (provider.priority > 1) {
                                                val targetPriority = provider.priority - 1
                                                providers = providers.map {
                                                    when (it.id) {
                                                        provider.id -> it.copy(priority = targetPriority)
                                                        else -> if (it.category == provider.category && it.priority == targetPriority) it.copy(priority = provider.priority) else it
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(24.dp),
                                        enabled = provider.priority > 1
                                    ) {
                                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Move Up", tint = if (provider.priority > 1) Color.DarkGray else Color.LightGray)
                                    }

                                    Text("${provider.priority}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)

                                    IconButton(
                                        onClick = {
                                            val targetPriority = provider.priority + 1
                                            providers = providers.map {
                                                when (it.id) {
                                                    provider.id -> it.copy(priority = targetPriority)
                                                    else -> if (it.category == provider.category && it.priority == targetPriority) it.copy(priority = provider.priority) else it
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Move Down", tint = Color.DarkGray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // MODULE 4: AUTOMATED FAILOVER SIMULATION
            // ==========================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                    border = BorderStroke(1.dp, Color(0xFF4DB6AC))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFF00796B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Failover Router Diagnostics", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF004D40))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Test how the core gateway automatically routes traffic if a high-priority partner database drops below 95% SLA. Initiating will trip Razorpay's circuit breaker to OPEN, switching active trunk lines instantly.",
                            fontSize = 11.sp,
                            color = Color(0xFF004D40).copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                consoleLogs = listOf(
                                    ApiConsoleLog("13:28:10", "Razorpay", "Simulated degradation triggered. SLA dropped to 92.4%.", "FAILOVER", "WARNING"),
                                    ApiConsoleLog("13:28:11", "Razorpay", "Circuit Breaker TRIPPED -> State: OPEN.", "FAILOVER", "FAILED"),
                                    ApiConsoleLog("13:28:11", "API Router", "Rerouting traffic from Razorpay to Cashfree (Priority 2)", "FAILOVER", "SUCCESS"),
                                    ApiConsoleLog("13:28:12", "Cashfree", "SLA Verification Successful. 100% traffic routed seamlessly.", "REQUEST", "SUCCESS")
                                ) + consoleLogs
                                
                                providers = providers.map {
                                    when (it.id) {
                                        "razorpay" -> it.copy(circuitBreaker = "OPEN", lastHealthStatus = "DEGRADED", successRate = 92.1)
                                        "cashfree" -> it.copy(successRate = 99.8)
                                        else -> it
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                            modifier = Modifier.fillMaxWidth().testTag("btn_failover_test")
                        ) {
                            Text("Trigger Circuit-Breaker Failover", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            // ==========================================
            // MODULE 5: SECURE WEBHOOK SIGNATURE CHECKER
            // ==========================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("HMAC-SHA256 Webhook Verifier", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = "All callback events are cryptographically authenticated at the gateway entry layer.",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )

                        OutlinedTextField(
                            value = testWebhookPayload,
                            onValueChange = { testWebhookPayload = it },
                            label = { Text("Raw Webhook JSON Payload", fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                        )

                        OutlinedTextField(
                            value = testWebhookSignature,
                            onValueChange = { testWebhookSignature = it },
                            label = { Text("X-Surya-Signature Header", fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                        )

                        Button(
                            onClick = {
                                if (testWebhookSignature.contains("sha256")) {
                                    webhookValidationResult = "SIGNATURE VERIFIED SECURE • Payload integrity protected."
                                    consoleLogs = listOf(
                                        ApiConsoleLog("13:29:05", "Webhook Engine", "Incoming event signature validated successfully.", "WEBHOOK", "SUCCESS")
                                    ) + consoleLogs
                                } else {
                                    webhookValidationResult = "VALIDATION FAILED • Corrupt header format or signature mismatch."
                                    consoleLogs = listOf(
                                        ApiConsoleLog("13:29:05", "Webhook Engine", "Rejected signature check from unverified sender.", "WEBHOOK", "FAILED")
                                    ) + consoleLogs
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Verify Callback Signatures", fontSize = 11.sp, color = Color.White)
                        }

                        webhookValidationResult?.let { result ->
                            Surface(
                                color = if (result.contains("VERIFIED")) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = result,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (result.contains("VERIFIED")) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // MODULE 6: REAL-TIME CONSOLE AUDIT LOGS
            // ==========================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E272C))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF46D870), shape = CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Router Log Terminal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text("Live Feed", color = Color(0xFF46D870), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Limit logs to top 6 items
                        consoleLogs.take(6).forEach { log ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "[${log.timestamp}] ${log.provider}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB0BEC5)
                                    )
                                    Text(
                                        text = log.type,
                                        fontSize = 9.sp,
                                        color = when (log.type) {
                                            "REQUEST" -> Color(0xFF81D4FA)
                                            "RESPONSE" -> Color(0xFFA5D6A7)
                                            "WEBHOOK" -> Color(0xFFE1BEE7)
                                            else -> Color(0xFFFFCC80)
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = log.action,
                                    fontSize = 10.sp,
                                    color = when (log.status) {
                                        "SUCCESS" -> Color(0xFFC8E6C9)
                                        "WARNING" -> Color(0xFFFFE0B2)
                                        else -> Color(0xFFFFCDD2)
                                    }
                                )
                                Divider(modifier = Modifier.padding(top = 4.dp), color = Color.White.copy(alpha = 0.05f))
                            }
                        }
                    }
                }
            }
        }
    }

    // Diagnostic Check Popup
    selectedDiagnosticProvider?.let { provider ->
        AlertDialog(
            onDismissRequest = {
                if (!isDiagnosticRunning) selectedDiagnosticProvider = null
            },
            title = {
                Text(
                    text = if (isDiagnosticRunning) "Running Diagnostic Checks" else "Diagnostic Complete",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Target: ${provider.name} (${provider.category})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )

                    if (isDiagnosticRunning) {
                        LinearProgressIndicator(
                            progress = diagnosticProgress,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF00ACC1),
                            trackColor = Color(0xFFE0F7FA)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFF212121), RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(diagnosticConsoleOutput) { line ->
                                Text(
                                    text = "> $line",
                                    color = if (line.contains("PASSED") || line.contains("HEALTHY")) Color(0xFF81C784) else Color(0xFFB0BEC5),
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { selectedDiagnosticProvider = null },
                    enabled = !isDiagnosticRunning
                ) {
                    Text("Close Panel")
                }
            }
        )
    }

    // Credential Edit Modal
    editingProviderCreds?.let { provider ->
        AlertDialog(
            onDismissRequest = { editingProviderCreds = null },
            title = {
                Text("Edit Secure Access Credentials", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "You are updating credentials for ${provider.name}. Keys are AES-256 encrypted at rest before storing in Spanner schema.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = tempEndpoint,
                        onValueChange = { tempEndpoint = it },
                        label = { Text("Secure Endpoint API URL", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tempApiKey,
                        onValueChange = { tempApiKey = it },
                        label = { Text("Auth Key Identifier (Client ID)", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tempApiSecret,
                        onValueChange = { tempApiSecret = it },
                        label = { Text("Encrypted API Client Secret", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tempIpWhitelist,
                        onValueChange = { tempIpWhitelist = it },
                        label = { Text("IP Restriction Whitelist (Comma Separated)", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        providers = providers.map {
                            if (it.id == provider.id) {
                                it.copy(
                                    apiEndpoint = tempEndpoint,
                                    apiKey = tempApiKey,
                                    apiSecret = tempApiSecret,
                                    ipWhitelist = tempIpWhitelist
                                )
                            } else it
                        }
                        consoleLogs = listOf(
                            ApiConsoleLog(
                                timestamp = "13:30:12",
                                provider = provider.name,
                                action = "Admin updated security access keys and verified IP whitelists.",
                                type = "REQUEST",
                                status = "SUCCESS"
                            )
                        ) + consoleLogs
                        editingProviderCreds = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1))
                ) {
                    Text("Apply & Sync Gateway", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingProviderCreds = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// =========================================================================================
// BUSINESS NETWORK & HIERARCHY MANAGEMENT: ENTERPRISE NETWORK ECOSYSTEM
// =========================================================================================

data class FranchiseRecord(
    val id: String,
    val name: String,
    val territoryState: String,
    val territoryDistrict: String,
    val walletBalance: Double,
    val creditOutstanding: Double,
    val kycStatus: String, // PENDING, VERIFIED, REJECTED
    val lifeTimeCommission: Double,
    val rankingBadge: String, // DIAMOND, PLATINUM, GOLD
    val activeRetailersCount: Int
)

data class DistributorRecord(
    val id: String,
    val name: String,
    val masterDistributor: String,
    val assignedState: String,
    val walletBalance: Double,
    val creditLimit: Double,
    val kycStatus: String,
    val totalSales: Double,
    val isApproved: Boolean
)

data class RetailerRecord(
    val id: String,
    val name: String,
    val assignedDistributor: String,
    val walletBalance: Double,
    val creditWallet: Double,
    val kycStatus: String,
    val orderHistoryCount: Int,
    val commissionPaid: Double
)

data class EmployeeRecord(
    val id: String,
    val name: String,
    val department: String, // Operations, Sales, Risk & Compliance, Support
    val attendanceToday: String, // PRESENT, ABSENT, LEAVE
    val currentSalary: Double,
    val taskAssigned: String,
    val monthlyTarget: Double,
    val currentProgress: Double
)

data class CustomRole(
    val roleId: String,
    val roleName: String,
    val parentRole: String,
    val permissions: List<String>
)

data class TargetRecord(
    val name: String,
    val category: String, // FRANCHISE, DISTRIBUTOR, EMPLOYEE
    val targetAmount: Double,
    val currentProgress: Double,
    val deadline: String
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BusinessNetworkManagementScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var activeTabState by remember { mutableStateOf(0) }
    val tabs = listOf("Franchise", "Distributors", "Retailers", "Employees", "Role Config", "Commissions", "Targets")

    // State collections with pre-populated enterprise data
    var franchises by remember {
        mutableStateOf(
            listOf(
                FranchiseRecord("fran_01", "Karnataka Apex Fintech Ltd", "Karnataka", "Bangalore Urban", 450000.0, 150000.0, "VERIFIED", 78400.0, "DIAMOND", 84),
                FranchiseRecord("fran_02", "Maharashtra Elite Tradelinks", "Maharashtra", "Mumbai City", 620000.0, 300000.0, "VERIFIED", 112000.0, "DIAMOND", 125),
                FranchiseRecord("fran_03", "NCR Surya Enterprises", "Delhi", "Central Delhi", 120000.0, 0.0, "PENDING", 15400.0, "GOLD", 19),
                FranchiseRecord("fran_04", "Tamil Nadu Merchant Union", "Tamil Nadu", "Chennai", 380000.0, 50000.0, "VERIFIED", 42300.0, "PLATINUM", 56)
            )
        )
    }

    var distributors by remember {
        mutableStateOf(
            listOf(
                DistributorRecord("dist_01", "Sanjay Kumar Payouts", "Karnataka Apex", "Karnataka", 85000.0, 50000.0, "VERIFIED", 1450000.0, true),
                DistributorRecord("dist_02", "Manoj Patil Distributors", "Maharashtra Elite", "Maharashtra", 125000.0, 100000.0, "VERIFIED", 2980000.0, true),
                DistributorRecord("dist_03", "Vikas Dubey Sales Agency", "NCR Surya", "Delhi", 12000.0, 0.0, "PENDING", 210000.0, false),
                DistributorRecord("dist_04", "Arun Kumar Enterprises", "Tamil Nadu Merchant", "Tamil Nadu", 94000.0, 40000.0, "VERIFIED", 850000.0, true)
            )
        )
    }

    var retailers by remember {
        mutableStateOf(
            listOf(
                RetailerRecord("ret_01", "Krishna Kirana Store", "Sanjay Kumar", 18500.0, 5000.0, "VERIFIED", 342, 12500.0),
                RetailerRecord("ret_02", "Balaji Telecom Center", "Manoj Patil", 31200.0, 15000.0, "VERIFIED", 650, 24800.0),
                RetailerRecord("ret_03", "Shiva Mobile & Recharge", "Vikas Dubey", 1500.0, 0.0, "PENDING", 41, 950.0),
                RetailerRecord("ret_04", "Raja General Store", "Arun Kumar", 9800.0, 2000.0, "VERIFIED", 189, 6200.0)
            )
        )
    }

    var employees by remember {
        mutableStateOf(
            listOf(
                EmployeeRecord("emp_01", "Amrita Rao", "Risk & Compliance", "PRESENT", 65000.0, "Complete pending KYC reviews", 100.0, 85.0),
                EmployeeRecord("emp_02", "Rohan Sharma", "Sales & Business Development", "PRESENT", 48000.0, "Onboard new retail clusters in North Bangalore", 500000.0, 420000.0),
                EmployeeRecord("emp_03", "Sneha Patel", "Operations Support", "LEAVE", 35000.0, "Resolve pending high-priority gateway tickets", 50.0, 50.0),
                EmployeeRecord("emp_04", "Rahul Verma", "IT Infrastructure", "PRESENT", 85000.0, "Monitor Redis session and cluster load balancing", 100.0, 95.0)
            )
        )
    }

    var customRoles by remember {
        mutableStateOf(
            listOf(
                CustomRole("ROLE_CORP", "Corporate Admin", "Super Admin", listOf("VIEW_ALL_STATS", "APPROVE_FRANCHISE", "UPDATE_COMMISSION", "MANAGE_ROLES")),
                CustomRole("ROLE_STATE_HEAD", "State Head", "Corporate Admin", listOf("VIEW_STATE_STATS", "APPROVE_DISTRIBUTOR", "MANAGE_EMPLOYEES")),
                CustomRole("ROLE_DIST_MGR", "District Manager", "State Head", listOf("VIEW_DISTRICT_STATS", "KYC_VERIFICATION", "LEAD_ASSIGNMENT"))
            )
        )
    }

    // Commission Engine Multi-Level Rules Configuration
    var ruleCorpAdminPercent by remember { mutableStateOf("0.5") }
    var ruleStateHeadPercent by remember { mutableStateOf("1.0") }
    var ruleMasterDistPercent by remember { mutableStateOf("1.5") }
    var ruleDistributorPercent by remember { mutableStateOf("2.0") }
    var ruleRetailerPercent by remember { mutableStateOf("5.0") }

    // Multi-Level dry-run calculator states
    var calcTxnAmount by remember { mutableStateOf("10000") }
    var calcCorpReward by remember { mutableStateOf(50.0) }
    var calcStateReward by remember { mutableStateOf(100.0) }
    var calcMasterReward by remember { mutableStateOf(150.0) }
    var calcDistReward by remember { mutableStateOf(200.0) }
    var calcRetReward by remember { mutableStateOf(500.0) }

    // Interactive Dialog / Sheet States
    var showAddFranchiseDialog by remember { mutableStateOf(false) }
    var newFranName by remember { mutableStateOf("") }
    var newFranState by remember { mutableStateOf("") }
    var newFranDistrict by remember { mutableStateOf("") }

    var showAddDistributorDialog by remember { mutableStateOf(false) }
    var newDistName by remember { mutableStateOf("") }
    var newDistMaster by remember { mutableStateOf("") }
    var newDistState by remember { mutableStateOf("") }

    var showAddRoleDialog by remember { mutableStateOf(false) }
    var newRoleId by remember { mutableStateOf("") }
    var newRoleName by remember { mutableStateOf("") }
    var newRoleParent by remember { mutableStateOf("") }
    var selectedPerms by remember { mutableStateOf(setOf<String>()) }

    val allBasePermissions = listOf(
        "TRANS_APPROVE", "WALLET_CREDIT", "KYC_VERIFY", "COMMISSION_RULES", "REPORTS_EXPORT", "BROADCAST_ALERTS"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Business Network Console", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Text("Enterprise Franchise & Distributor Hierarchy", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6F9))
                .padding(padding)
        ) {
            // Scrollable TabRow to switch sections smoothly
            ScrollableTabRow(
                selectedTabIndex = activeTabState,
                containerColor = Color.White,
                contentColor = Color(0xFF3F51B5),
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = activeTabState == index,
                        onClick = { activeTabState = index },
                        text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Centralized Content Workspace
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (activeTabState) {
                    // ==========================================
                    // WORKSPACE 1: FRANCHISE MANAGEMENT HUB
                    // ==========================================
                    0 -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Registered Franchises", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
                                Button(
                                    onClick = { showAddFranchiseDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("btn_register_franchise")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Register Franchise", fontSize = 11.sp)
                                }
                            }
                        }

                        items(franchises) { franchise ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(franchise.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = when (franchise.rankingBadge) {
                                                        "DIAMOND" -> Color(0xFFE0F7FA)
                                                        "PLATINUM" -> Color(0xFFEDE7F6)
                                                        else -> Color(0xFFFFF3E0)
                                                    },
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = franchise.rankingBadge,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = when (franchise.rankingBadge) {
                                                            "DIAMOND" -> Color(0xFF006064)
                                                            "PLATINUM" -> Color(0xFF4A148C)
                                                            else -> Color(0xFFE65100)
                                                        },
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "Territory: State ${franchise.territoryState} • District ${franchise.territoryDistrict}",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        // KYC Verification Badge
                                        Surface(
                                            color = when (franchise.kycStatus) {
                                                "VERIFIED" -> Color(0xFFE8F5E9)
                                                "PENDING" -> Color(0xFFFFF3E0)
                                                else -> Color(0xFFFFEBEE)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "KYC ${franchise.kycStatus}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (franchise.kycStatus) {
                                                    "VERIFIED" -> Color(0xFF2E7D32)
                                                    "PENDING" -> Color(0xFFE65100)
                                                    else -> Color(0xFFC62828)
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Wallet Balance", fontSize = 10.sp, color = Color.Gray)
                                            Text("₹${franchise.walletBalance}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
                                        }
                                        Column {
                                            Text("Credit Outstanding", fontSize = 10.sp, color = Color.Gray)
                                            Text("₹${franchise.creditOutstanding}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFC62828))
                                        }
                                        Column {
                                            Text("Life Commission", fontSize = 10.sp, color = Color.Gray)
                                            Text("₹${franchise.lifeTimeCommission}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                                        }
                                        Column {
                                            Text("Retailers Linked", fontSize = 10.sp, color = Color.Gray)
                                            Text("${franchise.activeRetailersCount}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF3F51B5))
                                        }
                                    }

                                    // Action buttons for approvals & audits
                                    if (franchise.kycStatus == "PENDING") {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    franchises = franchises.map {
                                                        if (it.id == franchise.id) it.copy(kycStatus = "REJECTED") else it
                                                    }
                                                },
                                                border = BorderStroke(1.dp, Color(0xFFC62828)),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                                                modifier = Modifier.height(28.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                            ) {
                                                Text("Reject KYC", fontSize = 10.sp)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = {
                                                    franchises = franchises.map {
                                                        if (it.id == franchise.id) it.copy(kycStatus = "VERIFIED") else it
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                                modifier = Modifier.height(28.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                            ) {
                                                Text("Approve KYC & Open Vault", fontSize = 10.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // WORKSPACE 2: DISTRIBUTOR OUTLET REGISTRY
                    // ==========================================
                    1 -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Registered Distributors", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
                                Button(
                                    onClick = { showAddDistributorDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("btn_add_distributor")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Distributor", fontSize = 11.sp)
                                }
                            }
                        }

                        items(distributors) { distributor ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(distributor.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Channel Parent: ${distributor.masterDistributor} Master", fontSize = 11.sp, color = Color.Gray)
                                        }

                                        Surface(
                                            color = if (distributor.isApproved) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (distributor.isApproved) "ACTIVE" else "PENDING APPROVAL",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (distributor.isApproved) Color(0xFF2E7D32) else Color(0xFFC62828),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("State Allocated", fontSize = 9.sp, color = Color.Gray)
                                            Text(distributor.assignedState, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Column {
                                            Text("Wallet balance", fontSize = 9.sp, color = Color.Gray)
                                            Text("₹${distributor.walletBalance}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                                        }
                                        Column {
                                            Text("Secured Credit Limit", fontSize = 9.sp, color = Color.Gray)
                                            Text("₹${distributor.creditLimit}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF3F51B5))
                                        }
                                        Column {
                                            Text("Total Sales Volume", fontSize = 9.sp, color = Color.Gray)
                                            Text("₹${distributor.totalSales}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2E7D32))
                                        }
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEEE))

                                    // Fast Actions: Toggle credit allocation limit
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Credit Limit Toggle", fontSize = 10.sp, color = Color.Gray)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Switch(
                                                checked = distributor.creditLimit > 0,
                                                onCheckedChange = { status ->
                                                    distributors = distributors.map {
                                                        if (it.id == distributor.id) {
                                                            it.copy(creditLimit = if (status) 50000.0 else 0.0)
                                                        } else it
                                                    }
                                                },
                                                modifier = Modifier.scale(0.6f)
                                            )
                                        }

                                        if (!distributor.isApproved) {
                                            Button(
                                                onClick = {
                                                    distributors = distributors.map {
                                                        if (it.id == distributor.id) it.copy(isApproved = true, kycStatus = "VERIFIED") else it
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                                                modifier = Modifier.height(28.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                            ) {
                                                Text("Onboard & Verify", fontSize = 10.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // WORKSPACE 3: RETAILER NETWORK LEDGER
                    // ==========================================
                    2 -> {
                        item {
                            Text("Assigned Retail Outlet Grid", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
                        }

                        items(retailers) { retailer ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(retailer.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Assigned Distributor: ${retailer.assignedDistributor}", fontSize = 11.sp, color = Color.Gray)
                                        }

                                        Surface(
                                            color = when (retailer.kycStatus) {
                                                "VERIFIED" -> Color(0xFFE8F5E9)
                                                else -> Color(0xFFFFEBEE)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "KYC ${retailer.kycStatus}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (retailer.kycStatus) {
                                                    "VERIFIED" -> Color(0xFF2E7D32)
                                                    else -> Color(0xFFC62828)
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Wallet Balance", fontSize = 9.sp, color = Color.Gray)
                                            Text("₹${retailer.walletBalance}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                                        }
                                        Column {
                                            Text("Credit Wallet Limit", fontSize = 9.sp, color = Color.Gray)
                                            Text("₹${retailer.creditWallet}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF3F51B5))
                                        }
                                        Column {
                                            Text("Order Frequency", fontSize = 9.sp, color = Color.Gray)
                                            Text("${retailer.orderHistoryCount} Trades", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                                        }
                                        Column {
                                            Text("Generated Comm.", fontSize = 9.sp, color = Color.Gray)
                                            Text("₹${retailer.commissionPaid}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2E7D32))
                                        }
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEEE))

                                    // Dynamic KYC fast verification control
                                    if (retailer.kycStatus == "PENDING") {
                                        Button(
                                            onClick = {
                                                retailers = retailers.map {
                                                    if (it.id == retailer.id) it.copy(kycStatus = "VERIFIED") else it
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Verify KYC & Unlock Marketplace Access", fontSize = 11.sp, color = Color.White)
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = Color(0xFFE8F5E9),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text("SERVICES ACTIVE", fontSize = 9.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                            TextButton(
                                                onClick = {
                                                    retailers = retailers.map {
                                                        if (it.id == retailer.id) it.copy(walletBalance = it.walletBalance + 5000.0) else it
                                                    }
                                                }
                                            ) {
                                                Text("Inject ₹5,000 Wallet", fontSize = 10.sp, color = Color(0xFF3F51B5))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // WORKSPACE 4: EMPLOYEE ATTENDANCE & TASKS
                    // ==========================================
                    3 -> {
                        item {
                            Text("Fintech Operations Employees", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
                        }

                        items(employees) { employee ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(employee.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Dept: ${employee.department}", fontSize = 11.sp, color = Color.Gray)
                                        }

                                        // Attendance Tracker Toggle
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Attendance: ", fontSize = 10.sp, color = Color.Gray)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Surface(
                                                color = when (employee.attendanceToday) {
                                                    "PRESENT" -> Color(0xFFE8F5E9)
                                                    "ABSENT" -> Color(0xFFFFEBEE)
                                                    else -> Color(0xFFFFF3E0)
                                                },
                                                shape = RoundedCornerShape(4.dp),
                                                modifier = Modifier.clickable {
                                                    employees = employees.map {
                                                        if (it.id == employee.id) {
                                                            val nextStatus = when (employee.attendanceToday) {
                                                                "PRESENT" -> "ABSENT"
                                                                "ABSENT" -> "LEAVE"
                                                                else -> "PRESENT"
                                                            }
                                                            it.copy(attendanceToday = nextStatus)
                                                        } else it
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = employee.attendanceToday,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (employee.attendanceToday) {
                                                        "PRESENT" -> Color(0xFF2E7D32)
                                                        "ABSENT" -> Color(0xFFC62828)
                                                        else -> Color(0xFFE65100)
                                                    },
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Current Task: ${employee.taskAssigned}", fontSize = 11.sp, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Progress bar for employee sales / task target
                                    val progressFraction = if (employee.monthlyTarget > 0) (employee.currentProgress / employee.monthlyTarget).toFloat().coerceIn(0f, 1f) else 1f
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Target Progress: ${String.format("%.1f%%", progressFraction * 100)}", fontSize = 10.sp, color = Color.Gray)
                                        Text("₹${employee.currentProgress} / ₹${employee.monthlyTarget}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = progressFraction,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFF3F51B5),
                                        trackColor = Color(0xFFE0E0E0)
                                    )
                                }
                            }
                        }
                    }

                    // ==========================================
                    // WORKSPACE 5: ROLE CONFIGURATION & PERMS
                    // ==========================================
                    4 -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Dynamic Business Hierarchy", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
                                Button(
                                    onClick = { showAddRoleDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("btn_build_role")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Build Role", fontSize = 11.sp)
                                }
                            }
                        }

                        items(customRoles) { role ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(role.roleName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Inherits From: ${role.parentRole}", fontSize = 11.sp, color = Color.Gray)
                                        }
                                        Text(role.roleId, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Granted Access Permissions:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Flex list of tags
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        role.permissions.forEach { perm ->
                                            Surface(
                                                color = Color(0xFFF0F2F5),
                                                shape = RoundedCornerShape(4.dp),
                                                border = BorderStroke(1.dp, Color(0xFFCFD8DC))
                                            ) {
                                                Text(
                                                    text = perm,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF455A64),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // WORKSPACE 6: MULTI-LEVEL COMMISSION RULE
                    // ==========================================
                    5 -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Multi-Level Commission Splitter", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(
                                        text = "Define exactly what percentage of a commercial transaction gets distributed down to which role within the franchise hierarchy chain dynamically.",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = ruleCorpAdminPercent,
                                            onValueChange = { ruleCorpAdminPercent = it },
                                            label = { Text("Corp Admin (%)", fontSize = 10.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = ruleStateHeadPercent,
                                            onValueChange = { ruleStateHeadPercent = it },
                                            label = { Text("State Head (%)", fontSize = 10.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = ruleMasterDistPercent,
                                            onValueChange = { ruleMasterDistPercent = it },
                                            label = { Text("Master Dist (%)", fontSize = 10.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = ruleDistributorPercent,
                                            onValueChange = { ruleDistributorPercent = it },
                                            label = { Text("Distributor (%)", fontSize = 10.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    OutlinedTextField(
                                        value = ruleRetailerPercent,
                                        onValueChange = { ruleRetailerPercent = it },
                                        label = { Text("Retailer Base Commission (%)", fontSize = 10.sp) },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Divider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFEEEEEE))

                                    // Dry Run Simulator tool
                                    Text("Live Dry-Run Simulator", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    OutlinedTextField(
                                        value = calcTxnAmount,
                                        onValueChange = {
                                            calcTxnAmount = it
                                            val amt = it.toDoubleOrNull() ?: 0.0
                                            val c = ruleCorpAdminPercent.toDoubleOrNull() ?: 0.0
                                            val s = ruleStateHeadPercent.toDoubleOrNull() ?: 0.0
                                            val m = ruleMasterDistPercent.toDoubleOrNull() ?: 0.0
                                            val d = ruleDistributorPercent.toDoubleOrNull() ?: 0.0
                                            val r = ruleRetailerPercent.toDoubleOrNull() ?: 0.0

                                            calcCorpReward = amt * (c / 100.0)
                                            calcStateReward = amt * (s / 100.0)
                                            calcMasterReward = amt * (m / 100.0)
                                            calcDistReward = amt * (d / 100.0)
                                            calcRetReward = amt * (r / 100.0)
                                        },
                                        label = { Text("Simulate Transaction Amount (₹)", fontSize = 10.sp) },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFF5F7FA), RoundedCornerShape(8.dp))
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("Calculated Distributed Payout Ledger:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Retailer Yield:", fontSize = 11.sp)
                                            Text("₹$calcRetReward", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 12.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Distributor Yield:", fontSize = 11.sp)
                                            Text("₹$calcDistReward", fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5), fontSize = 12.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Master Distributor Yield:", fontSize = 11.sp)
                                            Text("₹$calcMasterReward", fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 12.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("State Head Yield:", fontSize = 11.sp)
                                            Text("₹$calcStateReward", fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 12.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Corporate Admin Yield:", fontSize = 11.sp)
                                            Text("₹$calcCorpReward", fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // WORKSPACE 7: TARGETS & INCENTIVES DESK
                    // ==========================================
                    6 -> {
                        item {
                            Text("Active Network Target Pools", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
                        }

                        // Hardcoded target records shown beautifully
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Karnataka Q3 Expansion Target", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("Category: FRANCHISE", fontSize = 10.sp, color = Color.Gray)
                                        }
                                        Surface(
                                            color = Color(0xFFE8F5E9),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("IN PROGRESS", color = Color(0xFF2E7D32), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Deadline: 30-Sep-2026", fontSize = 10.sp, color = Color.Gray)
                                        Text("₹42,00,000 / ₹50,00,000", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = 0.84f,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFF3F51B5),
                                        trackColor = Color(0xFFEEEEEE)
                                    )
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Mumbai Distributor Onboarding", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("Category: DISTRIBUTOR", fontSize = 10.sp, color = Color.Gray)
                                        }
                                        Surface(
                                            color = Color(0xFFEDE7F6),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("ACHIEVED", color = Color(0xFF4A148C), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Deadline: 31-Jul-2026", fontSize = 10.sp, color = Color.Gray)
                                        Text("₹15,00,000 / ₹15,00,000", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = 1.0f,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFF2E7D32),
                                        trackColor = Color(0xFFEEEEEE)
                                    )
                                }
                            }
                        }

                        // Network Leaderboards
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E272C))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Enterprise Distributor Leaderboard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    listOf(
                                        Triple("Manoj Patil Distributors", "₹29,80,000", "Diamond Elite Badge"),
                                        Triple("Sanjay Kumar Payouts", "₹14,50,000", "Gold Star Badge"),
                                        Triple("Arun Kumar Enterprises", "₹8,50,000", "Silver Club Badge")
                                    ).forEachIndexed { i, leader ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("#${i+1}", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(leader.first, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Text(leader.third, color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp)
                                                }
                                            }
                                            Text(leader.second, color = Color(0xFF81C784), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Divider(color = Color.White.copy(alpha = 0.05f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet Dialogs
    if (showAddFranchiseDialog) {
        AlertDialog(
            onDismissRequest = { showAddFranchiseDialog = false },
            title = { Text("Register New Territory Franchise", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFranName,
                        onValueChange = { newFranName = it },
                        label = { Text("Franchise / Corporate Name", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("fran_input_name")
                    )
                    OutlinedTextField(
                        value = newFranState,
                        onValueChange = { newFranState = it },
                        label = { Text("Allocated State Territory", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("fran_input_state")
                    )
                    OutlinedTextField(
                        value = newFranDistrict,
                        onValueChange = { newFranDistrict = it },
                        label = { Text("Allocated District Territory", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("fran_input_district")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFranName.isNotEmpty()) {
                            franchises = franchises + FranchiseRecord(
                                id = "fran_0${franchises.size + 1}",
                                name = newFranName,
                                territoryState = newFranState.ifEmpty { "Karnataka" },
                                territoryDistrict = newFranDistrict.ifEmpty { "Bangalore" },
                                walletBalance = 0.0,
                                creditOutstanding = 0.0,
                                kycStatus = "PENDING",
                                lifeTimeCommission = 0.0,
                                rankingBadge = "GOLD",
                                activeRetailersCount = 0
                            )
                        }
                        showAddFranchiseDialog = false
                        newFranName = ""
                        newFranState = ""
                        newFranDistrict = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                ) {
                    Text("Register", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFranchiseDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showAddDistributorDialog) {
        AlertDialog(
            onDismissRequest = { showAddDistributorDialog = false },
            title = { Text("Register Distributor Client", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newDistName,
                        onValueChange = { newDistName = it },
                        label = { Text("Distributor Name", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("dist_input_name")
                    )
                    OutlinedTextField(
                        value = newDistMaster,
                        onValueChange = { newDistMaster = it },
                        label = { Text("Master Franchise Parent Name", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("dist_input_master")
                    )
                    OutlinedTextField(
                        value = newDistState,
                        onValueChange = { newDistState = it },
                        label = { Text("State Territory", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("dist_input_state")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDistName.isNotEmpty()) {
                            distributors = distributors + DistributorRecord(
                                id = "dist_0${distributors.size + 1}",
                                name = newDistName,
                                masterDistributor = newDistMaster.ifEmpty { "Karnataka Apex" },
                                assignedState = newDistState.ifEmpty { "Karnataka" },
                                walletBalance = 0.0,
                                creditLimit = 0.0,
                                kycStatus = "PENDING",
                                totalSales = 0.0,
                                isApproved = false
                            )
                        }
                        showAddDistributorDialog = false
                        newDistName = ""
                        newDistMaster = ""
                        newDistState = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                ) {
                    Text("Register Distributor", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDistributorDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showAddRoleDialog) {
        AlertDialog(
            onDismissRequest = { showAddRoleDialog = false },
            title = { Text("Create Custom Hierarchy Role", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newRoleId,
                        onValueChange = { newRoleId = it },
                        label = { Text("Role Code (e.g. ROLE_REGIONAL_MGR)", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("role_input_code")
                    )
                    OutlinedTextField(
                        value = newRoleName,
                        onValueChange = { newRoleName = it },
                        label = { Text("Role Label (e.g. Regional Manager)", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("role_input_label")
                    )
                    OutlinedTextField(
                        value = newRoleParent,
                        onValueChange = { newRoleParent = it },
                        label = { Text("Parent Node Role", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("role_input_parent")
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Assign Custom Permissions:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    
                    // Simple select list
                    allBasePermissions.forEach { perm ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                selectedPerms = if (selectedPerms.contains(perm)) selectedPerms - perm else selectedPerms + perm
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedPerms.contains(perm),
                                onCheckedChange = { checked ->
                                    selectedPerms = if (checked == true) selectedPerms + perm else selectedPerms - perm
                                },
                                modifier = Modifier.scale(0.8f)
                            )
                            Text(perm, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newRoleId.isNotEmpty() && newRoleName.isNotEmpty()) {
                            customRoles = customRoles + CustomRole(
                                roleId = newRoleId,
                                roleName = newRoleName,
                                parentRole = newRoleParent.ifEmpty { "Corporate Admin" },
                                permissions = selectedPerms.toList()
                            )
                        }
                        showAddRoleDialog = false
                        newRoleId = ""
                        newRoleName = ""
                        newRoleParent = ""
                        selectedPerms = emptySet()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                ) {
                    Text("Build Custom Role", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddRoleDialog = false }) { Text("Cancel") }
            }
        )
    }
}



