package br.com.woodriver.domain

import com.badlogic.gdx.Preferences

class PlayerUpgrades(private val preferences: Preferences) {
    companion object {
        const val UPGRADE_PREFIX = "upgrade_"
        const val MAX_LEVEL = 5
    }

    enum class UpgradeType {
        SHOOTING_SPEED,
        BULLET_DAMAGE,
        BULLET_SIZE,
        SHIP_SPEED,
        SHIP_HEALTH,
        SHIP_SKIN
    }

    data class Upgrade(
        val type: UpgradeType,
        val currentLevel: Int,
        val cost: Int,
        val description: String,
        val effect: Float
    )

    private val upgrades = mutableMapOf<UpgradeType, Upgrade>().apply {
        put(UpgradeType.SHOOTING_SPEED, Upgrade(
            type = UpgradeType.SHOOTING_SPEED,
            currentLevel = getSavedLevel(UpgradeType.SHOOTING_SPEED),
            cost = 100,
            description = "Faster shooting speed",
            effect = 0.1f
        ))
        put(UpgradeType.BULLET_DAMAGE, Upgrade(
            type = UpgradeType.BULLET_DAMAGE,
            currentLevel = getSavedLevel(UpgradeType.BULLET_DAMAGE),
            cost = 150,
            description = "Increased bullet damage",
            effect = 1f
        ))
        put(UpgradeType.BULLET_SIZE, Upgrade(
            type = UpgradeType.BULLET_SIZE,
            currentLevel = getSavedLevel(UpgradeType.BULLET_SIZE),
            cost = 200,
            description = "Larger bullet size",
            effect = 0.2f
        ))
        put(UpgradeType.SHIP_SPEED, Upgrade(
            type = UpgradeType.SHIP_SPEED,
            currentLevel = getSavedLevel(UpgradeType.SHIP_SPEED),
            cost = 100,
            description = "Faster ship movement",
            effect = 20f
        ))
        put(UpgradeType.SHIP_HEALTH, Upgrade(
            type = UpgradeType.SHIP_HEALTH,
            currentLevel = getSavedLevel(UpgradeType.SHIP_HEALTH),
            cost = 200,
            description = "Increased ship health",
            effect = 1f
        ))
        put(UpgradeType.SHIP_SKIN, Upgrade(
            type = UpgradeType.SHIP_SKIN,
            currentLevel = getSavedLevel(UpgradeType.SHIP_SKIN),
            cost = 500,
            description = "New ship skin",
            effect = 1f
        ))
    }

    private fun getSavedLevel(type: UpgradeType): Int {
        return preferences.getInteger("${UPGRADE_PREFIX}${type.name}", 0)
    }

    fun getUpgrade(type: UpgradeType): Upgrade {
        return upgrades[type] ?: throw IllegalArgumentException("Unknown upgrade type: $type")
    }

    fun getAllUpgrades(): List<Upgrade> {
        return upgrades.values.toList()
    }

    fun canUpgrade(type: UpgradeType, materials: Int): Boolean {
        val upgrade = getUpgrade(type)
        return upgrade.currentLevel < MAX_LEVEL && materials >= upgrade.cost
    }

    fun purchaseUpgrade(type: UpgradeType): Boolean {
        val upgrade = getUpgrade(type)
        if (upgrade.currentLevel >= MAX_LEVEL) return false

        val newLevel = upgrade.currentLevel + 1
        preferences.putInteger("${UPGRADE_PREFIX}${type.name}", newLevel)
        preferences.flush()
        
        upgrades[type] = upgrade.copy(currentLevel = newLevel)
        return true
    }

    fun getUpgradeEffect(type: UpgradeType): Float {
        val upgrade = getUpgrade(type)
        return upgrade.effect * upgrade.currentLevel
    }

    fun getUpgradeCost(type: UpgradeType): Int {
        return getUpgrade(type).cost
    }

    fun getUpgradeDescription(type: UpgradeType): String {
        return getUpgrade(type).description
    }

    fun getUpgradeLevel(type: UpgradeType): Int {
        return getUpgrade(type).currentLevel
    }

    fun isMaxLevel(type: UpgradeType): Boolean {
        return getUpgrade(type).currentLevel >= MAX_LEVEL
    }
} 