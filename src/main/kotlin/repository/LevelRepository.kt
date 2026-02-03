package br.com.woodriver.repository

import br.com.woodriver.domain.EnemyType
import br.com.woodriver.domain.LevelConfig

class LevelRepository {

    private val levelConfigs = mapOf(
        1 to LevelConfig(
            level = 1,
            allowedEnemies = listOf(EnemyType.ASTEROID),
            enemySpawnInterval = 1.5f
        ),
        2 to LevelConfig(
            level = 2,
            allowedEnemies = listOf(EnemyType.ASTEROID, EnemyType.UFO),
            enemySpawnInterval = 1.2f
        ),
        3 to LevelConfig(
            level = 3,
            allowedEnemies = listOf(EnemyType.ASTEROID, EnemyType.UFO, EnemyType.SPACE_SHIP),
            enemySpawnInterval = 1.0f
        ),
        4 to LevelConfig(
            level = 4,
            allowedEnemies = listOf(EnemyType.ASTEROID, EnemyType.UFO, EnemyType.SPACE_SHIP),
            enemySpawnInterval = 0.8f
        ),
        5 to LevelConfig(
            level = 5,
            allowedEnemies = listOf(EnemyType.UFO, EnemyType.SPACE_SHIP),
            enemySpawnInterval = 0.6f
        )
    )

    fun getLevelConfig(level: Int): LevelConfig {
        return levelConfigs[level] ?: throw IllegalArgumentException("Level not found: $level")
    }
}
