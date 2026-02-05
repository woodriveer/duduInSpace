package br.com.woodriver.game.systems

import br.com.woodriver.domain.Boss
import br.com.woodriver.domain.DamageNumber
import br.com.woodriver.domain.Enemy
import br.com.woodriver.domain.PowerUp
import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.domain.ZBot
import br.com.woodriver.game.GameProjectile
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class RenderSystem(private val batch: SpriteBatch, private val shapeRenderer: ShapeRenderer) {
    private val retroShader: ShaderProgram =
            ShaderProgram(
                            Gdx.files.internal("shaders/retro.vert"),
                            Gdx.files.internal("shaders/retro.frag")
                    )
                    .apply {
                        if (!isCompiled) {
                            Gdx.app.error("RenderSystem", "Shader compilation failed:\n$log")
                        }
                    }

    private var time = 0f
    private data class Star(var x: Float, var y: Float, val speed: Float)
    private val stars = mutableListOf<Star>()
    private val numStars = 100
    private var screenWidth = 0f
    private var screenHeight = 0f

    init {
        // Starfield initialization will happen when resize is called or first frame
    }

    fun initStarfield(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
        stars.clear()
        repeat(numStars) {
            val x = Math.random().toFloat() * screenWidth
            val y = Math.random().toFloat() * screenHeight
            val speed = 30f + Math.random().toFloat() * 70f
            stars.add(Star(x, y, speed))
        }
    }

    fun updateStars(delta: Float) {
        stars.forEach { star ->
            star.y -= star.speed * delta
            if (star.y < 0f) {
                star.y = screenHeight
                star.x = Math.random().toFloat() * screenWidth
            }
        }
    }

    fun render(
            player: SpaceShip,
            enemies: List<Enemy>,
            projectiles: List<GameProjectile>,
            boss: Boss?,
            powerUps: List<PowerUp>,
            zBot: ZBot?,
            damageNumbers: List<DamageNumber>,
            materialNumbers: List<DamageNumber>,
            delta: Float
    ) {
        time += delta

        // 1. Draw Stars
        shapeRenderer.projectionMatrix = batch.projectionMatrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.WHITE
        stars.forEach { star -> shapeRenderer.rect(star.x, star.y, 2f, 2f) }
        shapeRenderer.end()

        // 2. Apply Shader and Draw Entities
        batch.shader = retroShader
        if (retroShader.isCompiled) {
            retroShader.bind()
            if (retroShader.hasUniform("u_time")) {
                retroShader.setUniformf("u_time", time)
            }
            if (retroShader.hasUniform("u_resolution")) {
                retroShader.setUniformf("u_resolution", screenWidth, screenHeight)
            }
        }

        batch.begin()
        player.draw(batch)
        zBot?.draw(batch)
        enemies.forEach { it.draw(batch) }
        projectiles.forEach { proj ->
            batch.draw(player.projectileTexture, proj.x, proj.y, proj.width, proj.height)
        }
        powerUps.forEach { it.draw(batch) }
        boss?.draw(batch)
        damageNumbers.forEach { it.draw(batch) }
        materialNumbers.forEach { it.draw(batch) }
        batch.end()

        batch.shader = null // Reset shader
    }
}
