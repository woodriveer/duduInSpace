package br.com.woodriver.domain

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable

class Player(
    position: Vector2,
    private val upgrades: PlayerUpgrades
) : Disposable {
    private val texture = Texture("assets/spaceship-01.png")
    private val _bounds = Rectangle(position.x, position.y, 32f, 32f)
    var health = 100
    var speed = 300f
    var fireRate = 0.5f
    var lastShot = 0f

    fun update(delta: Float) {
        // Movement logic here
        lastShot += delta
    }

    fun draw(batch: SpriteBatch) {
        batch.draw(texture, _bounds.x, _bounds.y, _bounds.width, _bounds.height)
    }

    fun takeDamage(amount: Int) {
        health -= amount
        if (health <= 0) {
            // Game over logic
        }
    }

    val bounds: Rectangle
        get() = _bounds

    override fun dispose() {
        texture.dispose()
    }

    companion object {
        fun create(position: Vector2, upgrades: PlayerUpgrades): Player {
            return Player(position, upgrades)
        }
    }
} 