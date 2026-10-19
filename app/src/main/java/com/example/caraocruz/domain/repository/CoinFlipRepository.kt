package com.example.caraocruz.domain.repository

import com.example.caraocruz.domain.model.CoinSide

interface CoinFlipRepository {
    fun flip(): CoinSide
}
