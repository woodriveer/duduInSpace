package br.com.woodriver.game

import br.com.woodriver.domain.Enemy
import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.domain.SpaceShip.Companion.createSpaceShipRectangle
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import kotlin.math.max
import kotlin.math.min

class SpaceShooterGame : ApplicationAdapter() {
  private lateinit var batch: SpriteBatch

  private lateinit var playerShip: SpaceShip
  private lateinit var enemyShip: Texture

  // Projectile properties
  private val projectiles = mutableListOf<Rectangle>()
  private val projectileSpeed: Float = 500f
  private val projectileCooldown: Float = 0.05f
  private var lastProjectileTime: Float = 0f

  // Enemy properties
  private val enemies = mutableListOf<Enemy>()
  private var enemySpawnTimer: Float = 0f
  private val enemySpawnInterval: Float = 1f

  private lateinit var font: BitmapFont
  private var destroyedAsteroids: Int = 0
  private lateinit var spaceShipTexture: Texture
  private lateinit var projectileTexture: Texture
  private lateinit var asteroidTexture: Texture


  override fun create() {
    batch = SpriteBatch()

    spaceShipTexture = Texture("assets/spaceship-01.png")
    projectileTexture = Texture("assets/projectile-01.png")
    asteroidTexture = Texture("assets/asteroid-01.png")
    // Load textures
    playerShip = SpaceShip(
      spaceShipTexture,
      projectileTexture,
      createSpaceShipRectangle(spaceShipTexture)
    )

    enemyShip = asteroidTexture

    font = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
  }

  override fun render() {
    // Clear screen
    Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    // Delta time for frame-independent movement
    val deltaTime = Gdx.graphics.deltaTime

    // Player movement
    handlePlayerMovement(deltaTime)

    // Shooting mechanics
    handleShooting(deltaTime)

    // Spawn enemies
    spawnEnemies(deltaTime)

    // Update projectiles
    updateProjectiles(deltaTime)

    // Update enemies
    updateEnemies(deltaTime)

    // Render everything
    batch.begin()

    // Draw player
    batch.draw(playerShip.texture, playerShip.info.x, playerShip.info.y)

    // Draw projectiles
    projectiles.forEach { projectile ->
      batch.draw(playerShip.projectileTexture, projectile.x, projectile.y)
    }

    // Draw enemies
    enemies.forEach { enemy ->
      batch.draw(
        enemy.texture,
        enemy.info.x,
        enemy.info.y,
        enemy.info.width,
        enemy.info.height
      )
    }

    batch.end()

    batch.begin()
    font.draw(batch, "Asteroids Destroyed: $destroyedAsteroids", 10f, Gdx.graphics.height - 20f)
    batch.end()
  }

  private fun handlePlayerMovement(deltaTime: Float) {
    // Move left
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
      playerShip.info.x = max(0f, playerShip.info.x - playerShip.speed * deltaTime)
    }

    // Move right
    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
      playerShip.info.x = min(
        Gdx.graphics.width - playerShip.info.width,
        playerShip.info.x + playerShip.speed * deltaTime
      )
    }
  }

  private fun handleShooting(deltaTime: Float) {
    // Shooting cooldown
    lastProjectileTime += deltaTime

    // Shoot when space is pressed and cooldown is met
    if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && lastProjectileTime >= projectileCooldown) {
      val projectile = Rectangle(
        playerShip.info.x,
        playerShip.info.y,
        playerShip.projectileTexture.width.toFloat(),
        playerShip.projectileTexture.height.toFloat()
      )
      projectiles.add(projectile)
      lastProjectileTime = 0f
    }
  }

  private fun spawnEnemies(deltaTime: Float) {
    enemySpawnTimer += deltaTime

    if (enemySpawnTimer >= enemySpawnInterval) {
      val enemySize = (Math.random() * 3).toInt() + 1 // Randomly generate enemy size between 1 and 3
      val enemyWidth = enemySize * enemyShip.width // Adjust the width and height based on the enemy size
      val enemyHeight = enemySize * enemyShip.height

      val enemyX = (Math.random() * (Gdx.graphics.width - enemyShip.width)).toFloat()
      val enemy = Rectangle(
        enemyX,
        Gdx.graphics.height.toFloat(),
        enemyWidth.toFloat(),
        enemyHeight.toFloat()
      )

      enemies.add(Enemy(enemyShip,  enemy)) // Add the enemy to the enemies list (texture and enemy)
      enemySpawnTimer = 0f
    }
  }

  private fun updateProjectiles(deltaTime: Float) {
    // Move projectiles up
    projectiles.forEach { it.y += projectileSpeed * deltaTime }

    // Remove projectiles that are off-screen
    projectiles.removeAll { it.y > Gdx.graphics.height }
  }

  private fun updateEnemies(deltaTime: Float) {
    // Move enemies down
    enemies.forEach { enemy ->
      enemy.info.y -= 200 * deltaTime

      projectiles.forEach { projectile ->

        if (Intersector.overlaps(projectile, enemy.info)) {
          enemies.remove(enemy)
          projectiles.remove(projectile)
          destroyedAsteroids++
          return
        }
      }
    }

    // Remove enemies that are off-screen
    enemies.removeAll { it.info.y + it.info.height < 0 }
  }

  override fun dispose() {
    batch.dispose()
    playerShip.texture.dispose()
    playerShip.projectileTexture.dispose()
    enemyShip.dispose()
  }
}