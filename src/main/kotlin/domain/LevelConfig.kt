package br.com.woodriver.domain

import br.com.woodriver.domain.Wave

data class LevelConfig(
    val level: Int,
    val allowedEnemies: List<EnemyType>,
    val enemySpawnInterval: Float,
    val waves: List<Wave> = emptyList(),
    val bossTriggerThreshold: Int = 0
)
