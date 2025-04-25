package br.com.woodriver.domain

import com.badlogic.gdx.Preferences

data class Materials(
    var iron: Int = 0,
    var gold: Int = 0,
    var crystal: Int = 0
) {

    fun addIron(amount: Int) {
        iron += amount
    }

    fun addGold(amount: Int) {
        gold += amount
    }

    fun addCrystal(amount: Int) {
        crystal += amount
    }

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

    companion object {
        fun create(preferences: Preferences): Materials {
            return Materials(
                iron = preferences.getInteger("iron", 0),
                gold = preferences.getInteger("gold", 0),
                crystal = preferences.getInteger("crystal", 0)  
            )
        }
    }
}
 