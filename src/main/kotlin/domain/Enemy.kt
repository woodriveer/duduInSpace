package br.com.woodriver.domain

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Enemy(
    val type: EnemyType,
    val texture: Texture,
    val info: Rectangle,
    private var health: Int = type.getHealth()
) {
    val speed: Float = type.getSpeed()

    fun takeDamage(amount: Int = 1): Boolean {
        health -= amount
        return health <= 0
    }

    fun update(delta: Float) {
        info.y -= speed * delta
    }

    fun draw(batch: SpriteBatch) {
        batch.draw(
            texture,
            info.x,
            info.y,
            info.width,
            info.height
        )
    }

    fun isOffScreen(screenHeight: Float): Boolean {
        return info.y + info.height < 0
    }

    companion object {
        fun createEnemy(type: EnemyType, x: Float, y: Float, width: Float, height: Float): Enemy {
            val texture = Texture(type.getTexturePath())
            val info = Rectangle(x, y, width, height)
            return Enemy(type, texture, info)
        }
    }
}