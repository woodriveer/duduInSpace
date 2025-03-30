package br.com.woodriver

import br.com.woodriver.game.SpaceShooterGame
import br.com.woodriver.game.WelcomeScreen
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
  val config = Lwjgl3ApplicationConfiguration().apply {
    setTitle("Space Shooter")
    setWindowedMode(800, 600)
  }
  Lwjgl3Application(WelcomeScreen(), config)
}