package com.example.caraocruz.data.repository

import com.example.caraocruz.domain.model.CoinSide
import com.example.caraocruz.domain.repository.CoinFlipRepository
import kotlin.random.Random

class RandomCoinFlipRepository(
    private val random: Random = Random.Default
) : CoinFlipRepository {
    override fun flip(): CoinSide =
        if (random.nextBoolean()) CoinSide.CARA else CoinSide.CRUZ
}
