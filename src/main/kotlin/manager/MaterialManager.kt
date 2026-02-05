package br.com.woodriver.manager

import br.com.woodriver.domain.Materials
import com.badlogic.gdx.Preferences

class MaterialManager(private val preferences: Preferences) {
    private val MATERIAL_KEY = "materials"
    private val DROP_CHANCE = 0.3f

    fun getMaterialCount(): Int {
        return preferences.getInteger(MATERIAL_KEY, 0)
    }

    fun addMaterials(amount: Int = 1) {
        val current = getMaterialCount()
        preferences.putInteger(MATERIAL_KEY, current + amount)
        preferences.flush()
    }

    fun spendMaterial(amount: Int): Boolean {
        val current = getMaterialCount()
        if (current >= amount) {
            preferences.putInteger(MATERIAL_KEY, current - amount)
            preferences.flush()
            return true
        }
        return false
    }

    fun shouldDropMaterial(): Boolean {
        return Math.random() < DROP_CHANCE
    }

    // New method to handle material drop with position and weighted amount
    fun handleMaterialDrop(
            x: Float,
            y: Float,
            amount: Int = 1
    ): Pair<Boolean, Pair<Int, Pair<Float, Float>>> {
        return if (shouldDropMaterial()) {
            addMaterials(amount)
            Pair(true, Pair(amount, Pair(x, y)))
        } else {
            Pair(false, Pair(0, Pair(0f, 0f)))
        }
    }

    companion object {
        fun fromMaterials(materials: Materials, preferences: Preferences): MaterialManager {
            return MaterialManager(preferences).apply { addMaterials(materials.count) }
        }
    }
}
