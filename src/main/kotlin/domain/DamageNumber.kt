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
    private var color: Color = Color.RED // Default color is red
    private var showPlusSign: Boolean = false // Whether to show plus sign

    fun reset() {
        lifeTime = 1f
        alpha = 1f
        color = Color.RED // Reset to default color
        showPlusSign = false
    }

    fun setValue(newValue: Int) {
        value = newValue
    }

    fun setPosition(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    fun setColor(newColor: Color) {
        color = newColor
        showPlusSign = true // Show plus sign when color is set (for material drops)
    }

    fun update(delta: Float): Boolean {
        lifeTime -= delta
        alpha = lifeTime // Fade out
        y += floatSpeed * delta // Float upward
        return lifeTime <= 0f
    }

    fun draw(batch: SpriteBatch) {
        val originalColor = font.color
        font.color = Color(color.r, color.g, color.b, alpha) // Use the set color with alpha
        val text = if (showPlusSign) "+$value" else value.toString()
        font.draw(batch, text, x, y)
        font.color = originalColor // Reset color
    }
}
