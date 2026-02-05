package br.com.woodriver.domain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Disposable
import kotlin.random.Random

class Boss(
        val x: Float,
        val y: Float,
        val config: BossConfig = BossConfig(),
        val width: Float = 128f,
        val height: Float = 128f
) : Disposable {
    val texture = Texture(config.texturePath)
    private val asteroidTexture = Texture("assets/asteroid-01.png")
    val info = Rectangle(x, y, width, height)
    var health = config.health
    val maxHealth = config.health
    private var damage = config.damage
    var isDead = false
        private set

    // Movement properties
    private var moveSpeed = config.moveSpeed
    private var moveDirection = 1f // 1 for right, -1 for left
    private var moveTimer = 0f
    private val moveInterval = 2f // Change direction every 2 seconds
    private var verticalOffset = 0f
    private var verticalSpeed = 20f
    private var verticalDirection = 1f

    // Attack properties
    private var attackTimer = 0f
    private val attackInterval = config.attackInterval
    private val asteroidSpeed = config.asteroidSpeed
    val asteroids = mutableListOf<Rectangle>()
    private val asteroidSize = config.asteroidSize
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
            Gdx.app.log(
                    "Boss",
                    "Changing direction to: ${if (moveDirection > 0) "right" else "left"}"
            )
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

    fun drawHealthBar(shapeRenderer: ShapeRenderer) {
        if (!isDead) {
            val healthPercentage = health.toFloat() / maxHealth.toFloat()
            val barWidth = info.width
            val barHeight = 10f
            val barX = info.x
            val barY = info.y + info.height + 10f

            // Determine color
            val color =
                    when {
                        healthPercentage > 0.5f -> Color.GREEN
                        healthPercentage > 0.2f -> Color.YELLOW
                        else -> Color.RED
                    }

            // Draw background (gray)
            shapeRenderer.color = Color.GRAY
            shapeRenderer.rect(barX, barY, barWidth, barHeight)

            // Draw health
            shapeRenderer.color = color
            shapeRenderer.rect(barX, barY, barWidth * healthPercentage, barHeight)
        }
    }

    private fun throwAsteroid() {
        val healthPercentage = health.toFloat() / maxHealth.toFloat()
        val isEnraged = healthPercentage <= 0.2f

        val asteroidX = info.x + info.width / 2 - asteroidSize / 2

        // Primary asteroid
        val asteroid = Rectangle(asteroidX, info.y, asteroidSize, asteroidSize)
        asteroids.add(asteroid)
        Gdx.app.log(
                "Boss",
                "NEW ASTEROID CREATED - Position: ${asteroid.x}, ${asteroid.y}, Size: ${asteroid.width}x${asteroid.height}"
        )

        // Duplicate asteroid if enraged
        if (isEnraged) {
            val asteroid2 =
                    Rectangle(
                            asteroidX + if (Random.nextBoolean()) 50f else -50f, // Slight offset
                            info.y + 30f, // Slight vertical offset to look like a barrage
                            asteroidSize,
                            asteroidSize
                    )
            asteroids.add(asteroid2)
            Gdx.app.log("Boss", "ENRAGED MODE: EXTRA ASTEROID CREATED")
        }
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
                    batch.draw(
                            asteroidTexture,
                            asteroid.x,
                            asteroid.y,
                            asteroid.width,
                            asteroid.height
                    )
                }

                // Draw explosions
                explosions.forEach { explosion -> explosion.draw(batch) }
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
                explosions.add(
                        AsteroidExplosion(
                                x = asteroid.x,
                                y = asteroid.y,
                                size = asteroid.width,
                                texture = asteroidTexture
                        )
                )
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
        fun create(x: Float, y: Float, config: BossConfig = BossConfig()): Boss {
            return Boss(x, y, config)
        }
    }
}
