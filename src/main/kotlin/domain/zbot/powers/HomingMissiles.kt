package br.com.woodriver.domain.zbot.powers

import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.domain.ZBot
import br.com.woodriver.domain.zbot.ProjectileBehavior
import br.com.woodriver.domain.zbot.ZBotPower
import br.com.woodriver.domain.zbot.ZBotPowerManager
import br.com.woodriver.domain.zbot.ZBotPowerType
import br.com.woodriver.domain.zbot.ZBotProjectile
import br.com.woodriver.domain.zbot.config.HomingMissileConfig
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import kotlin.math.cos
import kotlin.math.sin

class HomingMissiles(
        private val config: HomingMissileConfig,
        private val zbot: ZBot,
        private val missileTexture: Texture?,
        private val powerManager: ZBotPowerManager
) : ZBotPower(ZBotPowerType.HOMING_MISSILES, config.cooldown) {

    override fun update(delta: Float, zbot: ZBot, ship: SpaceShip) {
        super.update(delta, zbot, ship)
    }

    override fun draw(batch: SpriteBatch) {
        // Missiles are drawn by the power manager
    }

    override fun onActivate() {
        launchMissiles()
    }

    private fun launchMissiles() {
        val texture = missileTexture ?: powerManager.projectileTexture ?: return

        // Launch missiles in slightly different directions to spread them out
        val angleStep = 360f / config.missileCount

        for (i in 0 until config.missileCount) {
            val angleInDegrees = angleStep * i
            val angleInRadians = Math.toRadians(angleInDegrees.toDouble()).toFloat()

            // Initial velocity in different directions
            val velocityX = cos(angleInRadians) * config.missileSpeed
            val velocityY = sin(angleInRadians) * config.missileSpeed

            val missile =
                    ZBotProjectile(
                            texture = texture,
                            x = zbot.x,
                            y = zbot.y,
                            velocityX = velocityX,
                            velocityY = velocityY,
                            damage = config.damage,
                            width = 20f,
                            height = 20f,
                            behavior = ProjectileBehavior.HOMING,
                            turnSpeed = config.turnSpeed,
                            lifetime = config.lifetime
                    )

            powerManager.addProjectile(missile)
        }
    }
}
