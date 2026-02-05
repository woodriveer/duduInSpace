package br.com.woodriver.domain.zbot.powers

import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.domain.ZBot
import br.com.woodriver.domain.zbot.ProjectileBehavior
import br.com.woodriver.domain.zbot.ZBotPower
import br.com.woodriver.domain.zbot.ZBotPowerManager
import br.com.woodriver.domain.zbot.ZBotPowerType
import br.com.woodriver.domain.zbot.ZBotProjectile
import br.com.woodriver.domain.zbot.config.OrbitalStormConfig
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import kotlin.math.cos
import kotlin.math.sin

class OrbitalBulletStorm(
        private val config: OrbitalStormConfig,
        private val zbot: ZBot,
        private val projectileTexture: Texture?,
        private val powerManager: ZBotPowerManager
) : ZBotPower(ZBotPowerType.ORBITAL_STORM, config.cooldown) {

    override fun update(delta: Float, zbot: ZBot, ship: SpaceShip) {
        super.update(delta, zbot, ship)
    }

    override fun draw(batch: SpriteBatch) {
        // Projectiles are drawn by the power manager
    }

    override fun onActivate() {
        when (config.pattern) {
            br.com.woodriver.domain.zbot.config.BulletPattern.BURST_360 -> fireBurst360()
            br.com.woodriver.domain.zbot.config.BulletPattern.SPIRAL -> fireSpiral()
            br.com.woodriver.domain.zbot.config.BulletPattern.ROTATING_BEAM -> fireRotatingBeam()
        }
    }

    private fun fireBurst360() {
        if (projectileTexture == null) return

        val angleStep = 360f / config.bulletCount

        for (i in 0 until config.bulletCount) {
            val angleInDegrees = angleStep * i
            val angleInRadians = Math.toRadians(angleInDegrees.toDouble()).toFloat()

            val velocityX = cos(angleInRadians) * config.bulletSpeed
            val velocityY = sin(angleInRadians) * config.bulletSpeed

            val projectile =
                    ZBotProjectile(
                            texture = projectileTexture,
                            x = zbot.x,
                            y = zbot.y,
                            velocityX = velocityX,
                            velocityY = velocityY,
                            damage = config.damage,
                            behavior = ProjectileBehavior.STRAIGHT
                    )

            powerManager.addProjectile(projectile)
        }
    }

    private fun fireSpiral() {
        // For spiral, we'd need to fire continuously over time
        // This is a simplified version that fires a burst with slight rotation
        if (projectileTexture == null) return

        val angleStep = 360f / config.bulletCount
        val spiralOffset = (System.currentTimeMillis() % 360).toFloat()

        for (i in 0 until config.bulletCount) {
            val angleInDegrees = angleStep * i + spiralOffset
            val angleInRadians = Math.toRadians(angleInDegrees.toDouble()).toFloat()

            val velocityX = cos(angleInRadians) * config.bulletSpeed
            val velocityY = sin(angleInRadians) * config.bulletSpeed

            val projectile =
                    ZBotProjectile(
                            texture = projectileTexture,
                            x = zbot.x,
                            y = zbot.y,
                            velocityX = velocityX,
                            velocityY = velocityY,
                            damage = config.damage,
                            behavior = ProjectileBehavior.STRAIGHT
                    )

            powerManager.addProjectile(projectile)
        }
    }

    private fun fireRotatingBeam() {
        // Fire 4 bullets in a rotating pattern
        if (projectileTexture == null) return

        val beamCount = 4
        val angleStep = 360f / beamCount
        val rotationOffset = (System.currentTimeMillis() % 360).toFloat()

        for (i in 0 until beamCount) {
            val angleInDegrees = angleStep * i + rotationOffset
            val angleInRadians = Math.toRadians(angleInDegrees.toDouble()).toFloat()

            val velocityX = cos(angleInRadians) * config.bulletSpeed
            val velocityY = sin(angleInRadians) * config.bulletSpeed

            val projectile =
                    ZBotProjectile(
                            texture = projectileTexture,
                            x = zbot.x,
                            y = zbot.y,
                            velocityX = velocityX,
                            velocityY = velocityY,
                            damage = config.damage,
                            behavior = ProjectileBehavior.STRAIGHT
                    )

            powerManager.addProjectile(projectile)
        }
    }
}
