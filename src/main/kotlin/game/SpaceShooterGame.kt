package br.com.woodriver.game

import br.com.woodriver.DuduInSpace
import br.com.woodriver.domain.Enemy
import br.com.woodriver.domain.PowerUp
import br.com.woodriver.domain.PowerUpType
import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.domain.SpaceShip.Companion.createSpaceShipRectangle
import br.com.woodriver.domain.EnemyType
import br.com.woodriver.domain.EnemySpawner
import br.com.woodriver.manager.LevelManager
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
import br.com.woodriver.game.GameState.GAME_OVER
import br.com.woodriver.manager.MaterialManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import br.com.woodriver.game.GlobalSkin
import br.com.woodriver.game.ProfileScreen
import br.com.woodriver.domain.PlayerUpgrades
import br.com.woodriver.domain.PlayerUpgrades.UpgradeType
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.audio.Music

class SpaceShooterGame(
    private val game: Game,
    private val levelNumber: Int,
    private val materialManager: MaterialManager,
    private val playerUpgrades: PlayerUpgrades
) : Screen {
    companion object {
        private const val TAG = "SpaceShooterGame"
        private const val MAX_PROJECTILES = 100
        private const val MAX_DAMAGE_NUMBERS = 50
        private const val MAX_ENEMIES = 30
        private const val PERFORMANCE_LOG_INTERVAL = 5f // Log performance every 5 seconds
        private const val DEFAULT_PROJECTILE_COOLDOWN = 0.2f
        private const val FASTER_SHOOTING_PROJECTILE_COOLDOWN = 0.1f
    }

    lateinit var batch: SpriteBatch
    private lateinit var shapeRenderer: ShapeRenderer

    private lateinit var playerShip: SpaceShip
    private lateinit var enemyShip: Texture
    private val powerUpTextures = mutableMapOf<PowerUpType, Texture>()

    // Projectile properties
    private val projectiles = mutableListOf<Rectangle>()
    private var projectileSpeed: Float = 500f
    private var projectileCooldown: Float = DEFAULT_PROJECTILE_COOLDOWN
    private var lastProjectileTime: Float = 0f
    private var isTripleShotActive: Boolean = false
    private var isBiggerProjectilesActive: Boolean = false
    private var powerUpTimer: Float = 0f
    private val powerUpDuration: Float = 10f
    private var baseProjectileDamage: Int = 1
    private var biggerProjectileDamage: Int = 3
    private var bulletSizeScale: Float = 1f

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
    private var materialsGained: Int = 0
    private lateinit var spaceShipTexture: Texture
    private lateinit var projectileTexture: Texture
    private lateinit var asteroidTexture: Texture
    private lateinit var levelManager: LevelManager
    private val damageNumbers = mutableListOf<DamageNumber>()
    private lateinit var damageFont: BitmapFont
    private val materialDropNumbers = mutableListOf<DamageNumber>()

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

    private val enemyPool = ObjectPool<Enemy>(
        maxSize = MAX_ENEMIES,
        factory = { Enemy.create(EnemyType.ASTEROID, 0f, 0f) },
        reset = {}
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

    private var disposed = false

    // Add starfield properties for sensation of movement using white dots as stars
    private data class Star(var x: Float, var y: Float, val speed: Float)
    private val stars = mutableListOf<Star>()
    private val numStars = 100
    private var screenWidth = 0f
    private var screenHeight = 0f

    private lateinit var attackSound: Sound
    private lateinit var backgroundMusic: Music
    private lateinit var victoryMusic: Music
    private lateinit var defeatMusic: Music
    private var currentMusic: Music? = null

    override fun show() {
        Gdx.app.log(TAG, "Starting level $levelNumber")
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()

        // Load music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/background.mp3"))
        victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/victory.mp3"))
        defeatMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/defeat.mp3"))

        // Configure music
        backgroundMusic.isLooping = true
        victoryMusic.isLooping = false
        defeatMusic.isLooping = false

        // Start background music
        currentMusic = backgroundMusic
        currentMusic?.volume = 0.5f
        currentMusic?.play()

        // Initialize starfield
        screenWidth = Gdx.graphics.width.toFloat()
        screenHeight = Gdx.graphics.height.toFloat()
        stars.clear()
        repeat(numStars) {
            val x = Math.random().toFloat() * screenWidth
            val y = Math.random().toFloat() * screenHeight
            val speed = 30f + Math.random().toFloat() * 70f
            stars.add(Star(x, y, speed))
        }

        // Load performance display preference
        showPerformanceInfo = preferences.getBoolean("show_performance_info", false)

        // Choose ship skin based on upgrade
        val skinLevel = playerUpgrades.getUpgradeLevel(UpgradeType.SHIP_SKIN)
        val skinFiles = listOf("spaceship-01.png", "enemy_ship.png", "ufo.png")
        val skinIndex = min(skinLevel, skinFiles.size - 1)
        val chosenSkin = skinFiles[skinIndex]
        spaceShipTexture = Texture("assets/$chosenSkin")
        projectileTexture = Texture("assets/projectile-01.png")
        asteroidTexture = Texture("assets/asteroid-01.png")
        attackSound = Gdx.audio.newSound(Gdx.files.internal("assets/sound/spaceship_attack_2.mp3"))

        // Apply player upgrades before creating the ship and projectiles
        val damageBoost = playerUpgrades.getUpgradeEffect(UpgradeType.BULLET_DAMAGE).toInt()
        val sizeBoost = playerUpgrades.getUpgradeEffect(UpgradeType.BULLET_SIZE)
        val shipSpeedBoost = playerUpgrades.getUpgradeEffect(UpgradeType.SHIP_SPEED)
        val shipHealthBoost = playerUpgrades.getUpgradeEffect(UpgradeType.SHIP_HEALTH).toInt()
        val shootingSpeedBoost = playerUpgrades.getUpgradeEffect(UpgradeType.SHOOTING_SPEED)

        // Projectile upgrades
        baseProjectileDamage += damageBoost
        biggerProjectileDamage += damageBoost
        projectileSpeed += shootingSpeedBoost
        bulletSizeScale = 1f + sizeBoost

        // Load power-up textures
        PowerUpType.entries.forEach { type ->
            powerUpTextures[type] = Texture(type.getTexturePath())
        }

        // Instantiate player ship with upgraded stats
        val upgradedSpeed = 300f + shipSpeedBoost
        val upgradedHealth = 5 + shipHealthBoost
        playerShip = SpaceShip(
            spaceShipTexture,
            projectileTexture,
            createSpaceShipRectangle(spaceShipTexture),
            speed = upgradedSpeed,
            health = upgradedHealth,
            maxHealth = upgradedHealth
        )

        enemyShip = asteroidTexture

        font = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
        font.data.setScale(0.5f) // Make the font smaller
        damageFont = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
        damageFont.data.setScale(0.5f)

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

        // Check for performance info toggle (F3 key)
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            showPerformanceInfo = !showPerformanceInfo
            preferences.putBoolean("show_performance_info", showPerformanceInfo)
            preferences.flush()
        }

        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Animate and render starfield background
        updateStars(delta)
        drawStars()

        if (levelManager.isLevelCompleting()) {
            handleLevelCompletion(delta)
            return
        }

        // Update game state
        playerShip.update(delta)
        updatePowerUpTimers(delta)
        handlePlayerMovement(delta)
        handleShooting(delta)
        spawnPowerUps(delta)
        updateProjectiles(delta)

        // Update enemies and boss
        levelManager.update(delta)?.let { newEnemy ->
            activeEnemies.add(newEnemy)
        }

        updateEnemies(delta)
        updatePowerUps(delta)

        // Draw game elements
        batch.begin()

        // Draw player
        try {
            playerShip.draw(batch)
        } catch (e: Exception) {
            Gdx.app.error(TAG, "Error drawing player: ${e.message}", e)
        }

        // Draw projectiles
        activeProjectiles.forEach { projectile ->
            // combine power-up size and upgrade size
            val powerUpScale = if (isBiggerProjectilesActive) 1.5f else 1f
            val scale = powerUpScale * bulletSizeScale
            try {
                if (!disposed) {
                    batch.draw(
                        playerShip.projectileTexture,
                        if (isBiggerProjectilesActive) biggerProjectileCorrection(projectile) else projectile.x,
                        projectile.y,
                        projectile.width * scale,
                        projectile.height * scale
                    )
                }
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error drawing projectile: ${e.message}", e)
                // Continue with the next projectile
            }
        }

        // Draw enemies
        activeEnemies.forEach { enemy ->
            try {
                if (!disposed) {
                    batch.draw(
                        enemy.texture,
                        enemy.x,
                        enemy.y,
                        enemy.width,
                        enemy.height
                    )
                }
            } catch (e: Exception) {
                Gdx.app.error("SpaceShooterGame", "Error drawing enemy: ${e.message}", e)
                // Continue with the next enemy
            }
        }

        // Draw power-ups
        powerUps.forEach { powerUp ->
            try {
                if (!disposed) {
                    powerUp.update(delta)
                    powerUp.draw(batch)
                }
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error updating/drawing power-up: ${e.message}", e)
                // Continue with the next power-up
            }
        }

        // Draw damage numbers
        activeDamageNumbers.forEach { damageNumber ->
            try {
                if (!disposed) {
                    damageNumber.draw(batch)
                }
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error drawing damage number: ${e.message}", e)
                // Continue with the next damage number
            }
        }

        // Draw material drop numbers
        materialDropNumbers.forEach { materialNumber ->
            try {
                if (!disposed) {
                    materialNumber.draw(batch)
                }
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error drawing material drop number: ${e.message}", e)
            }
        }

        // Draw UI
        try {
            if (!disposed) {
                font.draw(batch, "Asteroids Destroyed: $destroyedAsteroids", 10f, Gdx.graphics.height - 20f)
                font.draw(batch, "Materials Gained: $materialsGained", 10f, Gdx.graphics.height - 50f)
                font.draw(batch, "Health: ${playerShip.health}/${playerShip.maxHealth}", 10f, Gdx.graphics.height - 80f)
                if (isTripleShotActive || isBiggerProjectilesActive || projectileCooldown < DEFAULT_PROJECTILE_COOLDOWN) {
                    font.draw(batch, "Power-up active!", 10f, Gdx.graphics.height - 110f)
                }
            }
        } catch (e: Exception) {
            Gdx.app.error(TAG, "Error drawing UI: ${e.message}", e)
        }

        // Draw performance metrics (smaller and in right corner)
        if (showPerformanceInfo) {
            if (!disposed) {
                var smallFont: BitmapFont? = null
                try {
                    smallFont = BitmapFont(Gdx.files.internal("fonts/audiowide.fnt"))
                    smallFont.data.setScale(0.5f) // Make font smaller
                    smallFont.color = Color.WHITE // Set font color to white

                    val rightMargin = 10f
                    val lineHeight = 15f
                    var yPos = Gdx.graphics.height - 20f

                    // Draw a semi-transparent black background for better visibility
                    batch.end()
                    try {
                        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
                        shapeRenderer.setColor(0f, 0f, 0f, 0.5f)
                        shapeRenderer.rect(
                            Gdx.graphics.width - 160f,
                            yPos - 60f,
                            150f,
                            60f
                        )
                        shapeRenderer.end()
                    } catch (e: Exception) {
                        Gdx.app.error(TAG, "Error drawing performance metrics background: ${e.message}", e)
                    }
                    batch.begin()

                    try {
                        smallFont.draw(
                            batch, "FPS: ${Gdx.graphics.framesPerSecond}",
                            Gdx.graphics.width - 150f, yPos
                        )
                        yPos -= lineHeight

                        smallFont.draw(
                            batch, "Enemies: ${activeEnemies.size}",
                            Gdx.graphics.width - 150f, yPos
                        )
                        yPos -= lineHeight

                        smallFont.draw(
                            batch, "Projectiles: ${activeProjectiles.size}",
                            Gdx.graphics.width - 150f, yPos
                        )
                        yPos -= lineHeight

                        smallFont.draw(
                            batch, "Collisions: $collisionChecksPerFrame",
                            Gdx.graphics.width - 150f, yPos
                        )
                    } catch (e: Exception) {
                        Gdx.app.error(TAG, "Error drawing performance metrics text: ${e.message}", e)
                    }
                } catch (e: Exception) {
                    Gdx.app.error(TAG, "Error setting up performance metrics: ${e.message}", e)
                } finally {
                    try {
                        smallFont?.dispose()
                    } catch (e: Exception) {
                        Gdx.app.error(TAG, "Error disposing smallFont: ${e.message}", e)
                    }
                }
            }
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

        // Update material drop numbers after rendering
        val materialIterator = materialDropNumbers.iterator()
        while (materialIterator.hasNext()) {
            val materialNumber = materialIterator.next()
            if (materialNumber.update(delta)) {
                materialIterator.remove()
                damageNumberPool.free(materialNumber)
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
        projectileCooldown = DEFAULT_PROJECTILE_COOLDOWN
        isTripleShotActive = false
        isBiggerProjectilesActive = false
    }

    // Update star positions for moving starfield background
    private fun updateStars(delta: Float) {
        stars.forEach { star ->
            star.y -= star.speed * delta
            if (star.y < 0f) {
                star.y = screenHeight
                star.x = Math.random().toFloat() * screenWidth
            }
        }
    }

    // Draw stars as small white dots using the ShapeRenderer
    private fun drawStars() {
        // Use same projection as batch for correct screen coordinates
        shapeRenderer.projectionMatrix = batch.projectionMatrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(Color.WHITE)
        stars.forEach { star ->
            shapeRenderer.rect(star.x, star.y, 2f, 2f)
        }
        shapeRenderer.end()
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
                projectileCooldown = FASTER_SHOOTING_PROJECTILE_COOLDOWN
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
            attackSound.play(0.1f)
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

        // Create a copy of the collections to iterate over
        val currentEnemies = ArrayList(activeEnemies)
        val currentProjectiles = ArrayList(activeProjectiles)

        // Update enemy targets with player position
        for (enemy in currentEnemies) {
            enemy.updateTarget(playerShip.info.x, playerShip.info.y, delta)
            enemy.update(delta)

            for (projectile in currentProjectiles) {
                if (projectilesToRemove.contains(projectile)) continue // Skip already marked projectiles

                collisionChecksPerFrame++
                totalCollisionChecks++
                if (Intersector.overlaps(projectile, enemy.bounds)) {
                    val damage = if (isBiggerProjectilesActive) biggerProjectileDamage else baseProjectileDamage
                    Gdx.app.log(TAG, "Enemy hit! Damage: $damage")
                    if (enemy.takeDamage(damage)) {
                        enemiesToRemove.add(enemy)
                        destroyedAsteroids++
                        Gdx.app.log(TAG, "Enemy destroyed! Total destroyed: $destroyedAsteroids")

                        // Check for material drop with position
                        val (dropped, position) = materialManager.handleMaterialDrop(enemy.x, enemy.y)
                        if (dropped) {
                            materialsGained++
                            spawnMaterialDropNumber(position.first, position.second)
                            Gdx.app.log(TAG, "Material dropped! Total gained: $materialsGained")
                        }
                    }
                    // Add damage number
                    spawnDamageNumber(damage, projectile.x, projectile.y)
                    projectilesToRemove.add(projectile)
                }
            }

            // Check for collision with player
            if (Intersector.overlaps(enemy.bounds, playerShip.info)) {
                Gdx.app.log(TAG, "Player hit by enemy! Health: ${playerShip.health}")
                if (playerShip.takeDamage()) {
                    Gdx.app.log(TAG, "Game Over! Player health depleted")
                    gameOver()
                }
                enemiesToRemove.add(enemy)
            }

            if (enemy.isOffScreen(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())) {
                enemiesToRemove.add(enemy)
            }
        }

        // Remove enemies and return them to the pool
        for (enemy in enemiesToRemove) {
            activeEnemies.remove(enemy)
            enemyPool.free(enemy)
        }

        // Remove projectiles and return them to the pool
        for (projectile in projectilesToRemove) {
            activeProjectiles.remove(projectile)
            projectilePool.free(projectile)
        }
    }

    private fun spawnEnemy(x: Float, y: Float, width: Float, height: Float) {
        val enemy = enemyPool.obtain().apply {
            this.x = x
            this.y = y
            this.width = width
            this.height = height
        }
        activeEnemies.add(enemy)
    }

    private fun spawnDamageNumber(damage: Int, x: Float, y: Float) {
        val damageNumber = damageNumberPool.obtain()
        damageNumber.setValue(damage)
        damageNumber.setPosition(x, y)
        activeDamageNumbers.add(damageNumber)
    }

    private fun spawnMaterialDropNumber(x: Float, y: Float) {
        val materialNumber = damageNumberPool.obtain()
        materialNumber.setValue(1)
        materialNumber.setPosition(x, y)
        materialNumber.setColor(Color.BLUE) // Change color to blue
        materialDropNumbers.add(materialNumber)
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
                playVictoryMusic()
            }
            GameState.VICTORY_ANIMATION -> {
                // Update and render victory animation
                batch.begin()
                playerShip.draw(batch)
                batch.end()

                // Only transition when victory music is finished
                if (playerShip.updateVictoryAnimation(delta)) {
                    gameState = GameState.TRANSITIONING
                    transitionToNextLevel()
                }
            }
            GameState.TRANSITIONING -> {
                // Do nothing while transitioning
            }
            GAME_OVER -> {
                // Only transition when defeat music is finished
            }
        }
    }

    private fun playVictoryMusic() {
        currentMusic?.stop()
        currentMusic = victoryMusic
        currentMusic?.volume = 0.5f
        currentMusic?.play()
    }

    private fun playDefeatMusic() {
        currentMusic?.stop()
        currentMusic = defeatMusic
        currentMusic?.volume = 0.5f
        currentMusic?.play()
    }

    private fun transitionToNextLevel() {
        // Mark level as completed
        preferences.putBoolean("level_${levelNumber}_completed", true)
        // Update current stage in preferences
        preferences.putInteger("current_stage", levelNumber + 1)
        preferences.flush()

        // Navigate to ProfileScreen after stage completion
        val profileScreen = ProfileScreen(game as DuduInSpace)
        game.setScreen(profileScreen)
        dispose()
    }

    private fun gameOver() {
        Gdx.app.log(TAG, "Game Over - Level $levelNumber completed")
        playDefeatMusic()
        // Save progress
        preferences.putBoolean("level_${levelNumber}_completed", true)
        preferences.flush()

        val profileScreen = ProfileScreen(game as DuduInSpace)
        game.setScreen(profileScreen)
        dispose()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun hide() {
        // No need to dispose here as it will be handled in gameOver() or transitionToNextLevel()
    }

    override fun dispose() {
        if (!disposed) {
            Gdx.app.log(TAG, "Disposing game resources")
            var disposalError: Exception? = null

            // Clear active collections first to reduce potential issues
            try {
                Gdx.app.log(TAG, "Clearing active collections")
                activeProjectiles.clear()
                activeDamageNumbers.clear()
                //activeEnemies.clear()
                powerUps.clear()
                enemies.clear()
                projectiles.clear()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error clearing collections: ${e.message}")
                disposalError = disposalError ?: e
            }

            // Dispose resources in a safe manner, catching exceptions for each resource
            try {
                if (::batch.isInitialized) batch.dispose()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error disposing batch: ${e.message}")
                disposalError = disposalError ?: e
            }

            try {
                if (::shapeRenderer.isInitialized) shapeRenderer.dispose()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error disposing shapeRenderer: ${e.message}")
                disposalError = disposalError ?: e
            }

            try {
                if (::playerShip.isInitialized && ::playerShip.get().texture != null) playerShip.texture.dispose()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error disposing playerShip texture: ${e.message}")
                disposalError = disposalError ?: e
            }

            try {
                if (::playerShip.isInitialized && ::playerShip.get().projectileTexture != null) playerShip.projectileTexture.dispose()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error disposing projectile texture: ${e.message}")
                disposalError = disposalError ?: e
            }

            try {
                if (::enemyShip.isInitialized) enemyShip.dispose()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error disposing enemyShip: ${e.message}")
                disposalError = disposalError ?: e
            }

            // Dispose power-ups
            try {
                powerUps.forEach { powerUp ->
                    try { powerUp.dispose() } catch (e: Exception) {
                        Gdx.app.error(TAG, "Error disposing powerUp: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error in powerUps loop: ${e.message}")
                disposalError = disposalError ?: e
            }

            // Dispose enemies
            try {
                enemies.forEach { enemy ->
                    try { enemy.texture.dispose() } catch (e: Exception) {
                        Gdx.app.error(TAG, "Error disposing enemy texture: ${e.message}")
                    }
                }
                        } catch (e: Exception) {
                            Gdx.app.error(TAG, "Error in enemies loop: ${e.message}")
                            disposalError = disposalError ?: e
                        }

                        try {
                            if (::damageFont.isInitialized) damageFont.dispose()
                        } catch (e: Exception) {
                            Gdx.app.error(TAG, "Error disposing damageFont: ${e.message}")
                            disposalError = disposalError ?: e
                        }
            try {
                projectilePool.freeAll()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error freeing projectilePool: ${e.message}")
                disposalError = disposalError ?: e
            }

            try {
                damageNumberPool.freeAll()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error freeing damageNumberPool: ${e.message}")
                disposalError = disposalError ?: e
            }

            try {
                enemyPool.freeAll()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error freeing enemyPool: ${e.message}")
                disposalError = disposalError ?: e
            }

            // Dispose power-up textures
            try {
                powerUpTextures.values.forEach { texture ->
                    try { texture.dispose() } catch (e: Exception) {
                        Gdx.app.error(TAG, "Error disposing powerUpTexture: ${e.message}")
                    }
                }
                powerUpTextures.clear()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error disposing powerUpTextures: ${e.message}")
                disposalError = disposalError ?: e
            }

            // Mark as disposed even if there were errors
            disposed = true

            // Log a summary if there were errors
            if (disposalError != null) {
                Gdx.app.error(TAG, "Completed disposal with errors. See log for details.")
            } else {
                Gdx.app.log(TAG, "Successfully disposed all resources")
            }

            // Force garbage collection to clean up any lingering resources
            try {
                System.gc()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error forcing garbage collection: ${e.message}")
            }

            try {
                materialDropNumbers.clear()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error clearing materialDropNumbers: ${e.message}")
                disposalError = disposalError ?: e
            }
            try {
                attackSound.dispose()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error disposing attackSound: ${e.message}")
                disposalError = disposalError ?: e
            }

            // Stop and dispose all music
            try {
                currentMusic?.stop()
                backgroundMusic.dispose()
                bossStartMusic.dispose()
                bossLoopMusic.dispose()
                victoryMusic.dispose()
                defeatMusic.dispose()
            } catch (e: Exception) {
                Gdx.app.error(TAG, "Error disposing music: ${e.message}")
                disposalError = disposalError ?: e
            }
        }
    }
}
