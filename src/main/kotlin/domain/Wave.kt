package br.com.woodriver.domain

enum class Choreography {
    FROM_TOP,
    FROM_LEFT,
    FROM_RIGHT
}

data class Wave(
        val enemyType: EnemyType? = null,
        val totalEnemies: Int = 0,
        val spawnInterval: Float = 0f,
        val choreography: Choreography = Choreography.FROM_TOP,
        val initialDelay: Float = 0f,
        val preset: String? = null,
        val healthMultiplier: Float = 1f,
        val isMiniBoss: Boolean = false
)
