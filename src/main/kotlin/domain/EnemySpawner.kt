package br.com.woodriver.domain

import com.badlogic.gdx.Gdx
import kotlin.random.Random

class EnemySpawner(
    private val screenWidth: Float,
    private val screenHeight: Float,
    private val spawnInterval: Float = 1f
) {
    private var spawnTimer: Float = 0f
    private var difficulty: Float = 1f

    fun update(delta: Float): Enemy? {
        spawnTimer += delta
        if (spawnTimer >= spawnInterval / difficulty) {
            spawnTimer = 0f
            return spawnEnemy()
        }
        return null
    }

    private fun spawnEnemy(): Enemy {
        val enemyType = getRandomEnemyType()
        val size = getRandomSize(enemyType)
        val x = Random.nextFloat() * (screenWidth - size)
        return Enemy.create(
            type = enemyType,
            x = x,
            y = screenHeight
        )
    }

    private fun getRandomEnemyType(): EnemyType {
        val random = Random.nextFloat()
        return when {
            random < 0.6f -> EnemyType.ASTEROID
            random < 0.8f -> EnemyType.UFO
            else -> EnemyType.SPACE_SHIP
        }
    }

    private fun getRandomSize(enemyType: EnemyType): Float {
        return when (enemyType) {
            EnemyType.ASTEROID -> (Random.nextInt(1, 4) * 32).toFloat()
            EnemyType.UFO -> 48f
            EnemyType.SPACE_SHIP -> 64f
        }
    }

    fun increaseDifficulty() {
        difficulty = minOf(difficulty + 0.1f, 3f)
    }

    fun reset() {
        spawnTimer = 0f
        difficulty = 1f
    }
}
