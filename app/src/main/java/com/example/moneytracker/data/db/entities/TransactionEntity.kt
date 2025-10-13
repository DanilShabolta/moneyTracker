package com.example.moneytracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Определение типа транзакции для удобства
enum class TransactionType {
    EXPENSE, INCOME
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val categoryName: String, // Название категории
    val description: String?,
    val date: Long, // Храним дату как метку времени (timestamp) в Long
    val type: TransactionType // Расход или Доход
)