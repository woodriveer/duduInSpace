package br.com.woodriver

import br.com.woodriver.game.WelcomeScreen
import com.badlogic.gdx.Game
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

class DuduInSpace : Game() {
    override fun create() {
        setScreen(WelcomeScreen(this))
    }
}

fun main() {
    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("Space Shooter")
        setWindowedMode(800, 600)
    }
    Lwjgl3Application(DuduInSpace(), config)
}