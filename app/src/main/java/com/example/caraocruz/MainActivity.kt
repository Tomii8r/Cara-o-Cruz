package com.example.caraocruz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.caraocruz.presentation.coin.CoinFlipRoute
import com.example.caraocruz.ui.theme.CaraOCruzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaraOCruzTheme {
                CoinFlipRoute()
            }
        }
    }
}
