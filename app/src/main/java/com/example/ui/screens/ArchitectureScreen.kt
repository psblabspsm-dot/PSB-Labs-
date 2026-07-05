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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchitectureScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("NestJS API Console", "Riverpod State", "Prisma DB & Docker")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Row with Modern Amber/Orange Styling
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Color(0xFFFF8C00),
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                )
            }
        }

        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> NestJsApiConsoleTab()
                1 -> RiverpodStateTab()
                2 -> PrismaAndDockerTab()
            }
        }
    }
}

// ---------------- TAB 1: NESTJS API CONSOLE (SWAGGER INTERACTIVE SIMULATOR) ----------------

@Composable
fun NestJsApiConsoleTab() {
    val coroutineScope = rememberCoroutineScope()
    var selectedEndpointIndex by remember { mutableStateOf(0) }
    var responseOutput by remember { mutableStateOf("""{"status": "idle", "message": "Select an endpoint and click 'Send API Request' to test live."}""") }
    var isSending by remember { mutableStateOf(false) }
    var rateLimitRemaining by remember { mutableStateOf(100) }
    var inputParamValue1 by remember { mutableStateOf("9876543210") } // phoneNumber
    var inputParamValue2 by remember { mutableStateOf("9281") }       // MPIN / Amount

    val endpoints = listOf(
        ApiEndpointInfo(
            method = "POST",
            path = "/api/v1/auth/login",
            description = "Authenticate user session with JWT token signature",
            parameters = listOf("phoneNumber", "mpin"),
            defaultValues = listOf("9876543210", "9281")
        ),
        ApiEndpointInfo(
            method = "GET",
            path = "/api/v1/wallet/usr-dist-02/balance",
            description = "Fetch live dynamic ledger balance and B2B outstanding credit limits",
            parameters = listOf("userId"),
            defaultValues = listOf("usr-dist-02")
        ),
        ApiEndpointInfo(
            method = "POST",
            path = "/api/v1/wallet/usr-ret-03/transaction",
            description = "Execute a simulated outward transaction (DMT/AEPS) on ledger accounts",
            parameters = listOf("amount", "service"),
            defaultValues = listOf("4500", "DMT_TRANSFER")
        ),
        ApiEndpointInfo(
            method = "POST",
            path = "/api/v1/wallet/usr-ret-03/credit-repay",
            description = "Repay outstanding B2B credit lines using current wallet reserves",
            parameters = listOf("repayAmount"),
            defaultValues = listOf("3000")
        ),
        ApiEndpointInfo(
            method = "GET",
            path = "/api/v1/wallet/ledger",
            description = "Retrieve relational database double-entry ledger history logs",
            parameters = emptyList()
        )
    )

    // Sync input parameters when selection shifts
    LaunchedEffect(selectedEndpointIndex) {
        val curr = endpoints[selectedEndpointIndex]
        if (curr.parameters.isNotEmpty()) {
            inputParamValue1 = curr.defaultValues.getOrNull(0) ?: ""
            inputParamValue2 = curr.defaultValues.getOrNull(1) ?: ""
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Title banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NestJS Swagger Live Playground",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color(0xFFFF8C00)
                )
                Text(
                    text = "Verify secure JWT session routing and role guards.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            // Rate Limit counter UI
            Surface(
                color = if (rateLimitRemaining < 20) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = if (rateLimitRemaining < 20) Color.Red else Color(0xFF2E7D32),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rate: $rateLimitRemaining/100s",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (rateLimitRemaining < 20) Color.Red else Color(0xFF2E7D32)
                    )
                }
            }
        }

        // Endpoint Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Select API Target Endpoint:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                endpoints.forEachIndexed { idx, ep ->
                    val isSel = selectedEndpointIndex == idx
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent)
                            .clickable { selectedEndpointIndex = idx }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val mColor = if (ep.method == "POST") Color(0xFF2E7D32) else Color(0xFF1565C0)
                        val mBg = if (ep.method == "POST") Color(0xFFE8F5E9) else Color(0xFFE3F2FD)

                        Surface(color = mBg, shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = ep.method,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = mColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ep.path,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color(0xFFFF8C00) else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = ep.description,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Parameters Form
        val currentEp = endpoints[selectedEndpointIndex]
        if (currentEp.parameters.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Path & Query Payload Parameters:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    currentEp.parameters.forEachIndexed { index, paramName ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "$paramName:",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(90.dp)
                            )
                            if (index == 0) {
                                OutlinedTextField(
                                    value = inputParamValue1,
                                    onValueChange = { inputParamValue1 = it },
                                    shape = RoundedCornerShape(8.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                )
                            } else {
                                OutlinedTextField(
                                    value = inputParamValue2,
                                    onValueChange = { inputParamValue2 = it },
                                    shape = RoundedCornerShape(8.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Trigger Button
        Button(
            onClick = {
                if (!isSending) {
                    isSending = true
                    coroutineScope.launch {
                        delay(1000)
                        if (rateLimitRemaining > 0) {
                            rateLimitRemaining -= 1
                        }
                        responseOutput = simulateNestJsApiResponse(
                            selectedEndpointIndex,
                            inputParamValue1,
                            inputParamValue2
                        )
                        isSending = false
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (isSending) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("EXECUTING HANDSHAKE SECURE ROUTE...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Send, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SEND API REQUEST", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        // API HTTP Output Terminal Console
        Column {
            Text(
                text = "NestJS HTTP Server Console Log Output:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isSending) Color.Yellow else Color(0xFF00C853)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "STATUS: ${if (isSending) "PENDING" else "SUCCESS (200 OK)"}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSending) Color.Yellow else Color(0xFF00C853),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "application/json",
                            fontSize = 8.sp,
                            color = Color.LightGray,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = responseOutput,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF81C784),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

data class ApiEndpointInfo(
    val method: String,
    val path: String,
    val description: String,
    val parameters: List<String>,
    val defaultValues: List<String> = emptyList()
)

fun simulateNestJsApiResponse(index: Int, val1: String, val2: String): String {
    return when (index) {
        0 -> """
{
  "statusCode": 200,
  "message": "JWT session handshake created successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c3ItZGlzdC0wMiIsImVtYWlsIjoiZGlzdHJpYnV0b3JAc3VyeWFjcmVkaXQuY29tIiwicGhvbmVOdW1iZXIiOiIkdmFsMSIsInJvbGUiOiJESVNUUklCVVRPUiJ9",
    "expiresIn": "28800s (8 Hours)",
    "tokenType": "Bearer",
    "user": {
      "id": "usr-dist-02",
      "fullName": "Ramesh Pai Distributors",
      "email": "distributor@suryacredit.com",
      "phoneNumber": "$val1",
      "role": "DISTRIBUTOR",
      "securityToken": "SIGNATURE_VERIFIED"
    }
  }
}
        """.trimIndent()
        1 -> """
{
  "statusCode": 200,
  "message": "Account balances successfully loaded",
  "data": {
    "userId": "$val1",
    "walletBalance": 250000.00,
    "commissionEarned": 18450.00,
    "creditLimit": 500000.00,
    "usedCredit": 50000.00,
    "availableCredit": 450000.00,
    "settlementCycle": "T+0 Realtime",
    "complianceStatus": "KYC_VERIFIED"
  }
}
        """.trimIndent()
        2 -> {
            val amt = val1.toDoubleOrNull() ?: 1000.0
            val service = if (val2.isNotBlank()) val2 else "DMT_TRANSFER"
            val serviceCost = amt / 1.18
            val gst = amt - serviceCost
            """
{
  "statusCode": 200,
  "message": "Trade transaction successfully executed & auto-ledgered",
  "data": {
    "transactionId": "txn-${System.currentTimeMillis().toString().takeLast(6)}",
    "referenceId": "RRN${(100000000..999999999).random()}",
    "service": "$service",
    "debitedAmount": $amt,
    "charges": {
      "baseCost": ${String.format("%.2f", serviceCost)},
      "cgst": ${String.format("%.2f", gst / 2)},
      "sgst": ${String.format("%.2f", gst / 2)},
      "commissionSplit": ${String.format("%.2f", amt * 0.01)}
    },
    "routingGateway": "NPCI_IMPS_BANK_NODE",
    "responseCode": "00_SUCCESS",
    "timestamp": "${java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date())}"
  }
}
            """.trimIndent()
        }
        3 -> {
            val rAmt = val1.toDoubleOrNull() ?: 500.0
            """
{
  "statusCode": 200,
  "message": "B2B outstanding credit limits auto-repaid successfully",
  "data": {
    "settlementId": "set-${System.currentTimeMillis().toString().takeLast(6)}",
    "referenceId": "RRN${(100000000..999999999).random()}",
    "amountPaid": $rAmt,
    "remainingOutstanding": ${String.format("%.2f", 50000.0 - rAmt)},
    "restoredCreditBuffer": ${String.format("%.2f", 450000.0 + rAmt)},
    "repaymentTimestamp": "${java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date())}"
  }
}
            """.trimIndent()
        }
        else -> """
[
  {
    "id": "txn-101",
    "type": "COMMISSION",
    "service": "AEPS",
    "amount": 450.00,
    "description": "AEPS cash withdraw route commission share",
    "referenceId": "REF828103982",
    "status": "SUCCESS"
  },
  {
    "id": "txn-102",
    "type": "DEBIT",
    "service": "DMT",
    "amount": 15000.00,
    "description": "IMPS Outward Money Transfer to SBI Account",
    "referenceId": "REF382710381",
    "status": "SUCCESS"
  }
]
        """.trimIndent()
    }
}

// ---------------- TAB 2: FLUTTER RIVERPOD STATE MACHINE LOGGER ----------------

@Composable
fun RiverpodStateTab() {
    var selectedRole by remember { mutableStateOf("RETAILER") }
    var notifierLogs by remember { mutableStateOf(listOf("Riverpod state engine initialization...")) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedRole) {
        notifierLogs = listOf(
            "Listener detected: AuthNotifier triggered.",
            "State transitions from: AsyncValue.data(null) -> AsyncValue.loading()",
            "Establishing OAuth handshake with NestJS /api/v1/auth/login...",
            "Encrypting 4-digit security PIN signature..."
        )
        delay(600)
        notifierLogs = notifierLogs + listOf(
            "HTTP Response Status: 200 OK (Handshake payload verified)",
            "JWT token cached in secure local storage: eyJhbGciOiJIUzI1NiJ9...",
            "Parsing roles inside User.fromJson()..."
        )
        delay(600)
        notifierLogs = notifierLogs + listOf(
            "State updated to: AsyncValue.data(User)",
            "Active Participant Role: $selectedRole",
            "Multi-Tenant parent link: ${if (selectedRole == "RETAILER") "usr-dist-02" else "null (independent node)"}",
            "Auth state fully synchronized. Rendering dashboards."
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column {
            Text(
                text = "Riverpod Auth State Flow (Flutter Client)",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color(0xFFFF8C00)
            )
            Text(
                text = "Simulate role based auth state shifts using Flutter Riverpod providers.",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        // Toggle Roles buttons
        Column {
            Text(
                text = "Toggle Active Merchant Role:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("RETAILER", "DISTRIBUTOR", "SUPER_ADMIN").forEach { role ->
                    val isSel = selectedRole == role
                    Surface(
                        onClick = { selectedRole = role },
                        color = if (isSel) Color(0xFFFF8C00) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isSel) Color(0xFFFF8C00) else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = role,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // State diagram
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "State Visualizer: StateNotifierProvider<AuthNotifier, AuthState>",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    StateNodeBox("Unauthenticated", isHighlighted = false)
                    Icon(Icons.Default.ArrowForward, null, tint = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))
                    StateNodeBox("Loading Async", isHighlighted = notifierLogs.size < 6)
                    Icon(Icons.Default.ArrowForward, null, tint = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))
                    StateNodeBox("Success: $selectedRole", isHighlighted = notifierLogs.size >= 6)
                }
            }
        }

        // Riverpod Console logs
        Column {
            Text(
                text = "Riverpod Core Engine Logs (Flutter SDK):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F0F))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    notifierLogs.forEach { log ->
                        Text(
                            text = "► $log",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = if (log.contains("Success") || log.contains("fully")) Color(0xFF00FFCC) else Color(0xFF66FF66),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Code Preview block
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, null, tint = Color(0xFFFF8C00), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "lib/providers/auth_provider.dart",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
class AuthNotifier extends StateNotifier<AuthState> {
  Future<void> loginWithMpin(String phone, String pin) async {
    state = state.copyWith(user: const AsyncValue.loading());
    try {
      final user = await authRepo.login(phone, pin);
      state = state.copyWith(user: AsyncValue.data(user));
    } catch (err, stack) {
      state = state.copyWith(user: AsyncValue.error(err, stack));
    }
  }
}
                    """.trimIndent(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StateNodeBox(label: String, isHighlighted: Boolean) {
    Surface(
        color = if (isHighlighted) Color(0xFFFF8C00) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, if (isHighlighted) Color(0xFFFF8C00) else Color.LightGray)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlighted) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

// ---------------- TAB 3: PRISMA SCHEMA & DOCKER COMPOSE ----------------

@Composable
fun PrismaAndDockerTab() {
    var viewSelection by remember { mutableStateOf(0) } // 0 = Prisma, 1 = Docker, 2 = Swagger Docs Specs
    val clipboardManager = LocalClipboardManager.current

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column {
            Text(
                text = "Database Models & Container Layers",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color(0xFFFF8C00)
            )
            Text(
                text = "Review production-grade Docker environments and PostgreSQL relational models.",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        // Toggle Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Prisma Schema", "Docker Compose", "NestJS Configs").forEachIndexed { idx, item ->
                val isSel = viewSelection == idx
                Surface(
                    onClick = { viewSelection = idx },
                    color = if (isSel) Color(0xFFFF8C00) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isSel) Color(0xFFFF8C00) else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = item,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Code Area
        val selectedCode = when (viewSelection) {
            0 -> """
// prisma/schema.prisma
datasource db {
  provider = "postgresql"
  url      = env("DATABASE_URL")
}

generator client {
  provider = "prisma-client-js"
}

enum SuryaRole {
  SUPER_ADMIN
  ADMIN
  STATE_HEAD
  DISTRICT_DISTRIBUTOR
  MASTER_DISTRIBUTOR
  DISTRIBUTOR
  RETAILER
  VENDOR
  EMPLOYEE
  CUSTOMER
  SUPPORT_EXECUTIVE
  FINANCE_TEAM
  AUDITOR
}

model User {
  id           String      @id @default(uuid())
  email        String      @unique
  phoneNumber  String      @unique
  fullName     String
  role         SuryaRole   @default(RETAILER)
  isActive     Boolean     @default(true)
  parentId     String?
  createdAt    DateTime    @default(now())
}
            """.trimIndent()
            1 -> """
# docker-compose.yml
version: '3.8'

services:
  api:
    build: .
    ports:
      - "3000:3000"
    environment:
      - DATABASE_URL=postgresql://surya_admin:SuryaPassWord2026@postgres:5432/surya_db
      - JWT_SECRET=SURYA_CREDIT_SECURE_KEY_2026
      - REDIS_URL=redis://redis:6379
    depends_on:
      - postgres
      - redis

  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_USER=surya_admin
      - POSTGRES_PASSWORD=SuryaPassWord2026
      - POSTGRES_DB=surya_db
    volumes:
      - pgdata:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
            """.trimIndent()
            else -> """
// NestJS environment properties
JWT_SECRET=SURYA_CREDIT_SECURE_KEY_2026
JWT_EXPIRATION_TIME=8h
RATE_LIMIT_MAX_ATTEMPTS=100
RATE_LIMIT_WINDOW_SECONDS=60
REDIS_SESSION_STORE=true
REDIS_SESSION_TTL_SECONDS=28800
POSTGRES_MAX_CONNECTION_POOL=20
            """.trimIndent()
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (viewSelection == 0) "Prisma Schema Definition" else if (viewSelection == 1) "Docker Container Stack" else "B2B Production Specs",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    IconButton(
                        onClick = { clipboardManager.setText(AnnotatedString(selectedCode)) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedCode,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFF81C784),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
