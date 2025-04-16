package br.com.woodriver.domain

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Color

class DamageNumber(
    private var value: Int,
    var x: Float,
    var y: Float,
    private val font: BitmapFont
) {
    private var lifeTime: Float = 1f // Show for 1 second
    private var alpha: Float = 1f
    private val floatSpeed: Float = 50f // Pixels per second

    fun reset() {
        lifeTime = 1f
        alpha = 1f
    }

    fun setValue(newValue: Int) {
        value = newValue
    }

    fun setPosition(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    fun update(delta: Float): Boolean {
        lifeTime -= delta
        alpha = lifeTime // Fade out
        y += floatSpeed * delta // Float upward
        return lifeTime <= 0f
    }

    fun draw(batch: SpriteBatch) {
        val color = font.color
        font.color = Color(1f, 0f, 0f, alpha) // Red color with alpha
        font.draw(batch, value.toString(), x, y)
        font.color = color // Reset color
    }
} 