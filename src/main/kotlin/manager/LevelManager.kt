package br.com.woodriver.manager

import br.com.woodriver.domain.Enemy
import br.com.woodriver.domain.EnemySpawner
import br.com.woodriver.repository.LevelRepository

class LevelManager(
        private val screenWidth: Float,
        private val screenHeight: Float,
        private val levelNumber: Int = 1
) {
    private val levelRepository = LevelRepository()
    private val levelConfig = levelRepository.getLevelConfig(levelNumber)
    private val enemySpawner = EnemySpawner(screenWidth, screenHeight, levelConfig)
    private var bossFightTriggered = false
    private var levelFinished = false

    fun update(delta: Float): Enemy? {
        if (enemySpawner.isWaveFinished() && !bossFightTriggered) {
            if (levelConfig.bossTriggerThreshold > 0 &&
                            levelConfig.waves.size >= levelConfig.bossTriggerThreshold
            ) {
                bossFightTriggered = true
            } else {
                levelFinished = true
            }
        }
        return enemySpawner.update(delta)
    }

    fun isBossFight(): Boolean = bossFightTriggered

    fun onBossDefeated() {
        levelFinished = true
    }

    fun isLevelCompleting(): Boolean = levelFinished

    fun getLevelConfig() = levelConfig
}
