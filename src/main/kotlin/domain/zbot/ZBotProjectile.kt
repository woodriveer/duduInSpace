package br.com.woodriver.domain.zbot

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

enum class ProjectileBehavior {
    STRAIGHT,
    HOMING
}

class ZBotProjectile(
        val texture: Texture,
        var x: Float,
        var y: Float,
        var velocityX: Float,
        var velocityY: Float,
        val damage: Int = 1,
        val width: Float = 16f,
        val height: Float = 16f,
        val behavior: ProjectileBehavior = ProjectileBehavior.STRAIGHT,
        val turnSpeed: Float = 3f,
        val lifetime: Float = 5f
) {
    private var age: Float = 0f
    var isExpired: Boolean = false

    val bounds: Rectangle = Rectangle(x, y, width, height)

    fun update(delta: Float, targets: List<Rectangle>? = null) {
        age += delta

        if (age >= lifetime) {
            isExpired = true
            return
        }

        // Homing behavior
        if (behavior == ProjectileBehavior.HOMING && targets != null && targets.isNotEmpty()) {
            val nearestTarget = findNearestTarget(targets)
            if (nearestTarget != null) {
                val targetAngle =
                        atan2(
                                nearestTarget.y + nearestTarget.height / 2 - y,
                                nearestTarget.x + nearestTarget.width / 2 - x
                        )
                val currentAngle = atan2(velocityY, velocityX)

                // Smooth angle interpolation
                var angleDiff = targetAngle - currentAngle
                // Normalize angle difference to -PI to PI
                while (angleDiff > Math.PI) angleDiff -= (2 * Math.PI).toFloat()
                while (angleDiff < -Math.PI) angleDiff += (2 * Math.PI).toFloat()

                val newAngle = currentAngle + angleDiff * turnSpeed * delta
                val speed = kotlin.math.sqrt(velocityX * velocityX + velocityY * velocityY)

                velocityX = cos(newAngle) * speed
                velocityY = sin(newAngle) * speed
            }
        }

        // Update position
        x += velocityX * delta
        y += velocityY * delta
        bounds.setPosition(x, y)

        // Check if off screen
        if (x < -width || x > 800 + width || y < -height || y > 600 + height) {
            isExpired = true
        }
    }

    private fun findNearestTarget(targets: List<Rectangle>): Rectangle? {
        var nearest: Rectangle? = null
        var minDistance = Float.MAX_VALUE

        for (target in targets) {
            val dx = (target.x + target.width / 2) - x
            val dy = (target.y + target.height / 2) - y
            val distance = dx * dx + dy * dy // squared distance is fine for comparison

            if (distance < minDistance) {
                minDistance = distance
                nearest = target
            }
        }

        return nearest
    }

    fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }
}
