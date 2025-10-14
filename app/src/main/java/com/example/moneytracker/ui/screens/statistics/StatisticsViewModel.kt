package com.example.moneytracker.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.data.db.entities.TransactionType
import com.example.moneytracker.data.repository.TransactionRepository
import com.example.moneytracker.domain.model.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class CategoryStat(
    val categoryName: String,
    val amount: Double,
    val percentage: Float
)

data class StatisticsState(
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val expensesByCategory: List<CategoryStat> = emptyList(),
    val periodLabel: String = "Текущий месяц",
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    init {
        loadMonthlyStats()
    }

    private fun loadMonthlyStats() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startDate = calendar.timeInMillis
            val endDate = System.currentTimeMillis()

            repository.getAllTransactions()
                .map { entities -> entities.filter { it.date in startDate..endDate } }
                .map { entities -> entities.map { it.toDomain() } }
                .collect { transactions ->
                    val totalExpense = transactions
                        .filter { it.type == TransactionType.EXPENSE }
                        .sumOf { it.amount }

                    val totalIncome = transactions
                        .filter { it.type == TransactionType.INCOME }
                        .sumOf { it.amount }

                    val expensesByCategory = transactions
                        .filter { it.type == TransactionType.EXPENSE }
                        .groupBy { it.categoryName }
                        .map { (category, list) ->
                            val amount = list.sumOf { it.amount }
                            val percentage = if (totalExpense > 0) (amount / totalExpense * 100).toFloat() else 0f
                            CategoryStat(category, amount, percentage)
                        }
                        .sortedByDescending { it.amount }

                    _state.value = _state.value.copy(
                        totalExpense = totalExpense,
                        totalIncome = totalIncome,
                        expensesByCategory = expensesByCategory,
                        isLoading = false
                    )
                }
        }
    }
}