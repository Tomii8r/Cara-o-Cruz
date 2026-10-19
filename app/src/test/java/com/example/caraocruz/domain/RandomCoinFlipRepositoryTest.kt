package com.example.caraocruz.domain

import com.example.caraocruz.data.repository.RandomCoinFlipRepository
import com.example.caraocruz.domain.model.CoinSide
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class RandomCoinFlipRepositoryTest {

    @Test
    fun `flip returns both sides across many calls`() {
        val repository = RandomCoinFlipRepository(Random(1234))

        val results = List(100) { repository.flip() }

        assertEquals(setOf(CoinSide.CARA, CoinSide.CRUZ), results.toSet())
    }

    @Test
    fun `flip always returns a valid side`() {
        val repository = RandomCoinFlipRepository(Random(7))

        repeat(100) {
            assertTrue(repository.flip() in CoinSide.entries)
        }
    }
}
