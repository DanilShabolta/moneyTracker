package com.example.moneytracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moneytracker.ui.screens.add_edit.AddEditScreen
import com.example.moneytracker.ui.screens.home.HomeScreen
import com.example.moneytracker.ui.screens.statistics.StatisticsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(Routes.ADD) {
            AddEditScreen(navController = navController, transactionId = null)
        }

        composable(
            route = Routes.EDIT,
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getInt("transactionId")
            AddEditScreen(navController = navController, transactionId = transactionId)
        }

        composable(Routes.STATS) {
            StatisticsScreen(navController = navController)
        }
    }
}