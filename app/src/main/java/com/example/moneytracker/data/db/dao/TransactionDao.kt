package com.example.moneytracker.data.db.dao

import androidx.room.*
import com.example.moneytracker.data.db.entities.TransactionEntity
import com.example.moneytracker.data.db.entities.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // Получение всех транзакций, отсортированных по дате.
    // Flow делает эту операцию реактивной: UI автоматически обновится при изменении БД.
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    // Вставка или обновление (по id) транзакции
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    // Внутри интерфейса TransactionDao
// ...
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): TransactionEntity?
// ...

    // Удаление транзакции
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    // Для статистики: получение суммы по типу и за период
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    fun getTotalAmountByTypeAndPeriod(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<Double?>

    // Для фильтрации: получение по типу транзакции
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>>
}