package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class SuryaRepository(private val dao: SuryaDao) {

    val walletState: Flow<WalletState?> = dao.getWalletStateFlow()
    val transactions: Flow<List<Transaction>> = dao.getTransactionsFlow()
    val products: Flow<List<Product>> = dao.getProductsFlow()
    val orders: Flow<List<Order>> = dao.getOrdersFlow()
    val kycState: Flow<KycState?> = dao.getKycStateFlow()
    val chatHistory: Flow<List<ChatMessage>> = dao.getChatHistoryFlow()

    suspend fun ensureDefaultDataLoaded() {
        // 1. Initialise Wallet State if empty
        val currentWallet = dao.getWalletStateDirect()
        if (currentWallet == null) {
            dao.insertWalletState(WalletState())
            
            // Seed a few default transactions
            val initialTxns = listOf(
                Transaction(
                    type = "CREDIT",
                    amount = 50000.0,
                    service = "DMT",
                    description = "Wallet loaded via Cashfree PG",
                    status = "SUCCESS",
                    referenceId = "TXN${Random.nextInt(100000, 999999)}"
                ),
                Transaction(
                    type = "DEBIT",
                    amount = 4500.0,
                    service = "BBPS",
                    description = "Electricity Bill Payment - BESCOM",
                    status = "SUCCESS",
                    referenceId = "TXN${Random.nextInt(100000, 999999)}"
                ),
                Transaction(
                    type = "COMMISSION",
                    amount = 180.0,
                    service = "AEPS",
                    description = "Commission Earned - Cash Withdrawal",
                    status = "SUCCESS",
                    referenceId = "TXN${Random.nextInt(100000, 999999)}"
                )
            )
            for (t in initialTxns) {
                dao.insertTransaction(t)
            }
        }

        // 2. Initialise Products if empty
        val currentProducts = dao.getProductsFlow().firstOrNull()
        if (currentProducts.isNullOrEmpty()) {
            val defaultProducts = listOf(
                Product(
                    id = "p1",
                    name = "Surya Micro ATM POS Terminal",
                    price = 2499.0,
                    stock = 45,
                    category = "Biometric Devices",
                    description = "Handheld Micro ATM device with thermal printer, Bluetooth & Wi-Fi support. Enables AEPS & card payments.",
                    iconName = "atm"
                ),
                Product(
                    id = "p2",
                    name = "Mantra MFS100 Biometric Scanner",
                    price = 1899.0,
                    stock = 120,
                    category = "Biometric Devices",
                    description = "STQC Certified fingerprint scanner for AEPS cash withdrawals and KYC verifications.",
                    iconName = "fingerprint"
                ),
                Product(
                    id = "p3",
                    name = "High-Speed Dual Sim 4G Router",
                    price = 1499.0,
                    stock = 30,
                    category = "Network Accessories",
                    description = "Uninterrupted connectivity router with fallback switching, customized for remote B2B kiosk environments.",
                    iconName = "router"
                ),
                Product(
                    id = "p4",
                    name = "Thermal Receipt Paper Rolls (Pack of 50)",
                    price = 450.0,
                    stock = 250,
                    category = "Consumables",
                    description = "High sensitivity premium receipt paper for POS terminals and micro-ATM billing.",
                    iconName = "receipt"
                ),
                Product(
                    id = "p5",
                    name = "Surya Authorized Merchant Glow Signboard",
                    price = 599.0,
                    stock = 75,
                    category = "Marketing Kits",
                    description = "Outdoor water-proof LED backlit branding board to drive footfall to your retail kiosk.",
                    iconName = "storefront"
                )
            )
            dao.insertProducts(defaultProducts)
        }

        // 3. Initialise KYC state if empty
        val currentKyc = dao.getKycStateDirect()
        if (currentKyc == null) {
            dao.insertKycState(KycState())
        }
    }

    suspend fun performTransaction(
        service: String,
        type: String, // "DEBIT", "CREDIT"
        amount: Double,
        description: String,
        paymentMethod: String = "WALLET" // "WALLET" or "CREDIT_LINE"
    ): Boolean {
        val state = dao.getWalletStateDirect() ?: return false
        val newWalletState = when (paymentMethod) {
            "WALLET" -> {
                if (type == "DEBIT" && state.balance < amount) return false
                val nextBal = if (type == "DEBIT") state.balance - amount else state.balance + amount
                val commReward = if (type == "DEBIT") amount * 0.005 else 0.0 // 0.5% commission
                val cbReward = if (type == "DEBIT") amount * 0.001 else 0.0 // 0.1% cashback
                state.copy(
                    balance = nextBal,
                    commissionEarned = state.commissionEarned + commReward,
                    cashbackEarned = state.cashbackEarned + cbReward
                )
            }
            "CREDIT_LINE" -> {
                val availableCredit = state.creditLimit - state.usedCredit
                if (type == "DEBIT" && availableCredit < amount) return false
                val nextUsed = if (type == "DEBIT") state.usedCredit + amount else state.usedCredit - amount
                state.copy(usedCredit = nextUsed)
            }
            else -> state
        }

        dao.insertWalletState(newWalletState)

        // Record main transaction
        val txnRef = "TXN${Random.nextInt(100000, 999999)}"
        dao.insertTransaction(
            Transaction(
                type = type,
                amount = amount,
                service = service,
                description = "$description ($paymentMethod)",
                status = "SUCCESS",
                referenceId = txnRef
            )
        )

        // Record commission/cashback transactions if wallet debit occurred
        if (paymentMethod == "WALLET" && type == "DEBIT") {
            val comm = amount * 0.005
            if (comm > 0) {
                dao.insertTransaction(
                    Transaction(
                        type = "COMMISSION",
                        amount = comm,
                        service = service,
                        description = "Earned 0.5% Commission",
                        status = "SUCCESS",
                        referenceId = "COM${Random.nextInt(100000, 999999)}"
                    )
                )
            }
            val cb = amount * 0.001
            if (cb > 0) {
                dao.insertTransaction(
                    Transaction(
                        type = "CASHBACK",
                        amount = cb,
                        service = service,
                        description = "Earned 0.1% Instant Cashback",
                        status = "SUCCESS",
                        referenceId = "CSB${Random.nextInt(100000, 999999)}"
                    )
                )
            }
        }

        return true
    }

    suspend fun placeOrder(
        productsSelected: List<Pair<Product, Int>>,
        paymentMethod: String
    ): Boolean {
        if (productsSelected.isEmpty()) return false
        val total = productsSelected.sumOf { it.first.price * it.second }
        val qty = productsSelected.sumOf { it.second }
        val names = productsSelected.joinToString(", ") { "${it.first.name} x${it.second}" }

        // Deduct payment
        val success = performTransaction(
            service = "ORDER",
            type = "DEBIT",
            amount = total,
            description = "B2B Purchase: $names",
            paymentMethod = paymentMethod
        )

        if (success) {
            // Insert Order
            dao.insertOrder(
                Order(
                    productNames = names,
                    totalAmount = total,
                    quantity = qty,
                    status = "PROCESSING",
                    paymentMethod = paymentMethod
                )
            )

            // Deduct stock
            for (item in productsSelected) {
                dao.decreaseStock(item.first.id, item.second)
            }
            return true
        }

        return false
    }

    suspend fun updateKyc(pan: String, gst: String, aadhaar: String, bizName: String) {
        val current = dao.getKycStateDirect() ?: KycState()
        val next = current.copy(
            panNumber = pan,
            gstNumber = gst,
            aadhaarNumber = aadhaar,
            businessName = bizName,
            status = "APPROVED" // Automatically approve for prototyping demonstration
        )
        dao.insertKycState(next)
    }

    suspend fun requestCreditLineIncrease(requestedLimit: Double) {
        val current = dao.getWalletStateDirect() ?: WalletState()
        dao.insertWalletState(current.copy(creditLimit = requestedLimit))
        
        dao.insertTransaction(
            Transaction(
                type = "CREDIT",
                amount = requestedLimit,
                service = "CREDIT_PAY",
                description = "B2B credit line updated to ₹${String.format("%,.2f", requestedLimit)}",
                status = "SUCCESS",
                referenceId = "CRE${Random.nextInt(100000, 999999)}"
            )
        )
    }

    suspend fun addChatMessage(sender: String, content: String, thinkingProcess: String? = null) {
        dao.insertChatMessage(
            ChatMessage(
                sender = sender,
                content = content,
                thinkingProcess = thinkingProcess
            )
        )
    }

    suspend fun clearChat() {
        dao.clearChatHistory()
    }
}
