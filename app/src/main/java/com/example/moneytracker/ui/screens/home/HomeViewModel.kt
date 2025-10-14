package com.example.moneytracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.data.db.entities.TransactionType
import com.example.moneytracker.domain.model.Transaction
import com.example.moneytracker.domain.model.toDomain
import com.example.moneytracker.domain.usecase.GetTotalBalanceUseCase
import com.example.moneytracker.data.repository.TransactionRepository
import com.example.moneytracker.domain.model.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Calendar

sealed class HomeEvent {
    data class DeleteTransaction(val transaction: Transaction) : HomeEvent()
    data class ApplyFilter(val type: TransactionType?) : HomeEvent()
}

data class HomeState(
    val transactions: List<Transaction> = emptyList(),
    val totalBalance: Double = 0.0,
    val selectedFilter: TransactionType? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val getTotalBalanceUseCase: GetTotalBalanceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val filterFlow = MutableStateFlow<TransactionType?>(null)

    init {
        collectData()
    }

    private fun collectData() {
        viewModelScope.launch {
            val transactionsFlow = filterFlow.flatMapLatest { filterType ->
                repository.getFilteredTransactions(filterType)
                    .map { entities -> entities.map { it.toDomain() } }
            }

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_YEAR, 1)
            val startOfYear = calendar.timeInMillis
            val endOfMonth = System.currentTimeMillis()

            val balanceFlow = getTotalBalanceUseCase(startOfYear, endOfMonth)

            combine(transactionsFlow, balanceFlow, filterFlow) { transactions, balance, filter ->
                HomeState(
                    transactions = transactions,
                    totalBalance = balance,
                    selectedFilter = filter,
                    isLoading = false
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.DeleteTransaction -> {
                viewModelScope.launch {
                    repository.deleteTransaction(event.transaction.toEntity())
                }
            }
            is HomeEvent.ApplyFilter -> {
                filterFlow.value = event.type
            }
        }
    }
}