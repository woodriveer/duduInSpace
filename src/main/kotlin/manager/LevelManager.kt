package br.com.woodriver.manager

import br.com.woodriver.domain.Boss
import br.com.woodriver.domain.Enemy
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle

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


    fun update(delta: Float): Enemy? {
        return enemySpawner.update(delta)
    }

    fun isBossFight(): Boolean = false

    fun isLevelCompleting(): Boolean = false
}
