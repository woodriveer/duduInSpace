package br.com.woodriver.domain

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.g2d.SpriteBatch

enum class PowerUpType {
    FASTER_SHOOTING,
    TRIPLE_SHOT,
    BIGGER_PROJECTILES;

    fun getTexturePath(): String {
        return when (this) {
            FASTER_SHOOTING -> "assets/powerup_clock.png"
            TRIPLE_SHOT -> "assets/powerup_3.png"
            BIGGER_PROJECTILES -> "assets/powerup_muscle.png"
        }
    }
}

class PowerUp(
    val texture: Texture,
    val info: Rectangle,
    val type: PowerUpType,
    private var animation: Animation? = null
) {
    fun update(delta: Float) {
        animation?.update(delta)
    }

    fun draw(batch: SpriteBatch) {
        if (type == PowerUpType.TRIPLE_SHOT && animation != null) {
            animation?.draw(batch, info.x, info.y, info.width, info.height)
        } else {
            batch.draw(texture, info.x, info.y, info.width, info.height)
        }
    }

    fun dispose() {
        texture.dispose()
        animation?.dispose()
    }

    companion object {
        fun createPowerUp(type: PowerUpType, x: Float, y: Float, width: Float, height: Float): PowerUp {
            val texture = Texture(type.getTexturePath())
            val info = Rectangle(x, y, width, height)
            val animation = if (type == PowerUpType.TRIPLE_SHOT) {
                Animation(texture, 32, 32, 0.1f) // Assuming each frame is 32x32 and animation speed is 0.1s per frame
            } else null
            return PowerUp(texture, info, type, animation)
        }
    }
} 