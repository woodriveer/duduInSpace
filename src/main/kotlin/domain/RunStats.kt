package br.com.woodriver.domain

import kotlin.math.floor
import kotlin.math.pow

class RunStats {
    var currentLevel: Int = 1
        private set
    var currentXP: Int = 0
        private set
    var xpToNextLevel: Int = 100
        private set

    private val temporaryUpgrades = mutableListOf<MidRunUpgrade>()

    /**
     * Adds XP and returns true if a level-up occurred.
     */
    fun addXP(amount: Int): Boolean {
        currentXP += amount
        if (currentXP >= xpToNextLevel) {
            levelUp()
            return true
        }
        return false
    }

    private fun levelUp() {
        currentXP -= xpToNextLevel
        currentLevel++
        // Simple scaling formula: 100 * 1.5^(level-1)
        xpToNextLevel = floor(100 * 1.5.pow(currentLevel - 1)).toInt()
    }

    fun addUpgrade(upgrade: MidRunUpgrade) {
        temporaryUpgrades.add(upgrade)
    }

    fun getActiveUpgrades(): List<MidRunUpgrade> = temporaryUpgrades

    fun hasUpgrade(type: MidRunUpgradeType): Boolean {
        return temporaryUpgrades.any { it.type == type }
    }

    fun getXPPercentage(): Float {
        return currentXP.toFloat() / xpToNextLevel.toFloat()
    }
}
