package br.com.woodriver.domain.zbot

import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.domain.zbot.config.*
import br.com.woodriver.domain.zbot.powers.*
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle

class ZBotPowerManager(
        private val zbot: br.com.woodriver.domain.ZBot,
        private val ship: SpaceShip
) {
    private val activePowers = mutableListOf<ZBotPower>()
    val projectiles = mutableListOf<ZBotProjectile>()

    // Textures for projectiles (will be loaded externally)
    var projectileTexture: Texture? = null
    var missileTexture: Texture? = null
    var shieldTexture: Texture? = null

    init {
        // Initialize enabled powers based on configuration
        initializePowers()
    }

    private fun initializePowers() {
        val config = ZBotPowerConfiguration

        config.enabledPowers.forEach { powerType ->
            val power =
                    when (powerType) {
                        ZBotPowerType.RADIAL_PULSE -> RadialPulse(config.radialPulse, zbot, ship)
                        ZBotPowerType.ORBITAL_STORM ->
                                OrbitalBulletStorm(
                                        config.orbitalStorm,
                                        zbot,
                                        projectileTexture,
                                        this
                                )
                        ZBotPowerType.HOMING_MISSILES ->
                                HomingMissiles(config.homingMissiles, zbot, missileTexture, this)
                        ZBotPowerType.SHIELD_MATRIX ->
                                ShieldMatrix(config.shieldMatrix, ship, shieldTexture)
                        ZBotPowerType.LASER_SWEEP -> LaserSweep(config.laserSweep, zbot)
                    }
            activePowers.add(power)
        }
    }

    fun update(delta: Float, asteroidBounds: List<Rectangle> = emptyList()) {
        // Update all active powers
        activePowers.forEach { power -> power.update(delta, zbot, ship) }

        // Update projectiles
        projectiles.forEach { projectile -> projectile.update(delta, asteroidBounds) }

        // Remove expired projectiles
        projectiles.removeAll { it.isExpired }

        // Auto-activate powers when ready (optional - can be manual)
        autoActivatePowers()
    }

    private fun autoActivatePowers() {
        activePowers.forEach { power ->
            if (power.isReady()) {
                power.activate()
            }
        }
    }

    fun draw(batch: SpriteBatch) {
        // Draw all power effects
        activePowers.forEach { power -> power.draw(batch) }

        // Draw projectiles
        projectiles.forEach { projectile -> projectile.draw(batch) }
    }

    fun addProjectile(projectile: ZBotProjectile) {
        projectiles.add(projectile)
    }

    fun getPower(type: ZBotPowerType): ZBotPower? {
        return activePowers.find { it.type == type }
    }

    fun dispose() {
        activePowers.forEach { it.dispose() }
        activePowers.clear()
        projectiles.clear()
    }
}
