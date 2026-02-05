package br.com.woodriver.screen

import br.com.woodriver.DuduInSpace
import br.com.woodriver.domain.Materials
import br.com.woodriver.domain.PlayerUpgrades
import br.com.woodriver.game.GlobalSkin
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport

class StoryScreen(
        private val game: DuduInSpace,
        private val playerUpgrades: PlayerUpgrades,
        private val materials: Materials,
        private val levelNumber: Int
) : Screen {
    private val batch = SpriteBatch()
    private val stage = Stage(ScreenViewport())
    private val font = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
    private val skin = GlobalSkin.getInstance()

    private val fullText =
            """
        Dudu: "Z-Bot, are we ready for the jump?"
        Z-Bot: "Sensors indicate high asteroid density in this sector, Dudu. Proceed with caution."
        Dudu: "Caution is my middle name. Well, actually it's 'Danger', but you know what I mean."
        Z-Bot: "Humor subroutines detected. Let's start the mission."
    """.trimIndent()

    private var displayedText = ""
    private var typeTimer = 0f
    private val typeSpeed = 0.05f
    private var textIndex = 0
    private lateinit var storyLabel: Label

    override fun show() {
        Gdx.input.inputProcessor = stage

        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        storyLabel =
                Label("", skin).apply {
                    setWrap(true)
                    setFontScale(0.5f)
                }

        table.add(storyLabel).width(600f).pad(20f).row()

        val startButton =
                TextButton("Start Mission", skin).apply {
                    label.setFontScale(0.6f)
                    addListener(
                            object : ClickListener() {
                                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                                    val gameScreen =
                                            GameScreen(game, playerUpgrades, materials, levelNumber)
                                    game.setScreen(gameScreen)
                                    dispose()
                                }
                            }
                    )
                }

        table.add(startButton).width(200f).height(50f).padTop(40f)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Typewriting effect
        if (textIndex < fullText.length) {
            typeTimer += delta
            if (typeTimer >= typeSpeed) {
                displayedText += fullText[textIndex]
                storyLabel.setText(displayedText)
                textIndex++
                typeTimer = 0f
            }
        }

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
