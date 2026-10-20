package com.example.caraocruz.presentation.coin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

const val FLIP_DURATION_MS = 1500L

class CoinFlipViewModel : ViewModel() {

    private val random = Random.Default

    private val _uiState = MutableStateFlow<CoinFlipUiState>(CoinFlipUiState.Idle)
    val uiState: StateFlow<CoinFlipUiState> = _uiState.asStateFlow()

    private var flipJob: Job? = null
    private var nextFlipId: Int = 0

    fun flipCoin() {
        if (_uiState.value is CoinFlipUiState.Flipping) return
        flipJob?.cancel()
        val target = if (random.nextBoolean()) CoinSide.CARA else CoinSide.CRUZ
        val flipId = ++nextFlipId
        flipJob = viewModelScope.launch {
            _uiState.value = CoinFlipUiState.Flipping(target, flipId)
            delay(FLIP_DURATION_MS)
            _uiState.value = CoinFlipUiState.Result(target, flipId)
        }
    }

    override fun onCleared() {
        flipJob?.cancel()
        super.onCleared()
    }
}
