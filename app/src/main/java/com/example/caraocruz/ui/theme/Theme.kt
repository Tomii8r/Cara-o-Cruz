package com.example.caraocruz.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VioletAccent,
    onPrimary = Color.White,
    secondary = VioletSoft,
    onSecondary = VioletPrimary,
    tertiary = GoldDetail,
    background = BackgroundTop,
    onBackground = VioletPrimary,
    surface = Color.White,
    onSurface = VioletPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.White,
    secondary = DarkGlow,
    onSecondary = DarkBackground,
    tertiary = DarkGold,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkTextSecondary,
    outlineVariant = DarkOutline
)

@Composable
fun CaraOCruzTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
