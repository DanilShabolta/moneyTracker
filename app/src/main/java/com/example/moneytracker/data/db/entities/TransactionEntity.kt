package com.example.moneytracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    EXPENSE, INCOME
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val categoryName: String,
    val description: String?,
    val date: Long,
    val type: TransactionType
)