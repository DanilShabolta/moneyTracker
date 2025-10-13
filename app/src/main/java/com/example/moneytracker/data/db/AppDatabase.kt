package com.example.moneytracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moneytracker.data.db.dao.TransactionDao
import com.example.moneytracker.data.db.entities.TransactionEntity

@Database(
    entities = [TransactionEntity::class], // Список всех Entity
    version = 1, // Версия базы данных
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // Предоставляем доступ к DAO
    abstract fun transactionDao(): TransactionDao
}