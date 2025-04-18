package br.com.woodriver.domain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable

class World(
    private val playerUpgrades: PlayerUpgrades,
    private val materials: Materials
) : Disposable, InputProcessor {
    private val player = Player(Vector2(400f, 240f), playerUpgrades)
    private val enemies = mutableListOf<Enemy>()
    private val projectiles = mutableListOf<Projectile>()
    private var boss: Boss? = null
    private var score = 0
    private var gameOver = false

    fun update(delta: Float) {
        if (gameOver) return

        // Update player
        player.update(delta)

        // Update enemies
        enemies.forEach { it.update(delta) }
        enemies.removeAll { !it.isActive }

        // Update projectiles
        projectiles.forEach { it.update(delta) }
        projectiles.removeAll { it.isDestroyed() }

        // Update boss
        boss?.update(delta)
        if (boss?.isDead == true) {
            boss = null
        }

        // Spawn enemies
        if (enemies.size < 10 && Math.random() < 0.02) {
            val randomX = Gdx.graphics.width.toFloat()
            val randomY = (Math.random() * Gdx.graphics.height).toFloat()
            enemies.add(Enemy.create(EnemyType.values().random(), randomX, randomY))
        }

        // Spawn boss
        if (score >= 1000 && boss == null) {
            val bossX = (Gdx.graphics.width - 128f) / 2
            val bossY = Gdx.graphics.height - 150f
            boss = Boss.create(bossX, bossY)
        }

        // Check collisions
        checkCollisions()
    }

    fun render(batch: SpriteBatch) {
        player.draw(batch)
        enemies.forEach { it.draw(batch) }
        projectiles.forEach { it.draw(batch) }
        boss?.draw(batch)
    }

    private fun checkCollisions() {
        // Player-Enemy collisions
        enemies.forEach { enemy ->
            if (player.bounds.overlaps(enemy.bounds)) {
                player.takeDamage(enemy.damage)
                enemy.takeDamage(1)
            }
        }

        // Projectile-Enemy collisions
        projectiles.forEach { projectile ->
            enemies.forEach { enemy ->
                if (projectile.bounds.overlaps(enemy.bounds)) {
                    projectile.destroy()
                    if (enemy.takeDamage(projectile.damage)) {
                        score += enemy.score
                        // Add materials based on enemy type
                        when (enemy.type) {
                            EnemyType.ASTEROID -> materials.addIron(1)
                            EnemyType.UFO -> materials.addGold(1)
                            EnemyType.SPACE_SHIP -> materials.addCrystal(1)
                        }
                    }
                }
            }
        }

        // Projectile-Boss collisions
        boss?.let { boss ->
            projectiles.forEach { projectile ->
                if (projectile.bounds.overlaps(boss.bounds)) {
                    projectile.destroy()
                    if (boss.takeDamage(projectile.damage)) {
                        score += 100
                        // Add bonus materials for defeating boss
                        materials.addIron(5)
                        materials.addGold(3)
                        materials.addCrystal(1)
                    }
                }
            }
        }
    }

    override fun dispose() {
        player.dispose()
        enemies.forEach { it.dispose() }
        projectiles.forEach { it.dispose() }
        boss?.dispose()
    }

    // InputProcessor implementation
    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }
} 