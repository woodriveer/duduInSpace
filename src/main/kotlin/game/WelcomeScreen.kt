package br.com.woodriver.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class WelcomeScreen: ApplicationAdapter() {
  private lateinit var batch: SpriteBatch
  private lateinit var font: BitmapFont

  override fun create() {
    batch = SpriteBatch()

    font = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
  }

  override fun render() {
    // Clear screen 0
    Gdx.gl.glClearColor(0f, 0f, 0f, 1f)

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    // Render everything
    batch.begin()
    font.draw(batch, "DuduInSpace", Gdx.graphics.width / 2f - 120f, 480f)
    batch.end()
  }
}