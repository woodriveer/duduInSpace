package br.com.woodriver.domain

enum class MidRunUpgradeType {
        DUPLICATE_SHOT,
        RAPID_FIRE,
        PIERCING_LASER,
        SHIELD_REGEN,
        DEPLOY_ZBOT,
        ZBOT_OVERCHARGE,
        ZBOT_ORBITAL_STORM,
        ZBOT_MAGNET,
        SPEED_BOOST,
        ARMOR_PLATING,
        ADDITIONAL_SHOT
}

data class MidRunUpgrade(
        val type: MidRunUpgradeType,
        val name: String,
        val description: String,
        val level: Int = 1
) {
        companion object {
                fun getRandomUpgrades(
                        count: Int,
                        exclude: List<MidRunUpgradeType> = emptyList()
                ): List<MidRunUpgrade> {
                        return MidRunUpgradeType.entries
                                .filter { it !in exclude }
                                .shuffled()
                                .take(count)
                                .map { create(it) }
                }

                fun create(type: MidRunUpgradeType): MidRunUpgrade {
                        return when (type) {
                                MidRunUpgradeType.DUPLICATE_SHOT ->
                                        MidRunUpgrade(
                                                type,
                                                "Duplicate Shot",
                                                "Duplicate your base count shot"
                                        )
                                MidRunUpgradeType.RAPID_FIRE ->
                                        MidRunUpgrade(
                                                type,
                                                "Rapid Fire",
                                                "Increases attack speed by 25%"
                                        )
                                MidRunUpgradeType.PIERCING_LASER ->
                                        MidRunUpgrade(
                                                type,
                                                "Piercing Laser",
                                                "Bullets pass through 1 enemy"
                                        )
                                MidRunUpgradeType.SHIELD_REGEN ->
                                        MidRunUpgrade(
                                                type,
                                                "Nano-Repair",
                                                "Regenerate 1 HP every 30 seconds"
                                        )
                                MidRunUpgradeType.DEPLOY_ZBOT ->
                                        MidRunUpgrade(
                                                type,
                                                "Z-Bot Companion",
                                                "Deploy Z-Bot with Radial Pulse (8s cooldown)"
                                        )
                                MidRunUpgradeType.ZBOT_OVERCHARGE ->
                                        MidRunUpgrade(
                                                type,
                                                "Z-Bot Overcharge",
                                                "Z-Bot pulse fires 50% faster (4s cooldown)"
                                        )
                                MidRunUpgradeType.ZBOT_ORBITAL_STORM ->
                                        MidRunUpgrade(
                                                type,
                                                "Z-Bot Orbital Storm",
                                                "Z-Bot fires orbital bullets"
                                        )
                                MidRunUpgradeType.ZBOT_MAGNET ->
                                        MidRunUpgrade(
                                                type,
                                                "Titanium Magnet",
                                                "Attracts Tech Scraps from further away"
                                        )
                                MidRunUpgradeType.SPEED_BOOST ->
                                        MidRunUpgrade(
                                                type,
                                                "Overclocked Engines",
                                                "15% faster movement speed"
                                        )
                                MidRunUpgradeType.ARMOR_PLATING ->
                                        MidRunUpgrade(
                                                type,
                                                "Armor Plating",
                                                "Maximum HP increased by 2"
                                        )
                                MidRunUpgradeType.ADDITIONAL_SHOT ->
                                        MidRunUpgrade(
                                                type,
                                                "Additional Laser",
                                                "Fire +1 projectile per shot"
                                        )
                        }
                }
        }
}
