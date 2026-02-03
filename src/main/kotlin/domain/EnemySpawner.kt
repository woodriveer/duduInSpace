package br.com.woodriver.domain

import com.badlogic.gdx.Gdx
import kotlin.random.Random

class EnemySpawner(
    private val screenWidth: Float,
    private val screenHeight: Float,
    private val levelConfig: LevelConfig
) {
    private var spawnTimer: Float = 0f

    fun update(delta: Float): Enemy? {
        spawnTimer += delta
        if (spawnTimer >= levelConfig.enemySpawnInterval) {
            spawnTimer = 0f
            return spawnEnemy()
        }
        return null
    }

    private fun spawnEnemy(): Enemy {
        val enemyType = getRandomEnemyType()
        val x = Random.nextFloat() * (screenWidth)
        return Enemy.create(
            type = enemyType,
            x = x,
            y = screenHeight
        )
    }

    private fun getRandomEnemyType(): EnemyType {
        return levelConfig.allowedEnemies.random()
    }

    fun reset() {
        spawnTimer = 0f
    }
}
