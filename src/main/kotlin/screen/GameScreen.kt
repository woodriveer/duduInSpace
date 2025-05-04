package br.com.woodriver.game

import br.com.woodriver.DuduInSpace
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import br.com.woodriver.domain.Player
import br.com.woodriver.domain.Enemy
import br.com.woodriver.domain.Projectile
import br.com.woodriver.domain.Boss
import br.com.woodriver.domain.PlayerUpgrades
import br.com.woodriver.domain.Materials
import br.com.woodriver.manager.MaterialManager
import br.com.woodriver.domain.EnemyType
import com.badlogic.gdx.graphics.Color
import br.com.woodriver.domain.DamageNumber
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.InputProcessor

class GameScreen(
    private val game: DuduInSpace,
    private val playerUpgrades: PlayerUpgrades,
    private val materials: Materials
) : Screen {
    private val batch = SpriteBatch()
    private val camera = OrthographicCamera()
    private val player = Player.create(Vector2(0f, 0f), playerUpgrades)
    private val enemies = mutableListOf<Enemy>()
    private val projectiles = mutableListOf<Projectile>()
    private var boss: Boss? = null
    private var score = 0
    private var gameOver = false
    private val materialManager = MaterialManager.fromMaterials(materials, Gdx.app.getPreferences("SpaceShooterProgress"))
    private val spaceShooterGame = SpaceShooterGame(game, 1, materialManager, playerUpgrades)
    private val materialDropNumbers = mutableListOf<DamageNumber>()
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

    private fun update(delta: Float) {
        // Update player
        player.update(delta)

        // Update enemies
        enemies.forEach { it.update(delta) }
        enemies.removeAll { it.isDestroyed() }

        // Update projectiles
        projectiles.forEach { it.update(delta) }
        projectiles.removeAll { it.isDestroyed() }

        // Update boss
        boss?.update(delta)

        // Check collisions
        checkCollisions()

        // Spawn enemies and boss
        if (enemies.size < 10) {
            enemies.add(Enemy.create(EnemyType.ASTEROID, 0f, 0f))
        }

        if (score >= 1000 && boss == null) {
            boss = Boss.create(0f, 0f)
        }
    }

    private fun checkCollisions() {
        // Check projectile-enemy collisions
        projectiles.forEach { projectile ->
            enemies.forEach { enemy ->
                if (projectile.bounds.overlaps(enemy.bounds)) {
                    enemy.takeDamage(projectile.damage)
                    projectile.destroy()
                    if (enemy.isDestroyed()) {
                        score += 10
                        materials.addIron(1)
                        spawnMaterialDropNumber(enemy.bounds.x, enemy.bounds.y)
                    }
                }
            }
        }

        // Check projectile-boss collisions
        boss?.let { boss ->
            projectiles.forEach { projectile ->
                if (projectile.bounds.overlaps(boss.info)) {
                    boss.takeDamage(projectile.damage)
                    projectile.destroy()
                    if (boss.isDestroyed()) {
                        score += 100
                        materials.addIron(10)
                        spawnMaterialDropNumber(boss.info.x, boss.info.y)
                    }
                }
                boss.asteroids.forEach { asteroid ->
                    if (projectile.bounds.overlaps(asteroid)) {
                        projectile.destroy()
                        boss.asteroids.remove(asteroid)
                    }
                }
            }
        }
    }

    private fun spawnMaterialDropNumber(x: Float, y: Float) {
        val materialNumber = DamageNumber(1, x, y, damageFont)
        materialNumber.setColor(Color.BLUE)
        materialDropNumbers.add(materialNumber)
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
        batch.projectionMatrix = camera.combined
    }

    override fun show() {
        Gdx.input.inputProcessor = spaceShooterGame as InputProcessor
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
