package br.com.woodriver.game

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
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.Preferences
import br.com.woodriver.DuduInSpace
import br.com.woodriver.domain.Materials
import br.com.woodriver.domain.PlayerUpgrades
import br.com.woodriver.manager.MaterialManager

class LevelSelectionScreen(private val game: Game) : Screen {
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var stage: Stage
    private lateinit var materialLabel: Label
    private val preferences: Preferences = Gdx.app.getPreferences("SpaceShooterProgress")
    private val materialManager = MaterialManager(preferences)

    override fun show() {
        batch = SpriteBatch()
        stage = Stage(ScreenViewport())
        font = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
        val skin = GlobalSkin.getInstance()
        Gdx.input.inputProcessor = stage

        val rowHeight = Gdx.graphics.width / 12
        val colWidth = Gdx.graphics.width / 12

        // Title
        val titleLabel = Label("Select Level", skin, "title")
        titleLabel.setSize(Gdx.graphics.width.toFloat(), rowHeight.toFloat())
        titleLabel.setPosition(0f, Gdx.graphics.height - rowHeight.toFloat())
        titleLabel.setAlignment(Align.center)
        stage.addActor(titleLabel)

        // Material display
        materialLabel = Label("Special Materials: ${materialManager.getMaterialCount()}", skin)
        materialLabel.setSize(Gdx.graphics.width.toFloat(), rowHeight.toFloat())
        materialLabel.setPosition(0f, Gdx.graphics.height - rowHeight * 2f)
        materialLabel.setAlignment(Align.center)
        stage.addActor(materialLabel)

        // Level buttons
        for (i in 1..5) {
            val isCompleted = preferences.getBoolean("level_${i}_completed", false)
            val levelButton = TextButton("Level $i ${if (isCompleted) "✓" else ""}", skin)
            levelButton.setSize((colWidth * 4).toFloat(), rowHeight.toFloat())
            levelButton.setPosition(
                (colWidth * 4).toFloat(),
                (Gdx.graphics.height - rowHeight * (i + 3)).toFloat()
            )
            levelButton.addListener(object : InputListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    // Launch game with current upgrades
                    val duduGame = game as DuduInSpace
                    val gameScreen = SpaceShooterGame(
                        game,
                        i,
                        materialManager,
                        duduGame.playerUpgrades
                    )
                    game.setScreen(gameScreen)
                    dispose() // Dispose resources when transitioning to another screen
                    return true
                }
            })
            stage.addActor(levelButton)
        }

        // Back button
        val backButton = TextButton("Back", skin)
        backButton.setSize((colWidth * 4).toFloat(), rowHeight.toFloat())
        backButton.setPosition((colWidth * 4).toFloat(), rowHeight.toFloat())
        backButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (game is DuduInSpace) {
                    val startScreen = StartScreen(game, game.playerUpgrades, game.materials)
                    game.setScreen(startScreen)
                    dispose() // Dispose resources when transitioning to another screen
                }
                return true
            }
        })
        stage.addActor(backButton)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()

        batch.begin()
        font.draw(batch, "DuduInSpace", Gdx.graphics.width / 2f - 120f, 480f)
        batch.end()
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
