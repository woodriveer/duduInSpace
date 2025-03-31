package br.com.woodriver.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport


class WelcomeScreen: ApplicationAdapter() {
  private lateinit var batch: SpriteBatch
  private lateinit var font: BitmapFont
  private lateinit var skin: Skin
  private lateinit var stage: Stage

  override fun create() {
    stage = Stage(ScreenViewport())
    Gdx.input.inputProcessor = stage

    val Help_Guides = 12
    val row_height = Gdx.graphics.width / 12
    val col_width = Gdx.graphics.width / 12

    val mySkin = Skin(Gdx.files.internal("skin/glassy-ui.json"))

    val title: Label = Label("Buttons with Skins", mySkin, "big-black")
    title.setSize(Gdx.graphics.width, row_height * 2)
    title.setPosition(0, Gdx.graphics.height - row_height * 2)
    title.setAlignment(Align.center)
    stage.addActor(title)


    // Button
    val button1: Button = Button(mySkin, "small")
    button1.setSize(col_width * 4, row_height)
    button1.setPosition(col_width, Gdx.graphics.height - row_height * 3)
    button1.addListener(object : InputListener() {
      override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        outputLabel.setText("Press a Button")
      }

      override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        outputLabel.setText("Pressed Button")
        return true
      }
    })
    stage.addActor(button1)


    // Text Button
    val button2: Button = TextButton("Text Button", mySkin, "small")
    button2.setSize(col_width * 4, row_height)
    button2.setPosition(col_width * 7, Gdx.graphics.height - row_height * 3)
    button2.addListener(object : InputListener() {
      override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        outputLabel.setText("Press a Button")
      }

      override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        outputLabel.setText("Pressed Text Button")
        return true
      }
    })
    stage.addActor(button2)


    // ImageButton
    val button3 = ImageButton(mySkin)
    button3.setSize((col_width * 4).toFloat(), (row_height * 2).toFloat())
    button3.style.imageUp = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("switch_off.png"))))
    button3.style.imageDown = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("switch_on.png"))))
    button3.setPosition(col_width.toFloat(), (Gdx.graphics.height - row_height * 6).toFloat())
    button3.addListener(object : InputListener() {
      override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        outputLabel.setText("Press a Button")
      }

      override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        outputLabel.setText("Pressed Image Button")
        return true
      }
    })
    stage.addActor(button3)


    //ImageTextButton
    val button4 = ImageTextButton("ImageText Btn", mySkin, "small")
    button4.setSize((col_width * 4).toFloat(), (row_height * 2).toFloat())
    button4.style.imageUp = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("switch_off.png"))))
    button4.style.imageDown = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("switch_on.png"))))
    button4.setPosition((col_width * 7).toFloat(), (Gdx.graphics.height - row_height * 6).toFloat())
    button4.addListener(object : InputListener() {
      override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        outputLabel.setText("Press a Button")
      }

      override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        outputLabel.setText("Pressed Image Text Button")
        return true
      }
    })
    stage.addActor(button4)

    outputLabel = Label("Press a Button", mySkin, "black")
    outputLabel.setSize(Gdx.graphics.width, row_height)
    outputLabel.setPosition(0, row_height)
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
    /*batch.begin()
    font.draw(batch, "DuduInSpace", Gdx.graphics.width / 2f - 120f, 480f)
    batch.end()*/
  }
}