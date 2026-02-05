package br.com.woodriver.domain

data class BossConfig(
        val texturePath: String = "assets/boss.png",
        val health: Int = 100,
        val damage: Int = 20,
        val moveSpeed: Float = 100f,
        val attackInterval: Float = 1.5f,
        val asteroidSpeed: Float = 150f,
        val asteroidSize: Float = 128f
)
