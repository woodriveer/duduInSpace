package br.com.woodriver.domain

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Intersector
import kotlin.random.Random

class AsteroidExplosion(
    private val x: Float,
    private val y: Float,
    private val size: Float,
    private val texture: Texture
) {
    private val particles = mutableListOf<Particle>()
    private var isActive = true
    private val particleCount = 8
    private val particleSpeed = 200f
    private val particleLifetime = 0.5f
    private val particleDamage = 1

    init {
        try {
            // Create particles in a circular pattern
            for (i in 0 until particleCount) {
                val angle = (i * 360f / particleCount) * (Math.PI / 180f)
                val direction = Vector2(
                    Math.cos(angle).toFloat(),
                    Math.sin(angle).toFloat()
                )
                particles.add(
                    Particle(
                        x = x,
                        y = y,
                        width = size / 4,
                        height = size / 4,
                        direction = direction,
                        speed = particleSpeed,
                        lifetime = particleLifetime
                    )
                )
            }
        } catch (e: Exception) {
            isActive = false
        }
    }

    fun update(delta: Float): Boolean {
        if (!isActive) return false

        try {
            val particlesToRemove = mutableListOf<Particle>()
            var allParticlesDead = true

            particles.forEach { particle ->
                if (particle.update(delta)) {
                    allParticlesDead = false
                } else {
                    particlesToRemove.add(particle)
                }
            }

            // Remove dead particles after iteration
            particles.removeAll(particlesToRemove)

            if (allParticlesDead || particles.isEmpty()) {
                isActive = false
            }

            return isActive
        } catch (e: Exception) {
            isActive = false
            return false
        }
    }

    fun draw(batch: SpriteBatch) {
        if (!isActive) return

        try {
            particles.forEach { particle ->
                batch.draw(
                    texture,
                    particle.x,
                    particle.y,
                    particle.width,
                    particle.height
                )
            }
        } catch (e: Exception) {
            isActive = false
        }
    }

    fun checkCollision(playerBounds: Rectangle): Boolean {
        if (!isActive) return false

        try {
            val particlesToRemove = mutableListOf<Particle>()
            var collision = false

            particles.forEach { particle ->
                if (Intersector.overlaps(particle.bounds, playerBounds)) {
                    collision = true
                    particlesToRemove.add(particle)
                }
            }

            // Remove collided particles after iteration
            particles.removeAll(particlesToRemove)

            if (particles.isEmpty()) {
                isActive = false
            }

            return collision
        } catch (e: Exception) {
            isActive = false
            return false
        }
    }

    private class Particle(
        var x: Float,
        var y: Float,
        val width: Float,
        val height: Float,
        private val direction: Vector2,
        private val speed: Float,
        private var lifetime: Float
    ) {
        val bounds = Rectangle(x, y, width, height)

        fun update(delta: Float): Boolean {
            if (lifetime <= 0) return false

            try {
                x += direction.x * speed * delta
                y += direction.y * speed * delta
                lifetime -= delta
                bounds.setPosition(x, y)

                return lifetime > 0
            } catch (e: Exception) {
                return false
            }
        }
    }
} 