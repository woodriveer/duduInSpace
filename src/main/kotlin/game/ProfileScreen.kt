package br.com.woodriver.game

import br.com.woodriver.DuduInSpace
import br.com.woodriver.manager.MaterialManager
import br.com.woodriver.screen.GameScreen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport

class ProfileScreen(private val game: DuduInSpace) : Screen {
    private lateinit var batch: SpriteBatch
    private lateinit var stage: Stage
    private lateinit var font: BitmapFont
    private lateinit var preferences: Preferences
    private lateinit var materialManager: MaterialManager

    override fun show() {
        batch = SpriteBatch()
        stage = Stage(ScreenViewport())
        font = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
        preferences = Gdx.app.getPreferences("SpaceShooterProgress")
        materialManager = MaterialManager(preferences)
        val skin = GlobalSkin.getInstance()
        Gdx.input.inputProcessor = stage

        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        // Title
        val titleLabel = Label("Profile", skin, "title")
        titleLabel.setAlignment(Align.center)
        table.add(titleLabel).colspan(2).padBottom(40f).row()

        // Current Stage
        val currentStage = preferences.getInteger("current_stage", 1)
        val stageLabel = Label("Current Stage: $currentStage", skin)
        stageLabel.setAlignment(Align.center)
        table.add(stageLabel).colspan(2).padBottom(20f).row()

        // Materials
        val materialsCount = materialManager.getMaterialCount()
        val materialLabel = Label("Materials: $materialsCount", skin)
        materialLabel.setAlignment(Align.center)
        table.add(materialLabel).colspan(2).padBottom(40f).row()

        // Let's Flight Button
        val flightButton = TextButton("Let's Flight!", skin).apply { 
            label.setFontScale(0.6f)
        }
        flightButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val gameScreen =
                    GameScreen(game, game.playerUpgrades, game.materials, currentStage)
                game.setScreen(gameScreen)
                dispose()
                return true
            }
        })
        table.add(flightButton).width(200f).height(50f).padRight(20f)

        // Back Button
        val backButton = TextButton("Back", skin).apply { 
            label.setFontScale(0.6f)
        }
        backButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val startScreen = StartScreen(game, game.playerUpgrades, game.materials)
                game.setScreen(startScreen)
                dispose()
                return true
            }
        })
        table.add(backButton).width(100f).height(40f)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()

        batch.begin()
        font.draw(batch, "", 0f, 0f)
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