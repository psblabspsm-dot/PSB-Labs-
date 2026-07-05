package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_state")
data class WalletState(
    @PrimaryKey val id: Int = 1,
    val balance: Double = 250000.0,
    val creditLimit: Double = 1000000.0,
    val usedCredit: Double = 150000.0,
    val commissionEarned: Double = 12450.0,
    val cashbackEarned: Double = 3520.0
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // "CREDIT", "DEBIT", "COMMISSION", "CASHBACK"
    val amount: Double,
    val service: String, // "RECHARGE", "BBPS", "AEPS", "DMT", "CREDIT_PAY", "QR_PAY", "ORDER"
    val description: String,
    val status: String, // "SUCCESS", "PENDING", "FAILED"
    val referenceId: String
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val stock: Int,
    val category: String,
    val description: String,
    val iconName: String
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val productNames: String,
    val totalAmount: Double,
    val quantity: Int,
    val status: String, // "PENDING", "PROCESSING", "SHIPPED", "DELIVERED"
    val paymentMethod: String // "WALLET", "CREDIT_LINE", "CASHFREE"
)

@Entity(tableName = "kyc_state")
data class KycState(
    @PrimaryKey val id: Int = 1,
    val panNumber: String = "",
    val gstNumber: String = "",
    val aadhaarNumber: String = "",
    val businessName: String = "",
    val status: String = "PENDING" // "PENDING", "SUBMITTED", "APPROVED", "REJECTED"
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "USER" or "AI"
    val content: String,
    val thinkingProcess: String? = null, // Stores reasoning outputs if high thinking is used
    val timestamp: Long = System.currentTimeMillis()
)
