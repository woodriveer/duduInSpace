package br.com.woodriver.domain

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable

class Player(
    position: Vector2,
    private val upgrades: PlayerUpgrades
) : Disposable {
    private val _bounds = Rectangle(position.x, position.y, 32f, 32f)
    private var health = 100
    private var speed = 300f
    private var fireRate = 0.5f
    private var lastShot = 0f

    fun update(delta: Float) {
        // Movement logic here
        lastShot += delta
    }

    fun draw(batch: SpriteBatch) {
        // Drawing logic here
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
        // Cleanup logic here
    }
} 