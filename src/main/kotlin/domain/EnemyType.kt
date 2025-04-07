package br.com.woodriver.domain

public enum class EnemyType {
    ASTEROID,
    UFO,
    SPACE_SHIP;

    fun getTexturePath(): String {
        return when (this) {
            ASTEROID -> "assets/asteroid-01.png"
            UFO -> "assets/ufo.png"
            SPACE_SHIP -> "assets/enemy_ship.png"
        }
    }

    fun getSpeed(): Float {
        return when (this) {
            ASTEROID -> 200f
            UFO -> 300f
            SPACE_SHIP -> 250f
        }
    }

    fun getHealth(): Int {
        return when (this) {
            ASTEROID -> 1
            UFO -> 10 
            SPACE_SHIP -> 15
        }
    }
} 