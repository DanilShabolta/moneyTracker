package com.example.moneytracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moneytracker.data.db.dao.TransactionDao
import com.example.moneytracker.data.db.entities.TransactionEntity

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}