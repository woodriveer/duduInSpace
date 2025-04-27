package br.com.woodriver.domain

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlin.random.Random
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.Gdx

class Boss(
    x: Float,
    y: Float,
    width: Float = 128f,
    height: Float = 128f
) : Disposable {
    val texture = Texture("assets/boss.png")
    private val asteroidTexture = Texture("assets/asteroid-01.png")
    val info = Rectangle(x, y, width, height)
    var health = 100
    val maxHealth = 100
    private var damage = 20
    var isDead = false
        private set

    // Movement properties
    private var moveSpeed = 100f
    private var moveDirection = 1f // 1 for right, -1 for left
    private var moveTimer = 0f
    private val moveInterval = 2f // Change direction every 2 seconds
    private var verticalOffset = 0f
    private var verticalSpeed = 20f
    private var verticalDirection = 1f

    // Attack properties
    private var attackTimer = 0f
    private val attackInterval = 1.5f // Attack every 1.5 seconds
    private val asteroidSpeed = 150f
    val asteroids = mutableListOf<Rectangle>()
    private val asteroidSize = 128f // Made asteroids even bigger
    private val asteroidColor = Color(1f, 0.2f, 0.2f, 1f) // Bright red

    // Explosion properties
    private val explosions = mutableListOf<AsteroidExplosion>()

    init {
        Gdx.app.log("Boss", "Boss initialized at position: ${info.x}, ${info.y}")
    }

    fun update(deltaTime: Float) {
        if (isDead) return

        // Update movement
        moveTimer += deltaTime
        if (moveTimer >= moveInterval) {
            moveDirection *= -1
            moveTimer = 0f
            Gdx.app.log("Boss", "Changing direction to: ${if (moveDirection > 0) "right" else "left"}")
        }

        // Horizontal movement
        info.x += moveSpeed * moveDirection * deltaTime

        // Keep boss within screen bounds
        if (info.x < 0) {
            info.x = 0f
            moveDirection = 1f
        } else if (info.x + info.width > Gdx.graphics.width) {
            info.x = Gdx.graphics.width - info.width
            moveDirection = -1f
        }

        // Vertical movement (bobbing up and down)
        verticalOffset += verticalSpeed * verticalDirection * deltaTime
        if (verticalOffset > 20f) {
            verticalDirection = -1f
        } else if (verticalOffset < -20f) {
            verticalDirection = 1f
        }
        info.y = Gdx.graphics.height - 150f + verticalOffset

        // Update attack timer and throw asteroids
        attackTimer += deltaTime
        if (attackTimer >= attackInterval) {
            throwAsteroid()
            attackTimer = 0f
        }

        // Update asteroids
        updateAsteroids(deltaTime)

        // Update explosions
        updateExplosions(deltaTime)
    }

    private fun throwAsteroid() {
        val asteroid = Rectangle(
            info.x + info.width / 2 - asteroidSize / 2,
            info.y,
            asteroidSize,
            asteroidSize
        )
        asteroids.add(asteroid)
        Gdx.app.log("Boss", "NEW ASTEROID CREATED - Position: ${asteroid.x}, ${asteroid.y}, Size: ${asteroid.width}x${asteroid.height}")
    }

    private fun updateAsteroids(deltaTime: Float) {
        val iterator = asteroids.iterator()
        while (iterator.hasNext()) {
            val asteroid = iterator.next()
            asteroid.y -= asteroidSpeed * deltaTime

            // Remove asteroids that are off-screen
            if (asteroid.y + asteroid.height < 0) {
                iterator.remove()
                Gdx.app.log("Boss", "Removed asteroid at position: ${asteroid.x}, ${asteroid.y}")
            }
        }
    }

    private fun updateExplosions(deltaTime: Float) {
        try {
            val explosionsToRemove = mutableListOf<AsteroidExplosion>()
            explosions.forEach { explosion ->
                if (!explosion.update(deltaTime)) {
                    explosionsToRemove.add(explosion)
                }
            }
            explosions.removeAll(explosionsToRemove)
        } catch (e: Exception) {
            Gdx.app.error("Boss", "Error updating explosions: ${e.message}")
            explosions.clear()
        }
    }

    fun isDestroyed(): Boolean {
        return isDead
    }

    fun draw(batch: SpriteBatch) {
        if (!isDead) {
            try {
                // Draw boss
                batch.draw(texture, info.x, info.y, info.width, info.height)
                
                // Draw asteroids
                asteroids.forEach { asteroid ->
                    batch.draw(asteroidTexture, asteroid.x, asteroid.y, asteroid.width, asteroid.height)
                }

                // Draw explosions
                explosions.forEach { explosion ->
                    explosion.draw(batch)
                }
            } catch (e: Exception) {
                Gdx.app.error("Boss", "Error drawing boss elements: ${e.message}")
            }
        }
    }

    fun takeDamage(amount: Int): Boolean {
        health -= amount
        if (health <= 0 && !isDead) {
            isDead = true
            return true
        }
        return false
    }

    fun handleAsteroidHit(asteroid: Rectangle): Boolean {
        try {
            if (asteroids.remove(asteroid)) {
                // Create explosion at asteroid position
                explosions.add(AsteroidExplosion(
                    x = asteroid.x,
                    y = asteroid.y,
                    size = asteroid.width,
                    texture = asteroidTexture
                ))
                return true
            }
        } catch (e: Exception) {
            Gdx.app.error("Boss", "Error handling asteroid hit: ${e.message}")
        }
        return false
    }

    fun checkExplosionCollisions(playerBounds: Rectangle): Boolean {
        try {
            var collision = false
            explosions.forEach { explosion ->
                if (explosion.checkCollision(playerBounds)) {
                    collision = true
                }
            }
            return collision
        } catch (e: Exception) {
            Gdx.app.error("Boss", "Error checking explosion collisions: ${e.message}")
            return false
        }
    }

    override fun dispose() {
        texture.dispose()
        asteroidTexture.dispose()
        asteroids.clear()
        explosions.clear()
    }

    companion object {
        fun create(x: Float, y: Float): Boss {
            return Boss(x, y)
        }
    }
}
