package br.com.woodriver.domain

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
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

    fun update(delta: Float) {
        angle += orbitSpeed * delta

        animation.update(delta)

        // Orbit around the ship
        val targetX = ship.info.x + ship.info.width / 2 + cos(angle) * orbitRadius
        val targetY = ship.info.y + ship.info.height / 2 + sin(angle) * orbitRadius

        // Smoothly follow (Lerp)
        x += (targetX - x) * 5f * delta
        y += (targetY - y) * 5f * delta
    }

    fun draw(batch: SpriteBatch) {
        animation.draw(batch, x - 16f, y - 16f, 32f, 32f)
    }

    fun dispose() {
        texture.dispose()
    }
}
