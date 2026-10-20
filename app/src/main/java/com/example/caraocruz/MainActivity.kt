package com.example.caraocruz

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
        setContent {
            CaraOCruzTheme {
                val viewModel: CoinFlipViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                var showSettings by rememberSaveable { mutableStateOf(false) }
                CoinFlipScreen(
                    uiState = uiState,
                    onFlipClick = viewModel::flipCoin,
                    showSettings = showSettings,
                    onSettingsClick = { showSettings = true },
                    onDismissSettings = { showSettings = false }
                )
            }
        }
    }
}
