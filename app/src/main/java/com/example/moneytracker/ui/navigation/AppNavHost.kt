package com.example.moneytracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneytracker.ui.screens.add_edit.AddEditScreen
import com.example.moneytracker.ui.screens.home.HomeScreen
import com.example.moneytracker.ui.screens.statistics.StatisticsScreen

// Определение маршрутов (Routes)
object Routes {
    const val HOME = "home"
    const val ADD_EDIT = "add_edit/{transactionId}" // Маршрут с аргументом для редактирования
    const val STATS = "statistics"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME // Стартовый экран
    ) {
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        // Передача аргумента "transactionId"
        composable(Routes.ADD_EDIT) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")?.toIntOrNull()
            AddEditScreen(navController = navController, transactionId = transactionId)
        }

        composable(Routes.STATS) {
            StatisticsScreen(navController = navController)
        }
    }
}