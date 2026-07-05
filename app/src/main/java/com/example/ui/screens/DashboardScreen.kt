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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.data.WalletState
import com.example.ui.AppViewModel
import com.example.ui.SuryaRole
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val walletState by viewModel.walletState.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var showLoadWalletDialog by remember { mutableStateOf(false) }
    var showRequestCreditDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var showEmiDialog by remember { mutableStateOf(false) }
    var showBankSettleDialog by remember { mutableStateOf(false) }
    var showQrScanDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }

    var loadAmount by remember { mutableStateOf("10000") }
    var creditAmount by remember { mutableStateOf("1500000") }

    // Bank Settlement States
    var settleBeneficiary by remember { mutableStateOf("Ramesh Pai Proprietorship") }
    var settleBankName by remember { mutableStateOf("HDFC Bank") }
    var settleAccountNo by remember { mutableStateOf("50100239081284") }
    var settleIfsc by remember { mutableStateOf("HDFC0000009") }
    var settleAmount by remember { mutableStateOf("25000") }

    // QR Payment States
    var qrAmount by remember { mutableStateOf("1250") }
    var qrMpin by remember { mutableStateOf("") }
    var qrScannerPhase by remember { mutableStateOf("SCANNING") } // SCANNING, FORM

    // Multi-Wallet balances
    val rewardPoints = 12450.0
    val cashbackBalance = 3520.0

    // Transfer State
    var transferTargetPhone by remember { mutableStateOf("") }
    var transferAmount by remember { mutableStateOf("") }
    var transferSourceWallet by remember { mutableStateOf("Main") } // Main, Reward, Cashback

    // Simulated Outstanding B2B EMIs
    val emiList = remember {
        mutableStateListOf(
            B2B_EMI("EMI-3901-ATM", "Surya Smart mATM Devices (12 Months)", 12400.0, "2026-07-15", "PENDING"),
            B2B_EMI("EMI-8422-KSC", "Retailer Digital Kiosk Lease (6 Months)", 4900.0, "2026-07-20", "PENDING"),
            B2B_EMI("EMI-1049-PRT", "Surya High-Speed thermal printers batch", 8150.0, "2026-08-01", "PENDING")
        )
    }

    // Selected Wallet in Card Carousel
    var selectedWalletCarousel by remember { mutableStateOf("MAIN") } // MAIN, CREDIT, REWARDS, CASHBACK

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Upper Golden-Orange Solar Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF8C00), // Solar Gold Dark Orange
                            Color(0xFFFFB300)  // Warm Amber Yellow
                        )
                    )
                )
                .padding(top = 16.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Surya Credit Solutions",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "B2B FinTech B2B Marketplace & Super App",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    // Role Chip Selector Button
                    var expandedRoleMenu by remember { mutableStateOf(false) }
                    Box {
                        Button(
                            onClick = { expandedRoleMenu = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.25f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("role_select_button").height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwitchAccount,
                                contentDescription = "Switch views",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentRole.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedRoleMenu,
                            onDismissRequest = { expandedRoleMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            SuryaRole.values().forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = role.label,
                                                fontWeight = FontWeight.Bold,
                                                color = if (currentRole == role) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = role.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.setRole(role)
                                        expandedRoleMenu = false
                                    }
                               )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Enterprise Multi-Wallet Hub Carousel Segmented Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val wallets = listOf(
                        Triple("MAIN", "Main Wallet", Icons.Default.AccountBalanceWallet),
                        Triple("CREDIT", "B2B Credit Limit", Icons.Default.CreditCard),
                        Triple("REWARDS", "Reward Wallet", Icons.Default.EmojiEvents),
                        Triple("CASHBACK", "Cashback Ledger", Icons.Default.PriceCheck)
                    )

                    wallets.forEach { (key, title, ic) ->
                        val isSelected = selectedWalletCarousel == key
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .width(130.dp)
                                .clickable { selectedWalletCarousel = key }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Gray else Color.White
                                    )
                                    Icon(
                                        imageVector = ic,
                                        contentDescription = null,
                                        tint = if (isSelected) Color(0xFFFF8C00) else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                val valString = when(key) {
                                    "MAIN" -> "₹${String.format("%,.0f", walletState?.balance ?: 0.0)}"
                                    "CREDIT" -> "₹${String.format("%,.0f", (walletState?.creditLimit ?: 0.0) - (walletState?.usedCredit ?: 0.0))}"
                                    "REWARDS" -> "₹${String.format("%,.0f", rewardPoints)}"
                                    "CASHBACK" -> "₹${String.format("%,.0f", cashbackBalance)}"
                                    else -> "₹0.00"
                                }

                                Text(
                                    text = valString,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color(0xFF2E3E5C) else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Wallet Quick-Operations Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showLoadWalletDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f).height(32.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color(0xFFFF8C00), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Funds", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8C00))
                    }

                    Button(
                        onClick = { showTransferDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1.1f).height(32.dp)
                    ) {
                        Icon(Icons.Default.SwapHoriz, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Wallet Transfer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { showEmiDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1.2f).height(32.dp)
                    ) {
                        Icon(Icons.Default.HourglassBottom, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("EMIs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Wallet Quick-Operations Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showBankSettleDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1.2f).height(32.dp)
                    ) {
                        Icon(Icons.Default.AccountBalance, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bank Settle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { showQrScanDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.weight(1f).height(32.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("QR Pay", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Dashboard Body content according to Active Role
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dynamic Role Header banner
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Super App Workspace: ${currentRole.label}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Role specific metrics/actions
            when (currentRole) {
                SuryaRole.SUPER_ADMIN, SuryaRole.ADMIN -> {
                    item { AdminMetrics(walletState, viewModel) }
                }
                SuryaRole.STATE_HEAD, SuryaRole.DISTRICT_DISTRIBUTOR, SuryaRole.MASTER_DISTRIBUTOR, SuryaRole.DISTRIBUTOR -> {
                    item { DistributorMetrics(walletState, onManageCredit = { showRequestCreditDialog = true }) }
                }
                SuryaRole.RETAILER -> {
                    item { RetailerMetrics(walletState) }
                }
                SuryaRole.VENDOR -> {
                    item { VendorMetrics() }
                }
                SuryaRole.CUSTOMER -> {
                    item { CustomerMetrics(walletState) }
                }
                SuryaRole.EMPLOYEE, SuryaRole.SUPPORT_EXECUTIVE, SuryaRole.FINANCE_TEAM, SuryaRole.AUDITOR -> {
                    item { EmployeeMetrics() }
                }
            }

            // Transaction Ledger Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "National Settlement Ledger",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    if (isExporting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color(0xFFFF8C00))
                    } else {
                        TextButton(
                            onClick = {
                                isExporting = true
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(1200)
                                    isExporting = false
                                    viewModel.addNotification(
                                        title = "Statement Exported",
                                        message = "Your wallet ledger statement (PDF/Excel format) was successfully compiled and saved to storage.",
                                        type = "SUCCESS"
                                    )
                                    viewModel.showNotification("Statement PDF saved to downloads.")
                                }
                            }
                        ) {
                            Icon(Icons.Default.Download, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF8C00))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8C00))
                        }
                    }
                }
            }

            // Transaction List
            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No recorded ledger activities.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                items(transactions) { txn ->
                    LedgerItemRow(txn)
                }
            }
        }
    }

    // 1. ADD FUNDS DIALOGUE
    if (showLoadWalletDialog) {
        AlertDialog(
            onDismissRequest = { showLoadWalletDialog = false },
            title = { Text("Load Wallet Instantly") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Top up your master wallet instantly via secure UPI / QR Code scan gateways.")
                    OutlinedTextField(
                        value = loadAmount,
                        onValueChange = { loadAmount = it },
                        label = { Text("Amount (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = loadAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.loadWallet(amt)
                        }
                        showLoadWalletDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
                ) {
                    Text("Load ₹$loadAmount")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoadWalletDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. CREDIT INCREMENT DIALOGUE
    if (showRequestCreditDialog) {
        AlertDialog(
            onDismissRequest = { showRequestCreditDialog = false },
            title = { Text("B2B Credit Line Assessment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Apply for instant merchant credit line increase powered by Surya Credit rating logic.")
                    OutlinedTextField(
                        value = creditAmount,
                        onValueChange = { creditAmount = it },
                        label = { Text("Target Limit (₹)") },
                        leadingIcon = { Icon(Icons.Default.CreditCard, null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = creditAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.requestCreditIncrease(amt)
                        }
                        showRequestCreditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                ) {
                    Text("Submit Application")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRequestCreditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. WALLET-TO-WALLET MONEY TRANSFER DIALOGUE
    if (showTransferDialog) {
        AlertDialog(
            onDismissRequest = { showTransferDialog = false },
            title = { Text("W2W Instant Merchant Transfer") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Transfer money instantly from your Surya wallets to another registered retail distributor.", fontSize = 11.sp, color = Color.Gray)

                    // Wallet selector
                    Text("Select Funding Wallet:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Main", "Reward", "Cashback").forEach { w ->
                            FilterChip(
                                selected = transferSourceWallet == w,
                                onClick = { transferSourceWallet = w },
                                label = { Text(w, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = transferTargetPhone,
                        onValueChange = { transferTargetPhone = it },
                        label = { Text("Recipient Phone / UPI VPA") },
                        placeholder = { Text("e.g. 9876543210@surya") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = transferAmount,
                        onValueChange = { transferAmount = it },
                        label = { Text("Amount (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = transferAmount.toDoubleOrNull() ?: 0.0
                        if (transferTargetPhone.isNotBlank() && amt > 0) {
                            viewModel.executeServiceTxn(
                                service = "DMT",
                                amount = amt,
                                description = "W2W Merchant transfer to $transferTargetPhone from $transferSourceWallet wallet",
                                paymentMethod = "WALLET"
                            )
                            showTransferDialog = false
                            viewModel.addNotification(
                                title = "W2W Transfer Successful",
                                message = "Transferred ₹${String.format("%,.2f", amt)} to $transferTargetPhone instantly.",
                                type = "SUCCESS"
                            )
                        } else {
                            viewModel.showNotification("Please provide target phone and valid amount.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
                ) {
                    Text("Authorize W2W Transfer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTransferDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. EMIs AND CREDIT REPAYMENT DIALOGUE
    if (showEmiDialog) {
        AlertDialog(
            onDismissRequest = { showEmiDialog = false },
            title = { Text("Credit Line EMIs & Paybacks") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Outstanding procurement hardware financing repayments:", fontSize = 11.sp, color = Color.Gray)

                    if (emiList.isEmpty()) {
                        Text("No outstanding credit loans! You are fully paid.", fontWeight = FontWeight.Bold, color = Color(0xFF00C853))
                    } else {
                        emiList.forEach { emi ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = emi.item, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(text = "Due: ${emi.dueDate} • Code: ${emi.code}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Button(
                                        onClick = {
                                            viewModel.executeServiceTxn(
                                                service = "CREDIT_PAY",
                                                amount = emi.amount,
                                                description = "Repaid outstanding procurement EMI: ${emi.item}",
                                                paymentMethod = "WALLET"
                                            )
                                            viewModel.addNotification(
                                                title = "Credit EMI Settled",
                                                message = "Your outstanding EMI of ₹${String.format("%,.2f", emi.amount)} is settled.",
                                                type = "SUCCESS"
                                            )
                                            emiList.remove(emi)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("PAY ₹${emi.amount.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEmiDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // 5. BANK NODAL SETTLEMENT DIALOGUE
    if (showBankSettleDialog) {
        AlertDialog(
            onDismissRequest = { showBankSettleDialog = false },
            title = { Text("Bank Nodal Payout Settlement") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Settle your wallet balance into your linked company bank account.", fontSize = 11.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = settleBeneficiary,
                        onValueChange = { settleBeneficiary = it },
                        label = { Text("Beneficiary Name") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = settleBankName,
                        onValueChange = { settleBankName = it },
                        label = { Text("Bank Name") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = settleAccountNo,
                        onValueChange = { settleAccountNo = it },
                        label = { Text("Account Number") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = settleIfsc,
                        onValueChange = { settleIfsc = it },
                        label = { Text("IFSC Code") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = settleAmount,
                        onValueChange = { settleAmount = it },
                        label = { Text("Settlement Amount (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = settleAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0 && settleAccountNo.isNotBlank()) {
                            viewModel.executeServiceTxn(
                                service = "SETTLEMENT",
                                amount = amt,
                                description = "Outward Bank Settlement payout to $settleBankName Acc: ****${settleAccountNo.takeLast(4)}",
                                paymentMethod = "WALLET"
                            )
                            viewModel.addNotification(
                                title = "Settlement Initiated",
                                message = "Nodal payout of ₹${String.format("%,.2f", amt)} routed successfully to $settleBankName.",
                                type = "SUCCESS"
                            )
                            showBankSettleDialog = false
                        } else {
                            viewModel.showNotification("Please enter a valid amount and account number.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
                ) {
                    Text("Settle to Bank")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBankSettleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 6. QR SCANNER & MPIN DIALOGUE
    if (showQrScanDialog) {
        AlertDialog(
            onDismissRequest = { 
                showQrScanDialog = false 
                qrScannerPhase = "SCANNING"
                qrMpin = ""
            },
            title = { Text(if (qrScannerPhase == "SCANNING") "UPI QR Scanner" else "Authorize QR Payment") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (qrScannerPhase == "SCANNING") {
                        Text("Point your camera at any UPI or Surya partner merchant QR code.", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
                        
                        // Scanner view simulator
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .border(3.dp, Color(0xFFFF8C00), RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Scanner animated bar simulation
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .background(Color(0xFF00C853))
                                    .align(Alignment.Center)
                            )
                            Icon(Icons.Default.QrCodeScanner, null, tint = Color.Gray.copy(alpha = 0.3f), modifier = Modifier.size(80.dp))
                        }
                        
                        Button(
                            onClick = { qrScannerPhase = "FORM" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
                        ) {
                            Text("Simulate Scan Match")
                        }
                    } else if (qrScannerPhase == "FORM") {
                        Text("Recipient: Surya Retailer Kiosk Hub", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("UPI ID: suryakiosk@ybl", fontSize = 11.sp, color = Color.Gray)
                        
                        OutlinedTextField(
                            value = qrAmount,
                            onValueChange = { qrAmount = it },
                            label = { Text("Payment Amount (₹)") },
                            leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        OutlinedTextField(
                            value = qrMpin,
                            onValueChange = { qrMpin = it },
                            label = { Text("4-Digit Secure MPIN") },
                            leadingIcon = { Icon(Icons.Default.Lock, null) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (qrScannerPhase == "FORM") {
                    Button(
                        onClick = {
                            val amt = qrAmount.toDoubleOrNull() ?: 0.0
                            if (amt > 0 && qrMpin.length == 4) {
                                viewModel.executeServiceTxn(
                                    service = "QR_PAY",
                                    amount = amt,
                                    description = "UPI QR Scan payment to suryakiosk@ybl",
                                    paymentMethod = "WALLET"
                                )
                                viewModel.addNotification(
                                    title = "QR Payment Successful",
                                    message = "Paid ₹${String.format("%,.2f", amt)} to Surya Retailer Kiosk Hub.",
                                    type = "SUCCESS"
                                )
                                showQrScanDialog = false
                                qrScannerPhase = "SCANNING"
                                qrMpin = ""
                            } else {
                                viewModel.showNotification("Please enter a valid amount and 4-digit MPIN.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                    ) {
                        Text("Pay Securely")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showQrScanDialog = false 
                    qrScannerPhase = "SCANNING"
                    qrMpin = ""
                }) {
                    Text("Close")
                }
            }
        )
    }
}

data class B2B_EMI(
    val code: String,
    val item: String,
    val amount: Double,
    val dueDate: String,
    val status: String
)

// Stats Components according to roles
@Composable
fun VendorMetrics() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "B2B Supply Catalog Metrics",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total SKUs", fontSize = 12.sp, color = Color.Gray)
                    Text("184 Items", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Column {
                    Text("Pending Orders", fontSize = 12.sp, color = Color.Gray)
                    Text("12 Bulk Orders", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFFF8C00))
                }
                Column {
                    Text("Slab Discounts", fontSize = 12.sp, color = Color.Gray)
                    Text("Diamond Tier", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun SuperDistributorMetrics(walletState: WalletState?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Aggregate Network Volume",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Distributors", fontSize = 12.sp, color = Color.Gray)
                    Text("24", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Column {
                    Text("Active Retailers", fontSize = 12.sp, color = Color.Gray)
                    Text("4,150", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Column {
                    Text("Network Commission", fontSize = 12.sp, color = Color.Gray)
                    Text("₹${String.format("%,.2f", walletState?.commissionEarned ?: 0.0)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF00C853))
                }
            }
        }
    }
}

@Composable
fun DistributorMetrics(walletState: WalletState?, onManageCredit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Distributor Allocations",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = onManageCredit) {
                    Text("Request Funding limit", fontSize = 11.sp, color = Color(0xFFFF8C00))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Merchant Float", fontSize = 11.sp, color = Color.Gray)
                    Text("₹3,54,000", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column {
                    Text("Retailers Linked", fontSize = 11.sp, color = Color.Gray)
                    Text("186 Retailers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column {
                    Text("Used Credit Buffer", fontSize = 11.sp, color = Color.Gray)
                    Text("₹${String.format("%,.2f", walletState?.usedCredit ?: 0.0)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun RetailerMetrics(walletState: WalletState?) {
    walletState?.let { state ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("COMMISSION EARNED", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${String.format("%,.2f", state.commissionEarned)}", fontWeight = FontWeight.Bold, color = Color(0xFF00C853))
                }
            }
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("CASHBACK REWARDS", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${String.format("%,.2f", state.cashbackEarned)}", fontWeight = FontWeight.Bold, color = Color(0xFF00C853))
                }
            }
        }
    }
}

@Composable
fun CustomerMetrics(walletState: WalletState?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "My Personal Credit Line & Benefits",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Used Pay Later", fontSize = 11.sp, color = Color.Gray)
                    Text("₹${walletState?.usedCredit?.let { String.format("%,.2f", it) } ?: "0.00"}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Red)
                }
                Column {
                    Text("Cashback Balance", fontSize = 11.sp, color = Color.Gray)
                    Text("₹${walletState?.cashbackEarned?.let { String.format("%,.2f", it) } ?: "0.00"}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF00C853))
                }
                Column {
                    Text("Credit Health", fontSize = 11.sp, color = Color.Gray)
                    Text("Excellent (785)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF00C853))
                }
            }
        }
    }
}

@Composable
fun EmployeeMetrics() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Employee KYC & Risk Action Center",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("KYC Pending", fontSize = 11.sp, color = Color.Gray)
                    Text("14 Retailers", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFFFB300))
                }
                Column {
                    Text("DMT Alerts", fontSize = 11.sp, color = Color.Gray)
                    Text("0 High Risk", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF00C853))
                }
                Column {
                    Text("Audit Status", fontSize = 11.sp, color = Color.Gray)
                    Text("Compliant", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF00C853))
                }
            }
        }
    }
}

// ---------------- ENTERPRISE ADMIN COMMAND CENTER ----------------
@Composable
fun AdminMetrics(walletState: WalletState?, viewModel: AppViewModel) {
    var backupState by remember { mutableStateOf("IDLE") } // IDLE, RUNNING, COMPLETED
    var approvalCount by remember { mutableStateOf(3) }
    var oemSubmissionCount by remember { mutableStateOf(2) }

    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "System Administration Panel",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Color(0xFFFF8C00)
            )

            // System Health Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("GTV National Volume", fontSize = 11.sp, color = Color.Gray)
                    Text("₹18.52 Cr", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Text("Prisma API Latency", fontSize = 11.sp, color = Color.Gray)
                    Text("14ms (Healthy)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF00C853))
                }
                Column {
                    Text("K8s Cluster Node", fontSize = 11.sp, color = Color.Gray)
                    Text("4/4 Online", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF00C853))
                }
            }

            Divider()

            // Approval Queues Section
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // KYC Approvals Queue
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("KYC Verification", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$approvalCount Pending", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        if (approvalCount > 0) {
                            Button(
                                onClick = {
                                    approvalCount--
                                    viewModel.showNotification("KYC dossier approved successfully!")
                                },
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("APPROVE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("Clear ✓", fontSize = 11.sp, color = Color(0xFF00C853))
                        }
                    }
                }

                // OEM Product Submission Queue
                Card(
                    modifier = Modifier.weight(1.1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("OEM SKU Submissions", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("$oemSubmissionCount Pending", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        if (oemSubmissionCount > 0) {
                            Button(
                                onClick = {
                                    oemSubmissionCount--
                                    viewModel.showNotification("New SKU published to active catalogue.")
                                },
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("PUBLISH SKU", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("Approved ✓", fontSize = 11.sp, color = Color(0xFF00C853))
                        }
                    }
                }
            }

            Divider()

            // Automated Database Backup Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Automated DR Backup", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text(
                        text = if (backupState == "RUNNING") "Postgres binary backup active..." else "Postgres Database: 100% Intact",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                if (backupState == "RUNNING") {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color(0xFFFF8C00))
                } else {
                    Button(
                        onClick = {
                            backupState = "RUNNING"
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(1200)
                                backupState = "COMPLETED"
                                viewModel.showNotification("Prisma PostgreSQL snapshot compiled to Docker volume.")
                                viewModel.addNotification(
                                    title = "Database Backup Created",
                                    message = "Disaster Recovery backup complete. Snapshots pushed safely to persistent volume mounts.",
                                    type = "SUCCESS"
                                )
                            }
                        },
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Backup, null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RUN BACKUP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LedgerItemRow(txn: Transaction) {
    val containerColor = when (txn.type) {
        "CREDIT" -> Color(0xFFE8F5E9)
        "DEBIT" -> Color(0xFFFFEBEE)
        "COMMISSION" -> Color(0xFFFFF8E1)
        "CASHBACK" -> Color(0xFFE0F7FA)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val icon = when (txn.service) {
        "RECHARGE" -> Icons.Default.PhoneAndroid
        "BBPS" -> Icons.Default.Receipt
        "AEPS" -> Icons.Default.Fingerprint
        "DMT" -> Icons.Default.SwapHoriz
        "CREDIT_PAY" -> Icons.Default.CreditCard
        "QR_PAY" -> Icons.Default.QrCodeScanner
        "ORDER" -> Icons.Default.ShoppingBag
        else -> Icons.Default.AccountBalanceWallet
    }

    val typeColor = when (txn.type) {
        "CREDIT" -> Color(0xFF2E7D32)
        "DEBIT" -> Color(0xFFC62828)
        "COMMISSION" -> Color(0xFFEF6C00)
        "CASHBACK" -> Color(0xFF00838F)
        else -> MaterialTheme.colorScheme.onSurface
    }

    val sign = when (txn.type) {
        "CREDIT" -> "+"
        "DEBIT" -> "-"
        "COMMISSION" -> "+Comm "
        "CASHBACK" -> "+Cb "
        else -> ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    .clip(CircleShape)
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = txn.description,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ref: ${txn.referenceId}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (txn.status == "SUCCESS") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = txn.status,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (txn.status == "SUCCESS") Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$sign₹${String.format("%,.2f", txn.amount)}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = typeColor
            )
        }
    }
}

