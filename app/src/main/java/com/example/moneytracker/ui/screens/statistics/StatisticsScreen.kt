package com.example.moneytracker.ui.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.patrykand.compose.chart.pie.PieChart
import com.patrykand.compose.chart.pie.PieChartData
import com.patrykand.compose.chart.pie.draw.PieChartColors
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val decimalFormat = remember { DecimalFormat("#,##0.00") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отчеты и Статистика") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Заголовок отчета
                item {
                    Text(
                        text = "Отчет за: ${state.periodLabel}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Общие итоги
                item {
                    TotalSummaryCard(
                        expense = state.totalExpense,
                        income = state.totalIncome,
                        decimalFormat = decimalFormat
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // График (Круговая диаграмма расходов)
                if (state.totalExpense > 0) {
                    item {
                        Text("Распределение расходов по категориям", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                        ExpensePieChart(state.expensesByCategory)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Детальный список по категориям
                    item {
                        Text("Детализация", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    items(state.expensesByCategory) { stat ->
                        CategoryStatItem(stat, decimalFormat)
                    }
                } else {
                    item {
                        Text("Нет расходов за выбранный период.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun TotalSummaryCard(expense: Double, income: Double, decimalFormat: DecimalFormat) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Общие Итоги", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text("Доходы: ${decimalFormat.format(income)} ₽", color = Color.Green)
            Text("Расходы: ${decimalFormat.format(expense)} ₽", color = Color.Red)
            Divider(Modifier.padding(vertical = 4.dp))
            val balance = income - expense
            Text(
                "Баланс: ${decimalFormat.format(balance)} ₽",
                color = if (balance >= 0) MaterialTheme.colorScheme.primary else Color.Red,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ExpensePieChart(stats: List<CategoryStat>) {
    // Преобразование данных для PieChart
    val pieChartData = stats.mapIndexed { index, stat ->
        PieChartData.Slice(
            value = stat.amount.toFloat(),
            color = ChartColors[index % ChartColors.size], // Используем циклический набор цветов
            label = stat.categoryName
        )
    }

    if (pieChartData.isNotEmpty()) {
        // Компонент круговой диаграммы
        PieChart(
            pieChartData = pieChartData,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            // Настройка цветов (минимализм)
            colors = PieChartColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                chartColor = MaterialTheme.colorScheme.primary, // Основной цвет не используется для каждого среза
            ),
            // Дополнительная настройка: можно добавить легенду, центр. текст и т.д.
        )
    }
}

@Composable
fun CategoryStatItem(stat: CategoryStat, decimalFormat: DecimalFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(stat.categoryName, modifier = Modifier.weight(1f))
        Text("${stat.percentage.toInt()}%", modifier = Modifier.width(50.dp))
        Text(
            decimalFormat.format(stat.amount) + " ₽",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(100.dp)
        )
    }
    Divider()
}

// Набор спокойных цветов для минималистичного дизайна
val ChartColors = listOf(
    Color(0xFF64B5F6), // Светло-голубой
    Color(0xFF81C784), // Светло-зеленый
    Color(0xFFFFB74D), // Светло-оранжевый
    Color(0xFFE57373), // Светло-красный
    Color(0xFF9575CD), // Светло-фиолетовый
    Color(0xFF4DB6AC), // Бирюзовый
    Color(0xFFF06292)  // Розовый
)