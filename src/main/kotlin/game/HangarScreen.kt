package br.com.woodriver.game

import br.com.woodriver.DuduInSpace
import br.com.woodriver.domain.ShipClass
import br.com.woodriver.screen.ProfileScreen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport

class HangarScreen(private val game: DuduInSpace) : Screen {
    private val stage = Stage(ScreenViewport())
    private val skin = GlobalSkin.getInstance()
    private val preferences = game.preferences

    override fun show() {
        Gdx.input.inputProcessor = stage
        refreshUI()
    }

    private fun refreshUI() {
        stage.clear()
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        // Title
        val titleLabel =
                Label("Hangar", skin, "title").apply {
                    color = com.badlogic.gdx.graphics.Color.WHITE
                    setFontScale(0.8f)
                }
        root.add(titleLabel).colspan(3).padBottom(40f).row()

        // Materials Header
        val materialsTable = Table()
        val materialsLabel =
                Label("Materials: ${game.materials.count}", skin).apply {
                    color = com.badlogic.gdx.graphics.Color.WHITE
                    setFontScale(0.6f)
                }
        materialsTable.add(materialsLabel).pad(15f)
        root.add(materialsTable).colspan(3).padBottom(30f).row()

        // Ships List
        val ownedShips = preferences.getString("owned_ships", ShipClass.ASSAULT.name).split(",")

        ShipClass.entries.forEach { shipClass ->
            val shipTable = Table()
            shipTable.background = skin.getDrawable("button-over-c")
            shipTable.pad(20f) // Add some padding inside the ship box

            val nameLabel =
                    Label(shipClass.displayName, skin, "default").apply {
                        color = com.badlogic.gdx.graphics.Color.WHITE
                        setFontScale(0.6f)
                    }
            shipTable.add(nameLabel).padBottom(10f).row()

            val descLabel =
                    Label(shipClass.description, skin).apply {
                        color = com.badlogic.gdx.graphics.Color.WHITE
                        setFontScale(0.4f)
                        setWrap(true)
                        setAlignment(Align.center)
                    }
            shipTable.add(descLabel).width(200f).padBottom(15f).row()

            val statsTable = Table()
            val statStyle = { text: String ->
                Label(text, skin).apply {
                    color = com.badlogic.gdx.graphics.Color.WHITE
                    setFontScale(0.4f)
                }
            }

            statsTable.add(statStyle("Fire Rate: ${shipClass.fireRate}s")).padRight(10f)
            statsTable.add(statStyle("Damage: x${shipClass.damageMultiplier}")).row()
            statsTable.add(statStyle("Type: ${shipClass.weaponType}")).colspan(2).padTop(5f)
            shipTable.add(statsTable).padBottom(20f).row()

            if (game.selectedShipClass == shipClass) {
                val selectedLabel =
                        Label("SELECTED", skin).apply {
                            color = com.badlogic.gdx.graphics.Color.GREEN
                            setFontScale(0.5f)
                        }
                shipTable.add(selectedLabel).height(40f)
            } else if (ownedShips.contains(shipClass.name)) {
                val selectButton = TextButton("Select", skin).apply { label.setFontScale(0.5f) }
                selectButton.addListener(
                        object : ClickListener() {
                            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                                game.selectedShipClass = shipClass
                                preferences.putString("selected_ship_class", shipClass.name)
                                preferences.flush()
                                refreshUI()
                            }
                        }
                )
                shipTable.add(selectButton).width(120f).height(40f)
            } else {
                val costLabel =
                        Label("${shipClass.materialCost} Materials", skin).apply {
                            color = com.badlogic.gdx.graphics.Color.WHITE
                            setFontScale(0.4f)
                        }
                shipTable.add(costLabel).padBottom(10f).row()

                val buyButton = TextButton("Buy", skin).apply { label.setFontScale(0.5f) }
                val canAfford = game.materials.hasEnough(shipClass.materialCost)
                buyButton.isDisabled = !canAfford

                buyButton.addListener(
                        object : ClickListener() {
                            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                                if (!buyButton.isDisabled &&
                                                game.materials.spend(shipClass.materialCost)
                                ) {
                                    game.materials.save(preferences)
                                    val newOwned = ownedShips.toMutableList()
                                    newOwned.add(shipClass.name)
                                    preferences.putString("owned_ships", newOwned.joinToString(","))
                                    preferences.flush()
                                    refreshUI()
                                }
                            }
                        }
                )
                shipTable.add(buyButton).width(120f).height(40f)
            }

            root.add(shipTable).width(250f).pad(15f)
        }

        root.row()

        // Back Button
        val backButton = TextButton("Back", skin).apply { label.setFontScale(0.6f) }
        backButton.addListener(
                object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        game.setScreen(ProfileScreen(game))
                    }
                }
        )
        root.add(backButton).colspan(3).padTop(50f).width(150f).height(45f)
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
        stage.dispose()
    }
}
