package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiApiHelper
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class SuryaRole(val label: String, val description: String, val permissions: List<String> = emptyList()) {
    SUPER_ADMIN("Super Admin", "Full system configurations, security settings, and system-wide ledgers.", listOf("WRITE_SYS", "READ_ALL", "AUTH_BYPASS", "GRANT_CREDIT", "APPROVE_KYC")),
    ADMIN("Admin", "Enterprise operation metrics, partner audits, and service fees setup.", listOf("READ_ALL", "GRANT_CREDIT", "APPROVE_KYC", "AUDIT_LOGS")),
    STATE_HEAD("State Head", "Monitor state-wide sales volumes, commissions, and network growth.", listOf("READ_STATE", "MANAGE_DISTRIBUTORS")),
    DISTRICT_DISTRIBUTOR("District Distributor", "Manage district-level distributors and assign localized credit slabs.", listOf("READ_DISTRICT", "ALLOCATE_CREDIT")),
    MASTER_DISTRIBUTOR("Master Distributor", "Track distributor sub-networks and aggregate transactions commissions.", listOf("READ_NETWORK", "MANAGE_SUB_DISTRIBUTORS")),
    DISTRIBUTOR("Distributor", "Onboard retailers, allocate credit lines, and monitor retail trade flows.", listOf("ONBOARD_RETAILER", "ALLOCATE_CREDIT", "READ_SUB_RETAILERS")),
    RETAILER("Retailer", "Run your local digital kiosk: DMT, AEPS, BBPS, and B2B orders.", listOf("DMT_TXN", "AEPS_TXN", "BBPS_TXN", "B2B_ORDER")),
    VENDOR("Vendor", "Manage B2B inventory, catalog prices, SKU uploads, and logistics.", listOf("MANAGE_SKU", "PROCESS_ORDER")),
    EMPLOYEE("Employee", "Manage customer/retailer tickets, compliance review, and customer support.", listOf("READ_TICKETS", "COMPLIANCE_REVIEW")),
    CUSTOMER("Customer", "Instant scan-to-pay via QR, pre-approved digital loans, and utility checks.", listOf("CHECK_BAL", "PAY_QR")),
    SUPPORT_EXECUTIVE("Support Executive", "Resolve customer dispute tickets, transaction reversals, and compliance status.", listOf("RESOLVE_TICKET", "REVERSE_TXN")),
    FINANCE_TEAM("Finance Team", "Verify bank nodal settlements, tax invoices, and state-wide commission disbursements.", listOf("SETTLE_NODAL", "VIEW_TAX_INVOICES")),
    AUDITOR("Auditor", "Read-only access to national settlement ledgers and real-time audit logs.", listOf("READ_ALL", "AUDIT_LOGS_ONLY"))
}

enum class AuthState {
    SPLASH,
    LOGIN,
    OTP,
    REGISTER,
    AUTHENTICATED
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: String, // "INFO", "SUCCESS", "WARNING", "ALERT"
    val timestamp: String
)

data class SupportTicket(
    val id: String,
    val subject: String,
    val status: String, // "OPEN", "IN_PROGRESS", "RESOLVED"
    val priority: String, // "LOW", "MEDIUM", "HIGH"
    val category: String,
    val timestamp: String
)

sealed interface AiState {
    object Idle : AiState
    data class Thinking(val process: String) : AiState
    data class Success(val responseText: String) : AiState
    data class Error(val message: String) : AiState
}

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = SuryaRepository(db.suryaDao())

    // Authentication Flow State
    private val _authState = MutableStateFlow(AuthState.SPLASH)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Dynamic Theme Preference State (null = System, true = Dark, false = Light)
    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    // UI Navigation State
    private val _currentScreen = MutableStateFlow("dashboard")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Active Role State
    private val _currentRole = MutableStateFlow(SuryaRole.RETAILER)
    val currentRole: StateFlow<SuryaRole> = _currentRole.asStateFlow()

    // Notifications List State
    private val _notifications = MutableStateFlow(
        listOf(
            AppNotification("1", "Daily Commission Settled", "Your yesterday's AEPS and DMT commissions of ₹3,450.00 have been settled to your master wallet.", "SUCCESS", "10 mins ago"),
            AppNotification("2", "KYC Approved", "Congratulations! Your retail kiosk KYC profile has been verified and fully approved.", "SUCCESS", "1 hr ago"),
            AppNotification("3", "New Security Standard", "Dynamic UPI payment routing has been upgraded. Please verify your merchant QR status in Profile.", "INFO", "4 hrs ago"),
            AppNotification("4", "NPCI Settlement Delay Notice", "AEPS transaction settlement via State Bank of India is experiencing gateway delays. Please advise customers accordingly.", "WARNING", "1 day ago"),
            AppNotification("5", "B2B Credit Line Due", "Your credit repayment is due in 4 days to avoid automatic penalty buffers.", "ALERT", "2 days ago")
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Support Tickets List State
    private val _supportTickets = MutableStateFlow(
        listOf(
            SupportTicket("TCK-9281", "AEPS Cash Withdrawal Pending Settle", "IN_PROGRESS", "HIGH", "AEPS Service", "2 hours ago"),
            SupportTicket("TCK-8722", "Micro ATM Shipping Status", "RESOLVED", "MEDIUM", "Procurement", "1 day ago"),
            SupportTicket("TCK-7219", "PAN Card Verification Delay", "RESOLVED", "LOW", "Onboarding", "3 days ago")
        )
    )
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    // Repository States
    val walletState: StateFlow<WalletState?> = repository.walletState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val transactions: StateFlow<List<Transaction>> = repository.transactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val products: StateFlow<List<Product>> = repository.products.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val orders: StateFlow<List<Order>> = repository.orders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val kycState: StateFlow<KycState?> = repository.kycState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val chatHistory: StateFlow<List<ChatMessage>> = repository.chatHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Shopping Cart State
    private val _cart = MutableStateFlow<Map<Product, Int>>(emptyMap())
    val cart: StateFlow<Map<Product, Int>> = _cart.asStateFlow()

    // AI State
    private val _aiState = MutableStateFlow<AiState>(AiState.Idle)
    val aiState: StateFlow<AiState> = _aiState.asStateFlow()

    // Temporary notification state for user feedback
    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDefaultDataLoaded()
        }
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun setRole(role: SuryaRole) {
        _currentRole.value = role
        showNotification("Switched views to: ${role.label}")
    }

    fun showNotification(message: String) {
        _snackMessage.value = message
        viewModelScope.launch {
            // Dismiss after 3s
            kotlinx.coroutines.delay(3000)
            if (_snackMessage.value == message) {
                _snackMessage.value = null
            }
        }
    }

    // FinTech Transaction actions
    fun loadWallet(amount: Double) {
        viewModelScope.launch {
            val success = repository.performTransaction(
                service = "DMT",
                type = "CREDIT",
                amount = amount,
                description = "Wallet Loaded via CCAvenue PG"
            )
            if (success) {
                showNotification("Loaded ₹${String.format("%,.2f", amount)} successfully")
            } else {
                showNotification("Wallet load failed")
            }
        }
    }

    fun requestCreditIncrease(requested: Double) {
        viewModelScope.launch {
            repository.requestCreditLineIncrease(requested)
            showNotification("Applied for ₹${String.format("%,.2f", requested)} B2B credit line")
        }
    }

    fun executeServiceTxn(
        service: String,
        amount: Double,
        description: String,
        paymentMethod: String = "WALLET"
    ) {
        viewModelScope.launch {
            val success = repository.performTransaction(
                service = service,
                type = "DEBIT",
                amount = amount,
                description = description,
                paymentMethod = paymentMethod
            )
            if (success) {
                showNotification("Service Payment of ₹${String.format("%,.2f", amount)} completed successfully")
            } else {
                showNotification("Insufficient balance in your $paymentMethod to perform this transaction")
            }
        }
    }

    // Shopping Cart actions
    fun addToCart(product: Product) {
        val currentMap = _cart.value.toMutableMap()
        val count = currentMap[product] ?: 0
        if (count < product.stock) {
            currentMap[product] = count + 1
            _cart.value = currentMap
            showNotification("Added ${product.name} to cart")
        } else {
            showNotification("Out of stock!")
        }
    }

    fun removeFromCart(product: Product) {
        val currentMap = _cart.value.toMutableMap()
        val count = currentMap[product] ?: 0
        if (count > 1) {
            currentMap[product] = count - 1
        } else {
            currentMap.remove(product)
        }
        _cart.value = currentMap
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    fun checkoutCart(paymentMethod: String) {
        viewModelScope.launch {
            val items = _cart.value.map { Pair(it.key, it.value) }
            val success = repository.placeOrder(items, paymentMethod)
            if (success) {
                showNotification("B2B purchase order placed successfully!")
                clearCart()
            } else {
                showNotification("Payment failed. Please check available wallet or credit limits.")
            }
        }
    }

    // KYC Verification
    fun submitKyc(pan: String, gst: String, aadhaar: String, bizName: String) {
        viewModelScope.launch {
            repository.updateKyc(pan, gst, aadhaar, bizName)
            showNotification("KYC Documents submitted and instantly APPROVED!")
        }
    }

    // AI Conversation
    fun sendAiMessage(promptText: String) {
        if (promptText.isBlank()) return

        viewModelScope.launch {
            // Save user message
            repository.addChatMessage("USER", promptText)
            _aiState.value = AiState.Thinking("Initiating gemini-3.1-pro-preview reasoning model with HIGH thinking level...")

            // Call API with custom thinking loop
            val (thinking, responseText) = GeminiApiHelper.generateWithThinking(promptText)

            // Update AI state
            _aiState.value = AiState.Success(responseText)
            repository.addChatMessage("AI", responseText, thinking)
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
            _aiState.value = AiState.Idle
            showNotification("Chat conversation logs cleared")
        }
    }

    // Auth & Utility actions
    fun setAuthState(state: AuthState) {
        _authState.value = state
        if (state == AuthState.AUTHENTICATED) {
            showNotification("Session unlocked. Welcome back!")
        }
    }

    fun toggleDarkMode(dark: Boolean?) {
        _isDarkMode.value = dark
    }

    // Language Preference State ("en", "hi", "kn", "ta", "mr", "te")
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    // Offline database sync indicator
    private val _isOfflineSyncing = MutableStateFlow(false)
    val isOfflineSyncing: StateFlow<Boolean> = _isOfflineSyncing.asStateFlow()

    fun triggerOfflineSync() {
        viewModelScope.launch {
            _isOfflineSyncing.value = true
            kotlinx.coroutines.delay(2000) // Simulate local DB -> Cloud Ledger reconciliation sync duration
            _isOfflineSyncing.value = false
            showNotification("All transaction ledgers and KYC states synchronized with master Cloud Node!")
        }
    }

    fun raiseSupportTicket(subject: String, category: String, priority: String) {
        val nextId = "TCK-${kotlin.random.Random.nextInt(1000, 9999)}"
        val newTicket = SupportTicket(nextId, subject, "OPEN", priority, category, "Just now")
        _supportTickets.value = listOf(newTicket) + _supportTickets.value
        showNotification("Support Ticket $nextId raised successfully!")
    }

    fun addNotification(title: String, message: String, type: String) {
        val nextId = java.util.UUID.randomUUID().toString().take(6)
        val newNotif = AppNotification(nextId, title, message, type, "Just now")
        _notifications.value = listOf(newNotif) + _notifications.value
    }
}
