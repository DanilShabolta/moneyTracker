package com.example.moneytracker.di

import android.app.Application
import androidx.room.Room
import com.example.moneytracker.data.db.AppDatabase
import com.example.moneytracker.data.db.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Hilt Provider: Предоставляет экземпляр AppDatabase.
     */
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "money_tracker_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Hilt Provider: Предоставляет экземпляр TransactionDao.
     */
    @Provides
    @Singleton
    fun provideTransactionDao(db: AppDatabase): TransactionDao {
        return db.transactionDao()
    }
}
