package br.com.woodriver.domain

import br.com.woodriver.domain.EnemyType
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable

class Enemy(
    val type: EnemyType,
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float,
    var speed: Float,
    var health: Int,
    var isActive: Boolean = true
) : Disposable {
    private val _bounds = Rectangle(x, y, width, height)
    val damage = type.getDamage()
    val scoreValue = type.getScoreValue()
    val xpValue = type.getXPValue()
    var texture = Texture(type.getTexturePath())

    val bounds: Rectangle
        get() = _bounds

    fun update(deltaTime: Float) {
        y -= speed * deltaTime
        _bounds.setPosition(x, y)
    }

    fun isDestroyed(): Boolean {
        return !isActive
    }

    fun isOffScreen(screenWidth: Float, screenHeight: Float): Boolean {
        return (y + height < 0) || (y > screenHeight) || (x + width < 0) || (x > screenWidth)
    }

    fun updateTarget(targetX: Float, targetY: Float, deltaTime: Float) {
        when (type) {
            EnemyType.ASTEROID -> {
                y -= speed * deltaTime
                _bounds.setPosition(x, y)
            }
            else -> {
                val direction = Vector2(targetX - x, targetY - y)
                if (direction.len() != 0f) {
                    direction.nor()
                    x += direction.x * speed * deltaTime
                    y += direction.y * speed * deltaTime
                    _bounds.setPosition(x, y)
                }
            }
        }
    }

    fun draw(batch: SpriteBatch) {
        if (isActive) {
            batch.draw(texture, x, y, width, height)
        }
    }

    fun takeDamage(amount: Int): Boolean {
        health -= amount
        if (health <= 0) {
            isActive = false
            return true
        }
        return false
    }

    val isDead: Boolean
        get() = health <= 0

    val score: Int
        get() = scoreValue

    override fun dispose() {
        texture.dispose()
    }

    companion object {
        fun create(type: EnemyType, x: Float, y: Float): Enemy {
            return Enemy(
                type = type,
                x = x,
                y = y,
                width = 64f,
                height = 64f,
                speed = type.getSpeed(),
                health = type.getHealth()
            )
        }
    }
}
