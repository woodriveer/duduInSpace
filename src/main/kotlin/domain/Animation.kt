package br.com.woodriver.domain

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle

class Animation(
    private val texture: Texture,
    private val frameWidth: Int,
    private val frameHeight: Int,
    private val frameDuration: Float
) {
    private val frames: Array<TextureRegion>
    private var currentFrameTime: Float = 0f
    private var currentFrameIndex: Int = 0

    init {
        val frameCount = texture.width / frameWidth
        frames = Array(frameCount) { i ->
            TextureRegion(texture, i * frameWidth, 0, frameWidth, frameHeight)
        }
    }

    fun update(delta: Float) {
        currentFrameTime += delta
        if (currentFrameTime >= frameDuration) {
            currentFrameTime = 0f
            currentFrameIndex = (currentFrameIndex + 1) % frames.size
        }
    }

    fun draw(batch: SpriteBatch, x: Float, y: Float, width: Float, height: Float) {
        batch.draw(frames[currentFrameIndex], x, y, width, height)
    }

    fun dispose() {
        texture.dispose()
    }
}
