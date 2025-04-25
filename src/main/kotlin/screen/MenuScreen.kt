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
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ScreenViewport
import br.com.woodriver.DuduInSpace
import br.com.woodriver.game.SpaceShooterGame
import br.com.woodriver.game.MaterialManager
import com.badlogic.gdx.Preferences

class MenuScreen(
    private val game: DuduInSpace,
    private val playerUpgrades: PlayerUpgrades,
    private val materials: Materials
) : Screen {
    private val batch = SpriteBatch()
    private val stage = Stage(ScreenViewport())
    private val skin = Skin(Gdx.files.internal("assets/skin/quantum-horizon-ui.json"))
    private val font = BitmapFont()
    private val materialManager = MaterialManager(Gdx.app.getPreferences("SpaceShooterProgress"))

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Create and add UI elements to stage
        val titleLabel = com.badlogic.gdx.scenes.scene2d.ui.Label("Dudu In Space", skin, "title")
        val levelButton = TextButton("Level: 1", skin)
        val playButton = TextButton("Play", skin)
        playButton.addListener { event ->
            if (event.isHandled) {
                val gameScreen = SpaceShooterGame(game, 1, materialManager)
                game.setScreen(gameScreen)
                dispose() // Dispose resources when transitioning to another screen
            }
            true
        }
        // Center everything using a Table
        val table = Table()
        table.setFillParent(true)
        table.add(titleLabel).center().padBottom(40f).row()
        table.add(levelButton).center().width(200f).height(50f).padBottom(20f).row()
        table.add(playButton).center().width(200f).height(60f)
        stage.addActor(table)
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
