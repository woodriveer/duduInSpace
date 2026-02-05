package br.com.woodriver.domain

import com.badlogic.gdx.Preferences

data class Materials(var count: Int = 0) {

    fun add(amount: Int) {
        count += amount
    }

    fun hasEnough(needed: Int): Boolean {
        return count >= needed
    }

    fun spend(amount: Int): Boolean {
        if (hasEnough(amount)) {
            count -= amount
            return true
        }
        return false
    }

    fun save(preferences: Preferences) {
        preferences.putInteger("materials", count)
        preferences.flush()
    }

    companion object {
        fun create(preferences: Preferences): Materials {
            return Materials(count = preferences.getInteger("materials", 0))
        }
    }
}
