package br.com.woodriver.domain.zbot.config

import br.com.woodriver.domain.zbot.ZBotPowerType

// Configuration data classes for each power

data class RadialPulseConfig(
        val radius: Float = 200f,
        val damage: Int = 3,
        val cooldown: Float = 12f,
        val expansionSpeed: Float = 400f,
        val duration: Float = 0.5f
)

enum class BulletPattern {
    BURST_360, // All bullets at once in 360 degrees
    SPIRAL, // Continuous rotation
    ROTATING_BEAM // 2-4 bullets rotating
}

data class OrbitalStormConfig(
        val bulletCount: Int = 8,
        val bulletSpeed: Float = 300f,
        val damage: Int = 1,
        val cooldown: Float = 5f,
        val pattern: BulletPattern = BulletPattern.BURST_360
)

data class HomingMissileConfig(
        val missileCount: Int = 3,
        val missileSpeed: Float = 250f,
        val turnSpeed: Float = 3f,
        val damage: Int = 2,
        val cooldown: Float = 8f,
        val lifetime: Float = 5f
)

data class ShieldMatrixConfig(
        val segmentCount: Int = 3,
        val orbitRadius: Float = 100f,
        val rotationSpeed: Float = 3f,
        val duration: Float = 8f,
        val cooldown: Float = 15f,
        val hitPoints: Int = 10
)

data class LaserSweepConfig(
        val sweepAngle: Float = 180f,
        val sweepSpeed: Float = 180f, // degrees per second
        val damage: Int = 2,
        val cooldown: Float = 20f,
        val duration: Float = 3f,
        val beamLength: Float = 400f
)

// Main configuration object
object ZBotPowerConfiguration {
    // Which powers are enabled
    val enabledPowers: Set<ZBotPowerType> =
            setOf(
                    ZBotPowerType.RADIAL_PULSE,
                    ZBotPowerType.ORBITAL_STORM
                    // Add more powers here to enable them
                    // ZBotPowerType.HOMING_MISSILES,
                    // ZBotPowerType.SHIELD_MATRIX,
                    // ZBotPowerType.LASER_SWEEP
                    )

    // Configuration for each power
    val radialPulse = RadialPulseConfig()
    val orbitalStorm = OrbitalStormConfig()
    val homingMissiles = HomingMissileConfig()
    val shieldMatrix = ShieldMatrixConfig()
    val laserSweep = LaserSweepConfig()
}
