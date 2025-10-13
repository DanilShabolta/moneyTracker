package com.example.moneytracker.ui.screens.add_edit

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneytracker.data.db.entities.TransactionType
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    navController: NavController,
    transactionId: Int?, // null, если добавляем; ID, если редактируем
    viewModel: AddEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Эффект для навигации после успешного сохранения
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            Toast.makeText(context, "Запись сохранена!", Toast.LENGTH_SHORT).show()
            navController.popBackStack() // Возврат на предыдущий экран
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (transactionId == null) "Новая Запись" else "Редактировать Запись") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Выбор типа (Расход/Доход)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TransactionType.entries.forEach { type ->
                    val isSelected = state.type == type
                    OutlinedButton(
                        onClick = { viewModel.onTypeChange(type) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text(if (type == TransactionType.EXPENSE) "Расход" else "Доход")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Поле суммы
            OutlinedTextField(
                value = state.amount,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Сумма (₽)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 3. Поле категории
            OutlinedTextField(
                value = state.categoryName,
                onValueChange = viewModel::onCategoryChange,
                label = { Text("Категория (например, Питание)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 4. Поле описания
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 5. Выбор даты
            DateSelectionField(
                dateMillis = state.date,
                onShowDatePicker = { viewModel.onShowDatePicker(true) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Кнопка сохранения
            Button(
                onClick = viewModel::saveTransaction,
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Сохранить Запись")
                }
            }
        }
    }

    // Отображение диалога выбора даты
    if (state.showDatePicker) {
        DatePickerDialog(
            initialSelectedDateMillis = state.date,
            onDateSelected = viewModel::onDateChange,
            onDismiss = { viewModel.onShowDatePicker(false) }
        )
    }
}

// Вспомогательный Composable для выбора даты
@Composable
fun DateSelectionField(dateMillis: Long, onShowDatePicker: () -> Unit) {
    val formatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Дата: ${formatter.format(Date(dateMillis))}", style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onShowDatePicker) {
            Icon(Icons.Filled.DateRange, contentDescription = "Выбрать дату")
        }
    }
}

// Вспомогательный Composable для DatePicker (используем Material3 DatePicker)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialSelectedDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis
    )

    // В Compose 1.2.0 DatePickerDialog требует Dialog (или использовать Compose Material)
    // Для простоты здесь показан принцип:
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите дату") },
        text = {
            DatePicker(state = datePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                    onDismiss()
                }
            ) { Text("ОК") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}