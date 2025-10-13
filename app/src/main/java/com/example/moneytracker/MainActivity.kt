package com.example.moneytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import com.example.moneytracker.ui.navigation.AppNavHost

@AndroidEntryPoint // Использование Hilt в Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoneyTrackerTheme {
                AppNavHost() // Запуск навигации
            }
        }
    }
}