package br.com.woodriver.screen

import br.com.woodriver.domain.Materials
import br.com.woodriver.domain.PlayerUpgrades
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ScreenViewport
import br.com.woodriver.DuduInSpace

class MenuScreen(
    private val game: DuduInSpace,
    private val playerUpgrades: PlayerUpgrades,
    private val materials: Materials
) : Screen {
    private val batch = SpriteBatch()
    private val stage = Stage(ScreenViewport())
    private val skin = Skin()
    private val font = BitmapFont()

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Create and add buttons to stage
        val playButton = TextButton("Play", skin)
        playButton.addListener { event ->
            if (event.isHandled) {
                game.setScreen(GameScreen(playerUpgrades, materials))
            }
            true
        }
        stage.addActor(playButton)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        batch.dispose()
        stage.dispose()
        skin.dispose()
        font.dispose()
    }
} 