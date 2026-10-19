package com.example.caraocruz.domain.usecase

import com.example.caraocruz.domain.model.CoinSide
import com.example.caraocruz.domain.repository.CoinFlipRepository

class FlipCoinUseCase(
    private val repository: CoinFlipRepository
) {
    operator fun invoke(): CoinSide = repository.flip()
}
