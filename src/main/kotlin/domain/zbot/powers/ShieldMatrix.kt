package br.com.woodriver.domain.zbot.powers

import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.domain.ZBot
import br.com.woodriver.domain.zbot.ZBotPower
import br.com.woodriver.domain.zbot.ZBotPowerType
import br.com.woodriver.domain.zbot.config.ShieldMatrixConfig
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import kotlin.math.cos
import kotlin.math.sin

class ShieldMatrix(
        private val config: ShieldMatrixConfig,
        private val ship: SpaceShip,
        private val shieldTexture: Texture?
) : ZBotPower(ZBotPowerType.SHIELD_MATRIX, config.cooldown) {

    private var shieldAge: Float = 0f
    private var currentHitPoints: Int = config.hitPoints
    private var rotationAngle: Float = 0f

    data class ShieldSegment(
            var x: Float,
            var y: Float,
            val bounds: Rectangle,
            val width: Float = 32f,
            val height: Float = 32f
    )

    private val segments = mutableListOf<ShieldSegment>()

    init {
        // Initialize shield segments
        for (i in 0 until config.segmentCount) {
            segments.add(ShieldSegment(0f, 0f, Rectangle(0f, 0f, 32f, 32f)))
        }
    }

    override fun update(delta: Float, zbot: ZBot, ship: SpaceShip) {
        super.update(delta, zbot, ship)

        if (isActive) {
            shieldAge += delta
            rotationAngle += config.rotationSpeed * delta

            // Update shield segment positions
            updateSegmentPositions()

            // Check if shield should deactivate
            if (shieldAge >= config.duration || currentHitPoints <= 0) {
                deactivateShield()
            }
        }
    }

    private fun updateSegmentPositions() {
        val centerX = ship.info.x + ship.info.width / 2
        val centerY = ship.info.y + ship.info.height / 2

        val angleStep = (2 * Math.PI / config.segmentCount).toFloat()

        for (i in segments.indices) {
            val angle = rotationAngle + angleStep * i
            val segment = segments[i]

            segment.x = centerX + cos(angle) * config.orbitRadius - segment.width / 2
            segment.y = centerY + sin(angle) * config.orbitRadius - segment.height / 2
            segment.bounds.setPosition(segment.x, segment.y)
        }
    }

    override fun draw(batch: SpriteBatch) {
        if (isActive && shieldTexture != null) {
            val alpha =
                    if (shieldAge > config.duration - 1f) {
                        // Fade out in last second
                        (config.duration - shieldAge).coerceIn(0f, 1f)
                    } else {
                        1f
                    }

            val oldColor = batch.color
            batch.setColor(1f, 1f, 1f, alpha)

            segments.forEach { segment ->
                batch.draw(shieldTexture, segment.x, segment.y, segment.width, segment.height)
            }

            batch.color = oldColor
        }
    }

    override fun onActivate() {
        shieldAge = 0f
        currentHitPoints = config.hitPoints
        rotationAngle = 0f
    }

    private fun deactivateShield() {
        isActive = false
        shieldAge = 0f
        currentHitPoints = config.hitPoints
    }

    /**
     * Check if an asteroid collides with any shield segment Call this from game logic to destroy
     * asteroids
     */
    fun checkAsteroidCollision(asteroidBounds: Rectangle): Boolean {
        if (!isActive) return false

        for (segment in segments) {
            if (segment.bounds.overlaps(asteroidBounds)) {
                currentHitPoints--
                return true
            }
        }
        return false
    }

    fun getSegmentBounds(): List<Rectangle> {
        return if (isActive) segments.map { it.bounds } else emptyList()
    }
}
