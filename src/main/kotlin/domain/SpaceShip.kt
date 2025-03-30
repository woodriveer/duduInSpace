package br.com.woodriver.domain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle

data class SpaceShip(
  val texture: Texture,
  val projectileTexture: Texture,
  val info: Rectangle,
  var speed: Float = 500f
) {
  companion object {
    const val SPACE_SHIP_Y_POSITION_OFFSET = 50f

    fun createSpaceShipRectangle(texture: Texture): Rectangle {
      return Rectangle(
        Gdx.graphics.width / 2f - texture.width / 2f,
        SPACE_SHIP_Y_POSITION_OFFSET,
        texture.width.toFloat(),
        texture.height.toFloat()
      )
    }
  }
}