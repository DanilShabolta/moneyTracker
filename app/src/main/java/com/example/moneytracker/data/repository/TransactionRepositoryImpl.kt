package com.example.moneytracker.data.repository

import com.example.moneytracker.data.db.dao.TransactionDao
import com.example.moneytracker.data.db.entities.TransactionEntity
import com.example.moneytracker.data.db.entities.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Аннотация @Inject для внедрения зависимостей через Hilt
class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return dao.getAllTransactions()
    }

    override suspend fun getTransactionById(id: Int): TransactionEntity? {
        return dao.getTransactionById(id)
    }

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        dao.insertTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }

    override fun getTotalAmountByTypeAndPeriod(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<Double?> {
        return dao.getTotalAmountByTypeAndPeriod(type, startDate, endDate)
    }

    override fun getFilteredTransactions(type: TransactionType?): Flow<List<TransactionEntity>> {
        // Простая логика фильтрации в Repository
        return if (type == null) {
            dao.getAllTransactions()
        } else {
            dao.getTransactionsByType(type)
        }
    }
}