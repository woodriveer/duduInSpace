package br.com.woodriver.domain

enum class MidRunUpgradeType {
    TRIPLE_SHOT,
    RAPID_FIRE,
    PIERCING_LASER,
    SHIELD_REGEN,
    ZBOT_TURRET,
    ZBOT_MAGNET,
    SPEED_BOOST,
    ARMOR_PLATING
}

data class MidRunUpgrade(
    val type: MidRunUpgradeType,
    val name: String,
    val description: String,
    val level: Int = 1
) {
    companion object {
        fun getRandomUpgrades(count: Int, exclude: List<MidRunUpgradeType> = emptyList()): List<MidRunUpgrade> {
            return MidRunUpgradeType.entries
                .filter { it !in exclude }
                .shuffled()
                .take(count)
                .map { create(it) }
        }

        fun create(type: MidRunUpgradeType): MidRunUpgrade {
            return when (type) {
                MidRunUpgradeType.TRIPLE_SHOT -> MidRunUpgrade(type, "Triple Shot", "Fire 3 projectiles in a spread")
                MidRunUpgradeType.RAPID_FIRE -> MidRunUpgrade(type, "Rapid Fire", "Increases attack speed by 25%")
                MidRunUpgradeType.PIERCING_LASER -> MidRunUpgrade(type, "Piercing Laser", "Bullets pass through 1 enemy")
                MidRunUpgradeType.SHIELD_REGEN -> MidRunUpgrade(type, "Nano-Repair", "Regenerate 1 HP every 30 seconds")
                MidRunUpgradeType.ZBOT_TURRET -> MidRunUpgrade(type, "Z-Bot Turret", "Z-Bot fires automated pulses")
                MidRunUpgradeType.ZBOT_MAGNET -> MidRunUpgrade(type, "Titanium Magnet", "Attracts Tech Scraps from further away")
                MidRunUpgradeType.SPEED_BOOST -> MidRunUpgrade(type, "Overclocked Engines", "15% faster movement speed")
                MidRunUpgradeType.ARMOR_PLATING -> MidRunUpgrade(type, "Armor Plating", "Maximum HP increased by 2")
            }
        }
    }
}
