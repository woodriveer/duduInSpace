package br.com.woodriver.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class RunStatsTest {

    @Test
    fun `test initial values`() {
        val stats = RunStats()
        assertEquals(1, stats.currentLevel)
        assertEquals(0, stats.currentXP)
        assertEquals(100, stats.xpToNextLevel)
    }

    @Test
    fun `test add xp without leveling`() {
        val stats = RunStats()
        val leveled = stats.addXP(50)
        assertFalse(leveled)
        assertEquals(1, stats.currentLevel)
        assertEquals(50, stats.currentXP)
    }

    @Test
    fun `test level up`() {
        val stats = RunStats()
        val leveled = stats.addXP(120)
        assertTrue(leveled)
        assertEquals(2, stats.currentLevel)
        assertEquals(20, stats.currentXP)
        // Level 2 should need 100 * 1.5 = 150 XP
        assertEquals(150, stats.xpToNextLevel)
    }

    @Test
    fun `test multiple level ups`() {
        val stats = RunStats()
        stats.addXP(100) // Level 2
        stats.addXP(150) // Level 3
        assertEquals(3, stats.currentLevel)
        // Level 3 should need 100 * 1.5^2 = 225 XP
        assertEquals(225, stats.xpToNextLevel)
    }
}
