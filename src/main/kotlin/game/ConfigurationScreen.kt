package br.com.woodriver.game

import com.badlogic.gdx.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.Preferences

class ConfigurationScreen(private val game: SpaceShooterGame) : Screen {
    private val stage = Stage(ScreenViewport())
    private val skin = Skin(Gdx.files.internal("assets/skin/quantum-horizon-ui.json"))
    private val preferences: Preferences = Gdx.app.getPreferences("SpaceShooterPreferences")

    override fun show() {
        Gdx.input.inputProcessor = stage
        setupUI()
    }

    private fun setupUI() {
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        val titleLabel = Label("Settings", skin, "title")
        table.add(titleLabel).colspan(2).padBottom(20f).row()

        // Music Volume Slider
        val musicVolumeLabel = Label("Music Volume", skin)
        val musicVolumeSlider = Slider(0f, 1f, 0.1f, false, skin)
        musicVolumeSlider.value = preferences.getFloat("musicVolume", 0.5f)
        musicVolumeSlider.addListener {
            preferences.putFloat("musicVolume", musicVolumeSlider.value)
            preferences.flush()
            false
        }
        table.add(musicVolumeLabel).left().pad(10f)
        table.add(musicVolumeSlider).width(200f).pad(10f).row()

        // Sound Effects Volume Slider
        val soundVolumeLabel = Label("Sound Effects Volume", skin)
        val soundVolumeSlider = Slider(0f, 1f, 0.1f, false, skin)
        soundVolumeSlider.value = preferences.getFloat("soundVolume", 0.5f)
        soundVolumeSlider.addListener {
            preferences.putFloat("soundVolume", soundVolumeSlider.value)
            preferences.flush()
            false
        }
        table.add(soundVolumeLabel).left().pad(10f)
        table.add(soundVolumeSlider).width(200f).pad(10f).row()

        // Back Button
        val backButton = TextButton("Back", skin)
        backButton.addListener {
           // game.screen = WelcomeScreen(game)
            false
        }
        table.add(backButton).colspan(2).padTop(20f)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(Math.min(Gdx.graphics.deltaTime, 1 / 30f))
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
