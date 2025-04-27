package br.com.woodriver.game

import br.com.woodriver.domain.PlayerUpgrades
import br.com.woodriver.domain.Materials
import br.com.woodriver.DuduInSpace
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter

class UpgradeScreen(
    private val game: br.com.woodriver.DuduInSpace,
    private val playerUpgrades: PlayerUpgrades,
    private val materials: Materials
) : Screen {
    private val stage: Stage = Stage(ScreenViewport())
    private val skin: Skin
    private val font: BitmapFont
    private val titleFont: BitmapFont
    private val materialsLabel: Label
    private val upgradeTable: Table
    private val backButton: TextButton

    init {
        Gdx.input.inputProcessor = stage

        // Create skin
        skin = Skin(Gdx.files.internal("assets/skin/quantum-horizon-ui.json"))

        // Generate fonts
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/kenvector_future.ttf"))
        val parameter = FreeTypeFontParameter().apply {
            size = 24
        }
        font = generator.generateFont(parameter)
        parameter.size = 36
        titleFont = generator.generateFont(parameter)
        generator.dispose()

        // Create styles
        val labelStyle = LabelStyle(font, Color.WHITE)
        val titleStyle = LabelStyle(titleFont, Color.WHITE)
        val buttonStyle = TextButtonStyle().apply {
            font = font
            up = skin.newDrawable("white", Color.DARK_GRAY)
            down = skin.newDrawable("white", Color.GRAY)
        }

        // Create materials label
        materialsLabel = Label("Materials: Iron: ${materials.iron}, Gold: ${materials.gold}, Crystal: ${materials.crystal}", labelStyle)

        // Create upgrade table
        upgradeTable = Table()
        upgradeTable.setFillParent(true)
        upgradeTable.pad(20f)

        // Add title
        upgradeTable.add(Label("Upgrades", titleStyle)).colspan(2).padBottom(20f).row()

        // Add materials label
        upgradeTable.add(materialsLabel).colspan(2).padBottom(20f).row()

        // Add upgrade buttons
        playerUpgrades.getAllUpgrades().forEach { upgrade ->
            val upgradeButton = TextButton("${upgrade.description} (Cost: ${upgrade.cost})", buttonStyle)
            upgradeButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (playerUpgrades.canUpgrade(upgrade.type, materials.iron)) {
                        materials.iron -= upgrade.cost
                        playerUpgrades.purchaseUpgrade(upgrade.type)
                        materialsLabel.setText("Materials: Iron: ${materials.iron}, Gold: ${materials.gold}, Crystal: ${materials.crystal}")
                    }
                }
            })
            upgradeTable.add(upgradeButton).colspan(2).padBottom(10f).row()
        }

        // Add back button
        backButton = TextButton("Back", buttonStyle)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val startScreen = StartScreen(game, playerUpgrades, materials)
                game.setScreen(startScreen)
                dispose()
            }
        })
        upgradeTable.add(backButton).colspan(2).padTop(20f)

        stage.addActor(upgradeTable)
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

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun hide() {}
    override fun pause() {}
    override fun resume() {}
    override fun dispose() {
        stage.dispose()
        skin.dispose()
        font.dispose()
        titleFont.dispose()
    }
} 
