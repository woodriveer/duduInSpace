package br.com.woodriver.domain

import br.com.woodriver.domain.zbot.ZBotPowerManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import kotlin.math.cos
import kotlin.math.sin

class ZBot(val ship: SpaceShip) {
    var x: Float = 0f
    var y: Float = 0f
    private val texture = Texture("assets/zbot.png")
    val animation: Animation = Animation(texture, 32, 32, 0.1f)
    private var angle: Float = 0f
    private val orbitRadius = 80f
    private val orbitSpeed = 2f

    // Power system
    val powerManager: ZBotPowerManager = ZBotPowerManager(this, ship)

    fun update(delta: Float, asteroidBounds: List<Rectangle> = emptyList()) {
        angle += orbitSpeed * delta

        animation.update(delta)

        // Orbit around the ship
        val targetX = ship.info.x + ship.info.width / 2 + cos(angle) * orbitRadius
        val targetY = ship.info.y + ship.info.height / 2 + sin(angle) * orbitRadius

        // Smoothly follow (Lerp)
        x += (targetX - x) * 5f * delta
        y += (targetY - y) * 5f * delta

        // Update power system
        powerManager.update(delta, asteroidBounds)
    }

    fun draw(batch: SpriteBatch) {
        animation.draw(batch, x - 16f, y - 16f, 32f, 32f)

        // Draw power effects
        powerManager.draw(batch)
    }

    fun dispose() {
        texture.dispose()
        powerManager.dispose()
    }
}
