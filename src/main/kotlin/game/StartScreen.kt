package br.com.woodriver.game

import br.com.woodriver.DuduInSpace
import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import br.com.woodriver.domain.Materials
import br.com.woodriver.domain.PlayerUpgrades

class StartScreen(
    private val game: Game,
    private val playerUpgrades: PlayerUpgrades,
    private val materials: Materials
) : Screen {
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var stage: Stage
    private lateinit var outputLabel: Label

    override fun show() {
        try {
            println("Initializing StartScreen...")

            println("Creating SpriteBatch...")
            batch = SpriteBatch()

            println("Creating Stage...")
            stage = Stage(ScreenViewport())

            println("Loading font file: fonts/audiowide.fnt")
            font = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))

            println("Loading skin from GlobalSkin")
            val skin = GlobalSkin.getInstance()
            println("Skin loaded successfully")

            Gdx.input.inputProcessor = stage

            val rowHeight = Gdx.graphics.width / 12
            val colWidth = Gdx.graphics.width / 12

            println("Creating UI elements...")
            
            // Create a table to center everything
            val table = Table()
            table.setFillParent(true)
            stage.addActor(table)

            // Title
            val titleLabel = Label("DuduInSpace", skin, "title")
            table.add(titleLabel).center().padBottom(40f).row()

            // Start Game Button
            val startGameButton: Button = TextButton("Start Game", skin, "audiowide")
            startGameButton.setSize((colWidth * 4).toFloat(), rowHeight.toFloat())
            startGameButton.addListener(object : InputListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    outputLabel.setText("Press a Button")
                }

                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    outputLabel.setText("Pressed Start Game Button")
                    val levelScreen = LevelSelectionScreen(game)
                    game.setScreen(levelScreen)
                    dispose() // Dispose resources when transitioning to another screen
                    return true
                }
            })
            table.add(startGameButton).center().width(200f).height(50f).padBottom(20f).row()

            // Configuration Button
            val configurationGameButton: Button = TextButton("Configuration", skin, "default")
            configurationGameButton.setSize((colWidth * 4).toFloat(), rowHeight.toFloat())
            configurationGameButton.addListener(object : InputListener() {
                override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                    outputLabel.setText("Press a Button")
                }

                override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    outputLabel.setText("Pressed Configuration Button")
                    if (game is DuduInSpace) {
                        val configScreen = UpgradeScreen(game, playerUpgrades, materials)
                        game.setScreen(configScreen)
                        dispose() // Dispose resources when transitioning to another screen
                    }
                    return true
                }
            })
            table.add(configurationGameButton).center().width(200f).height(50f).padBottom(20f).row()

            // Output Label
            outputLabel = Label("Press a Button", skin, "title")
            outputLabel.setSize(Gdx.graphics.width.toFloat(), rowHeight.toFloat())
            outputLabel.setAlignment(Align.center)
            table.add(outputLabel).center().width(200f).height(50f)

            println("StartScreen initialization completed successfully")
        } catch (e: Exception) {
            println("Error during StartScreen initialization: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    override fun render(delta: Float) {
        // Clear screen
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
        font.dispose()
        stage.dispose()
    }
} 