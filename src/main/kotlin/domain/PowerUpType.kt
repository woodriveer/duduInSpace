package br.com.woodriver.domain

enum class PowerUpType {
    FASTER_SHOOTING,
    TRIPLE_SHOT,
    BIGGER_PROJECTILES;

    fun getTexturePath(): String {
        return when (this) {
            FASTER_SHOOTING -> "assets/powerup_clock.png"
            TRIPLE_SHOT -> "assets/powerup_3.png"
            BIGGER_PROJECTILES -> "assets/powerup_muscle.png"
        }
    }
}