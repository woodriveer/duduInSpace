package br.com.woodriver.manager

import com.badlogic.gdx.Preferences
import br.com.woodriver.domain.Materials

class MaterialManager(private val preferences: Preferences) {
    private val MATERIAL_KEY = "special_materials"
    private val DROP_CHANCE = 0.3f // 10% chance to drop material

    fun getMaterialCount(): Int {
        return preferences.getInteger(MATERIAL_KEY, 0)
    }

    fun addMaterial(amount: Int = 1) {
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

    // New method to handle material drop with position
    fun handleMaterialDrop(x: Float, y: Float): Pair<Boolean, Pair<Float, Float>> {
        return if (shouldDropMaterial()) {
            addMaterial()
            Pair(true, Pair(x, y))
        } else {
            Pair(false, Pair(0f, 0f))
        }
    }

    companion object {
        fun fromMaterials(materials: Materials, preferences: Preferences): MaterialManager {
            return MaterialManager(preferences).apply {
                // Convert iron to special materials
                addMaterial(materials.iron)
            }
        }
    }
}