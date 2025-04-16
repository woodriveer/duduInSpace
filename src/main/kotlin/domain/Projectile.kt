package br.com.woodriver.domain

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Disposable

class Projectile(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    private val speed: Float,
    val damage: Int
) : Disposable {
    private val texture = Texture("assets/projectile-01.png")
    private val _bounds = Rectangle(x, y, width, height)
    private var destroyed = false

    val bounds: Rectangle
        get() = _bounds

    fun update(deltaTime: Float) {
        _bounds.y += speed * deltaTime
    }

    fun draw(batch: SpriteBatch) {
        if (!destroyed) {
            batch.draw(texture, _bounds.x, _bounds.y, _bounds.width, _bounds.height)
        }
    }

    fun destroy() {
        destroyed = true
    }

    fun isDestroyed(): Boolean = destroyed

    override fun dispose() {
        texture.dispose()
    }
} 