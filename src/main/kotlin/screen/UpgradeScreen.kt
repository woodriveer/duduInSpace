package br.com.woodriver.screen

import br.com.woodriver.domain.PlayerUpgrades
import br.com.woodriver.domain.Materials
import br.com.woodriver.DuduInSpace
import br.com.woodriver.screen.MenuScreen
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
        PlayerUpgrades.UpgradeType.values().forEach { type ->
            val upgrade = playerUpgrades.getUpgrade(type)
            val levelLabel = Label("Level: ${upgrade.currentLevel}/${PlayerUpgrades.MAX_LEVEL}", labelStyle)
            val descriptionLabel = Label(upgrade.description, labelStyle)
            val costLabel = Label("Cost: ${upgrade.cost}", labelStyle)
            val upgradeButton = TextButton("Upgrade", buttonStyle)

            upgradeButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (playerUpgrades.canUpgrade(type, materials.iron)) {
                        if (playerUpgrades.purchaseUpgrade(type)) {
                            // Update materials and refresh screen
                            materials.spend(upgrade.cost, 0, 0)
                            val newScreen = UpgradeScreen(game, playerUpgrades, materials)
                            game.screen = newScreen
                            dispose() // Dispose resources when transitioning to another screen
                        }
                    }
                }
            })

            upgradeTable.add(descriptionLabel).left().pad(5f)
            upgradeTable.add(levelLabel).right().pad(5f).row()
            upgradeTable.add(costLabel).left().pad(5f)
            upgradeTable.add(upgradeButton).right().pad(5f).row()
            upgradeTable.add().height(20f).colspan(2).row()
        }

        // Create back button
        backButton = TextButton("Back to Menu", buttonStyle)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val menuScreen = MenuScreen(game, playerUpgrades, materials)
                game.screen = menuScreen
                dispose() // Dispose resources when transitioning to another screen
            }
        })

        // Add back button
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
