package br.com.woodriver.domain

enum class WeaponType {
    RAPID,
    PIERCING,
    SPREAD
}

enum class ShipClass(
        val displayName: String,
        val texturePath: String,
        val materialCost: Int,
        val fireRate: Float,
        val damageMultiplier: Float,
        val weaponType: WeaponType,
        val description: String
) {
    ASSAULT(
            displayName = "Assault",
            texturePath = "assets/spaceship-01.png",
            materialCost = 0,
            fireRate = 0.2f,
            damageMultiplier = 1.0f,
            weaponType = WeaponType.RAPID,
            description = "High fire rate, reliable damage."
    ),
    SNIPER(
            displayName = "Sniper",
            texturePath = "assets/enemy_ship.png",
            materialCost = 1000,
            fireRate = 0.6f,
            damageMultiplier = 2.5f,
            weaponType = WeaponType.PIERCING,
            description = "Slow fire, but shots pierce through enemies."
    ),
    SPREADER(
            displayName = "Spreader",
            texturePath = "assets/ufo.png",
            materialCost = 1500,
            fireRate = 0.4f,
            damageMultiplier = 0.7f,
            weaponType = WeaponType.SPREAD,
            description = "Shoots 3 projectiles in a cone."
    );

    companion object {
        fun fromName(name: String?): ShipClass {
            return entries.find { it.name == name } ?: ASSAULT
        }
    }
}
