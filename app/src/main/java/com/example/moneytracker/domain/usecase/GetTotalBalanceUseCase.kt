package com.example.moneytracker.domain.usecase

import com.example.moneytracker.data.db.entities.TransactionType
import com.example.moneytracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetTotalBalanceUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<Double> {
        val incomeFlow = repository.getTotalAmountByTypeAndPeriod(
            TransactionType.INCOME,
            startDate,
            endDate
        )
        val expenseFlow = repository.getTotalAmountByTypeAndPeriod(
            TransactionType.EXPENSE,
            startDate,
            endDate
        )

        return incomeFlow.combine(expenseFlow) { income, expense ->
            val totalIncome = income ?: 0.0
            val totalExpense = expense ?: 0.0

            totalIncome - totalExpense
        }
    }
}