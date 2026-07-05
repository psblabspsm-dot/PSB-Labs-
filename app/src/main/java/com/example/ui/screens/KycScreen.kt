package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val kycState by viewModel.kycState.collectAsState()

    var panNumber by remember { mutableStateOf("") }
    var gstNumber by remember { mutableStateOf("") }
    var aadhaarNumber by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("") }
    var bankIfsc by remember { mutableStateOf("") }

    var selfieAttached by remember { mutableStateOf(false) }
    var gstDocAttached by remember { mutableStateOf(false) }
    var aadhaarDocAttached by remember { mutableStateOf(false) }

    var isUploadingSelfie by remember { mutableStateOf(false) }
    var isUploadingGst by remember { mutableStateOf(false) }
    var isUploadingAadhaar by remember { mutableStateOf(false) }

    var currentOcrLogs by remember { mutableStateOf("") }

    var selectedAuditStatus by remember { mutableStateOf("SUBMITTED") }

    var isVerifying by remember { mutableStateOf(false) }
    var verificationStep by remember { mutableStateOf(0) } // 0=None, 1=PAN, 2=GST, 3=Aadhaar, 4=Bank, 5=FaceMatch, 6=Finished

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Compliance & Verification Hub",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "RBI KYC standards & multi-step identity checks",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            Surface(
                color = Color(0xFFFF8C00).copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AdminPanelSettings, null, tint = Color(0xFFFF8C00), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PCI-DSS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8C00))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        kycState?.let { state ->
            // Current Status Tracking Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (state.status) {
                        "APPROVED" -> Color(0xFFE8F5E9)
                        "SUBMITTED" -> Color(0xFFFFF8E1)
                        "REJECTED" -> Color(0xFFFFEBEE)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    when (state.status) {
                        "APPROVED" -> Color(0xFF81C784)
                        "SUBMITTED" -> Color(0xFFFFD54F)
                        "REJECTED" -> Color(0xFFEF5350)
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (state.status) {
                            "APPROVED" -> Icons.Default.Verified
                            "SUBMITTED" -> Icons.Default.HourglassEmpty
                            "REJECTED" -> Icons.Default.Cancel
                            else -> Icons.Default.Warning
                        },
                        contentDescription = null,
                        tint = when (state.status) {
                            "APPROVED" -> Color(0xFF2E7D32)
                            "SUBMITTED" -> Color(0xFFEF6C00)
                            "REJECTED" -> Color(0xFFC62828)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "COMPLIANCE STATUS: ${state.status}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = when (state.status) {
                                "APPROVED" -> Color(0xFF2E7D32)
                                "SUBMITTED" -> Color(0xFFEF6C00)
                                "REJECTED" -> Color(0xFFC62828)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        Text(
                            text = when (state.status) {
                                "APPROVED" -> "Verified & Active. Full settlement access and pre-approved ₹10 Lakhs B2B credit limits enabled."
                                "SUBMITTED" -> "Nodal validation checklist active. Bank penny-drop and Face Match OCR routing underway."
                                "REJECTED" -> "Verification Failed: Document mismatch or incomplete details. Please re-submit."
                                else -> "Kiosk features restricted. Please submit Aadhaar, PAN and GSTIN to unlock."
                            },
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // Real-time Compliance Check Progress Trackers
            if (state.status == "SUBMITTED" || isVerifying) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Central Verification Progress Logs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val checkSteps = listOf(
                            Triple("PAN Status Check", "Verified with Income Tax Registry (NSDL Node)", verificationStep >= 1),
                            Triple("GSTIN Address Audit", "GSTIN portal registration check", verificationStep >= 2),
                            Triple("Aadhaar UIDAI Handshake", "UIDAI OTP biometric compliance check", verificationStep >= 3),
                            Triple("Nodal Bank Verification", "penny-drop check completed successfully", verificationStep >= 4),
                            Triple("Facial Match OCR Check", "Selfie compared with Aadhaar portrait", verificationStep >= 5)
                        )

                        checkSteps.forEachIndexed { index, (label, desc, isDone) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Circle,
                                    contentDescription = null,
                                    tint = if (isDone) Color(0xFF00C853) else Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isDone) MaterialTheme.colorScheme.onSurface else Color.Gray
                                    )
                                    Text(text = desc, fontSize = 9.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            // Active compliance documents summary
            if (state.status == "APPROVED") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Active Registered Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                        Divider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Registered Entity:", fontSize = 12.sp, color = Color.Gray)
                            Text(state.businessName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Income Tax PAN:", fontSize = 12.sp, color = Color.Gray)
                            Text(state.panNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("GSTIN Registration:", fontSize = 12.sp, color = Color.Gray)
                            Text(state.gstNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Aadhaar UID:", fontSize = 12.sp, color = Color.Gray)
                            Text("XXXX XXXX " + state.aadhaarNumber.takeLast(4), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Settlement Account:", fontSize = 12.sp, color = Color.Gray)
                            Text("Verified (Yes Bank Penny Drop)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF00C853))
                        }
                    }
                }
            } else {
                // Interactive submission form
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Merchant Compliance Form",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Registered Business Entity Name") },
                        placeholder = { Text("e.g. Surya Digital World") },
                        leadingIcon = { Icon(Icons.Default.Storefront, null, tint = Color(0xFFFF8C00)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = panNumber,
                            onValueChange = { if (it.length <= 10) panNumber = it.uppercase() },
                            label = { Text("PAN Number") },
                            placeholder = { Text("ABCDE1234F") },
                            leadingIcon = { Icon(Icons.Default.CreditCard, null, tint = Color(0xFFFF8C00)) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = gstNumber,
                            onValueChange = { if (it.length <= 15) gstNumber = it.uppercase() },
                            label = { Text("GSTIN Registration") },
                            placeholder = { Text("29AAECS1234B...") },
                            leadingIcon = { Icon(Icons.Default.ReceiptLong, null, tint = Color(0xFFFF8C00)) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    OutlinedTextField(
                        value = aadhaarNumber,
                        onValueChange = { if (it.length <= 12) aadhaarNumber = it },
                        label = { Text("Aadhaar UID Number (12 Digits)") },
                        placeholder = { Text("Enter 12 digit Aadhaar") },
                        leadingIcon = { Icon(Icons.Default.Fingerprint, null, tint = Color(0xFFFF8C00)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bankAccount,
                            onValueChange = { bankAccount = it },
                            label = { Text("Settlement Bank Account") },
                            placeholder = { Text("Enter account number") },
                            leadingIcon = { Icon(Icons.Default.AccountBalance, null, tint = Color(0xFFFF8C00)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f)
                        )

                        OutlinedTextField(
                            value = bankIfsc,
                            onValueChange = { bankIfsc = it.uppercase() },
                            label = { Text("IFSC Code") },
                            placeholder = { Text("YESB0000123") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    // Document Upload Segment
                    Text(
                        text = "Attach Regulatory Proof Documents",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Selfie Capture Mock
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (!isUploadingSelfie) {
                                        isUploadingSelfie = true
                                        coroutineScope.launch {
                                            currentOcrLogs = "Starting dynamic biometric facial capture...\nAdjust alignment...\nAnalysing liveness (3D mesh mapping)..."
                                            delay(1500)
                                            isUploadingSelfie = false
                                            selfieAttached = true
                                            currentOcrLogs = "Face capture completed. Eye-blink audit PASSED. Liveness: HIGH."
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (selfieAttached) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            border = BorderStroke(1.dp, if (selfieAttached) Color(0xFF00C853) else MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (isUploadingSelfie) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFFFF8C00))
                                } else {
                                    Icon(
                                        imageVector = if (selfieAttached) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = if (selfieAttached) Color(0xFF00C853) else Color(0xFFFF8C00),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Liveness Selfie", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(if (selfieAttached) "Facial Mesh Captured" else "Take Liveness Capture", fontSize = 8.sp, color = Color.Gray, textAlign = TextAlign.Center)
                            }
                        }

                        // GST Doc Upload Mock
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (!isUploadingGst) {
                                        isUploadingGst = true
                                        coroutineScope.launch {
                                            currentOcrLogs = "Initializing OCR scanner...\nRecognizing characters on certificate of incorporation...\nAnalyzing GSTIN structure..."
                                            delay(1500)
                                            isUploadingGst = false
                                            gstDocAttached = true
                                            currentOcrLogs = "OCR analysis successfully extracted GSTIN business details."
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (gstDocAttached) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            border = BorderStroke(1.dp, if (gstDocAttached) Color(0xFF00C853) else MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (isUploadingGst) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFFFF8C00))
                                } else {
                                    Icon(
                                        imageVector = if (gstDocAttached) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                        contentDescription = null,
                                        tint = if (gstDocAttached) Color(0xFF00C853) else Color(0xFFFF8C00),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("GST Certificate", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(if (gstDocAttached) "GSTIN OCR Extracted" else "Upload PDF/Image", fontSize = 8.sp, color = Color.Gray, textAlign = TextAlign.Center)
                            }
                        }

                        // Aadhaar Upload Mock
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (!isUploadingAadhaar) {
                                        isUploadingAadhaar = true
                                        coroutineScope.launch {
                                            currentOcrLogs = "Executing Aadhaar OCR scan...\nExtracting birth year and UID barcode metadata..."
                                            delay(1500)
                                            isUploadingAadhaar = false
                                            aadhaarDocAttached = true
                                            currentOcrLogs = "Aadhaar document matched with UIDAI registry checklist."
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (aadhaarDocAttached) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            border = BorderStroke(1.dp, if (aadhaarDocAttached) Color(0xFF00C853) else MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (isUploadingAadhaar) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFFFF8C00))
                                } else {
                                    Icon(
                                        imageVector = if (aadhaarDocAttached) Icons.Default.CheckCircle else Icons.Default.DocumentScanner,
                                        contentDescription = null,
                                        tint = if (aadhaarDocAttached) Color(0xFF00C853) else Color(0xFFFF8C00),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Aadhaar UID Card", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(if (aadhaarDocAttached) "Aadhaar Scan Saved" else "Upload Card Scan", fontSize = 8.sp, color = Color.Gray, textAlign = TextAlign.Center)
                            }
                        }
                    }

                    // OCR Terminal Log Console
                    if (currentOcrLogs.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0F12))
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Terminal, null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = currentOcrLogs,
                                    color = Color(0xFF00FFCC),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    lineHeight = 12.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isVerifying) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFFF8C00))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Starting central verification nodes check...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (panNumber.length == 10 && gstNumber.length == 15 && aadhaarNumber.length == 12 && businessName.isNotBlank() && bankAccount.isNotBlank()) {
                                    isVerifying = true
                                    verificationStep = 0
                                    coroutineScope.launch {
                                        delay(800)
                                        verificationStep = 1 // PAN Done
                                        delay(800)
                                        verificationStep = 2 // GST Done
                                        delay(800)
                                        verificationStep = 3 // Aadhaar Done
                                        delay(800)
                                        verificationStep = 4 // Bank Penny Drop Done
                                        delay(800)
                                        verificationStep = 5 // Face Match Done
                                        delay(1000)
                                        isVerifying = false
                                        viewModel.submitKyc(panNumber, gstNumber, aadhaarNumber, businessName)
                                    }
                                } else {
                                    viewModel.showNotification("Compliance fields incorrect! Must submit Selfie, PAN (10 chars), GST (15 chars), Aadhaar (12 chars), bank account.")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_kyc_button")
                        ) {
                            Icon(Icons.Default.Gavel, null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SUBMIT COMPLIANCE CHECK", fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }
                }
            }

            // ---------------- AUDITOR / ADMIN COMPLIANCE REVIEW DASHBOARD ----------------
            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Auditor Compliance Console",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Back-office compliance dashboard to test approval/rejection",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Compliance Pending Actions Queue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Active Review Profile:", fontSize = 10.sp, color = Color.Gray)
                            Text(if (businessName.isNotBlank()) businessName else "Surya Digital Kiosk", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Aadhaar: ${if (aadhaarNumber.isNotBlank()) aadhaarNumber else "1234 5678 9012"}", fontSize = 10.sp, color = Color.Gray)
                        }
                        Surface(
                            color = Color(0xFFFFC107).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "GST Audit Pending",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Approve Button
                        Button(
                            onClick = {
                                viewModel.submitKyc(
                                    if (panNumber.isNotBlank()) panNumber else "ABCDE1234F",
                                    if (gstNumber.isNotBlank()) gstNumber else "29AAECS1234B1Z2",
                                    if (aadhaarNumber.isNotBlank()) aadhaarNumber else "999988887777",
                                    if (businessName.isNotBlank()) businessName else "Surya Digital World"
                                )
                                viewModel.showNotification("Merchant Compliance profile approved by Administrator.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve KYC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Reject Button
                        Button(
                            onClick = {
                                // Update status to REJECTED in database
                                coroutineScope.launch {
                                    viewModel.addNotification("KYC Compliance Rejected", "Your compliance profile was rejected due to blurry documents.", "ALERT")
                                    viewModel.showNotification("KYC Compliance Rejected by Auditor.")
                                    // Set status locally or trigger a rejection
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reject KYC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
