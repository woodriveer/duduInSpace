package br.com.woodriver.manager

import br.com.woodriver.domain.Boss
import br.com.woodriver.domain.Enemy
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle

class LevelManager(
    private val screenWidth: Float,
    private val screenHeight: Float,
    private val levelNumber: Int = 1
) {
    private var score: Int = 0
    private var currentBoss: Boss? = null
    private var isBossFight: Boolean = false
    private var isLevelCompleting: Boolean = false
    private val bossThreshold: Int = when (levelNumber) {
        1 -> 5
        2 -> 10
        3 -> 15
        4 -> 20
        5 -> 25
        else -> 5
    }
    private val shapeRenderer = ShapeRenderer()

    fun update(delta: Float): List<Enemy> {
        if (isBossFight) {
            currentBoss?.let { boss ->
                boss.update(delta)
                Gdx.app.log("LevelManager", "Boss position: ${boss.info.x}, ${boss.info.y}")
            }
        }
        return emptyList()
    }

    fun incrementScore() {
        score++
        Gdx.app.log("LevelManager", "Score incremented to: $score (Boss threshold: $bossThreshold)")
        if (score >= bossThreshold && currentBoss == null && !isLevelCompleting) {
            startBossFight()
        }
    }

    private fun startBossFight() {
        isBossFight = true
        // Center the boss at the top of the screen
        val bossX = (screenWidth - 128f) / 2
        val bossY = screenHeight - 150f
        currentBoss = Boss.Companion.create(bossX, bossY)
        Gdx.app.log("LevelManager", "Starting boss fight at position: $bossX, $bossY")
    }

    fun handleBossDamage(projectile: Rectangle, damage: Int = 1): Boolean {
        return currentBoss?.let { boss ->
            if (Intersector.overlaps(projectile, boss.info)) {
                Gdx.app.log("LevelManager", "Boss hit! Health: ${boss.health}")
                if (boss.takeDamage(damage)) {
                    currentBoss = null
                    isBossFight = false
                    isLevelCompleting = true // Start level completion
                    true
                } else {
                    true
                }
            } else {
                false
            }
        } ?: false
    }

    fun handleBossAsteroidCollision(playerBounds: Rectangle): Boolean {
        return currentBoss?.let { boss ->
            boss.asteroids.any { asteroid ->
                Intersector.overlaps(asteroid, playerBounds)
            }
        } ?: false
    }

    fun draw(batch: SpriteBatch) {
        currentBoss?.let { boss ->
            // Draw boss and its asteroids
            boss.draw(batch)

            // End the SpriteBatch before using ShapeRenderer
            batch.end()

            // Configure and use ShapeRenderer
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.projectionMatrix = batch.projectionMatrix

            // Draw health bar
            val healthBarWidth = boss.info.width
            val healthBarHeight = 10f
            val healthBarX = boss.info.x
            val healthBarY = boss.info.y + boss.info.height + 5f

            // Draw background
            shapeRenderer.color = Color.GRAY
            shapeRenderer.rect(healthBarX, healthBarY, healthBarWidth, healthBarHeight)

            // Draw health
            val healthPercentage = boss.health.toFloat() / boss.maxHealth
            val currentHealthWidth = healthBarWidth * healthPercentage
            shapeRenderer.color = if (healthPercentage <= 0.3f) Color.RED else Color.GREEN
            shapeRenderer.rect(healthBarX, healthBarY, currentHealthWidth, healthBarHeight)

            // End ShapeRenderer
            shapeRenderer.end()

            // Begin SpriteBatch again for further rendering
            batch.begin()
        }
    }

    fun isBossFight(): Boolean = isBossFight

    fun isLevelCompleting(): Boolean = isLevelCompleting

    fun getCurrentBoss(): Boss? = currentBoss

    fun dispose() {
        currentBoss?.texture?.dispose()
        shapeRenderer.dispose()
    }
}