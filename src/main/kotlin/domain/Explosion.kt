package br.com.woodriver.domain

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

class Explosion(
    texture: Texture,
    private val frameCols: Int,
    private val frameRows: Int,
    frameDuration: Float,
    val width: Float,
    val height: Float
) {
    private val animation: Animation<TextureRegion>
    private var stateTime = 0f
    var x = 0f
    var y = 0f
    var isFinished = false
        private set

    init {
        val tmp = TextureRegion.split(
            texture,
            texture.width / frameCols,
            texture.height / frameRows
        )

        val frames = arrayOfNulls<TextureRegion>(frameCols * frameRows)
        var index = 0
        for (i in 0 until frameRows) {
            for (j in 0 until frameCols) {
                frames[index++] = tmp[i][j]
            }
        }

        animation = Animation(frameDuration, *frames)
    }

    fun update(delta: Float) {
        stateTime += delta
        if (animation.isAnimationFinished(stateTime)) {
            isFinished = true
        }
    }

    fun draw(batch: SpriteBatch) {
        if (!isFinished) {
            val currentFrame = animation.getKeyFrame(stateTime, false)
            batch.draw(currentFrame, x, y, width, height)
        }
    }

    fun reset() {
        stateTime = 0f
        isFinished = false
    }
}
