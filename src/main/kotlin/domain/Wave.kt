package br.com.woodriver.domain

enum class Choreography {
    FROM_TOP,
    FROM_LEFT,
    FROM_RIGHT
}
data class Wave(
    val enemyType: EnemyType,
    val totalEnemies: Int,
    val spawnInterval: Float,
    val choreography: Choreography = Choreography.FROM_TOP,
    val initialDelay: Float = 0f
)
