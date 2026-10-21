package com.example.caraocruz

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caraocruz.presentation.coin.CoinFlipScreen
import com.example.caraocruz.presentation.coin.CoinFlipViewModel
import com.example.caraocruz.ui.theme.CaraOCruzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        val preferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        setContent {
            var isDarkMode by rememberSaveable {
                mutableStateOf(preferences.getBoolean("dark_mode", false))
            }

            val view = LocalView.current
            SideEffect {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !isDarkMode
                    isAppearanceLightNavigationBars = !isDarkMode
                }
            }

            CaraOCruzTheme(darkTheme = isDarkMode) {
                val viewModel: CoinFlipViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                var showSettings by rememberSaveable { mutableStateOf(false) }
                CoinFlipScreen(
                    uiState = uiState,
                    onFlipClick = viewModel::flipCoin,
                    showSettings = showSettings,
                    isDarkMode = isDarkMode,
                    onSettingsClick = { showSettings = true },
                    onDismissSettings = { showSettings = false },
                    onDarkModeChange = { enabled ->
                        isDarkMode = enabled
                        preferences.edit().putBoolean("dark_mode", enabled).apply()
                    }
                )
            }
        }
    }
}
