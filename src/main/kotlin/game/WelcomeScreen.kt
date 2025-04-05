package br.com.woodriver.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport


class WelcomeScreen: ApplicationAdapter() {
  private lateinit var batch: SpriteBatch
  private lateinit var font: BitmapFont
  private lateinit var skin: Skin
  private lateinit var stage: Stage
  private lateinit var outputLabel: Label

  override fun create() {
    batch = SpriteBatch()
    stage = Stage(ScreenViewport())
    font = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
    skin = Skin(Gdx.files.internal("assets/skin/quantum-horizon-ui.json"))

    Gdx.input.inputProcessor = stage

    val rowHeight = Gdx.graphics.width / 12
    val colWidth = Gdx.graphics.width / 12

    // Text Button
    val startGameButton: Button = TextButton("Start Game", skin, "audiowide")
    startGameButton.setSize((colWidth * 4).toFloat(), rowHeight.toFloat())
    startGameButton.setPosition((colWidth * 4).toFloat(), (Gdx.graphics.height - rowHeight * 5).toFloat())
    startGameButton.addListener(object : InputListener() {
      override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        outputLabel.setText("Press a Button")
      }

      override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        outputLabel.setText("Pressed Start Game Button")
        return true
      }
    })
    stage.addActor(startGameButton)

    val configurationGameButton: Button = TextButton("Configuration", skin, "default")
    configurationGameButton.setSize((colWidth * 4).toFloat(), rowHeight.toFloat())
    configurationGameButton.setPosition((colWidth * 4).toFloat(), (Gdx.graphics.height - rowHeight * 6).toFloat())
    configurationGameButton.addListener(object : InputListener() {
      override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
        outputLabel.setText("Press a Button")
      }

      override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        outputLabel.setText("Pressed Configuration Button")
        return true
      }
    })
    stage.addActor(configurationGameButton)



    outputLabel = Label("Press a Button", skin, "default")
    outputLabel.setSize(Gdx.graphics.width.toFloat(), rowHeight.toFloat())
    outputLabel.setPosition(0F, rowHeight.toFloat())
    outputLabel.setAlignment(Align.center)
    stage.addActor(outputLabel)

  }

  override fun render() {
    // Clear screen 0
    Gdx.gl.glClearColor(0f, 0f, 0f, 1f)

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    stage.act()
    stage.draw()



    // Render everything
    batch.begin()
    font.draw(batch, "DuduInSpace", Gdx.graphics.width / 2f - 120f, 480f)
    batch.end()
  }
}