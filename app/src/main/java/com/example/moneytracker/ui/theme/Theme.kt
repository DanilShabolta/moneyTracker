package com.example.moneytracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 1. Определение цветовых схем (можно использовать любые цвета)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9), // Светло-голубой
    secondary = Color(0xFF80CBC4), // Светло-бирюзовый
    tertiary = Color(0xFFFFCC80), // Светло-оранжевый
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3), // Синий
    secondary = Color(0xFF009688), // Бирюзовый
    tertiary = Color(0xFFFF9800), // Оранжевый
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White
)

// 2. Composable функция темы
@Composable
fun MoneyTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Мы можем добавить логику для Android 12+ (динамические цвета), но пока оставим простую логику.
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // Используем стандартную типографику
        content = content
    )
}