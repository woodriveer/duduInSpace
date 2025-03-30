package br.com.woodriver.domain

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle

data class Enemy(
  val texture: Texture,
  val info: Rectangle,
)