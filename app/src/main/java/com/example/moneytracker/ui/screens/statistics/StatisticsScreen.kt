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
import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.drawscope.drawArc
import androidx.compose.foundation.Canvas
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
                item {
                    Text(
                        text = "Отчет за: ${state.periodLabel}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    TotalSummaryCard(
                        expense = state.totalExpense,
                        income = state.totalIncome,
                        decimalFormat = decimalFormat
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (state.totalExpense > 0) {
                    item {
                        Text("Распределение расходов по категориям", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                        ExpensePieChart(state.expensesByCategory)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

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

    val totalAmount = stats.sumOf { it.amount }
    if (totalAmount == 0.0) return

    var startAngle = 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2f
            val circleSize = Size(radius * 2, radius * 2)

            stats.forEachIndexed { index, stat ->
                val sweepAngle = (stat.amount / totalAmount * 360).toFloat()

                drawArc(
                    color = ChartColors[index % ChartColors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        (size.width - circleSize.width) / 2,
                        (size.height - circleSize.height) / 2
                    ),
                    size = circleSize
                )
                startAngle += sweepAngle
            }
        }
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

val ChartColors = listOf(
    Color(0xFF64B5F6),
    Color(0xFF81C784),
    Color(0xFFFFB74D),
    Color(0xFFE57373),
    Color(0xFF9575CD),
    Color(0xFF4DB6AC),
    Color(0xFFF06292)
)