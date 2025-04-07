package br.com.woodriver.game

import br.com.woodriver.domain.Enemy
import br.com.woodriver.domain.PowerUp
import br.com.woodriver.domain.PowerUpType
import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.domain.SpaceShip.Companion.createSpaceShipRectangle
import br.com.woodriver.domain.EnemyType
import br.com.woodriver.domain.EnemySpawner
import com.badlogic.gdx.Screen
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

class SpaceShooterGame : Screen {
  lateinit var batch: SpriteBatch

  private lateinit var playerShip: SpaceShip
  private lateinit var enemyShip: Texture
  private val powerUpTextures = mutableMapOf<PowerUpType, Texture>()

  // Projectile properties
  private val projectiles = mutableListOf<Rectangle>()
  private val projectileSpeed: Float = 500f
  private var projectileCooldown: Float = 0.05f
  private var lastProjectileTime: Float = 0f
  private var isTripleShotActive: Boolean = false
  private var isBiggerProjectilesActive: Boolean = false
  private var powerUpTimer: Float = 0f
  private val powerUpDuration: Float = 10f

  // Enemy properties
  private val enemies = mutableListOf<Enemy>()
  private var enemySpawnTimer: Float = 0f
  private val enemySpawnInterval: Float = 1f

  // Power-up properties
  private val powerUps = mutableListOf<PowerUp>()
  private var powerUpSpawnTimer: Float = 0f
  private val powerUpSpawnInterval: Float = 15f

  private lateinit var font: BitmapFont
  private var destroyedAsteroids: Int = 0
  private lateinit var spaceShipTexture: Texture
  private lateinit var projectileTexture: Texture
  private lateinit var asteroidTexture: Texture
  private lateinit var enemySpawner: EnemySpawner

  override fun show() {
    batch = SpriteBatch()

    spaceShipTexture = Texture("assets/spaceship-01.png")
    projectileTexture = Texture("assets/projectile-01.png")
    asteroidTexture = Texture("assets/asteroid-01.png")
    
    // Load power-up textures
    PowerUpType.entries.forEach { type ->
      powerUpTextures[type] = Texture(type.getTexturePath())
    }

    playerShip = SpaceShip(
      spaceShipTexture,
      projectileTexture,
      createSpaceShipRectangle(spaceShipTexture)
    )

    enemyShip = asteroidTexture

    font = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))

    enemySpawner = EnemySpawner(
      screenWidth = Gdx.graphics.width.toFloat(),
      screenHeight = Gdx.graphics.height.toFloat()
    )
  }

  override fun render(delta: Float) {
    // Clear screen
    Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    // Update power-up timers
    updatePowerUpTimers(delta)

    // Player movement
    handlePlayerMovement(delta)

    // Shooting mechanics
    handleShooting(delta)

    // Spawn enemies and power-ups
    spawnEnemies(delta)
    spawnPowerUps(delta)

    // Update projectiles
    updateProjectiles(delta)

    // Spawn enemies
    enemySpawner.update(delta)?.let { newEnemy ->
      enemies.add(newEnemy)
    }

    // Update enemies
    updateEnemies(delta)

    // Update power-ups
    updatePowerUps(delta)

    // Render everything
    batch.begin()

    // Draw player
    batch.draw(playerShip.texture, playerShip.info.x, playerShip.info.y)

    // Draw projectiles
    projectiles.forEach { projectile ->
      val scale = if (isBiggerProjectilesActive) 1.5f else 1f
      batch.draw(
        playerShip.projectileTexture,
        if (isBiggerProjectilesActive) biggerProjectileCorrection(projectile) else projectile.x,
        projectile.y,
        projectile.width * scale,
        projectile.height * scale
      )
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

    // Draw power-ups
    powerUps.forEach { powerUp ->
      powerUp.update(delta)
      powerUp.draw(batch)
    }

    batch.end()

    batch.begin()
    font.draw(batch, "Asteroids Destroyed: $destroyedAsteroids", 10f, Gdx.graphics.height - 20f)
    if (isTripleShotActive || isBiggerProjectilesActive || projectileCooldown < 0.05f) {
      font.draw(batch, "Power-up active!", 10f, Gdx.graphics.height - 50f)
    }
    batch.end()
  }

  private fun updatePowerUpTimers(delta: Float) {
    if (powerUpTimer > 0) {
      powerUpTimer -= delta
      if (powerUpTimer <= 0) {
        resetPowerUps()
      }
    }
  }

  private fun biggerProjectileCorrection(projectile: Rectangle): Float {
    return projectile.x + 8 - playerShip.projectileTexture.width / 2
  }

  private fun resetPowerUps() {
    projectileCooldown = 0.05f
    isTripleShotActive = false
    isBiggerProjectilesActive = false
  }

  private fun spawnPowerUps(delta: Float) {
    powerUpSpawnTimer += delta

    if (powerUpSpawnTimer >= powerUpSpawnInterval) {
      val powerUpType = PowerUpType.values().random()
      val powerUpX = (Math.random() * (Gdx.graphics.width - 32)).toFloat()
      powerUps.add(PowerUp.createPowerUp(powerUpType, powerUpX, Gdx.graphics.height.toFloat(), 32f, 32f))
      powerUpSpawnTimer = 0f
    }
  }

  private fun updatePowerUps(delta: Float) {
    // Move power-ups down
    powerUps.forEach { powerUp ->
      powerUp.info.y -= 100 * delta

      // Check collision with player
      if (Intersector.overlaps(powerUp.info, playerShip.info)) {
        applyPowerUp(powerUp.type)
        powerUps.remove(powerUp)
        return
      }
    }

    // Remove power-ups that are off-screen
    powerUps.removeAll { it.info.y + it.info.height < 0 }
  }

  private fun applyPowerUp(type: PowerUpType) {
    powerUpTimer = powerUpDuration
    when (type) {
      PowerUpType.FASTER_SHOOTING -> projectileCooldown = 0.02f
      PowerUpType.TRIPLE_SHOT -> isTripleShotActive = true
      PowerUpType.BIGGER_PROJECTILES -> isBiggerProjectilesActive = true
    }
  }

  private fun handleShooting(deltaTime: Float) {
    lastProjectileTime += deltaTime

    if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && lastProjectileTime >= projectileCooldown) {
      if (isTripleShotActive) {
        // Create three projectiles in a spread pattern
        for (i in -1..1) {
          val offset = i * 20f
          val projectile = Rectangle(
            playerShip.info.x + offset,
            playerShip.info.y,
            playerShip.projectileTexture.width.toFloat(),
            playerShip.projectileTexture.height.toFloat()
          )
          projectiles.add(projectile)
        }
      } else {
        val projectile = Rectangle(
          playerShip.info.x,
          playerShip.info.y,
          playerShip.projectileTexture.width.toFloat(),
          playerShip.projectileTexture.height.toFloat()
        )
        projectiles.add(projectile)
      }
      lastProjectileTime = 0f
    }
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

      enemies.add(Enemy.createEnemy(EnemyType.ASTEROID, enemyX, Gdx.graphics.height.toFloat(), enemyWidth.toFloat(), enemyHeight.toFloat()))
      enemySpawnTimer = 0f
    }
  }

  private fun updateProjectiles(deltaTime: Float) {
    // Move projectiles up
    projectiles.forEach { it.y += projectileSpeed * deltaTime }

    // Remove projectiles that are off-screen
    projectiles.removeAll { it.y > Gdx.graphics.height }
  }

  private fun updateEnemies(delta: Float) {
    val enemiesToRemove = mutableListOf<Enemy>()
    val projectilesToRemove = mutableListOf<Rectangle>()

    enemies.forEach { enemy ->
      enemy.update(delta)

      projectiles.forEach { projectile ->
        if (Intersector.overlaps(projectile, enemy.info)) {
          if (enemy.takeDamage()) {
            enemiesToRemove.add(enemy)
            destroyedAsteroids++
          }
          projectilesToRemove.add(projectile)
        }
      }

      if (enemy.isOffScreen(Gdx.graphics.height.toFloat())) {
        enemiesToRemove.add(enemy)
      }
    }

    enemies.removeAll(enemiesToRemove)
    projectiles.removeAll(projectilesToRemove)
  }

  override fun resize(width: Int, height: Int) {}

  override fun pause() {}

  override fun resume() {}

  override fun hide() {}

  override fun dispose() {
    batch.dispose()
    playerShip.texture.dispose()
    playerShip.projectileTexture.dispose()
    enemyShip.dispose()
    powerUps.forEach { it.dispose() }
    enemies.forEach { it.texture.dispose() }
  }
}