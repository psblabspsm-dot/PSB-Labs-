package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SuryaDao {
    // Wallet State
    @Query("SELECT * FROM wallet_state WHERE id = 1 LIMIT 1")
    fun getWalletStateFlow(): Flow<WalletState?>

    @Query("SELECT * FROM wallet_state WHERE id = 1 LIMIT 1")
    suspend fun getWalletStateDirect(): WalletState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletState(state: WalletState)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getTransactionsFlow(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    // Products
    @Query("SELECT * FROM products")
    fun getProductsFlow(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId")
    suspend fun decreaseStock(productId: String, quantity: Int)

    // Orders
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getOrdersFlow(): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    // KYC State
    @Query("SELECT * FROM kyc_state WHERE id = 1 LIMIT 1")
    fun getKycStateFlow(): Flow<KycState?>

    @Query("SELECT * FROM kyc_state WHERE id = 1 LIMIT 1")
    suspend fun getKycStateDirect(): KycState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKycState(state: KycState)

    // Chat History
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getChatHistoryFlow(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}
