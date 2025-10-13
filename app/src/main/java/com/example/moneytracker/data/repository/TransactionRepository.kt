package com.example.moneytracker.data.repository

import com.example.moneytracker.data.db.entities.TransactionEntity
import com.example.moneytracker.data.db.entities.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    suspend fun insertTransaction(transaction: TransactionEntity)

    suspend fun getTransactionById(id: Int): TransactionEntity?

    suspend fun deleteTransaction(transaction: TransactionEntity)

    fun getTotalAmountByTypeAndPeriod(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<Double?>

    fun getFilteredTransactions(type: TransactionType?): Flow<List<TransactionEntity>>
}