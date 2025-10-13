package com.example.moneytracker.domain.model

import com.example.moneytracker.data.db.entities.TransactionEntity
import com.example.moneytracker.data.db.entities.TransactionType

/**
 * Чистая модель транзакции, используемая во ViewModel и UI.
 */
data class Transaction(
    val id: Int,
    val amount: Double,
    val categoryName: String,
    val description: String,
    val date: Long,
    val type: TransactionType
)

// ======================================================
// Функции-расширения для преобразования (Mappers)
// ======================================================

/**
 * Преобразует TransactionEntity (из БД) в Transaction (для UI/Domain).
 */
fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = this.id,
        amount = this.amount,
        categoryName = this.categoryName,
        description = this.description ?: "", // Обработка nullable-поля
        date = this.date,
        type = this.type
    )
}

/**
 * Преобразует Transaction (из UI/Domain) в TransactionEntity (для БД).
 */
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