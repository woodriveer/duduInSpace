package br.com.woodriver.domain

data class LevelConfig(
    val level: Int,
    val allowedEnemies: List<EnemyType>,
    val enemySpawnInterval: Float
)
