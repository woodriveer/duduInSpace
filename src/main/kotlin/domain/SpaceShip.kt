package br.com.woodriver.domain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import kotlin.math.min

data class SpaceShip(
  val texture: Texture,
  val projectileTexture: Texture,
  val info: Rectangle,
  var speed: Float = 300f,
  var health: Int = 5,
  var maxHealth: Int = 5
) {
  private var isVictoryAnimation: Boolean = false
  private var victorySpeed: Float = 0f
  private val victoryAcceleration: Float = 500f
  private val maxVictorySpeed: Float = 500f
  private var isInvulnerable: Boolean = false
  private var invulnerabilityTimer: Float = 0f
  private val invulnerabilityDuration: Float = 2f

  fun takeDamage(amount: Int = 1): Boolean {
    if (isInvulnerable) return false
    
    health -= amount
    isInvulnerable = true
    invulnerabilityTimer = invulnerabilityDuration
    
    return health <= 0
  }

  fun update(delta: Float) {
    if (isInvulnerable) {
      invulnerabilityTimer -= delta
      if (invulnerabilityTimer <= 0) {
        isInvulnerable = false
      }
    }
  }

  fun startVictoryAnimation() {
    isVictoryAnimation = true
    victorySpeed = speed
  }

  fun updateVictoryAnimation(delta: Float): Boolean {
    if (!isVictoryAnimation) return false

    victorySpeed = min(victorySpeed + victoryAcceleration * delta, maxVictorySpeed)
    info.y += victorySpeed * delta

    return info.y > Gdx.graphics.height // Return true when ship is off screen
  }

  fun isInVictoryAnimation(): Boolean = isVictoryAnimation

  fun draw(batch: SpriteBatch) {
    // Flash the ship when invulnerable
    if (isInvulnerable && (invulnerabilityTimer * 10).toInt() % 2 == 0) {
      return // Skip drawing every other frame to create a flashing effect
    }
    batch.draw(texture, info.x, info.y, info.width, info.height)
  }

  companion object {
    const val SPACE_SHIP_Y_POSITION_OFFSET = 50f

    fun createSpaceShipRectangle(texture: Texture): Rectangle {
      val width = texture.width * 1.5f
      val height = texture.height * 1.5f
      return Rectangle(
        (Gdx.graphics.width - width) / 2f,
        SPACE_SHIP_Y_POSITION_OFFSET,
        width,
        height
      )
    }
  }
}