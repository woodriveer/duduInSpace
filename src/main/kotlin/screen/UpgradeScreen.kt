package br.com.woodriver.screen

import br.com.woodriver.DuduInSpace
import br.com.woodriver.domain.PlayerUpgrades
import br.com.woodriver.manager.MaterialManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport

class UpgradeScreen(
        private val game: DuduInSpace,
        private val playerUpgrades: PlayerUpgrades,
) : Screen {
    private val stage: Stage = Stage(ScreenViewport())
    private val skin: Skin
    private val font: BitmapFont
    private val titleFont: BitmapFont
    private val materialsLabel: Label
    private val upgradeTable: Table
    private val preferences: Preferences
    private val materialManager: MaterialManager
    private val backButton: TextButton

    init {
        Gdx.input.inputProcessor = stage

        preferences = Gdx.app.getPreferences("DuduInSpace")
        materialManager = MaterialManager(preferences)

        // Create skin
        skin = Skin(Gdx.files.internal("assets/skin/quantum-horizon-ui.json"))

        font = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
        titleFont = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))

        // Create styles
        val labelStyle = LabelStyle(font, Color.WHITE)
        val titleStyle = LabelStyle(titleFont, Color.WHITE)
        val buttonStyle =
                TextButtonStyle().apply {
                    font = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
                    up = skin.newDrawable("white", Color.DARK_GRAY)
                    down = skin.newDrawable("white", Color.GRAY)
                }

        // Create materials label
        materialsLabel = Label("Materials: ${materialManager.getMaterialCount()}", labelStyle)

        // Create upgrade table
        upgradeTable = Table()
        upgradeTable.setFillParent(true)
        upgradeTable.pad(20f)

        // Add title
        upgradeTable.add(Label("Permanent Upgrades", titleStyle)).colspan(2).padBottom(20f).row()

        // Add materials label
        upgradeTable.add(materialsLabel).colspan(2).padBottom(20f).row()

        // Add upgrade buttons for each upgrade type, showing level and cost, disabling when maxed
        // or insufficient materials
        val maxLevel = PlayerUpgrades.MAX_LEVEL
        playerUpgrades.getAllUpgrades().forEach { upgrade ->
            val currentLevel = playerUpgrades.getUpgradeLevel(upgrade.type)
            val cost = playerUpgrades.getUpgradeCost(upgrade.type)
            val buttonText = "${upgrade.description} LVL: $currentLevel/$maxLevel (Cost: $cost)"
            val upgradeButton = TextButton(buttonText, buttonStyle)
            // Disable if at max level or not enough materials
            val canUpgrade =
                    playerUpgrades.canUpgrade(upgrade.type, materialManager.getMaterialCount())
            upgradeButton.isDisabled = !canUpgrade
            upgradeButton.addListener(
                    object : ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            if (playerUpgrades.canUpgrade(
                                            upgrade.type,
                                            materialManager.getMaterialCount()
                                    )
                            ) {
                                // spend materials
                                materialManager.spendMaterial(cost)
                                // apply upgrade
                                playerUpgrades.purchaseUpgrade(upgrade.type)
                                // update materials label
                                materialsLabel.setText(
                                        "Materials: ${materialManager.getMaterialCount()}"
                                )
                                // update button text and state
                                val newLevel = playerUpgrades.getUpgradeLevel(upgrade.type)
                                upgradeButton.label.setText(
                                        "${upgrade.description} LVL: $newLevel/$maxLevel (Cost: ${playerUpgrades.getUpgradeCost(upgrade.type)})"
                                )
                                val stillCanUpgrade =
                                        playerUpgrades.canUpgrade(
                                                upgrade.type,
                                                materialManager.getMaterialCount()
                                        )
                                upgradeButton.isDisabled = !stillCanUpgrade
                            }
                        }
                    }
            )
            upgradeTable.add(upgradeButton).colspan(2).padBottom(10f).row()
        }

        // Add back button
        backButton = TextButton("Back", buttonStyle)
        backButton.addListener(
                object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        // Save current state to domain materials to stay in sync
                        game.materials.count = materialManager.getMaterialCount()
                        game.materials.save(game.preferences)

                        val startScreen = StartScreen(game, playerUpgrades, game.materials)
                        game.setScreen(startScreen)
                        dispose()
                    }
                }
        )
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
