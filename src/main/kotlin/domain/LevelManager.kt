package br.com.woodriver.domain

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.Color

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
        1 -> 10
        2 -> 15
        3 -> 20
        4 -> 25
        5 -> 30
        else -> 10
    }
    private val shapeRenderer = ShapeRenderer()

    fun update(delta: Float): List<Enemy> {
        return currentBoss?.update(delta, screenWidth) ?: emptyList()
    }

    fun incrementScore() {
        score++
        if (score >= bossThreshold && currentBoss == null && !isLevelCompleting) {
            startBossFight()
        }
    }

    private fun startBossFight() {
        isBossFight = true
        currentBoss = Boss.createBoss(screenWidth, screenHeight)
    }

    fun handleBossDamage(projectile: Rectangle, damage: Int = 1): Boolean {
        return currentBoss?.let { boss ->
            if (Intersector.overlaps(projectile, boss.info)) {
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

    fun draw(batch: SpriteBatch) {
        currentBoss?.let { boss ->
            // First draw the boss texture
            batch.draw(boss.texture, boss.info.x, boss.info.y, boss.info.width, boss.info.height)
            
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