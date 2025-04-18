package br.com.woodriver.domain

class Materials {
    private var iron = 0
    private var gold = 0
    private var crystal = 0

    fun addIron(amount: Int) {
        iron += amount
    }

    fun addGold(amount: Int) {
        gold += amount
    }

    fun addCrystal(amount: Int) {
        crystal += amount
    }

    fun getIron(): Int = iron
    fun getGold(): Int = gold
    fun getCrystal(): Int = crystal

    fun hasEnough(ironNeeded: Int, goldNeeded: Int, crystalNeeded: Int): Boolean {
        return iron >= ironNeeded && gold >= goldNeeded && crystal >= crystalNeeded
    }

    fun spend(ironAmount: Int, goldAmount: Int, crystalAmount: Int): Boolean {
        if (hasEnough(ironAmount, goldAmount, crystalAmount)) {
            iron -= ironAmount
            gold -= goldAmount
            crystal -= crystalAmount
            return true
        }
        return false
    }
} 