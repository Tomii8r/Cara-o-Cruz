package com.example.caraocruz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caraocruz.presentation.coin.CoinFlipScreen
import com.example.caraocruz.presentation.coin.CoinFlipViewModel
import com.example.caraocruz.ui.theme.CaraOCruzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaraOCruzTheme {
                val viewModel: CoinFlipViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                CoinFlipScreen(
                    uiState = uiState,
                    onFlipClick = viewModel::flipCoin
                )
            }
        }
    }
}
