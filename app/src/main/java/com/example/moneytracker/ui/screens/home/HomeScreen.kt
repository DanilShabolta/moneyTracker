package com.example.moneytracker.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneytracker.data.db.entities.TransactionType
import com.example.moneytracker.domain.model.Transaction
import com.example.moneytracker.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel() // Получение ViewModel через Hilt
) {
    // Собираем (коллектим) StateFlow в Compose State
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Трекер Расходов") })
        },
        floatingActionButton = {
            // Кнопка для добавления новой записи
            FloatingActionButton(
                onClick = { navController.navigate(Routes.ADD_EDIT.replace("/{transactionId}", "")) }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить запись")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            // Показать индикатор загрузки, пока данные загружаются
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Отображение общего баланса
                Text(
                    text = "Баланс за год: ${"%.2f".format(state.totalBalance)} ₽",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Список транзакций
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(top = 8.dp)
                ) {
                    items(state.transactions, key = { it.id }) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onEdit = {
                                // Переход к экрану редактирования
                                navController.navigate(Routes.ADD_EDIT.replace("{transactionId}", transaction.id.toString()))
                            },
                            onDelete = {
                                viewModel.onEvent(HomeEvent.DeleteTransaction(transaction))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onEdit // Клик для редактирования
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(transaction.categoryName, style = MaterialTheme.typography.titleMedium)
                Text(transaction.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            // Сумма с цветом в зависимости от типа
            Text(
                text = "${"%.2f".format(transaction.amount)} ₽",
                color = if (transaction.type == TransactionType.INCOME) Color.Green else Color.Red,
                style = MaterialTheme.typography.titleMedium
            )
            // (В реальном приложении здесь лучше использовать контекстное меню для удаления)
        }
    }
}