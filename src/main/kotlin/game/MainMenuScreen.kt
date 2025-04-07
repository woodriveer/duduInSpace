package br.com.woodriver.game

import com.badlogic.gdx.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

class MainMenuScreen(private val game: SpaceShooterGame) : Screen {
    override fun show() {
        // Initialize UI elements and input processors
    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        game.batch.begin()
        // Draw UI elements
        game.batch.end()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        // Dispose of assets specific to this screen
    }
}
