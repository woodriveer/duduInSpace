package br.com.woodriver.domain

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlin.random.Random
import com.badlogic.gdx.utils.Disposable

class Boss(
    x: Float,
    y: Float,
    width: Float = 128f,
    height: Float = 128f
) : Disposable {
    private val texture = Texture("assets/boss.png")
    private val _bounds = Rectangle(x, y, width, height)
    private var health = 100
    private var damage = 20
    var isDead = false
        private set

    val bounds: Rectangle
        get() = _bounds

    fun update(deltaTime: Float) {
        // Boss movement pattern can be implemented here
    }

    fun draw(batch: SpriteBatch) {
        if (!isDead) {
            batch.draw(texture, _bounds.x, _bounds.y, _bounds.width, _bounds.height)
        }
    }

    fun takeDamage(amount: Int): Boolean {
        health -= amount
        if (health <= 0 && !isDead) {
            isDead = true
            return true
        }
        return false
    }

    override fun dispose() {
        texture.dispose()
    }

    companion object {
        fun create(x: Float, y: Float): Boss {
            return Boss(x, y)
        }
    }
} 