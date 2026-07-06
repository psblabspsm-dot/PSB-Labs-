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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import java.text.SimpleDateFormat
import java.util.*

// Local Employee Data Structure for rich interactive state
data class EmployeeMaster(
    val id: String,
    val employeeCode: String,
    val fullName: String,
    val photoUrl: String,
    val designation: String,
    val department: String,
    val branch: String,
    val reportingManager: String,
    val mobileNumber: String,
    val email: String,
    val aadhaarMasked: String,
    val panMasked: String,
    val dateOfJoining: String,
    val dateOfBirth: String,
    val bloodGroup: String,
    val address: String,
    val emergencyContact: String,
    val employmentType: String, // FULL_TIME, CONTRACT, INTERN
    val basicSalary: Double,
    val hra: Double,
    val specialAllowance: Double,
    val incentive: Double,
    val bonus: Double,
    val pfDeduction: Double,
    val esicDeduction: Double,
    val ptDeduction: Double,
    val bankAccount: String,
    val ifscCode: String,
    val uan: String,
    val esicNumber: String,
    val pfNumber: String,
    val status: String // ACTIVE, INACTIVE
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeHrmsScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Dashboard, 1 = Employee Master, 2 = ID Cards, 3 = Payroll & Payslips
    val coroutineScope = rememberCoroutineScope()

    // Default seeded employee database state
    var employeesState by remember {
        mutableStateOf(
            listOf(
                EmployeeMaster(
                    id = "emp_01",
                    employeeCode = "SCS-1001",
                    fullName = "Amrita Rao",
                    photoUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?q=80&w=200",
                    designation = "Senior Compliance Officer",
                    department = "Risk & Compliance",
                    branch = "Bangalore Head Office",
                    reportingManager = "Vignesh K. (Director of Operations)",
                    mobileNumber = "+91 98860 12345",
                    email = "amrita.rao@suryacredit.com",
                    aadhaarMasked = "XXXX-XXXX-4312",
                    panMasked = "XXXXX5412D",
                    dateOfJoining = "2024-03-01",
                    dateOfBirth = "1995-07-15",
                    bloodGroup = "B+",
                    address = "Flat 402, Block C, Prestige Heights, Outer Ring Road, Bangalore - 560103",
                    emergencyContact = "+91 98860 99999 (Father)",
                    employmentType = "FULL_TIME",
                    basicSalary = 32500.0,
                    hra = 16250.0,
                    specialAllowance = 9750.0,
                    incentive = 6500.0,
                    bonus = 0.0,
                    pfDeduction = 3900.0,
                    esicDeduction = 487.5,
                    ptDeduction = 200.0,
                    bankAccount = "501004128919",
                    ifscCode = "HDFC0000104",
                    uan = "100912448212",
                    esicNumber = "3112485918",
                    pfNumber = "KN/BAN/00124/091A",
                    status = "ACTIVE"
                ),
                EmployeeMaster(
                    id = "emp_02",
                    employeeCode = "SCS-1002",
                    fullName = "Rohan Sharma",
                    photoUrl = "https://images.unsplash.com/photo-1560250097-0b93528c311a?q=80&w=200",
                    designation = "Sales Lead",
                    department = "Sales & Business Development",
                    branch = "Bangalore Head Office",
                    reportingManager = "Vignesh K. (Director of Operations)",
                    mobileNumber = "+91 99160 54321",
                    email = "rohan.sharma@suryacredit.com",
                    aadhaarMasked = "XXXX-XXXX-8901",
                    panMasked = "XXXXX8901C",
                    dateOfJoining = "2024-05-15",
                    dateOfBirth = "1992-11-20",
                    bloodGroup = "O+",
                    address = "No. 42, 4th Main, HSR Layout Sector 3, Bangalore - 560102",
                    emergencyContact = "+91 99160 88888 (Spouse)",
                    employmentType = "FULL_TIME",
                    basicSalary = 24000.0,
                    hra = 12000.0,
                    specialAllowance = 7200.0,
                    incentive = 4800.0,
                    bonus = 0.0,
                    pfDeduction = 2880.0,
                    esicDeduction = 360.0,
                    ptDeduction = 200.0,
                    bankAccount = "91802004218901",
                    ifscCode = "ICIC0000002",
                    uan = "100814918201",
                    esicNumber = "3128941092",
                    pfNumber = "KN/BAN/00124/112C",
                    status = "ACTIVE"
                ),
                EmployeeMaster(
                    id = "emp_03",
                    employeeCode = "SCS-1003",
                    fullName = "Sneha Patel",
                    photoUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956?q=80&w=200",
                    designation = "Operations Lead",
                    department = "Operations Support",
                    branch = "Pune Branch Office",
                    reportingManager = "Vignesh K. (Director of Operations)",
                    mobileNumber = "+91 91122 33445",
                    email = "sneha.patel@suryacredit.com",
                    aadhaarMasked = "XXXX-XXXX-7110",
                    panMasked = "XXXXX7110A",
                    dateOfJoining = "2025-01-10",
                    dateOfBirth = "1997-04-05",
                    bloodGroup = "A-",
                    address = "Apt 12, Rose Wood Society, Koregaon Park, Pune - 411001",
                    emergencyContact = "+91 91122 99911 (Mother)",
                    employmentType = "FULL_TIME",
                    basicSalary = 17500.0,
                    hra = 8750.0,
                    specialAllowance = 5250.0,
                    incentive = 3500.0,
                    bonus = 0.0,
                    pfDeduction = 2100.0,
                    esicDeduction = 262.5,
                    ptDeduction = 200.0,
                    bankAccount = "30291048128912",
                    ifscCode = "SBIN0001242",
                    uan = "100741289104",
                    esicNumber = "3152810482",
                    pfNumber = "PN/PUN/00248/018F",
                    status = "ACTIVE"
                )
            )
        )
    }

    // Active payroll processing status states
    var isPayrollProcessing by remember { mutableStateOf(false) }
    var payrollMonthState by remember { mutableStateOf("June 2026") }
    var payrollDisbursedState by remember { mutableStateOf(true) }

    // Selected items for detail modals
    var selectedEmpForProfile by remember { mutableStateOf<EmployeeMaster?>(null) }
    var selectedEmpForIdCard by remember { mutableStateOf<EmployeeMaster?>(null) }
    var selectedEmpForPayslip by remember { mutableStateOf<EmployeeMaster?>(null) }
    var selectedEmpForSalaryEdit by remember { mutableStateOf<EmployeeMaster?>(null) }

    // Form modal state for adding employee
    var showAddEmployeeDialog by remember { mutableStateOf(false) }
    var qrVerificationData by remember { mutableStateOf<EmployeeMaster?>(null) }

    Scaffold(
        topBar = {
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Surya HRMS Console",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                    label = { Text("Dashboard", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.People, "Employees") },
                    label = { Text("Staff List", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.Badge, "ID Cards") },
                    label = { Text("ID Cards", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Default.ReceiptLong, "Payroll") },
                    label = { Text("Payroll", fontSize = 11.sp) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Screen switching animations
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "HrmsTabTransition"
            ) { tab ->
                when (tab) {
                    0 -> HrmsDashboardView(
                        employees = employeesState,
                        payrollMonth = payrollMonthState,
                        payrollDisbursed = payrollDisbursedState,
                        onProcessPayroll = {
                            isPayrollProcessing = true
                            coroutineScope.launch {
                                delay(2000)
                                isPayrollProcessing = false
                                payrollDisbursedState = true
                                viewModel.showNotification("Salary disbursal batch settled via RazorpayX secure channels!")
                            }
                        },
                        isProcessing = isPayrollProcessing,
                        onNavigateToTab = { activeTab = it }
                    )
                    1 -> EmployeeMasterListView(
                        employees = employeesState,
                        onAddEmployeeClick = { showAddEmployeeDialog = true },
                        onViewProfile = { selectedEmpForProfile = it },
                        onEditSalary = { selectedEmpForSalaryEdit = it }
                    )
                    2 -> DigitalIdCardsHubView(
                        employees = employeesState,
                        onViewIdCard = { selectedEmpForIdCard = it },
                        onVerifyQr = { qrVerificationData = it }
                    )
                    3 -> PayrollAndPayslipView(
                        employees = employeesState,
                        payrollMonth = payrollMonthState,
                        onViewPayslip = { selectedEmpForPayslip = it }
                    )
                }
            }
        }
    }

    // Modal Sheet / Dialog to view Full Employee Profile
    selectedEmpForProfile?.let { emp ->
        EmployeeProfileDialog(
            employee = emp,
            onDismiss = { selectedEmpForProfile = null },
            onStatusChange = { newStatus ->
                employeesState = employeesState.map {
                    if (it.id == emp.id) it.copy(status = newStatus) else it
                }
                selectedEmpForProfile = selectedEmpForProfile?.copy(status = newStatus)
                viewModel.showNotification("Employee status updated to $newStatus successfully!")
            }
        )
    }

    // Dialog for Interactive digital ID card
    selectedEmpForIdCard?.let { emp ->
        DigitalIdCardViewerDialog(
            employee = emp,
            onDismiss = { selectedEmpForIdCard = null },
            onAction = { actionName ->
                viewModel.showNotification("Initiating $actionName download for ${emp.fullName}...")
            }
        )
    }

    // Dialog to preview interactive Secure Payslips
    selectedEmpForPayslip?.let { emp ->
        PayslipViewerDialog(
            employee = emp,
            month = payrollMonthState,
            onDismiss = { selectedEmpForPayslip = null },
            onAction = { actionName ->
                viewModel.showNotification("$actionName trigger succeeded for ${emp.fullName}'s payslip.")
            }
        )
    }

    // Dialog to edit specific salary structure components
    selectedEmpForSalaryEdit?.let { emp ->
        SalaryStructureEditDialog(
            employee = emp,
            onDismiss = { selectedEmpForSalaryEdit = null },
            onSave = { updatedEmp ->
                employeesState = employeesState.map {
                    if (it.id == updatedEmp.id) updatedEmp else it
                }
                selectedEmpForSalaryEdit = null
                viewModel.showNotification("Salary structure updated & recalculated securely.")
            }
        )
    }

    // Dialog for on-demand QR code Verification Simulator
    qrVerificationData?.let { emp ->
        QrVerificationDeskDialog(
            employee = emp,
            onDismiss = { qrVerificationData = null }
        )
    }

    // Add Employee Form Dialog
    if (showAddEmployeeDialog) {
        AddEmployeeFormDialog(
            onDismiss = { showAddEmployeeDialog = false },
            onSave = { newEmp ->
                employeesState = employeesState + newEmp
                showAddEmployeeDialog = false
                viewModel.showNotification("Employee profile created successfully! ID card generated.")
            }
        )
    }
}

// ==========================================================================
// 1. HRMS DASHBOARD VIEW
// ==========================================================================
@Composable
fun HrmsDashboardView(
    employees: List<EmployeeMaster>,
    payrollMonth: String,
    payrollDisbursed: Boolean,
    onProcessPayroll: () -> Unit,
    isProcessing: Boolean,
    onNavigateToTab: (Int) -> Unit
) {
    val activeCount = employees.count { it.status == "ACTIVE" }
    val totalNetDisbursal = employees.sumOf { it.basicSalary + it.hra + it.specialAllowance + it.incentive - it.pfDeduction - it.esicDeduction - it.ptDeduction }
    val avgSalary = if (employees.isNotEmpty()) employees.sumOf { it.basicSalary } / employees.size else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Overview Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Executive Payroll & Workforce Command",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Secure digital workspace monitoring professional rosters, structural payroll dispatches, statutory PF audits, and verified biometric badges.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Analytical Metrics Grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardMetricCard(
                    title = "Active Headcount",
                    value = "$activeCount Staff",
                    subtext = "100% Onboarded",
                    icon = Icons.Default.People,
                    color = Color(0xFF673AB7),
                    modifier = Modifier.weight(1f)
                )
                DashboardMetricCard(
                    title = "Monthly Payroll",
                    value = "₹" + String.format(Locale.US, "%,.0f", totalNetDisbursal),
                    subtext = "June 2026 Batch",
                    icon = Icons.Default.CurrencyRupee,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardMetricCard(
                    title = "Attendance Rate",
                    value = "96.4%",
                    subtext = "Biometric Match",
                    icon = Icons.Default.HowToReg,
                    color = Color(0xFF0288D1),
                    modifier = Modifier.weight(1f)
                )
                DashboardMetricCard(
                    title = "Compliance Status",
                    value = "PF / ESIC Audited",
                    subtext = "100% Compliant",
                    icon = Icons.Default.VerifiedUser,
                    color = Color(0xFFE65100),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Live Administrative Control Board for Quick Payroll Processing
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Ecosystem Payroll Disbursement",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Current billing: $payrollMonth",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                        Surface(
                            color = if (payrollDisbursed) Color(0xFF2E7D32).copy(alpha = 0.15f) else Color(0xFFC62828).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (payrollDisbursed) "RELEASED" else "DRAFT STAGE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (payrollDisbursed) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Base Salary", fontSize = 11.sp, color = Color.Gray)
                            Text("₹" + String.format(Locale.US, "%,.0f", employees.sumOf { it.basicSalary }), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column {
                            Text("Additions (HRA/Incentive)", fontSize = 11.sp, color = Color.Gray)
                            Text("₹" + String.format(Locale.US, "%,.0f", employees.sumOf { it.hra + it.specialAllowance + it.incentive }), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32))
                        }
                        Column {
                            Text("Statutory Retainers", fontSize = 11.sp, color = Color.Gray)
                            Text("₹" + String.format(Locale.US, "%,.0f", employees.sumOf { it.pfDeduction + it.esicDeduction + it.ptDeduction }), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFC62828))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isProcessing) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Calling bank gateways & generating system payslips...",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Button(
                            onClick = onProcessPayroll,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Payment, "Disburse", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PROCESS & SETTLE JUNE PAYROLL", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Quick Navigation Shortcuts
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Operational Modules", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { onNavigateToTab(1) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.People, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Manage Staff", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { onNavigateToTab(2) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Badge, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ID Card Hub", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardMetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp).padding(4.dp)
                    )
                }
                Text(text = subtext, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, fontSize = 11.sp, color = Color.Gray)
        }
    }
}


// ==========================================================================
// 2. EMPLOYEE MASTER & LIST VIEW
// ==========================================================================
@Composable
fun EmployeeMasterListView(
    employees: List<EmployeeMaster>,
    onAddEmployeeClick: () -> Unit,
    onViewProfile: (EmployeeMaster) -> Unit,
    onEditSalary: (EmployeeMaster) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredEmployees = employees.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) || it.employeeCode.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & Add Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Code or Name...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Button(
                onClick = onAddEmployeeClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Staff", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Master Rosters (${filteredEmployees.size})",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(filteredEmployees) { emp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            ) {
                                // Fallback avatar text representing letters
                                Text(
                                    text = emp.fullName.firstOrNull()?.toString() ?: "",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(emp.fullName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(emp.employeeCode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("•", fontSize = 11.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(emp.designation, fontSize = 11.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }

                            Surface(
                                color = if (emp.status == "ACTIVE") Color(0xFF2E7D32).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = emp.status,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (emp.status == "ACTIVE") Color(0xFF2E7D32) else Color.Gray,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Dept: ${emp.department}", fontSize = 11.sp, color = Color.Gray)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(
                                    onClick = { onEditSalary(emp) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.EditCalendar, null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Salary Structure", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { onViewProfile(emp) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Full Profile", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================================================
// 3. DIGITAL ID CARDS HUB VIEW
// ==========================================================================
@Composable
fun DigitalIdCardsHubView(
    employees: List<EmployeeMaster>,
    onViewIdCard: (EmployeeMaster) -> Unit,
    onVerifyQr: (EmployeeMaster) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Tamper-Proof ID Cards Hub", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Digital cryptographically secured identity credentials featuring clickable test verification scans.", fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Employee Identity Badge", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(employees) { emp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCode, null, tint = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(emp.fullName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("ID Code: ${emp.employeeCode} | ${emp.designation}", fontSize = 11.sp, color = Color.Gray)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { onVerifyQr(emp) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Scan Verify", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onViewIdCard(emp) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Show Card", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================================================
// 4. PAYROLL & PAYSLIPS VIEW
// ==========================================================================
@Composable
fun PayrollAndPayslipView(
    employees: List<EmployeeMaster>,
    payrollMonth: String,
    onViewPayslip: (EmployeeMaster) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32).copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.2f))
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FactCheck, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Monthly Payslip Registry", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Text("Select employee to view, download, print or mail itemized salary slips.", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Generated Slips ($payrollMonth)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("June 2026 Batch", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(employees) { emp ->
                val netPay = emp.basicSalary + emp.hra + emp.specialAllowance + emp.incentive - emp.pfDeduction - emp.esicDeduction - emp.ptDeduction
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Receipt, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(emp.fullName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Payslip No: SCS-202606-${emp.employeeCode.split('-')[1]}", fontSize = 10.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹" + String.format(Locale.US, "%,.0f", netPay), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Text("Net Salary", fontSize = 9.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Disbursal: ${emp.bankAccount.takeLast(4).padStart(emp.bankAccount.length, 'X')} (${emp.ifscCode})", fontSize = 10.sp, color = Color.Gray)
                            Button(
                                onClick = { onViewPayslip(emp) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Visibility, null, modifier = Modifier.size(12.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("View Slip", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================================================
// MODALS, SHEETS, & DIALOG COMPLEX COMPONENTS
// ==========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeProfileDialog(
    employee: EmployeeMaster,
    onDismiss: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Employee Master Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    }
                }

                // Scrollable Profile Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Profile Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(employee.fullName.take(1), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(employee.fullName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("${employee.designation} (${employee.employeeCode})", fontSize = 12.sp, color = Color.Gray)
                                Text("Dept: ${employee.department} | ${employee.branch}", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Section: General Information
                    ProfileSectionTitle("GENERAL INFORMATION")
                    HrmsProfileDetailRow("Employment Type", employee.employmentType)
                    HrmsProfileDetailRow("Date of Joining", employee.dateOfJoining)
                    HrmsProfileDetailRow("Reporting Manager", employee.reportingManager)
                    HrmsProfileDetailRow("Corporate Email", employee.email)
                    HrmsProfileDetailRow("Mobile Number", employee.mobileNumber)

                    // Section: Identity Credentials (Masked)
                    ProfileSectionTitle("IDENTITY CREDENTIALS (SECURE)")
                    HrmsProfileDetailRow("Aadhaar Number", employee.aadhaarMasked)
                    HrmsProfileDetailRow("PAN Number", employee.panMasked)
                    HrmsProfileDetailRow("Blood Group", employee.bloodGroup)
                    HrmsProfileDetailRow("Date of Birth", employee.dateOfBirth)

                    // Section: Statutory Declarations
                    ProfileSectionTitle("STATUTORY DECLARATIONS")
                    HrmsProfileDetailRow("UAN (EPF)", employee.uan)
                    HrmsProfileDetailRow("ESIC Card Number", employee.esicNumber)
                    HrmsProfileDetailRow("Provident Fund Code", employee.pfNumber)

                    // Section: Disbursal Account details
                    ProfileSectionTitle("SALARY DISBURSAL BANK ACCOUNT")
                    HrmsProfileDetailRow("Bank Account", employee.bankAccount)
                    HrmsProfileDetailRow("IFSC Branch Code", employee.ifscCode)

                    // Section: Emergency details
                    ProfileSectionTitle("EMERGENCY & CONTACT")
                    HrmsProfileDetailRow("Address Log", employee.address)
                    HrmsProfileDetailRow("Emergency Contact", employee.emergencyContact)
                }

                // Footer actions (Change employee status)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (employee.status == "ACTIVE") {
                        Button(
                            onClick = { onStatusChange("INACTIVE") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("DEACTIVATE ROSTER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = { onStatusChange("ACTIVE") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ACTIVATE ROSTER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
fun HrmsProfileDetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.widthIn(max = 180.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}


// ==========================================================================
// DIGITAL ID CARD VIEWER & FLIPPING ANIMATION
// ==========================================================================
@Composable
fun DigitalIdCardViewerDialog(
    employee: EmployeeMaster,
    onDismiss: () -> Unit,
    onAction: (String) -> Unit
) {
    var isFrontSide by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Control
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Digital Identity Badge", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Flippable Card Presentation
                AnimatedContent(
                    targetState = isFrontSide,
                    transitionSpec = {
                        slideInHorizontally() togetherWith slideOutHorizontally()
                    },
                    label = "IdCardFlip"
                ) { side ->
                    if (side) {
                        // FRONT SIDE DESIGN
                        Card(
                            modifier = Modifier
                                .width(280.dp)
                                .height(420.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Corporate Header Banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color(0xFF4A154B), Color(0xFF673AB7))
                                            )
                                        )
                                        .padding(12.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Text("SURYA CREDIT SOLUTIONS", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, letterSpacing = 1.sp)
                                        Text("DIGITAL IDENTITY BADGE", color = Color(0xFFD1C4E9), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Body Details
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Photo Frame
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(CircleShape)
                                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(employee.fullName.take(1), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Name and Designation
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(employee.fullName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        Text(employee.designation, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Details Columns
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IdCardRow("Employee Code", employee.employeeCode)
                                        IdCardRow("Department", employee.department)
                                        IdCardRow("Issue Date", employee.dateOfJoining)
                                        IdCardRow("Valid Till", "2031-03-01")
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Front Side Signatures and QR Checksum
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("SCS-VERIFIED", fontFamily = FontFamily.Monospace, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                                            Text("Employee Signature", fontSize = 8.sp, color = Color.Gray)
                                        }

                                        // Placeholder for barcode
                                        Text("||||| ||| |||| || ||", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("AUTHORIZED", fontFamily = FontFamily.Monospace, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                            Text("Issuer Sign", fontSize = 8.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // BACK SIDE DESIGN
                        Card(
                            modifier = Modifier
                                .width(280.dp)
                                .height(420.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, Color.Gray),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("TERMS & INSTRUCTIONS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "1. This identity credential remains the absolute property of Surya Credit Solutions Private Limited.\n\n" +
                                                "2. If found, please return this card to the Head Office Address instantly.\n\n" +
                                                "3. Misuse of this identity card is a cyber offence and will immediately trigger regulatory legal actions.",
                                        fontSize = 9.sp,
                                        color = Color.Gray,
                                        lineHeight = 12.sp
                                    )
                                }

                                Divider()

                                // Contact Info and QR Scan check
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("OFFICE ADDRESS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                    Text("No. 12, outer Ring Road, HSR, Bangalore - 560102", fontSize = 9.sp, color = Color.Gray, textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Emergency Call: +91 98860 99999", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                    Text("HR Support: hr@suryacredit.com", fontSize = 9.sp, color = Color.Gray)
                                }

                                Divider()

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.QrCode, "QR", modifier = Modifier.size(54.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("TAMPER-PROOF DIGEST SECURE CODE", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                                    Text("V1:7BD49FA18C029E", fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Flipping controller
                OutlinedButton(
                    onClick = { isFrontSide = !isFrontSide },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Flip, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isFrontSide) "Flip to View Back Side" else "Flip to View Front Side", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fast PDF, PNG or Pass Download buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onAction("PDF") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DownloadForOffline, null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { onAction("PNG") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Image, null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PNG", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { onAction("Wallet Pass") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun IdCardRow(label: String, value: String) {
    Row(
        modifier = Modifier.width(220.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        Text(value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}


// ==========================================================================
// QR CODE SCAN VERIFICATION SIMULATOR
// ==========================================================================
@Composable
fun QrVerificationDeskDialog(
    employee: EmployeeMaster,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Secure QR Verification Scan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Result of decrypting live employee identity passport", fontSize = 11.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                // Detail Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32).copy(alpha = 0.05f)),
                    border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Company Name", fontSize = 11.sp, color = Color.Gray)
                            Text("Surya Credit Solutions Pvt Ltd", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Staff Name", fontSize = 11.sp, color = Color.Gray)
                            Text(employee.fullName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Staff Code", fontSize = 11.sp, color = Color.Gray)
                            Text(employee.employeeCode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Designation", fontSize = 11.sp, color = Color.Gray)
                            Text(employee.designation, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Department", fontSize = 11.sp, color = Color.Gray)
                            Text(employee.department, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Roster Status", fontSize = 11.sp, color = Color.Gray)
                            Text("ACTIVE STAFF", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Scan Timestamp", fontSize = 11.sp, color = Color.Gray)
                            Text(SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US).format(Date()), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pass Verification Badge
                Surface(
                    color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TAMPER PROOF SIGNATURE PASSED", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("CLOSE DESK SCAN")
                }
            }
        }
    }
}


// ==========================================================================
// PAYSLIP DETAIL VIEWER DIALOG
// ==========================================================================
@Composable
fun PayslipViewerDialog(
    employee: EmployeeMaster,
    month: String,
    onDismiss: () -> Unit,
    onAction: (String) -> Unit
) {
    val basic = employee.basicSalary
    val hra = employee.hra
    val special = employee.specialAllowance
    val inc = employee.incentive
    val bonus = employee.bonus
    val gross = basic + hra + special + inc + bonus
    val pf = employee.pfDeduction
    val esic = employee.esicDeduction
    val pt = employee.ptDeduction
    val totalDeduction = pf + esic + pt
    val net = gross - totalDeduction

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2E7D32))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Monthly Earnings Ledger", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    }
                }

                // Scrollable Payslip content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Company Header Info
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("SURYA CREDIT SOLUTIONS PRIVATE LIMITED", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color.Black)
                        Text("No. 12, HSR Sector 3, Outer Ring Road, Bangalore - 560102", fontSize = 10.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Salary Slip for Month: $month", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2E7D32))
                        Text("Payslip No: SCS-202606-${employee.employeeCode.split('-')[1]}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                    }

                    Divider()

                    // Employee details block
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            PayslipLabelValue("Employee Name", employee.fullName)
                            PayslipLabelValue("Employee ID", employee.employeeCode)
                            PayslipLabelValue("Department", employee.department)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            PayslipLabelValue("Bank Disbursal", employee.bankAccount.takeLast(4).padStart(employee.bankAccount.length, 'X'), textAlign = TextAlign.End)
                            PayslipLabelValue("IFSC Code", employee.ifscCode, textAlign = TextAlign.End)
                            PayslipLabelValue("UAN (EPF)", employee.uan, textAlign = TextAlign.End)
                        }
                    }

                    Divider()

                    // Roster Working statistics
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Calendar Days: 30", fontSize = 11.sp, color = Color.Gray)
                        Text("Present Days: 24", fontSize = 11.sp, color = Color.Gray)
                        Text("Paid Off Days: 6", fontSize = 11.sp, color = Color.Gray)
                    }

                    Divider()

                    // Breakdown (Earnings vs Deductions)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Earnings Column
                        Column(modifier = Modifier.weight(1f)) {
                            Text("EARNINGS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.height(6.dp))
                            BreakdownRow("Basic Salary", basic)
                            BreakdownRow("HRA Component", hra)
                            BreakdownRow("Special Allowance", special)
                            BreakdownRow("Incentives Pay", inc)
                            BreakdownRow("Retention Bonus", bonus)
                            Spacer(modifier = Modifier.height(6.dp))
                            Divider(color = Color.LightGray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Gross Pay", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("₹" + String.format(Locale.US, "%,.0f", gross), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Deductions Column
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DEDUCTIONS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFC62828))
                            Spacer(modifier = Modifier.height(6.dp))
                            BreakdownRow("Provident Fund (PF)", pf)
                            BreakdownRow("ESIC Deduction", esic)
                            BreakdownRow("Professional Tax", pt)
                            BreakdownRow("Income Tax (TDS)", 0.0)
                            Spacer(modifier = Modifier.height(18.dp)) // padding
                            Divider(color = Color.LightGray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Ded.", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("₹" + String.format(Locale.US, "%,.0f", totalDeduction), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Divider()

                    // Net Pay Summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32).copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("NET SALARY TRANSFERRED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    Text("Transferred securely to bank account", fontSize = 9.sp, color = Color.Gray)
                                }
                                Text("₹" + String.format(Locale.US, "%,.2f", net), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                            }
                        }
                    }

                    // Bottom Signatures & Tamper Proof check
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("V1:SCS-RELEASE-2026", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = Color.Gray)
                            Text("Authorized CFO Signature", fontSize = 8.sp, color = Color.Gray)
                        }

                        Icon(Icons.Default.QrCode, null, modifier = Modifier.size(36.dp), tint = Color.Gray)
                    }
                }

                // Actions buttons (PDF Download, Print, Email, Share Link)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onAction("PDF") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DownloadForOffline, null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download PDF", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { onAction("Email") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Email, null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Email Payslip", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PayslipLabelValue(label: String, value: String, textAlign: TextAlign = TextAlign.Start) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(label, fontSize = 9.sp, color = Color.Gray, textAlign = textAlign)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = textAlign)
    }
}

@Composable
fun BreakdownRow(label: String, valNum: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        Text("₹" + String.format(Locale.US, "%,.0f", valNum), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}


// ==========================================================================
// SALARY STRUCTURE EDIT DIALOG
// ==========================================================================
@Composable
fun SalaryStructureEditDialog(
    employee: EmployeeMaster,
    onDismiss: () -> Unit,
    onSave: (EmployeeMaster) -> Unit
) {
    var basicInput by remember { mutableStateOf(employee.basicSalary.toInt().toString()) }
    var hraInput by remember { mutableStateOf(employee.hra.toInt().toString()) }
    var allowanceInput by remember { mutableStateOf(employee.specialAllowance.toInt().toString()) }
    var incentiveInput by remember { mutableStateOf(employee.incentive.toInt().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Configure Salary Structure",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Configure earnings slabs for ${employee.fullName}. Deductions are calculated automatically according to statutory norms (PF: 12% of Basic, ESIC: 0.75% of Gross, PT: ₹200).",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 14.sp
                )

                OutlinedTextField(
                    value = basicInput,
                    onValueChange = { basicInput = it },
                    label = { Text("Basic Salary Component (₹)") },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hraInput,
                    onValueChange = { hraInput = it },
                    label = { Text("HRA Component (₹)") },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = allowanceInput,
                    onValueChange = { allowanceInput = it },
                    label = { Text("Special Allowances (₹)") },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = incentiveInput,
                    onValueChange = { incentiveInput = it },
                    label = { Text("Performance Incentives (₹)") },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val basic = basicInput.toDoubleOrNull() ?: employee.basicSalary
                            val hra = hraInput.toDoubleOrNull() ?: employee.hra
                            val allowance = allowanceInput.toDoubleOrNull() ?: employee.specialAllowance
                            val incentive = incentiveInput.toDoubleOrNull() ?: employee.incentive

                            // Recalculate automatic deductions
                            val pf = basic * 0.12
                            val gross = basic + hra + allowance + incentive
                            val esic = gross * 0.0075

                            onSave(
                                employee.copy(
                                    basicSalary = basic,
                                    hra = hra,
                                    specialAllowance = allowance,
                                    incentive = incentive,
                                    pfDeduction = pf,
                                    esicDeduction = esic
                                )
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save & Calculate")
                    }
                }
            }
        }
    }
}


// ==========================================================================
// ADD EMPLOYEE FORM DIALOG
// ==========================================================================
@Composable
fun AddEmployeeFormDialog(
    onDismiss: () -> Unit,
    onSave: (EmployeeMaster) -> Unit
) {
    var code by remember { mutableStateOf("SCS-" + (1000 + kotlin.random.Random.nextInt(100, 999))) }
    var name by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Risk & Compliance") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var aadhaarInput by remember { mutableStateOf("") }
    var panInput by remember { mutableStateOf("") }
    var bankAccountInput by remember { mutableStateOf("") }
    var ifscInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {
                    Text("Register New Employee Roster", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Employee Code") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Legal Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = designation, onValueChange = { designation = it }, label = { Text("Designation") }, modifier = Modifier.fillMaxWidth())
                    
                    // Simple Dropdown replacement using row selection
                    Text("Department Select", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    val depts = listOf("Risk & Compliance", "Sales & Business Development", "Operations Support", "IT Infrastructure")
                    depts.forEach { dept ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { department = dept }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = department == dept, onClick = { department = dept })
                            Text(dept, fontSize = 12.sp)
                        }
                    }

                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Corporate Email") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile Number") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Residential Address") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = emergencyContact, onValueChange = { emergencyContact = it }, label = { Text("Emergency Contact (Relationship)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = aadhaarInput, onValueChange = { aadhaarInput = it }, label = { Text("Aadhaar Number (12 digit)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = panInput, onValueChange = { panInput = it }, label = { Text("PAN Number (10 digit Alpha-Numeric)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = bankAccountInput, onValueChange = { bankAccountInput = it }, label = { Text("Disbursal Bank Account No") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = ifscInput, onValueChange = { ifscInput = it }, label = { Text("Bank IFSC Code") }, modifier = Modifier.fillMaxWidth())
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (name.isEmpty() || designation.isEmpty() || email.isEmpty()) {
                                return@Button
                            }

                            // Mask sensitive details
                            val maskedAadhaar = if (aadhaarInput.length >= 4) "XXXX-XXXX-${aadhaarInput.takeLast(4)}" else "XXXX-XXXX-9912"
                            val maskedPan = if (panInput.length >= 4) "XXXXX${panInput.takeLast(4)}X" else "XXXXX1234X"

                            onSave(
                                EmployeeMaster(
                                    id = "emp_" + kotlin.random.Random.nextInt(100, 999),
                                    employeeCode = code,
                                    fullName = name,
                                    photoUrl = "",
                                    designation = designation,
                                    department = department,
                                    branch = "Bangalore Head Office",
                                    reportingManager = "Vignesh K. (Director of Operations)",
                                    mobileNumber = mobile,
                                    email = email,
                                    aadhaarMasked = maskedAadhaar,
                                    panMasked = maskedPan,
                                    dateOfJoining = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                                    dateOfBirth = "1996-05-12",
                                    bloodGroup = "O+",
                                    address = address,
                                    emergencyContact = emergencyContact,
                                    employmentType = "FULL_TIME",
                                    basicSalary = 20000.0,
                                    hra = 10000.0,
                                    specialAllowance = 4000.0,
                                    incentive = 0.0,
                                    bonus = 0.0,
                                    pfDeduction = 2400.0,
                                    esicDeduction = 255.0,
                                    ptDeduction = 200.0,
                                    bankAccount = bankAccountInput,
                                    ifscCode = ifscInput,
                                    uan = "100" + kotlin.random.Random.nextInt(100000, 999999),
                                    esicNumber = "31" + kotlin.random.Random.nextInt(100000, 999999),
                                    pfNumber = "KN/BAN/00124/" + kotlin.random.Random.nextInt(100, 999) + "A",
                                    status = "ACTIVE"
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Roster")
                    }
                }
            }
        }
    }
}
