package com.example.caraocruz.presentation.coin

import com.example.caraocruz.domain.model.CoinSide

sealed interface CoinFlipUiState {
    data object Idle : CoinFlipUiState
    data class Flipping(val target: CoinSide, val flipId: Int) : CoinFlipUiState
    data class Result(val side: CoinSide, val flipId: Int) : CoinFlipUiState
}
