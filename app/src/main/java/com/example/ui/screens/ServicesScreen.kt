package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface ServiceType {
    val title: String
    val icon: ImageVector
    val color: Color

    object Recharge : ServiceType {
        override val title = "Recharge"
        override val icon = Icons.Default.PhoneAndroid
        override val color = Color(0xFF1E88E5)
    }

    object BBPS : ServiceType {
        override val title = "Bharat Bill Pay (BBPS)"
        override val icon = Icons.Default.Receipt
        override val color = Color(0xFF00ACC1)
    }

    object AEPS : ServiceType {
        override val title = "Aadhaar Pay (AEPS)"
        override val icon = Icons.Default.Fingerprint
        override val color = Color(0xFF43A047)
    }

    object DMT : ServiceType {
        override val title = "Money Transfer (DMT)"
        override val icon = Icons.Default.SwapHoriz
        override val color = Color(0xFF8E24AA)
    }

    object GST : ServiceType {
        override val title = "GST Billing"
        override val icon = Icons.Default.Description
        override val color = Color(0xFFE53935)
    }

    object Loans : ServiceType {
        override val title = "B2B Credit Loans"
        override val icon = Icons.Default.MonetizationOn
        override val color = Color(0xFFFB8C00)
    }

    object PAN : ServiceType {
        override val title = "PAN Services"
        override val icon = Icons.Default.Assignment
        override val color = Color(0xFF00796B)
    }

    object Travel : ServiceType {
        override val title = "Travel Booking"
        override val icon = Icons.Default.Flight
        override val color = Color(0xFF7B1FA2)
    }

    object Insurance : ServiceType {
        override val title = "Insurance"
        override val icon = Icons.Default.Security
        override val color = Color(0xFFC2185B)
    }

    object AdminPanel : ServiceType {
        override val title = "Admin & Control"
        override val icon = Icons.Default.Settings
        override val color = Color(0xFF455A64)
    }
}

data class ServiceReceipt(
    val title: String,
    val referenceId: String,
    val date: String = "2026-07-02 11:28:17",
    val status: String = "SUCCESS",
    val amount: Double,
    val paymentMethod: String,
    val details: Map<String, String>,
    val commission: Double,
    val cashback: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    var activeService by remember { mutableStateOf<ServiceType>(ServiceType.Recharge) }
    val coroutineScope = rememberCoroutineScope()

    // Input States
    var mobileNumber by remember { mutableStateOf("") }
    var operatorSelected by remember { mutableStateOf("JIO Unlimited 5G") }
    var rechargeAmount by remember { mutableStateOf("299") }

    var bbpsCategory by remember { mutableStateOf("Electricity") }
    var bbpsBiller by remember { mutableStateOf("BESCOM Karnataka") }
    var bbpsConsumerNo by remember { mutableStateOf("") }
    var bbpsAmount by remember { mutableStateOf("1250") }

    var aepsAadhaarNo by remember { mutableStateOf("") }
    var aepsBank by remember { mutableStateOf("State Bank of India") }
    var aepsAmount by remember { mutableStateOf("5000") }
    var aepsScanningFinger by remember { mutableStateOf(false) }

    var dmtBeneficiary by remember { mutableStateOf("") }
    var dmtBankAcc by remember { mutableStateOf("") }
    var dmtIfsc by remember { mutableStateOf("") }
    var dmtAmount by remember { mutableStateOf("10000") }

    var gstBusiness by remember { mutableStateOf("") }
    var gstProductItem by remember { mutableStateOf("Surya POS Thermal Printer") }
    var gstItemQty by remember { mutableStateOf("1") }
    var gstItemPrice by remember { mutableStateOf("2499") }
    var gstTaxRate by remember { mutableStateOf("18") } // %

    var loanTerm by remember { mutableStateOf("6 Months") }
    var loanAmountReq by remember { mutableStateOf("50000") }

    // PAN Card States
    var panApplicantName by remember { mutableStateOf("") }
    var panApplicationType by remember { mutableStateOf("New PAN Card (Form 49A)") }
    var panAadhaarNo by remember { mutableStateOf("") }
    var panContactNo by remember { mutableStateOf("") }
    var panDocUploaded by remember { mutableStateOf(false) }

    // Travel Booking States
    var travelCategory by remember { mutableStateOf("Flight") } // Flight, Bus, Hotel
    var travelSource by remember { mutableStateOf("Bangalore (BLR)") }
    var travelDestination by remember { mutableStateOf("Mumbai (BOM)") }
    var travelDate by remember { mutableStateOf("2026-07-15") }
    var travelGuests by remember { mutableStateOf("1 Traveller") }
    var travelClassSelection by remember { mutableStateOf("Economy Class") }
    var travelBookingAmount by remember { mutableStateOf("5500") }

    // Insurance States
    var insuranceCategory by remember { mutableStateOf("Health Insurance") } // Health, Life, Motor, Shop
    var insurancePremiumYearly by remember { mutableStateOf("8400") }
    var insuranceApplicantAge by remember { mutableStateOf("32") }
    var insuranceSumAssured by remember { mutableStateOf("10,00,000") }
    var insuranceVehicleRegNo by remember { mutableStateOf("") }

    // Admin & Provider Configuration States
    var configRechargeProvider by remember { mutableStateOf("Surya Telecom Hub") }
    var configAepsGateway by remember { mutableStateOf("Yes Bank AEPS Engine v2") }
    var configDmtRoute by remember { mutableStateOf("Surya Nodal IMPS Channel") }
    var configPanPartner by remember { mutableStateOf("UTI Infrastructure Technology (UTIITSL)") }
    var configTravelAggregator by remember { mutableStateOf("Surya B2B Galileo API") }
    var configInsuranceBroker by remember { mutableStateOf("Surya Premium Insurance Desk") }
    var isRechargeEnabled by remember { mutableStateOf(true) }
    var isBbpsEnabled by remember { mutableStateOf(true) }
    var isAepsEnabled by remember { mutableStateOf(true) }
    var isDmtEnabled by remember { mutableStateOf(true) }
    var isPanEnabled by remember { mutableStateOf(true) }
    var isTravelEnabled by remember { mutableStateOf(true) }
    var isInsuranceEnabled by remember { mutableStateOf(true) }

    // Receipt State
    var latestReceipt by remember { mutableStateOf<ServiceReceipt?>(null) }
    var paymentMode by remember { mutableStateOf("WALLET") } // WALLET or CREDIT_LINE

    val walletState by viewModel.walletState.collectAsState()

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Left Column: Service Selection Bar
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            val list = listOf(
                ServiceType.Recharge,
                ServiceType.BBPS,
                ServiceType.AEPS,
                ServiceType.DMT,
                ServiceType.GST,
                ServiceType.Loans,
                ServiceType.PAN,
                ServiceType.Travel,
                ServiceType.Insurance,
                ServiceType.AdminPanel
            )

            list.forEach { service ->
                val isSelected = activeService == service
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeService = service }
                        .padding(vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) service.color else service.color.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = service.icon,
                            contentDescription = service.title,
                            tint = if (isSelected) Color.White else service.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = service.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        lineHeight = 12.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // Right Column: Dynamic Form Section
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = activeService.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = activeService.color
            )
            Text(
                text = "Configure transaction parameters below.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Payment Option (except for GST Bill & Loans which don't deduct Wallet the same way)
            if (activeService != ServiceType.GST && activeService != ServiceType.Loans) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Funding Mechanism",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { paymentMode = "WALLET" }
                            ) {
                                RadioButton(
                                    selected = paymentMode == "WALLET",
                                    onClick = { paymentMode = "WALLET" }
                                )
                                Text("Wallet Balance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { paymentMode = "CREDIT_LINE" }
                            ) {
                                RadioButton(
                                    selected = paymentMode == "CREDIT_LINE",
                                    onClick = { paymentMode = "CREDIT_LINE" }
                                )
                                Text("B2B Credit Line", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Forms
            when (activeService) {
                ServiceType.Recharge -> {
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text("Mobile Number (10 Digit)") },
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = operatorSelected,
                        onValueChange = { operatorSelected = it },
                        label = { Text("Telecom Circle & Operator") },
                        leadingIcon = { Icon(Icons.Default.CellTower, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = rechargeAmount,
                        onValueChange = { rechargeAmount = it },
                        label = { Text("Recharge Price (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }
                ServiceType.BBPS -> {
                    OutlinedTextField(
                        value = bbpsCategory,
                        onValueChange = { bbpsCategory = it },
                        label = { Text("Utility Category (Electricity/Water/LPG)") },
                        leadingIcon = { Icon(Icons.Default.ElectricalServices, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = bbpsBiller,
                        onValueChange = { bbpsBiller = it },
                        label = { Text("Biller Name") },
                        leadingIcon = { Icon(Icons.Default.Business, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = bbpsConsumerNo,
                        onValueChange = { bbpsConsumerNo = it },
                        label = { Text("Consumer Account Number") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = bbpsAmount,
                        onValueChange = { bbpsAmount = it },
                        label = { Text("Bill Amount (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }
                ServiceType.AEPS -> {
                    OutlinedTextField(
                        value = aepsAadhaarNo,
                        onValueChange = { aepsAadhaarNo = it },
                        label = { Text("Aadhaar Number (12 Digit)") },
                        leadingIcon = { Icon(Icons.Default.Fingerprint, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = aepsBank,
                        onValueChange = { aepsBank = it },
                        label = { Text("Linked Bank Account") },
                        leadingIcon = { Icon(Icons.Default.AccountBalance, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = aepsAmount,
                        onValueChange = { aepsAmount = it },
                        label = { Text("Withdrawal / Query Amount (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    // Biometric scanning state
                    if (aepsScanningFinger) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = Color(0xFF43A047), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Awaiting STQC biometric finger response from Mantra Micro-ATM...", fontSize = 11.sp, color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }
                ServiceType.DMT -> {
                    OutlinedTextField(
                        value = dmtBeneficiary,
                        onValueChange = { dmtBeneficiary = it },
                        label = { Text("Beneficiary Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = dmtBankAcc,
                        onValueChange = { dmtBankAcc = it },
                        label = { Text("Bank Account Number") },
                        leadingIcon = { Icon(Icons.Default.CreditCard, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = dmtIfsc,
                        onValueChange = { dmtIfsc = it },
                        label = { Text("Bank IFSC Code") },
                        leadingIcon = { Icon(Icons.Default.Key, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = dmtAmount,
                        onValueChange = { dmtAmount = it },
                        label = { Text("Transfer Amount (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }
                ServiceType.GST -> {
                    OutlinedTextField(
                        value = gstBusiness,
                        onValueChange = { gstBusiness = it },
                        label = { Text("Customer/Client Business Name") },
                        leadingIcon = { Icon(Icons.Default.Business, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = gstProductItem,
                        onValueChange = { gstProductItem = it },
                        label = { Text("Item / Service Billed") },
                        leadingIcon = { Icon(Icons.Default.Description, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = gstItemQty,
                            onValueChange = { gstItemQty = it },
                            label = { Text("Qty") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = gstItemPrice,
                            onValueChange = { gstItemPrice = it },
                            label = { Text("Rate (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(2f)
                        )
                        OutlinedTextField(
                            value = gstTaxRate,
                            onValueChange = { gstTaxRate = it },
                            label = { Text("GST %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                ServiceType.Loans -> {
                    OutlinedTextField(
                        value = loanAmountReq,
                        onValueChange = { loanAmountReq = it },
                        label = { Text("Requested Credit Financed (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = loanTerm,
                        onValueChange = { loanTerm = it },
                        label = { Text("Repayment Tenure") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }
                ServiceType.PAN -> {
                    OutlinedTextField(
                        value = panApplicantName,
                        onValueChange = { panApplicantName = it },
                        label = { Text("Applicant Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = panApplicationType,
                        onValueChange = { panApplicationType = it },
                        label = { Text("PAN Application Type") },
                        leadingIcon = { Icon(Icons.Default.Assignment, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = panAadhaarNo,
                        onValueChange = { panAadhaarNo = it },
                        label = { Text("Applicant Aadhaar Number") },
                        leadingIcon = { Icon(Icons.Default.Fingerprint, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = panContactNo,
                        onValueChange = { panContactNo = it },
                        label = { Text("Contact Number") },
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    Button(
                        onClick = { panDocUploaded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (panDocUploaded) Color(0xFF2E7D32) else Color(0xFF00796B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Icon(if (panDocUploaded) Icons.Default.CheckCircle else Icons.Default.CloudUpload, null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (panDocUploaded) "Documents Attached Successfully" else "Upload Aadhaar & Photo PDF (Max 2MB)", color = Color.White)
                    }
                }
                ServiceType.Travel -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = listOf("Flight", "Bus", "Hotel")
                        categories.forEach { cat ->
                            Button(
                                onClick = { 
                                    travelCategory = cat
                                    if (cat == "Flight") {
                                        travelBookingAmount = "5500"
                                    } else if (cat == "Bus") {
                                        travelBookingAmount = "850"
                                    } else {
                                        travelBookingAmount = "2400"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (travelCategory == cat) Color(0xFF7B1FA2) else Color(0xFF7B1FA2).copy(alpha = 0.15f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(cat, color = if (travelCategory == cat) Color.White else Color(0xFF7B1FA2), fontSize = 11.sp)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = travelSource,
                        onValueChange = { travelSource = it },
                        label = { Text(if (travelCategory == "Hotel") "City / Location" else "Source Station") },
                        leadingIcon = { Icon(Icons.Default.Place, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    if (travelCategory != "Hotel") {
                        OutlinedTextField(
                            value = travelDestination,
                            onValueChange = { travelDestination = it },
                            label = { Text("Destination Station") },
                            leadingIcon = { Icon(Icons.Default.Navigation, null) },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = travelDate,
                            onValueChange = { travelDate = it },
                            label = { Text("Journey Date") },
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = travelGuests,
                            onValueChange = { travelGuests = it },
                            label = { Text("Travellers") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = travelBookingAmount,
                        onValueChange = { travelBookingAmount = it },
                        label = { Text("Calculated Ticket Cost (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }
                ServiceType.Insurance -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val insCategories = listOf("Health", "Life", "Motor", "Shop")
                        insCategories.forEach { cat ->
                            Button(
                                onClick = { 
                                    insuranceCategory = "$cat Insurance"
                                    if (cat == "Health") {
                                        insurancePremiumYearly = "8400"
                                        insuranceSumAssured = "5,00,000"
                                    } else if (cat == "Life") {
                                        insurancePremiumYearly = "12000"
                                        insuranceSumAssured = "50,00,000"
                                    } else if (cat == "Motor") {
                                        insurancePremiumYearly = "3400"
                                        insuranceSumAssured = "2,50,000 IDV"
                                    } else {
                                        insurancePremiumYearly = "4800"
                                        insuranceSumAssured = "15,00,000"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (insuranceCategory.startsWith(cat)) Color(0xFFC2185B) else Color(0xFFC2185B).copy(alpha = 0.15f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(cat, color = if (insuranceCategory.startsWith(cat)) Color.White else Color(0xFFC2185B), fontSize = 10.sp)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = insuranceCategory,
                        onValueChange = { insuranceCategory = it },
                        label = { Text("Selected Plan") },
                        leadingIcon = { Icon(Icons.Default.Security, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = insuranceSumAssured,
                        onValueChange = { insuranceSumAssured = it },
                        label = { Text("Sum Assured Coverage (₹)") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    if (insuranceCategory.contains("Motor")) {
                        OutlinedTextField(
                            value = insuranceVehicleRegNo,
                            onValueChange = { insuranceVehicleRegNo = it },
                            label = { Text("Vehicle Registration No") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )
                    } else {
                        OutlinedTextField(
                            value = insuranceApplicantAge,
                            onValueChange = { insuranceApplicantAge = it },
                            label = { Text("Applicant Age (Years)") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )
                    }
                    OutlinedTextField(
                        value = insurancePremiumYearly,
                        onValueChange = { insurancePremiumYearly = it },
                        label = { Text("Annualized Premium (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }
                ServiceType.AdminPanel -> {
                    Text("API Routing Providers Configuration", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF455A64))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = configRechargeProvider,
                        onValueChange = { configRechargeProvider = it },
                        label = { Text("Recharge API Gateway") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = configAepsGateway,
                        onValueChange = { configAepsGateway = it },
                        label = { Text("AEPS Biometric Gateway") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = configDmtRoute,
                        onValueChange = { configDmtRoute = it },
                        label = { Text("DMT Remittance Switch") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = configPanPartner,
                        onValueChange = { configPanPartner = it },
                        label = { Text("PAN Service Operator") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    
                    Text("Service Status Enablers", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF455A64))
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Mobile Recharge Gateways", fontSize = 12.sp)
                        Switch(checked = isRechargeEnabled, onCheckedChange = { isRechargeEnabled = it })
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Bharat Bill Pay (BBPS)", fontSize = 12.sp)
                        Switch(checked = isBbpsEnabled, onCheckedChange = { isBbpsEnabled = it })
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Aadhaar Enabled Pay (AEPS)", fontSize = 12.sp)
                        Switch(checked = isAepsEnabled, onCheckedChange = { isAepsEnabled = it })
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("DMT Money Transfers", fontSize = 12.sp)
                        Switch(checked = isDmtEnabled, onCheckedChange = { isDmtEnabled = it })
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("PAN & Travel Portals", fontSize = 12.sp)
                        Switch(checked = isPanEnabled, onCheckedChange = { isPanEnabled = it })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Action Execution Button
            Button(
                onClick = {
                    when (activeService) {
                        ServiceType.Recharge -> {
                            val amt = rechargeAmount.toDoubleOrNull() ?: 0.0
                            if (mobileNumber.length >= 10 && amt > 0) {
                                viewModel.executeServiceTxn(
                                    service = "RECHARGE",
                                    amount = amt,
                                    description = "Mobile Prepaid Recharge - Operator $operatorSelected, Target: $mobileNumber",
                                    paymentMethod = paymentMode
                                )
                                latestReceipt = ServiceReceipt(
                                    title = "Telecom Mobile Recharge",
                                    referenceId = "RCH${Random.nextInt(100000, 999999)}",
                                    amount = amt,
                                    paymentMethod = paymentMode,
                                    details = mapOf(
                                        "Mobile Number" to mobileNumber,
                                        "Operator" to operatorSelected,
                                        "Status" to "SUCCESSFUL TOP UP"
                                    ),
                                    commission = amt * 0.005,
                                    cashback = amt * 0.001
                                )
                            } else {
                                viewModel.showNotification("Please enter a valid mobile number & price")
                            }
                        }
                        ServiceType.BBPS -> {
                            val amt = bbpsAmount.toDoubleOrNull() ?: 0.0
                            if (bbpsConsumerNo.isNotBlank() && amt > 0) {
                                viewModel.executeServiceTxn(
                                    service = "BBPS",
                                    amount = amt,
                                    description = "BBPS utility: Biller $bbpsBiller, Cons: $bbpsConsumerNo",
                                    paymentMethod = paymentMode
                                )
                                latestReceipt = ServiceReceipt(
                                    title = "Bharat Bill Payment System",
                                    referenceId = "BPS${Random.nextInt(100000, 999999)}",
                                    amount = amt,
                                    paymentMethod = paymentMode,
                                    details = mapOf(
                                        "Utility Category" to bbpsCategory,
                                        "Biller Name" to bbpsBiller,
                                        "Consumer ID" to bbpsConsumerNo
                                    ),
                                    commission = amt * 0.005,
                                    cashback = amt * 0.001
                                )
                            } else {
                                viewModel.showNotification("Please supply full BBPS criteria")
                            }
                        }
                        ServiceType.AEPS -> {
                            val amt = aepsAmount.toDoubleOrNull() ?: 0.0
                            if (aepsAadhaarNo.length == 12 && amt > 0) {
                                aepsScanningFinger = true
                                // Simulate STQC biometric finger scanning duration (1.5 seconds)
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(1500)
                                    aepsScanningFinger = false
                                    viewModel.executeServiceTxn(
                                        service = "AEPS",
                                        amount = amt,
                                        description = "Aadhaar Cash Withdrawal via Bank $aepsBank",
                                        paymentMethod = paymentMode
                                    )
                                    latestReceipt = ServiceReceipt(
                                        title = "Aadhaar Enabled Payment (AEPS)",
                                        referenceId = "APS${Random.nextInt(100000, 999999)}",
                                        amount = amt,
                                        paymentMethod = paymentMode,
                                        details = mapOf(
                                            "Bank" to aepsBank,
                                            "Aadhaar UID" to "XXXXXXXX" + aepsAadhaarNo.takeLast(4),
                                            "Biometrics" to "STQC Certified Finger Matching"
                                        ),
                                        commission = amt * 0.005,
                                        cashback = amt * 0.001
                                    )
                                }
                            } else {
                                viewModel.showNotification("Aadhaar Number must be exactly 12 digits")
                            }
                        }
                        ServiceType.DMT -> {
                            val amt = dmtAmount.toDoubleOrNull() ?: 0.0
                            if (dmtBeneficiary.isNotBlank() && dmtBankAcc.isNotBlank() && amt > 0) {
                                viewModel.executeServiceTxn(
                                    service = "DMT",
                                    amount = amt,
                                    description = "DMT Domestic Money Transfer to $dmtBeneficiary",
                                    paymentMethod = paymentMode
                                )
                                latestReceipt = ServiceReceipt(
                                    title = "Domestic Money Transfer (DMT)",
                                    referenceId = "DMT${Random.nextInt(100000, 999999)}",
                                    amount = amt,
                                    paymentMethod = paymentMode,
                                    details = mapOf(
                                        "Beneficiary" to dmtBeneficiary,
                                        "Bank Account" to dmtBankAcc,
                                        "IFSC" to dmtIfsc
                                    ),
                                    commission = amt * 0.005,
                                    cashback = amt * 0.001
                                )
                            } else {
                                viewModel.showNotification("Please provide Beneficiary Account & Transfer Limit")
                            }
                        }
                        ServiceType.GST -> {
                            val qty = gstItemQty.toIntOrNull() ?: 1
                            val price = gstItemPrice.toDoubleOrNull() ?: 0.0
                            val rate = gstTaxRate.toDoubleOrNull() ?: 18.0
                            val subtotal = qty * price
                            val gstAmt = subtotal * (rate / 100)
                            val totalAmt = subtotal + gstAmt

                            if (gstBusiness.isNotBlank() && totalAmt > 0) {
                                latestReceipt = ServiceReceipt(
                                    title = "GST Invoice Output",
                                    referenceId = "INV${Random.nextInt(100000, 999999)}",
                                    amount = totalAmt,
                                    paymentMethod = "B2B_GST_INVOICING",
                                    details = mapOf(
                                        "Billed Business" to gstBusiness,
                                        "Product Billed" to "$gstProductItem x$qty",
                                        "Sub-total" to "₹${String.format("%,.2f", subtotal)}",
                                        "CGST (${rate/2}%)" to "₹${String.format("%,.2f", gstAmt/2)}",
                                        "SGST (${rate/2}%)" to "₹${String.format("%,.2f", gstAmt/2)}"
                                    ),
                                    commission = 0.0,
                                    cashback = 0.0
                                )
                                viewModel.showNotification("GST Invoice output successfully simulated")
                            } else {
                                viewModel.showNotification("GST billing requires Customer Business info")
                            }
                        }
                        ServiceType.Loans -> {
                            val reqAmt = loanAmountReq.toDoubleOrNull() ?: 0.0
                            if (reqAmt > 0) {
                                coroutineScope.launch {
                                    // Process loan request
                                    viewModel.executeServiceTxn(
                                        service = "CREDIT_PAY",
                                        amount = reqAmt,
                                        description = "Disbursed Surya Micro-Finance Loan ($loanTerm Term)",
                                        paymentMethod = "CREDIT_LINE"
                                    )
                                    latestReceipt = ServiceReceipt(
                                        title = "Surya Credit Loan Disbursal",
                                        referenceId = "LON${Random.nextInt(100000, 999999)}",
                                        amount = reqAmt,
                                        paymentMethod = "MICRO_LOAN_DISBURSAL",
                                        details = mapOf(
                                            "Status" to "DISBURSED TO WALLET",
                                            "Tenure Requested" to loanTerm,
                                            "Interest Rate" to "1.15% Monthly Reducing"
                                        ),
                                        commission = 0.0,
                                        cashback = 0.0
                                    )
                                }
                            } else {
                                viewModel.showNotification("Requested credit must be greater than 0")
                            }
                        }
                        ServiceType.PAN -> {
                            if (!panDocUploaded) {
                                viewModel.showNotification("Please upload candidate's Aadhaar & photograph PDF")
                            } else if (panApplicantName.isBlank() || panAadhaarNo.length != 12) {
                                viewModel.showNotification("Please supply full 12-digit Aadhaar & Name")
                            } else {
                                val cost = 107.0
                                viewModel.executeServiceTxn(
                                    service = "BBPS",
                                    amount = cost,
                                    description = "PAN Application fee: $panApplicantName ($panApplicationType)",
                                    paymentMethod = paymentMode
                                )
                                latestReceipt = ServiceReceipt(
                                    title = "PAN Card Kiosk Service",
                                    referenceId = "PAN${Random.nextInt(100000, 999999)}",
                                    amount = cost,
                                    paymentMethod = paymentMode,
                                    details = mapOf(
                                        "Applicant" to panApplicantName,
                                        "Application Type" to panApplicationType,
                                        "Aadhaar Number" to "XXXXXXXX" + panAadhaarNo.takeLast(4),
                                        "Partner Operator" to configPanPartner,
                                        "Status" to "SUBMITTED TO NSDL / UTI"
                                    ),
                                    commission = cost * 0.005,
                                    cashback = cost * 0.001
                                )
                            }
                        }
                        ServiceType.Travel -> {
                            val cost = travelBookingAmount.toDoubleOrNull() ?: 0.0
                            if (cost <= 0) {
                                viewModel.showNotification("Invalid ticket cost calculated")
                            } else if (travelSource.isBlank()) {
                                viewModel.showNotification("Please specify departure location / city")
                            } else {
                                viewModel.executeServiceTxn(
                                    service = "BBPS",
                                    amount = cost,
                                    description = "$travelCategory Booking: $travelSource to $travelDestination on $travelDate",
                                    paymentMethod = paymentMode
                                )
                                latestReceipt = ServiceReceipt(
                                    title = "B2B Travel Ticket Issuance",
                                    referenceId = "TRV${Random.nextInt(100000, 999999)}",
                                    amount = cost,
                                    paymentMethod = paymentMode,
                                    details = mapOf(
                                        "Type" to travelCategory,
                                        "Route" to "$travelSource -> $travelDestination",
                                        "Journey Date" to travelDate,
                                        "Travellers" to travelGuests,
                                        "Aggregator Gateway" to configTravelAggregator,
                                        "Status" to "CONFIRMED & ISSUED"
                                    ),
                                    commission = cost * 0.005,
                                    cashback = cost * 0.001
                                )
                            }
                        }
                        ServiceType.Insurance -> {
                            val cost = insurancePremiumYearly.toDoubleOrNull() ?: 0.0
                            if (cost <= 0) {
                                viewModel.showNotification("Invalid premium calculated")
                            } else {
                                viewModel.executeServiceTxn(
                                    service = "BBPS",
                                    amount = cost,
                                    description = "$insuranceCategory Premium Cover: ₹$insuranceSumAssured",
                                    paymentMethod = paymentMode
                                )
                                latestReceipt = ServiceReceipt(
                                    title = "Surya Insurance Policy Purchase",
                                    referenceId = "INS${Random.nextInt(100000, 999999)}",
                                    amount = cost,
                                    paymentMethod = paymentMode,
                                    details = mapOf(
                                        "Plan Selected" to insuranceCategory,
                                        "Coverage Cover" to "₹$insuranceSumAssured",
                                        "Annual Premium" to "₹$cost",
                                        "Identity / Reg" to (if (insuranceCategory.contains("Motor")) insuranceVehicleRegNo else "Age: $insuranceApplicantAge Years"),
                                        "Assigned Desk" to configInsuranceBroker,
                                        "Status" to "POLICY GENERATED"
                                    ),
                                    commission = cost * 0.005,
                                    cashback = cost * 0.001
                                )
                            }
                        }
                        ServiceType.AdminPanel -> {
                            viewModel.showNotification("System service routing & gate rules successfully recompiled.")
                            latestReceipt = ServiceReceipt(
                                title = "Admin & Gateway Re-route Control",
                                referenceId = "CFG${Random.nextInt(100000, 999999)}",
                                amount = 0.0,
                                paymentMethod = "SYSTEM_ROOT",
                                details = mapOf(
                                    "Recharge Route" to configRechargeProvider,
                                    "AEPS Route" to configAepsGateway,
                                    "DMT Route" to configDmtRoute,
                                    "PAN Route" to configPanPartner,
                                    "Active Gates" to "Recharge=${if(isRechargeEnabled) "ON" else "OFF"}, BBPS=${if(isBbpsEnabled) "ON" else "OFF"}, AEPS=${if(isAepsEnabled) "ON" else "OFF"}"
                                ),
                                commission = 0.0,
                                cashback = 0.0
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = activeService.color),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("execute_service_button")
            ) {
                Text("Process ${activeService.title} Transaction", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Digital Receipt Section
            AnimatedVisibility(
                visible = latestReceipt != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                latestReceipt?.let { receipt ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Latest Digital Audit Receipt", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            TextButton(onClick = { latestReceipt = null }) {
                                Text("Clear", color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        // Receipt Thermal Printer Graphics Layer
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "SURYA CREDIT SOLUTIONS",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "AUTHORIZED MERCHANT AUDIT SYSTEM",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.DarkGray
                                )
                                Text(
                                    text = "--------------------------------",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )

                                Text(
                                    text = receipt.title.uppercase(),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    color = activeService.color
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Key Details
                                receipt.details.forEach { (key, value) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "$key:",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = value,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Ref ID:",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = receipt.referenceId,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Funding:",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = receipt.paymentMethod,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "- - - - - - - - - - - - - - - -",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "TOTAL AMOUNT:",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "₹${String.format("%,.2f", receipt.amount)}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFC62828)
                                    )
                                }

                                if (receipt.commission > 0 || receipt.cashback > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "COMMISSION (+0.5%):",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = Color(0xFF2E7D32)
                                        )
                                        Text(
                                            text = "₹${String.format("%,.2f", receipt.commission)}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "CASHBACK (+0.1%):",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = Color(0xFF00838F)
                                        )
                                        Text(
                                            text = "₹${String.format("%,.2f", receipt.cashback)}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00838F)
                                        )
                                    }
                                }

                                Text(
                                    text = "--------------------------------",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )

                                Text(
                                    text = "STATUS: SUCCESS / COMPLIANT",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = "SECURE TRANSIT PORTAL CLOUD",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Simple simulated barcode using text characters
                                Text(
                                    text = "|||| ||||| | ||||| || ||| || ||||",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    letterSpacing = 2.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
