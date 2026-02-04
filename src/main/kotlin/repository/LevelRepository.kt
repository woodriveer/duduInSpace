package br.com.woodriver.repository

import br.com.woodriver.domain.Choreography
import br.com.woodriver.domain.EnemyType
import br.com.woodriver.domain.LevelConfig
import br.com.woodriver.domain.Wave

class LevelRepository {

    private val levelConfigs = mapOf(
        1 to LevelConfig(
            level = 1,
            allowedEnemies = listOf(EnemyType.ASTEROID),
            enemySpawnInterval = 1.5f,
            waves = listOf(
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 5, spawnInterval = 0.5f, choreography = Choreography.FROM_TOP),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 8, spawnInterval = 0.4f, choreography = Choreography.FROM_LEFT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 13, spawnInterval = 0.3f, choreography = Choreography.FROM_RIGHT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 21, spawnInterval = 0.2f, choreography = Choreography.FROM_TOP, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 34, spawnInterval = 0.1f, choreography = Choreography.FROM_LEFT, initialDelay = 2f)
            ),
            bossTriggerThreshold = 5
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
        ),
        6 to LevelConfig(
            level = 6,
            allowedEnemies = listOf(EnemyType.UFO, EnemyType.SPACE_SHIP),
            waves = listOf(
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 5, spawnInterval = 0.5f, choreography = Choreography.FROM_TOP),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 8, spawnInterval = 0.4f, choreography = Choreography.FROM_LEFT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 34, spawnInterval = 0.1f, choreography = Choreography.FROM_LEFT, initialDelay = 2f)
            ),
            enemySpawnInterval = 0.6f
        ),
        7 to LevelConfig(
            level = 7,
            allowedEnemies = listOf(EnemyType.UFO, EnemyType.SPACE_SHIP),
            waves = listOf(
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 5, spawnInterval = 0.5f, choreography = Choreography.FROM_TOP),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 8, spawnInterval = 0.4f, choreography = Choreography.FROM_LEFT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 13, spawnInterval = 0.3f, choreography = Choreography.FROM_RIGHT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 34, spawnInterval = 0.1f, choreography = Choreography.FROM_LEFT, initialDelay = 2f)
            ),
            enemySpawnInterval = 0.6f
        ),
        8 to LevelConfig(
            level = 8,
            allowedEnemies = listOf(EnemyType.ASTEROID),
            enemySpawnInterval = 1.5f,
            waves = listOf(
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 5, spawnInterval = 0.5f, choreography = Choreography.FROM_TOP),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 8, spawnInterval = 0.4f, choreography = Choreography.FROM_LEFT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 13, spawnInterval = 0.3f, choreography = Choreography.FROM_RIGHT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 21, spawnInterval = 0.2f, choreography = Choreography.FROM_TOP, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 34, spawnInterval = 0.1f, choreography = Choreography.FROM_LEFT, initialDelay = 2f)
            ),
            bossTriggerThreshold = 5
        ),
        9 to LevelConfig(
            level = 9,
            allowedEnemies = listOf(EnemyType.ASTEROID),
            enemySpawnInterval = 1.5f,
            waves = listOf(
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 5, spawnInterval = 0.5f, choreography = Choreography.FROM_TOP),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 8, spawnInterval = 0.4f, choreography = Choreography.FROM_LEFT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 13, spawnInterval = 0.3f, choreography = Choreography.FROM_RIGHT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 21, spawnInterval = 0.2f, choreography = Choreography.FROM_TOP, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 34, spawnInterval = 0.1f, choreography = Choreography.FROM_LEFT, initialDelay = 2f)
            ),
            bossTriggerThreshold = 5
        ),
        10 to LevelConfig(
            level = 10,
            allowedEnemies = listOf(EnemyType.ASTEROID),
            enemySpawnInterval = 1.5f,
            waves = listOf(
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 5, spawnInterval = 0.5f, choreography = Choreography.FROM_TOP),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 8, spawnInterval = 0.4f, choreography = Choreography.FROM_LEFT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 13, spawnInterval = 0.3f, choreography = Choreography.FROM_RIGHT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 21, spawnInterval = 0.2f, choreography = Choreography.FROM_TOP, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 34, spawnInterval = 0.1f, choreography = Choreography.FROM_LEFT, initialDelay = 2f)
            ),
            bossTriggerThreshold = 5
        ),
        11 to LevelConfig(
            level = 11,
            allowedEnemies = listOf(EnemyType.ASTEROID),
            enemySpawnInterval = 1.5f,
            waves = listOf(
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 5, spawnInterval = 0.5f, choreography = Choreography.FROM_TOP),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 8, spawnInterval = 0.4f, choreography = Choreography.FROM_LEFT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 13, spawnInterval = 0.3f, choreography = Choreography.FROM_RIGHT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 21, spawnInterval = 0.2f, choreography = Choreography.FROM_TOP, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 34, spawnInterval = 0.1f, choreography = Choreography.FROM_LEFT, initialDelay = 2f)
            ),
            bossTriggerThreshold = 5
        ),
        12 to LevelConfig(
            level = 12,
            allowedEnemies = listOf(EnemyType.ASTEROID),
            enemySpawnInterval = 1.5f,
            waves = listOf(
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 5, spawnInterval = 0.5f, choreography = Choreography.FROM_TOP),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 8, spawnInterval = 0.4f, choreography = Choreography.FROM_LEFT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 13, spawnInterval = 0.3f, choreography = Choreography.FROM_RIGHT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 21, spawnInterval = 0.2f, choreography = Choreography.FROM_TOP, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 34, spawnInterval = 0.1f, choreography = Choreography.FROM_LEFT, initialDelay = 2f)
            ),
            bossTriggerThreshold = 5
        ),
        13 to LevelConfig(
            level = 13,
            allowedEnemies = listOf(EnemyType.ASTEROID),
            enemySpawnInterval = 1.5f,
            waves = listOf(
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 5, spawnInterval = 0.5f, choreography = Choreography.FROM_TOP),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 8, spawnInterval = 0.4f, choreography = Choreography.FROM_LEFT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 13, spawnInterval = 0.3f, choreography = Choreography.FROM_RIGHT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 21, spawnInterval = 0.2f, choreography = Choreography.FROM_TOP, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 34, spawnInterval = 0.1f, choreography = Choreography.FROM_LEFT, initialDelay = 2f)
            ),
            bossTriggerThreshold = 5
        ),
        14 to LevelConfig(
            level = 14,
            allowedEnemies = listOf(EnemyType.ASTEROID),
            enemySpawnInterval = 1.5f,
            waves = listOf(
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 5, spawnInterval = 0.5f, choreography = Choreography.FROM_TOP),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 8, spawnInterval = 0.4f, choreography = Choreography.FROM_LEFT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 13, spawnInterval = 0.3f, choreography = Choreography.FROM_RIGHT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 21, spawnInterval = 0.2f, choreography = Choreography.FROM_TOP, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 34, spawnInterval = 0.1f, choreography = Choreography.FROM_LEFT, initialDelay = 2f)
            ),
            bossTriggerThreshold = 5
        ),
        15 to LevelConfig(
            level = 15,
            allowedEnemies = listOf(EnemyType.ASTEROID),
            enemySpawnInterval = 1.5f,
            waves = listOf(
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 5, spawnInterval = 0.5f, choreography = Choreography.FROM_TOP),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 8, spawnInterval = 0.4f, choreography = Choreography.FROM_LEFT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 13, spawnInterval = 0.3f, choreography = Choreography.FROM_RIGHT, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 21, spawnInterval = 0.2f, choreography = Choreography.FROM_TOP, initialDelay = 2f),
                Wave(enemyType = EnemyType.ASTEROID, totalEnemies = 34, spawnInterval = 0.1f, choreography = Choreography.FROM_LEFT, initialDelay = 2f)
            ),
            bossTriggerThreshold = 5
        )

    )

    fun getLevelConfig(level: Int): LevelConfig {
        return levelConfigs[level] ?: throw IllegalArgumentException("Level not found: $level")
    }
}
