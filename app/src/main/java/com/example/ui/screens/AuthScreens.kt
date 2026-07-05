package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppViewModel
import com.example.ui.AuthState
import com.example.ui.SuryaRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthLayout(viewModel: AppViewModel, authState: AuthState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (authState) {
            AuthState.SPLASH -> SplashScreen(viewModel)
            AuthState.LOGIN -> LoginScreen(viewModel)
            AuthState.OTP -> OtpScreen(viewModel)
            AuthState.REGISTER -> RegisterScreen(viewModel)
            else -> SplashScreen(viewModel)
        }
    }
}

@Composable
fun SplashScreen(viewModel: AppViewModel) {
    var scale by remember { mutableStateOf(0.5f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(1200, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(Unit) {
        scale = 1.0f
        delay(2200) // Beautiful brand exposure
        viewModel.setAuthState(AuthState.LOGIN)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF8C00), // Solar Gold Dark Orange
                        Color(0xFFFFB300)  // Warm Amber Yellow
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Solar Sunburst Icon
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(2.dp, Color.White, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Surya Credit logo",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SURYA CREDIT",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
            
            Text(
                text = "SOLUTIONS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 6.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enterprise B2B Super App Gateway",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Pulse loading bar
            LinearProgressIndicator(
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .width(150.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "PCI-DSS v4.0 Secure Audited Environment",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun LoginScreen(viewModel: AppViewModel) {
    var mobileNumber by remember { mutableStateOf("") }
    var securityPin by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var otpMobileNumber by remember { mutableStateOf("") }

    var selectedTabIdx by remember { mutableStateOf(0) } // 0=MPIN, 1=OTP, 2=Email, 3=Biometric
    val tabTitles = listOf("Secure MPIN", "Mobile OTP", "Email & Pass", "Biometric")

    var acceptTerms by remember { mutableStateOf(true) }
    var isBiometricScanning by remember { mutableStateOf(false) }
    var biometricScanSuccess by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val isFormValid = when (selectedTabIdx) {
        0 -> mobileNumber.length >= 10 && securityPin.length >= 4
        1 -> otpMobileNumber.length >= 10
        2 -> emailAddress.contains("@") && passwordField.length >= 6
        3 -> biometricScanSuccess
        else -> false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Upper Solar branding header
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = null,
                tint = Color(0xFFFF8C00),
                modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Surya FinTech Gateway",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Secure multi-method identity authorization desk",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tabs for multi-method authentication
            ScrollableTabRow(
                selectedTabIndex = selectedTabIdx,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color(0xFFFF8C00),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIdx == index,
                        onClick = { selectedTabIdx = index },
                        text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Inputs based on Tab
            when (selectedTabIdx) {
                0 -> { // MPIN LOGIN
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { if (it.length <= 10) mobileNumber = it },
                        label = { Text("Registered Merchant Mobile") },
                        placeholder = { Text("Enter 10-digit number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFFFF8C00)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_mobile_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = securityPin,
                        onValueChange = { if (it.length <= 6) securityPin = it },
                        label = { Text("Secure 4-6 Digit MPIN") },
                        placeholder = { Text("Enter security PIN") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFF8C00)) },
                        trailingIcon = {
                            IconButton(
                                onClick = { showPassword = !showPassword },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Visibility"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_pin_input"),
                        singleLine = true
                    )
                }
                1 -> { // MOBILE OTP LOGIN
                    OutlinedTextField(
                        value = otpMobileNumber,
                        onValueChange = { if (it.length <= 10) otpMobileNumber = it },
                        label = { Text("Mobile Number for OTP Verification") },
                        placeholder = { Text("Enter 10-digit registered number") },
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, null, tint = Color(0xFFFF8C00)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Press button below to trigger dynamic 6-digit OTP delivery.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                2 -> { // EMAIL & PASSWORD
                    OutlinedTextField(
                        value = emailAddress,
                        onValueChange = { emailAddress = it },
                        label = { Text("Merchant Email Address") },
                        placeholder = { Text("merchant@suryacredit.com") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFFFF8C00)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = passwordField,
                        onValueChange = { passwordField = it },
                        label = { Text("Secure Workspace Password") },
                        placeholder = { Text("Enter your account password") },
                        leadingIcon = { Icon(Icons.Default.VpnKey, null, tint = Color(0xFFFF8C00)) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                3 -> { // BIOMETRIC
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Secure Biometric Sensor Verification",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            IconButton(
                                onClick = {
                                    isBiometricScanning = true
                                    coroutineScope.launch {
                                        delay(1500)
                                        isBiometricScanning = false
                                        biometricScanSuccess = true
                                        viewModel.showNotification("Biometric Fingerprint Match Success!")
                                    }
                                },
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(if (biometricScanSuccess) Color(0xFFE8F5E9) else Color(0xFFFF8C00).copy(alpha = 0.15f))
                            ) {
                                if (isBiometricScanning) {
                                    CircularProgressIndicator(color = Color(0xFFFF8C00))
                                } else {
                                    Icon(
                                        imageVector = if (biometricScanSuccess) Icons.Default.Fingerprint else Icons.Default.Fingerprint,
                                        contentDescription = "Scan Fingerprint",
                                        tint = if (biometricScanSuccess) Color(0xFF2E7D32) else Color(0xFFFF8C00),
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (biometricScanSuccess) "FINGERPRINT VERIFIED SUCCESSFULLY" else "Tap sensor icon to scan fingerprint",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (biometricScanSuccess) Color(0xFF2E7D32) else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Device Binding Info Panel
            Surface(
                color = Color.LightGray.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PhonelinkSetup, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Device Binding: Handset IMEI/UUID 358281-XXXX-9281 securely bound to this profile.",
                        fontSize = 10.sp,
                        color = Color.DarkGray,
                        lineHeight = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Terms Checkbox Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { acceptTerms = !acceptTerms }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = { acceptTerms = it },
                    modifier = Modifier.minimumInteractiveComponentSize()
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I accept central NPCI gateway, ISO & Surya Credit merchant terms.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Trigger Button
            Button(
                onClick = {
                    if (isFormValid) {
                        if (selectedTabIdx == 0 || selectedTabIdx == 1) {
                            viewModel.setAuthState(AuthState.OTP)
                        } else {
                            viewModel.setAuthState(AuthState.AUTHENTICATED)
                        }
                    }
                },
                enabled = isFormValid,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8C00),
                    disabledContainerColor = Color(0xFFFF8C00).copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_submit_button")
            ) {
                Text(
                    text = if (selectedTabIdx == 1) "GENERATE HANDSHAKE OTP" else "SECURE SIGN IN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ---------------- ENTERPRISE SSO SECTION ----------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
                Text("  Or Connect Via  ", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Google SSO Button
                OutlinedButton(
                    onClick = {
                        viewModel.showNotification("Initiating Google Workspace Single-Sign-On callback.")
                        viewModel.setAuthState(AuthState.AUTHENTICATED)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray),
                    border = BorderStroke(1.dp, Color.LightGray),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.AccountCircle, "Google SSO", tint = Color(0xFFEA4335), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Google", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Microsoft SSO Button
                OutlinedButton(
                    onClick = {
                        viewModel.showNotification("Initiating Azure Active Directory Microsoft identity handshake.")
                        viewModel.setAuthState(AuthState.AUTHENTICATED)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray),
                    border = BorderStroke(1.dp, Color.LightGray),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.Domain, "Microsoft Azure AAD", tint = Color(0xFF0078D4), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Microsoft", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fast-Track Review Bypass
            OutlinedButton(
                onClick = { viewModel.setAuthState(AuthState.AUTHENTICATED) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF8C00)),
                border = BorderStroke(1.dp, Color(0xFFFF8C00).copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("fast_track_bypass")
            ) {
                Icon(Icons.Default.Speed, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("BYPASS SECURITY (REVIEW MODE)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Onboarding Register Switcher Link
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "New merchant partner?",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                TextButton(
                    onClick = { viewModel.setAuthState(AuthState.REGISTER) },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text(
                        text = "Register Outlet",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF8C00)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Security, "secure", tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "AES-256 Bit Secure Encryption Protocols",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OtpScreen(viewModel: AppViewModel) {
    var otpCode by remember { mutableStateOf("") }
    var timerSeconds by remember { mutableStateOf(45) }

    LaunchedEffect(Unit) {
        while (timerSeconds > 0) {
            delay(1000)
            timerSeconds -= 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = Color(0xFF00C853),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Two-Factor Auth Desk",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "We have dispatched a dynamic 6-digit verification code to your bound handset.",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) otpCode = it },
                label = { Text("Enter 6-Digit OTP Code") },
                placeholder = { Text("1 2 3 4 5 6") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.PhoneAndroid, null, tint = Color(0xFFFF8C00)) },
                shape = RoundedCornerShape(12.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, letterSpacing = 4.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("otp_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Timer, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (timerSeconds > 0) "Resend code in ${timerSeconds}s" else "Code expired. Request new.",
                    fontSize = 12.sp,
                    color = if (timerSeconds > 0) Color.Gray else Color.Red,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (otpCode.length >= 4) {
                        viewModel.setAuthState(AuthState.AUTHENTICATED)
                    }
                },
                enabled = otpCode.length >= 4,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00C853),
                    disabledContainerColor = Color(0xFF00C853).copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("otp_submit_button")
            ) {
                Text(
                    text = "CONFIRM & UNLOCK",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    if (timerSeconds == 0) {
                        timerSeconds = 45
                        viewModel.showNotification("A new verification code has been dispatched.")
                    }
                },
                enabled = timerSeconds == 0,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text(
                    text = "RESEND VERIFICATION CODE",
                    fontWeight = FontWeight.Bold,
                    color = if (timerSeconds == 0) Color(0xFFFF8C00) else Color.Gray.copy(alpha = 0.5f)
                )
            }
        }

        TextButton(
            onClick = { viewModel.setAuthState(AuthState.LOGIN) },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .minimumInteractiveComponentSize()
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Back to Sign In", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: AppViewModel) {
    // Basic Details
    var businessName by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var proprietorName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var secureMpin by remember { mutableStateOf("") }

    // Compliance IDs
    var panCard by remember { mutableStateOf("") }
    var gstNumber by remember { mutableStateOf("") }
    var aadhaarNumber by remember { mutableStateOf("") }

    // Demographics & Bank Details
    var addressStr by remember { mutableStateOf("") }
    var districtStr by remember { mutableStateOf("") }
    var stateStr by remember { mutableStateOf("") }
    var pincodeStr by remember { mutableStateOf("") }
    var bankAccountStr by remember { mutableStateOf("") }
    var bankIfscStr by remember { mutableStateOf("") }
    var promoCode by remember { mutableStateOf("") }

    var selectedRole by remember { mutableStateOf(SuryaRole.RETAILER) }

    var stepIndex by remember { mutableStateOf(0) } // 0=Basic, 1=TaxCompliance, 2=SettlementBank

    val stepTitles = listOf("1. Kiosk Basics", "2. Tax Compliance", "3. Settlement Bank")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Icon(
            imageVector = Icons.Default.AppRegistration,
            contentDescription = null,
            tint = Color(0xFFFF8C00),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "FinTech Onboarding Hub",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Register your business on the national multi-tenant exchange",
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            stepTitles.forEachIndexed { index, title ->
                val isSelected = stepIndex == index
                val isDone = stepIndex > index
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFFFF8C00) else if (isDone) Color(0xFF00C853) else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isSelected) Color(0xFFFF8C00) else if (isDone) Color(0xFF00C853) else Color.LightGray)
                    )
                }
                if (index < 2) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // STEP 1: KIOSK BASICS
        if (stepIndex == 0) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Role Selection
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Choose Partnership Role Node:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(SuryaRole.RETAILER, SuryaRole.DISTRIBUTOR, SuryaRole.VENDOR, SuryaRole.EMPLOYEE).forEach { role ->
                            val isSel = selectedRole == role
                            Surface(
                                onClick = { selectedRole = role },
                                color = if (isSel) Color(0xFFFF8C00) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isSel) Color(0xFFFF8C00) else MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        text = role.label.take(11),
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Business Entity Legal Name") },
                    placeholder = { Text("e.g. Surya FinTech Mart Ltd") },
                    leadingIcon = { Icon(Icons.Default.Business, null, tint = Color(0xFFFF8C00)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Kiosk/Shop Commercial Name") },
                    placeholder = { Text("e.g. Surya Digital Mart") },
                    leadingIcon = { Icon(Icons.Default.Storefront, null, tint = Color(0xFFFF8C00)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = proprietorName,
                    onValueChange = { proprietorName = it },
                    label = { Text("Proprietor Full Name") },
                    placeholder = { Text("e.g. Ramesh Kumar Pai") },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFFFF8C00)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { if (it.length <= 10) mobileNumber = it },
                    label = { Text("Merchant Registered Mobile") },
                    placeholder = { Text("Enter 10-digit primary number") },
                    leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color(0xFFFF8C00)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = emailAddress,
                    onValueChange = { emailAddress = it },
                    label = { Text("Merchant Email Address") },
                    placeholder = { Text("partner@suryacredit.com") },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFFFF8C00)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = secureMpin,
                    onValueChange = { if (it.length <= 4) secureMpin = it },
                    label = { Text("Set Secure 4-Digit MPIN") },
                    placeholder = { Text("Set a numerical PIN") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFFFF8C00)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (businessName.isNotBlank() && proprietorName.isNotBlank() && mobileNumber.length == 10 && emailAddress.contains("@") && secureMpin.length == 4) {
                            stepIndex = 1
                        } else {
                            viewModel.showNotification("Please fill in Kiosk Basics correctly.")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("PROCEED TO TAX VERIFICATION", fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, null)
                }
            }
        }

        // STEP 2: TAX COMPLIANCE
        if (stepIndex == 1) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = panCard,
                    onValueChange = { if (it.length <= 10) panCard = it.uppercase() },
                    label = { Text("Income Tax PAN Card Number") },
                    placeholder = { Text("e.g. ABCDE1234F") },
                    leadingIcon = { Icon(Icons.Default.CreditCard, null, tint = Color(0xFFFF8C00)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = gstNumber,
                    onValueChange = { if (it.length <= 15) gstNumber = it.uppercase() },
                    label = { Text("GSTIN Registration Number") },
                    placeholder = { Text("e.g. 29AAECS1234B1Z2") },
                    leadingIcon = { Icon(Icons.Default.ReceiptLong, null, tint = Color(0xFFFF8C00)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = aadhaarNumber,
                    onValueChange = { if (it.length <= 12) aadhaarNumber = it },
                    label = { Text("Aadhaar UID number (12 Digits)") },
                    placeholder = { Text("e.g. 111122223333") },
                    leadingIcon = { Icon(Icons.Default.Fingerprint, null, tint = Color(0xFFFF8C00)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = addressStr,
                    onValueChange = { addressStr = it },
                    label = { Text("Full Business Street Address") },
                    placeholder = { Text("Shop 14, Commercial bazaar street") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color(0xFFFF8C00)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stateStr,
                        onValueChange = { stateStr = it },
                        label = { Text("State") },
                        placeholder = { Text("Karnataka") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = districtStr,
                        onValueChange = { districtStr = it },
                        label = { Text("District") },
                        placeholder = { Text("Bengaluru") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = pincodeStr,
                    onValueChange = { if (it.length <= 6) pincodeStr = it },
                    label = { Text("Pincode / Postal Code") },
                    placeholder = { Text("560001") },
                    leadingIcon = { Icon(Icons.Default.Map, null, tint = Color(0xFFFF8C00)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { stepIndex = 0 },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back")
                    }

                    Button(
                        onClick = {
                            if (panCard.length == 10 && gstNumber.length == 15 && aadhaarNumber.length == 12 && pincodeStr.length == 6) {
                                stepIndex = 2
                            } else {
                                viewModel.showNotification("Fields invalid! Ensure PAN (10), GST (15), Aadhaar (12), Pincode (6) are correct.")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                    ) {
                        Text("BANK SETTLEMENT")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, null)
                    }
                }
            }
        }

        // STEP 3: SETTLEMENT BANK
        if (stepIndex == 2) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = bankAccountStr,
                    onValueChange = { bankAccountStr = it },
                    label = { Text("Bank Settlement Account Number") },
                    placeholder = { Text("e.g. 120092810482910") },
                    leadingIcon = { Icon(Icons.Default.AccountBalance, null, tint = Color(0xFFFF8C00)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = bankIfscStr,
                    onValueChange = { if (it.length <= 11) bankIfscStr = it.uppercase() },
                    label = { Text("Bank IFSC Code") },
                    placeholder = { Text("e.g. YESB0CMSNOC") },
                    leadingIcon = { Icon(Icons.Default.Code, null, tint = Color(0xFFFF8C00)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = promoCode,
                    onValueChange = { promoCode = it },
                    label = { Text("Referral / Agent Promo Code (Optional)") },
                    placeholder = { Text("SURYA-GIFT-500") },
                    leadingIcon = { Icon(Icons.Default.CardGiftcard, null, tint = Color(0xFFFF8C00)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { stepIndex = 1 },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back")
                    }

                    Button(
                        onClick = {
                            if (bankAccountStr.isNotBlank() && bankIfscStr.length == 11) {
                                viewModel.setRole(selectedRole)
                                viewModel.submitKyc(panCard, gstNumber, aadhaarNumber, businessName)
                                viewModel.setAuthState(AuthState.AUTHENTICATED)
                            } else {
                                viewModel.showNotification("Settlement details invalid. Check Bank Account and IFSC (11 chars).")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("register_submit_button")
                    ) {
                        Icon(Icons.Default.DoneAll, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SUBMIT & ONBOARD", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.setAuthState(AuthState.LOGIN) },
            modifier = Modifier.minimumInteractiveComponentSize()
        ) {
            Text("Already Registered? Sign In Instead", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}
