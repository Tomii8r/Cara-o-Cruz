package com.example.caraocruz.ui.theme

import androidx.compose.material3.MaterialTheme
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

@Composable
fun CaraOCruzTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
