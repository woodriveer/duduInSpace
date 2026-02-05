package br.com.woodriver.domain.zbot.powers

import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.domain.ZBot
import br.com.woodriver.domain.zbot.ZBotPower
import br.com.woodriver.domain.zbot.ZBotPowerType
import br.com.woodriver.domain.zbot.config.LaserSweepConfig
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import kotlin.math.cos
import kotlin.math.sin

class LaserSweep(private val config: LaserSweepConfig, private val zbot: ZBot) :
        ZBotPower(ZBotPowerType.LASER_SWEEP, config.cooldown) {

    private var sweepAge: Float = 0f
    private var currentAngle: Float = 0f
    private val shapeRenderer = ShapeRenderer()

    override fun update(delta: Float, zbot: ZBot, ship: SpaceShip) {
        super.update(delta, zbot, ship)

        if (isActive) {
            sweepAge += delta
            currentAngle += config.sweepSpeed * delta

            if (sweepAge >= config.duration) {
                isActive = false
                sweepAge = 0f
                currentAngle = 0f
            }
        }
    }

    override fun draw(batch: SpriteBatch) {
        if (isActive) {
            batch.end() // End sprite batch to use shape renderer

            shapeRenderer.projectionMatrix = batch.projectionMatrix
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

            val alpha = 1f - (sweepAge / config.duration) * 0.5f

            // Draw laser beam
            val angleInRadians = Math.toRadians(currentAngle.toDouble()).toFloat()
            val endX = zbot.x + cos(angleInRadians) * config.beamLength
            val endY = zbot.y + sin(angleInRadians) * config.beamLength

            // Draw thick line as laser
            shapeRenderer.setColor(1f, 0.3f, 0.3f, alpha)
            shapeRenderer.rectLine(zbot.x, zbot.y, endX, endY, 8f)

            // Draw glow effect
            shapeRenderer.setColor(1f, 0.6f, 0.6f, alpha * 0.5f)
            shapeRenderer.rectLine(zbot.x, zbot.y, endX, endY, 12f)

            shapeRenderer.end()
            batch.begin() // Resume sprite batch
        }
    }

    override fun onActivate() {
        sweepAge = 0f
        currentAngle = 0f
    }

    /** Check if an asteroid is hit by the laser beam Uses line-circle collision detection */
    fun isAsteroidInBeam(asteroidBounds: Rectangle): Boolean {
        if (!isActive) return false

        val angleInRadians = Math.toRadians(currentAngle.toDouble()).toFloat()
        val beamEndX = zbot.x + cos(angleInRadians) * config.beamLength
        val beamEndY = zbot.y + sin(angleInRadians) * config.beamLength

        // Simplified collision: check if asteroid center is near the beam line
        val asteroidCenterX = asteroidBounds.x + asteroidBounds.width / 2
        val asteroidCenterY = asteroidBounds.y + asteroidBounds.height / 2

        // Calculate distance from point to line segment
        val distance =
                distanceFromPointToLineSegment(
                        asteroidCenterX,
                        asteroidCenterY,
                        zbot.x,
                        zbot.y,
                        beamEndX,
                        beamEndY
                )

        // Check if within beam width + asteroid radius
        val asteroidRadius = (asteroidBounds.width + asteroidBounds.height) / 4f
        return distance <= (8f + asteroidRadius)
    }

    private fun distanceFromPointToLineSegment(
            px: Float,
            py: Float,
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float
    ): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        val lengthSquared = dx * dx + dy * dy

        if (lengthSquared == 0f) {
            // Line segment is a point
            val dpx = px - x1
            val dpy = py - y1
            return kotlin.math.sqrt(dpx * dpx + dpy * dpy)
        }

        // Calculate projection of point onto line
        var t = ((px - x1) * dx + (py - y1) * dy) / lengthSquared
        t = t.coerceIn(0f, 1f)

        val projectionX = x1 + t * dx
        val projectionY = y1 + t * dy

        val dpx = px - projectionX
        val dpy = py - projectionY

        return kotlin.math.sqrt(dpx * dpx + dpy * dpy)
    }

    fun getDamage(): Int = config.damage

    override fun dispose() {
        shapeRenderer.dispose()
    }
}
