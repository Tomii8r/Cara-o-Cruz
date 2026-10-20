package com.example.caraocruz.di

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.caraocruz.data.repository.RandomCoinFlipRepository
import com.example.caraocruz.domain.usecase.FlipCoinUseCase
import com.example.caraocruz.presentation.coin.CoinFlipViewModel

object CoinFlipViewModelFactory {
    val Factory = viewModelFactory {
        initializer {
            CoinFlipViewModel(
                flipCoinUseCase = FlipCoinUseCase(RandomCoinFlipRepository())
            )
        }
    }
}
