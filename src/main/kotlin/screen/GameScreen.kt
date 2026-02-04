package br.com.woodriver.screen

import br.com.woodriver.DuduInSpace
import br.com.woodriver.domain.Materials
import br.com.woodriver.domain.PlayerUpgrades
import br.com.woodriver.game.SpaceShooterGame
import br.com.woodriver.manager.MaterialManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class GameScreen(
    private val game: DuduInSpace,
    private val playerUpgrades: PlayerUpgrades,
    private val materials: Materials,
    private val levelNumber: Int
) : Screen {
    private val batch = SpriteBatch()
    private val camera = OrthographicCamera()
    private var gameOver = false
    private val materialManager = MaterialManager.fromMaterials(materials, Gdx.app.getPreferences("SpaceShooterProgress"))
    private val spaceShooterGame = SpaceShooterGame(game, levelNumber, materialManager, playerUpgrades)
    private val damageFont = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))

    init {
        camera.setToOrtho(false, 800f, 480f)
        batch.projectionMatrix = camera.combined
        damageFont.data.setScale(0.5f)
    }

    override fun render(delta: Float) {
        if (gameOver) {
            val upgradeScreen = UpgradeScreen(game, playerUpgrades, materialManager)
            game.setScreen(upgradeScreen)
            dispose() // Dispose resources when transitioning to another screen
            return
        }

        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        batch.projectionMatrix = camera.combined

        spaceShooterGame.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
        batch.projectionMatrix = camera.combined
        spaceShooterGame.resize(width, height)
    }

    override fun show() {
        spaceShooterGame.show()
    }

    override fun hide() {}
    override fun pause() {}
    override fun resume() {}
    override fun dispose() {
        batch.dispose()
        spaceShooterGame.dispose()
        damageFont.dispose()
    }
}
