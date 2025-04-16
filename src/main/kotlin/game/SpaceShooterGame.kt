package br.com.woodriver.game

import br.com.woodriver.domain.Enemy
import br.com.woodriver.domain.PowerUp
import br.com.woodriver.domain.PowerUpType
import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.domain.SpaceShip.Companion.createSpaceShipRectangle
import br.com.woodriver.domain.EnemyType
import br.com.woodriver.domain.EnemySpawner
import br.com.woodriver.domain.LevelManager
import br.com.woodriver.domain.DamageNumber
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
import com.badlogic.gdx.Game
import com.badlogic.gdx.Preferences
import br.com.woodriver.domain.ObjectPool
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class SpaceShooterGame(
    private val game: Game,
    private val levelNumber: Int,
    private val materialManager: MaterialManager
) : Screen {
    companion object {
        private const val TAG = "SpaceShooterGame"
        private const val MAX_PROJECTILES = 100
        private const val MAX_DAMAGE_NUMBERS = 50
        private const val MAX_ENEMIES = 30
        private const val PERFORMANCE_LOG_INTERVAL = 5f // Log performance every 5 seconds
    }

    lateinit var batch: SpriteBatch
    private lateinit var shapeRenderer: ShapeRenderer

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
    private val baseProjectileDamage: Int = 1
    private val biggerProjectileDamage: Int = 3

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
    private lateinit var levelManager: LevelManager
    private val damageNumbers = mutableListOf<DamageNumber>()
    private lateinit var damageFont: BitmapFont

    private var gameState: GameState = GameState.PLAYING

    private val preferences: Preferences = Gdx.app.getPreferences("SpaceShooterProgress")

    // Object pools
    private val projectilePool = ObjectPool(
        maxSize = MAX_PROJECTILES,
        factory = { Rectangle() },
        reset = { rect ->
            rect.x = 0f
            rect.y = 0f
            rect.width = projectileTexture.width.toFloat()
            rect.height = projectileTexture.height.toFloat()
        }
    )

    private val damageNumberPool = ObjectPool(
        maxSize = MAX_DAMAGE_NUMBERS,
        factory = { DamageNumber(0, 0f, 0f, damageFont) },
        reset = { number ->
            number.reset()
        }
    )

    private val enemyPool = ObjectPool(
        maxSize = MAX_ENEMIES,
        factory = { Enemy.createEnemy(EnemyType.ASTEROID, 0f, 0f, 32f, 32f) },
        reset = { enemy ->
            enemy.reset()
        }
    )

    // Active objects lists (replace existing lists)
    private val activeProjectiles = mutableListOf<Rectangle>()
    private val activeDamageNumbers = mutableListOf<DamageNumber>()
    private val activeEnemies = mutableListOf<Enemy>()

    // Performance monitoring
    private var performanceLogTimer: Float = 0f
    private var frameCount: Int = 0
    private var lastFps: Float = 0f
    private var lastLogTime: Long = 0L
    private var totalCollisionChecks: Int = 0
    private var collisionChecksPerFrame: Int = 0
    private var showPerformanceInfo: Boolean = false // Toggle for performance info

    override fun show() {
        Gdx.app.log(TAG, "Starting level $levelNumber")
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()

        // Load performance display preference
        showPerformanceInfo = preferences.getBoolean("show_performance_info", false)

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
        damageFont = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
        damageFont.data.setScale(0.5f)

        enemySpawner = EnemySpawner(
            screenWidth = Gdx.graphics.width.toFloat(),
            screenHeight = Gdx.graphics.height.toFloat()
        )

        levelManager = LevelManager(
            screenWidth = Gdx.graphics.width.toFloat(),
            screenHeight = Gdx.graphics.height.toFloat(),
            levelNumber = levelNumber
        )

        Gdx.app.log(TAG, "Game initialized successfully")
    }

    override fun render(delta: Float) {
        // Performance monitoring
        frameCount++
        performanceLogTimer += delta
        collisionChecksPerFrame = 0

        // Log boss position if boss exists
        levelManager.getCurrentBoss()?.let { boss ->
            Gdx.app.log(TAG, "Boss Position - X: ${boss.info.x}, Y: ${boss.info.y}, Width: ${boss.info.width}, Height: ${boss.info.height}")
        }

        // Check for performance info toggle (F3 key)
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            showPerformanceInfo = !showPerformanceInfo
            preferences.putBoolean("show_performance_info", showPerformanceInfo)
            preferences.flush()
        }

        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (levelManager.isLevelCompleting()) {
            handleLevelCompletion(delta)
            return
        }

        // Update game state
        playerShip.update(delta)
        updatePowerUpTimers(delta)
        handlePlayerMovement(delta)
        handleShooting(delta)
        spawnEnemies(delta)
        spawnPowerUps(delta)
        updateProjectiles(delta)

        // Update enemies and boss
        if (!levelManager.isBossFight()) {
            enemySpawner.update(delta)?.let { newEnemy ->
                activeEnemies.add(newEnemy)
            }
        }

        levelManager.update(delta).forEach { enemy: Enemy ->
            activeEnemies.add(enemy)
        }

        updateEnemies(delta)
        updatePowerUps(delta)

        // Draw game elements
        batch.begin()

        // Draw player
        playerShip.draw(batch)

        // Draw projectiles
        activeProjectiles.forEach { projectile ->
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
        activeEnemies.forEach { enemy ->
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

        // Draw boss
        levelManager.draw(batch)

        // Draw damage numbers
        activeDamageNumbers.forEach { damageNumber ->
            damageNumber.draw(batch)
        }

        // Draw UI
        font.draw(batch, "Asteroids Destroyed: $destroyedAsteroids", 10f, Gdx.graphics.height - 20f)
        font.draw(batch, "Health: ${playerShip.health}/${playerShip.maxHealth}", 10f, Gdx.graphics.height - 50f)
        if (isTripleShotActive || isBiggerProjectilesActive || projectileCooldown < 0.05f) {
            font.draw(batch, "Power-up active!", 10f, Gdx.graphics.height - 80f)
        }

        // Draw performance metrics (smaller and in right corner)
        if (showPerformanceInfo) {
            val smallFont = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
            smallFont.data.setScale(0.5f) // Make font smaller
            smallFont.color = com.badlogic.gdx.graphics.Color.WHITE // Set font color to white
            
            val rightMargin = 10f
            val lineHeight = 15f
            var yPos = Gdx.graphics.height - 20f
            
            // Draw a semi-transparent black background for better visibility
            batch.end()
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.setColor(0f, 0f, 0f, 0.5f)
            shapeRenderer.rect(
                Gdx.graphics.width - 160f,
                yPos - 60f,
                150f,
                60f
            )
            shapeRenderer.end()
            batch.begin()
            
            smallFont.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", 
                Gdx.graphics.width - 150f, yPos)
            yPos -= lineHeight
            
            smallFont.draw(batch, "Enemies: ${activeEnemies.size}", 
                Gdx.graphics.width - 150f, yPos)
            yPos -= lineHeight
            
            smallFont.draw(batch, "Projectiles: ${activeProjectiles.size}", 
                Gdx.graphics.width - 150f, yPos)
            yPos -= lineHeight
            
            smallFont.draw(batch, "Collisions: $collisionChecksPerFrame", 
                Gdx.graphics.width - 150f, yPos)
            
            smallFont.dispose()
        }

        batch.end()

        // Update damage numbers after rendering
        val damageIterator = activeDamageNumbers.iterator()
        while (damageIterator.hasNext()) {
            val damageNumber = damageIterator.next()
            if (damageNumber.update(delta)) {
                damageIterator.remove()
                damageNumberPool.free(damageNumber)
            }
        }

        // Log performance metrics periodically
        if (performanceLogTimer >= PERFORMANCE_LOG_INTERVAL) {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - lastLogTime
            val fps = frameCount / (elapsedTime / 1000f)
            
            Gdx.app.log(TAG, """
                Performance Metrics:
                - FPS: $fps
                - Active Enemies: ${activeEnemies.size}
                - Active Projectiles: ${activeProjectiles.size}
                - Active Damage Numbers: ${activeDamageNumbers.size}
                - Collision Checks/Frame: $collisionChecksPerFrame
                - Total Collision Checks: $totalCollisionChecks
                - Memory Usage: ${Runtime.getRuntime().totalMemory() / (1024 * 1024)}MB
            """.trimIndent())
            
            performanceLogTimer = 0f
            frameCount = 0
            lastLogTime = currentTime
            totalCollisionChecks = 0
        }
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
            PowerUpType.FASTER_SHOOTING -> {
                projectileCooldown = 0.02f
                Gdx.app.log(TAG, "Power-up activated: Faster Shooting")
            }
            PowerUpType.TRIPLE_SHOT -> {
                isTripleShotActive = true
                Gdx.app.log(TAG, "Power-up activated: Triple Shot")
            }
            PowerUpType.BIGGER_PROJECTILES -> {
                isBiggerProjectilesActive = true
                Gdx.app.log(TAG, "Power-up activated: Bigger Projectiles (Damage: $biggerProjectileDamage)")
            }
        }
    }

    private fun handleShooting(deltaTime: Float) {
        if (levelManager.isLevelCompleting()) return

        lastProjectileTime += deltaTime

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && lastProjectileTime >= projectileCooldown) {
            if (isTripleShotActive) {
                // Create three projectiles in a spread pattern
                for (i in -1..1) {
                    val offset = i * 20f
                    spawnProjectile(playerShip.info.x + offset, playerShip.info.y)
                }
            } else {
                spawnProjectile(playerShip.info.x, playerShip.info.y)
            }
            lastProjectileTime = 0f
        }
    }

    private fun handlePlayerMovement(deltaTime: Float) {
        if (levelManager.isLevelCompleting()) return

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
            spawnEnemy(enemyX, Gdx.graphics.height.toFloat(), enemyWidth.toFloat(), enemyHeight.toFloat())
            enemySpawnTimer = 0f
        }
    }

    private fun updateProjectiles(deltaTime: Float) {
        val iterator = activeProjectiles.iterator()
        while (iterator.hasNext()) {
            val projectile = iterator.next()
            projectile.y += projectileSpeed * deltaTime

            if (projectile.y > Gdx.graphics.height) {
                iterator.remove()
                projectilePool.free(projectile)
            }
        }
    }

    private fun spawnProjectile(x: Float, y: Float) {
        val projectile = projectilePool.obtain().apply {
            this.x = x
            this.y = y
            width = projectileTexture.width.toFloat()
            height = projectileTexture.height.toFloat()
        }
        activeProjectiles.add(projectile)
    }

    private fun updateEnemies(delta: Float) {
        val enemiesToRemove = mutableListOf<Enemy>()
        val projectilesToRemove = mutableListOf<Rectangle>()

        // Update enemy targets with player position
        activeEnemies.forEach { enemy ->
            enemy.updateTarget(playerShip.info.x, playerShip.info.y)
            enemy.update(delta)

            activeProjectiles.forEach { projectile ->
                collisionChecksPerFrame++
                totalCollisionChecks++
                if (Intersector.overlaps(projectile, enemy.info)) {
                    val damage = if (isBiggerProjectilesActive) biggerProjectileDamage else baseProjectileDamage
                    Gdx.app.log(TAG, "Enemy hit! Damage: $damage")
                    if (enemy.takeDamage(damage)) {
                        enemiesToRemove.add(enemy)
                        destroyedAsteroids++
                        levelManager.incrementScore()
                        Gdx.app.log(TAG, "Enemy destroyed! Total destroyed: $destroyedAsteroids")
                        
                        // Check for material drop
                        if (materialManager.shouldDropMaterial()) {
                            materialManager.addMaterial()
                            Gdx.app.log(TAG, "Material dropped!")
                        }
                    }
                    // Add damage number
                    spawnDamageNumber(damage, projectile.x, projectile.y)
                    projectilesToRemove.add(projectile)
                }
            }

            // Check for collision with player
            if (Intersector.overlaps(enemy.info, playerShip.info)) {
                Gdx.app.log(TAG, "Player hit by enemy! Health: ${playerShip.health}")
                if (playerShip.takeDamage()) {
                    Gdx.app.log(TAG, "Game Over! Player health depleted")
                    gameOver()
                }
                enemiesToRemove.add(enemy)
            }

            if (enemy.isOffScreen(Gdx.graphics.height.toFloat())) {
                enemiesToRemove.add(enemy)
            }
        }

        // Check for projectile collisions with boss
        activeProjectiles.forEach { projectile ->
            if (!projectilesToRemove.contains(projectile)) { // Only check if projectile hasn't already hit something
                val damage = if (isBiggerProjectilesActive) biggerProjectileDamage else baseProjectileDamage
                if (levelManager.handleBossDamage(projectile, damage)) {
                    Gdx.app.log(TAG, "Boss hit! Damage: $damage")
                    // Add damage number
                    spawnDamageNumber(damage, projectile.x, projectile.y)
                    projectilesToRemove.add(projectile)
                }
            }
        }

        // Check for boss collision with player
        levelManager.getCurrentBoss()?.let { boss ->
            if (Intersector.overlaps(boss.info, playerShip.info)) {
                Gdx.app.log(TAG, "Player hit by boss! Health: ${playerShip.health}")
                if (playerShip.takeDamage()) {
                    Gdx.app.log(TAG, "Game Over! Player health depleted")
                    gameOver()
                }
            }
        }

        // Remove enemies and return them to the pool
        enemiesToRemove.forEach { enemy ->
            activeEnemies.remove(enemy)
            enemyPool.free(enemy)
        }

        // Remove projectiles and return them to the pool
        projectilesToRemove.forEach { projectile ->
            activeProjectiles.remove(projectile)
            projectilePool.free(projectile)
        }
    }

    private fun spawnEnemy(x: Float, y: Float, width: Float, height: Float) {
        val enemy = enemyPool.obtain().apply {
            info.x = x
            info.y = y
            info.width = width
            info.height = height
        }
        activeEnemies.add(enemy)
    }

    private fun spawnDamageNumber(damage: Int, x: Float, y: Float) {
        val damageNumber = damageNumberPool.obtain()
        damageNumber.setValue(damage)
        damageNumber.setPosition(x, y)
        activeDamageNumbers.add(damageNumber)
    }

    private fun handleLevelCompletion(delta: Float) {
        when (gameState) {
            GameState.PLAYING -> {
                // Initialize victory sequence
                playerShip.startVictoryAnimation()
                enemies.clear()
                projectiles.clear()
                powerUps.clear()
                gameState = GameState.VICTORY_ANIMATION
            }
            GameState.VICTORY_ANIMATION -> {
                // Update and render victory animation
                batch.begin()
                playerShip.draw(batch)
                batch.end()
                
                if (playerShip.updateVictoryAnimation(delta)) {
                    gameState = GameState.TRANSITIONING
                    transitionToNextLevel()
                }
            }
            GameState.TRANSITIONING -> {
                // Do nothing while transitioning
            }
        }
    }

    private fun transitionToNextLevel() {
        // Mark level as completed
        preferences.putBoolean("level_${levelNumber}_completed", true)
        preferences.flush()
        
        // Clean up current level
        dispose()
        game.setScreen(LevelSelectionScreen(game))
    }

    private fun gameOver() {
        Gdx.app.log(TAG, "Game Over - Level $levelNumber completed")
        // Save progress
        preferences.putBoolean("level_${levelNumber}_completed", true)
        preferences.flush()
        
        // Clean up and return to level selection
        dispose()
        game.setScreen(LevelSelectionScreen(game))
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        Gdx.app.log(TAG, "Disposing game resources")
        batch.dispose()
        shapeRenderer.dispose()
        playerShip.texture.dispose()
        playerShip.projectileTexture.dispose()
        enemyShip.dispose()
        powerUps.forEach { it.dispose() }
        enemies.forEach { it.texture.dispose() }
        levelManager.dispose()
        damageFont.dispose()
        projectilePool.freeAll()
        damageNumberPool.freeAll()
        enemyPool.freeAll()
    }
}