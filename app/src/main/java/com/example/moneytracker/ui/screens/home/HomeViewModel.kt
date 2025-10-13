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

// 1. Определение событий, которые UI может отправить ViewModel
sealed class HomeEvent {
    data class DeleteTransaction(val transaction: Transaction) : HomeEvent()
    data class ApplyFilter(val type: TransactionType?) : HomeEvent() // null для всех
}

// 2. Определение состояния экрана (все, что нужно для отображения)
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

    // Внутренний MutableStateFlow для управления состоянием
    private val _state = MutableStateFlow(HomeState())
    // Публичный StateFlow, который собирается в Compose
    val state: StateFlow<HomeState> = _state.asStateFlow()

    // Flow, отслеживающий текущий выбранный фильтр (тип транзакции)
    private val filterFlow = MutableStateFlow<TransactionType?>(null)

    init {
        // Запускаем сбор данных при инициализации ViewModel
        collectData()
    }

    private fun collectData() {
        viewModelScope.launch {
            // Flow, который возвращает либо все транзакции, либо отфильтрованные
            val transactionsFlow = filterFlow.flatMapLatest { filterType ->
                repository.getFilteredTransactions(filterType)
                    .map { entities -> entities.map { it.toDomain() } }
            }

            // Устанавливаем период для расчета баланса (например, текущий год)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_YEAR, 1)
            val startOfYear = calendar.timeInMillis
            val endOfMonth = System.currentTimeMillis() // До текущего момента

            // Flow, который рассчитывает общий баланс
            val balanceFlow = getTotalBalanceUseCase(startOfYear, endOfMonth)

            // Комбинируем все необходимые потоки для обновления HomeState
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

    // Обработчик событий от UI
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.DeleteTransaction -> {
                viewModelScope.launch {
                    // Используем toEntity для преобразования обратно в Entity перед удалением
                    repository.deleteTransaction(event.transaction.toEntity())
                }
            }
            is HomeEvent.ApplyFilter -> {
                // Обновляем фильтр, что автоматически вызовет пересборку combine Flow
                filterFlow.value = event.type
            }
        }
    }
}