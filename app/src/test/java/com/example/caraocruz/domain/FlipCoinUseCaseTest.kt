package com.example.caraocruz.domain

import com.example.caraocruz.domain.model.CoinSide
import com.example.caraocruz.domain.repository.CoinFlipRepository
import com.example.caraocruz.domain.usecase.FlipCoinUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class FlipCoinUseCaseTest {

    @Test
    fun `invoke returns the repository result`() {
        val useCase = FlipCoinUseCase(FakeCoinFlipRepository(CoinSide.CARA))

        assertEquals(CoinSide.CARA, useCase())
    }

    @Test
    fun `invoke delegates to the repository exactly once`() {
        val repository = FakeCoinFlipRepository(CoinSide.CRUZ)
        val useCase = FlipCoinUseCase(repository)

        useCase()

        assertEquals(1, repository.invocations)
    }

    private class FakeCoinFlipRepository(
        private val side: CoinSide
    ) : CoinFlipRepository {
        var invocations: Int = 0
            private set

        override fun flip(): CoinSide {
            invocations++
            return side
        }
    }
}
