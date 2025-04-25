package br.com.woodriver.screen

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
import br.com.woodriver.domain.SpaceShooter
import br.com.woodriver.domain.EnemyType


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
    private val spaceShooter = SpaceShooter(playerUpgrades, materials)

    init {
        camera.setToOrtho(false, 800f, 480f)
        batch.projectionMatrix = camera.combined
    }

    override fun render(delta: Float) {
        if (gameOver) {
            val upgradeScreen = UpgradeScreen(game, playerUpgrades, materials)
            game.setScreen(upgradeScreen)
            dispose() // Dispose resources when transitioning to another screen
            return
        }

        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        batch.projectionMatrix = camera.combined

        spaceShooter.update(delta)

        batch.begin()
        spaceShooter.render(batch)
        batch.end()
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
        // Player-Enemy collisions
        enemies.forEach { enemy ->
            if (player.bounds.overlaps(enemy.bounds)) {
                player.takeDamage(1)
                enemy.isActive = false
                if (player.health <= 0) {
                    gameOver = true
                }
            }
        }

        // Projectile-Enemy collisions
        projectiles.forEach { projectile ->
            enemies.forEach { enemy ->
                if (projectile.bounds.overlaps(enemy.bounds)) {
                    enemy.takeDamage(projectile.damage)
                    projectile.destroy()
                    if (!enemy.isActive) {
                        score += 10
                        materials.addIron(1)
                    }
                }
            }
        }

        // Projectile-Boss collisions
        boss?.let { boss ->
            projectiles.forEach { projectile ->
                if (projectile.bounds.overlaps(boss.info)) {
                    boss.takeDamage(projectile.damage)
                    projectile.destroy()
                    if (boss.isDestroyed()) {
                        score += 100
                        materials.addIron(10)
                    }
                }
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
        batch.projectionMatrix = camera.combined
    }

    override fun show() {
        Gdx.input.inputProcessor = spaceShooter
    }

    override fun hide() {}
    override fun pause() {}
    override fun resume() {}
    override fun dispose() {
        batch.dispose()
        spaceShooter.dispose()
    }
} 
