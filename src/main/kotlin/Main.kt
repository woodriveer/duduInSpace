package br.com.woodriver

import br.com.woodriver.domain.Materials
import br.com.woodriver.domain.Materials.Companion.create
import br.com.woodriver.domain.PlayerUpgrades
import br.com.woodriver.domain.ShipClass
import br.com.woodriver.domain.ShipClass.ASSAULT
import br.com.woodriver.screen.StartScreen
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

class DuduInSpace : Game() {
    lateinit var preferences: Preferences
    lateinit var playerUpgrades: PlayerUpgrades
    lateinit var materials: Materials
    lateinit var selectedShipClass: ShipClass

    override fun create() {
        try {
            println("Starting game initialization...")
            preferences = Gdx.app.getPreferences("DuduInSpace")
            playerUpgrades = PlayerUpgrades(preferences)
            materials = create(preferences)
            selectedShipClass =
                    ShipClass.fromName(
                            preferences.getString("selected_ship_class", ASSAULT.name)
                    )
            setScreen(StartScreen(this, playerUpgrades, materials))
            println("Game initialization completed successfully")
        } catch (e: Exception) {
            println("Error during game initialization: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    override fun dispose() {
        super.dispose()
        screen?.dispose()
    }
}

fun main() {
    println("Starting DuduInSpace application...")
    try {
        val config =
                Lwjgl3ApplicationConfiguration().apply {
                    setTitle("Space Shooter")
                    setWindowedMode(800, 600)
                }
        println("Configuration created, initializing game...")
        Lwjgl3Application(DuduInSpace(), config)
        println("Game started successfully")
    } catch (e: Exception) {
        println("Fatal error during game startup: ${e.message}")
        e.printStackTrace()
    }
}
