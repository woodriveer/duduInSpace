package br.com.woodriver.domain

import com.badlogic.gdx.Gdx
import kotlin.random.Random

class EnemySpawner(
    private val screenWidth: Float,
    private val screenHeight: Float,
    private val levelConfig: LevelConfig
) {
    private var spawnTimer: Float = 0f
    private var waveIndex = 0
    private var enemiesSpawned = 0
    private var waveDelayTimer = 0f

    fun update(delta: Float): Enemy? {
        if (waveIndex >= levelConfig.waves.size) {
            return null
        }

        val currentWave = levelConfig.waves[waveIndex]

        if (waveDelayTimer < currentWave.initialDelay) {
            waveDelayTimer += delta
            return null
        }

        spawnTimer += delta
        if (spawnTimer >= currentWave.spawnInterval && enemiesSpawned < currentWave.totalEnemies) {
            spawnTimer = 0f
            enemiesSpawned++
            return spawnEnemy(currentWave)
        }

        if (enemiesSpawned >= currentWave.totalEnemies) {
            waveIndex++
            enemiesSpawned = 0
            waveDelayTimer = 0f
        }

        return null
    }

    private fun spawnEnemy(wave: Wave): Enemy {
        val x = when (wave.choreography) {
            Choreography.FROM_LEFT -> 0f
            Choreography.FROM_RIGHT -> screenWidth
            Choreography.FROM_TOP -> Random.nextFloat() * screenWidth
        }
        return Enemy.create(
            type = wave.enemyType,
            x = x,
            y = screenHeight
        )
    }

    fun isWaveFinished(): Boolean {
        return waveIndex >= levelConfig.waves.size
    }

    fun reset() {
        spawnTimer = 0f
        waveIndex = 0
        enemiesSpawned = 0
        waveDelayTimer = 0f
    }
}
