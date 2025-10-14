package com.example.moneytracker.domain.model

import com.example.moneytracker.data.db.entities.TransactionEntity
import com.example.moneytracker.data.db.entities.TransactionType

data class Transaction(
    val id: Int,
    val amount: Double,
    val categoryName: String,
    val description: String,
    val date: Long,
    val type: TransactionType
)

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = this.id,
        amount = this.amount,
        categoryName = this.categoryName,
        description = this.description ?: "",
        date = this.date,
        type = this.type
    )
}


fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        amount = this.amount,
        categoryName = this.categoryName,
        description = this.description,
        date = this.date,
        type = this.type
    )
}