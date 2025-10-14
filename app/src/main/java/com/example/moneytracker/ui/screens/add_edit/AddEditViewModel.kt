package com.example.moneytracker.ui.screens.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.data.db.entities.TransactionType
import com.example.moneytracker.data.repository.TransactionRepository
import com.example.moneytracker.domain.model.Transaction
import com.example.moneytracker.domain.model.toDomain // <-- Нужен для преобразования Entity в Domain Model
import com.example.moneytracker.domain.model.toEntity // <-- Нужен для преобразования Domain Model в Entity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject // <-- Убедитесь, что этот импорт есть!

// 1. Определение состояния формы
data class AddEditState(
    val id: Int? = null,
    val amount: String = "",
    val categoryName: String = "",
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val type: TransactionType = TransactionType.EXPENSE,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val showDatePicker: Boolean = false,
    val error: String? = null // Добавим поле для ошибок
)

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditState())
    val state: StateFlow<AddEditState> = _state.asStateFlow()

    private val transactionId: Int? = savedStateHandle.get<Int>("transactionId")

    init {
        if (transactionId != null && transactionId > 0) {
            loadTransaction(transactionId)
        }
    }

    private fun loadTransaction(id: Int) {
        viewModelScope.launch {
            repository.getTransactionById(id)?.let { entity ->
                val transaction = entity.toDomain()
                _state.value = _state.value.copy(
                    id = transaction.id,
                    amount = transaction.amount.toString(),
                    categoryName = transaction.categoryName,
                    description = transaction.description,
                    date = transaction.date,
                    type = transaction.type
                )
            } ?: run {
                _state.value = _state.value.copy(error = "Транзакция не найдена.")
            }
        }
    }

    fun onAmountChange(newAmount: String) {
        _state.value = _state.value.copy(amount = newAmount, error = null)
    }

    fun onCategoryChange(newCategory: String) {
        _state.value = _state.value.copy(categoryName = newCategory, error = null)
    }

    fun onDescriptionChange(newDescription: String) {
        _state.value = _state.value.copy(description = newDescription, error = null)
    }

    fun onTypeChange(newType: TransactionType) {
        _state.value = _state.value.copy(type = newType, error = null)
    }

    fun onDateChange(newDate: Long) {
        _state.value = _state.value.copy(date = newDate, showDatePicker = false)
    }

    fun onShowDatePicker(show: Boolean) {
        _state.value = _state.value.copy(showDatePicker = show)
    }

    fun saveTransaction() {
        val currentState = _state.value

        val amountValue = currentState.amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            _state.value = currentState.copy(error = "Введите корректную сумму.")
            return
        }
        if (currentState.categoryName.isBlank()) {
            _state.value = currentState.copy(error = "Введите категорию.")
            return
        }

        _state.value = currentState.copy(isSaving = true, error = null)

        viewModelScope.launch {
            val transactionToSave = Transaction(
                id = currentState.id ?: 0,
                amount = amountValue,
                categoryName = currentState.categoryName.trim(),
                description = currentState.description.trim(),
                date = currentState.date,
                type = currentState.type
            )

            repository.insertTransaction(transactionToSave.toEntity())

            _state.value = currentState.copy(isSaving = false, saveSuccess = true)
        }
    }
}