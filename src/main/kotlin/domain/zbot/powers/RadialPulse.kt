package br.com.woodriver.domain.zbot.powers

import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.domain.ZBot
import br.com.woodriver.domain.zbot.ZBotPower
import br.com.woodriver.domain.zbot.ZBotPowerType
import br.com.woodriver.domain.zbot.config.RadialPulseConfig
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class RadialPulse(
        private val config: RadialPulseConfig,
        private val zbot: ZBot,
        private val ship: SpaceShip
) : ZBotPower(ZBotPowerType.RADIAL_PULSE, config.cooldown) {

    private var currentRadius: Float = 0f
    private var pulseAge: Float = 0f
    private val shapeRenderer = ShapeRenderer()

    override fun update(delta: Float, zbot: ZBot, ship: SpaceShip) {
        super.update(delta, zbot, ship)

        if (isActive) {
            pulseAge += delta
            currentRadius += config.expansionSpeed * delta

            if (pulseAge >= config.duration) {
                isActive = false
                currentRadius = 0f
                pulseAge = 0f
            }
        }
    }

    override fun draw(batch: SpriteBatch) {
        if (isActive && currentRadius > 0f) {
            batch.end() // End sprite batch to use shape renderer

            shapeRenderer.projectionMatrix = batch.projectionMatrix
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

            // Draw expanding ring
            val alpha = 1f - (pulseAge / config.duration)
            shapeRenderer.setColor(0.3f, 0.8f, 1f, alpha)
            shapeRenderer.circle(zbot.x, zbot.y, currentRadius, 32)

            // Draw inner ring for effect
            if (currentRadius > 20f) {
                shapeRenderer.setColor(0.5f, 0.9f, 1f, alpha * 0.5f)
                shapeRenderer.circle(zbot.x, zbot.y, currentRadius - 20f, 32)
            }

            shapeRenderer.end()
            batch.begin() // Resume sprite batch
        }
    }

    override fun onActivate() {
        currentRadius = 0f
        pulseAge = 0f
        // Sound effect would be played here
    }

    /** Check if an asteroid is within the pulse radius Call this from game logic to apply damage */
    fun isAsteroidInPulse(asteroidX: Float, asteroidY: Float): Boolean {
        if (!isActive) return false

        val dx = asteroidX - zbot.x
        val dy = asteroidY - zbot.y
        val distanceSquared = dx * dx + dy * dy

        return distanceSquared <= currentRadius * currentRadius
    }

    fun getDamage(): Int = config.damage

    override fun dispose() {
        shapeRenderer.dispose()
    }
}
