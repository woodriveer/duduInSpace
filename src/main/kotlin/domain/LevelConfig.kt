package br.com.woodriver.domain

data class LevelConfig(
        val level: Int = 0,
        val allowedEnemies: List<EnemyType> = emptyList(),
        val enemySpawnInterval: Float = 1.5f,
        var waves: List<Wave> = emptyList(),
        val bossTriggerThreshold: Int = 0,
        val bossConfig: BossConfig? = null
)
