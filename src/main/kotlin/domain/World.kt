package br.com.woodriver.domain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable

class SpaceShooter(
    private val playerUpgrades: PlayerUpgrades,
    private val materials: Materials
) : Disposable, InputProcessor {
    private val powerUps = mutableListOf<PowerUp>()
    private var powerUpTimer = 0f
    private var powerUpInterval = 5f // seconds, can randomize later

    private val player = Player(Vector2(400f, 240f), playerUpgrades)
    private val enemies = mutableListOf<Enemy>()
    private val projectiles = mutableListOf<Projectile>()
    private var boss: Boss? = null
    private var score = 0
    private var gameOver = false

    // These will be dynamically set from upgrades
    private var shootCooldown = 0.25f
    private var shootTimer = 0f
    private var projectileDamage = 1
    private var projectileWidth = 16f
    private var projectileHeight = 32f
    private var playerBaseSpeed = 300f
    private var playerBaseHealth = 100

    fun update(delta: Float) {
        if (gameOver) return

        // --- APPLY UPGRADES ---
        // Ship speed
        val speedUpgrade = playerUpgrades.getUpgradeEffect(PlayerUpgrades.UpgradeType.SHIP_SPEED)
        player.speed = playerBaseSpeed + speedUpgrade
        // Fire rate (lower cooldown = faster shooting)
        val shootingSpeedUpgrade = playerUpgrades.getUpgradeEffect(PlayerUpgrades.UpgradeType.SHOOTING_SPEED)
        shootCooldown = 0.25f - shootingSpeedUpgrade.coerceAtMost(0.2f) // Clamp so cooldown doesn't go negative
        // Bullet damage
        projectileDamage = 1 + playerUpgrades.getUpgradeEffect(PlayerUpgrades.UpgradeType.BULLET_DAMAGE).toInt()
        // Bullet size
        val bulletSizeUpgrade = playerUpgrades.getUpgradeEffect(PlayerUpgrades.UpgradeType.BULLET_SIZE)
        projectileWidth = 16f + bulletSizeUpgrade * 8f
        projectileHeight = 32f + bulletSizeUpgrade * 8f
        // Ship health
        val healthUpgrade = playerUpgrades.getUpgradeEffect(PlayerUpgrades.UpgradeType.SHIP_HEALTH)
        player.health = playerBaseHealth + healthUpgrade.toInt()

        // --- PLAYER MOVEMENT ---
        val moveSpeed = player.speed * delta
        val bounds = player.bounds
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            bounds.x = (bounds.x - moveSpeed).coerceAtLeast(0f)
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            bounds.x = (bounds.x + moveSpeed).coerceAtMost(Gdx.graphics.width - bounds.width)
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) {
            bounds.y = (bounds.y + moveSpeed).coerceAtMost(Gdx.graphics.height - bounds.height)
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) {
            bounds.y = (bounds.y - moveSpeed).coerceAtLeast(0f)
        }

        // --- SHOOTING ---
        shootTimer += delta
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SPACE) && shootTimer >= shootCooldown) {
            // Shoot a projectile upward from the center-top of the player
            val projX = bounds.x + bounds.width / 2 - projectileWidth / 2f
            val projY = bounds.y + bounds.height
            projectiles.add(Projectile(projX, projY, projectileWidth, projectileHeight, 500f, projectileDamage))
            shootTimer = 0f
        }

        // Update player
        player.update(delta)

        // Update enemies
        enemies.forEach { it.update(delta) }
        enemies.removeAll { !it.isActive }

        // Update projectiles
        projectiles.forEach { it.update(delta) }
        projectiles.removeAll { it.isDestroyed() }

        // Update boss
        boss?.update(delta)
        if (boss?.isDead == true) {
            boss = null
        }

        // Spawn enemies
        if (enemies.size < 10 && Math.random() < 0.02) {
            val enemyWidth = 64f
            val randomX = (Math.random() * (Gdx.graphics.width - enemyWidth)).toFloat()
            val randomY = (Math.random() * (Gdx.graphics.height - enemyWidth)).toFloat()
            enemies.add(Enemy.create(EnemyType.values().random(), randomX, randomY))
        }

        // --- POWER-UP SPAWNING ---
        powerUpTimer += delta
        if (powerUps.size < 3 && powerUpTimer >= powerUpInterval) {
            powerUpTimer = 0f
            powerUpInterval = 5f + (Math.random() * 5f).toFloat() // randomize next interval
            val powerUpType = PowerUpType.values().random()
            val powerUpWidth = 48f
            val powerUpHeight = 48f
            val x = (Math.random() * (Gdx.graphics.width - powerUpWidth)).toFloat()
            val y = (Math.random() * (Gdx.graphics.height - powerUpHeight)).toFloat()
            powerUps.add(PowerUp.createPowerUp(powerUpType, x, y, powerUpWidth, powerUpHeight))
        }

        // Update power-ups
        powerUps.forEach { it.update(delta) }
        // (Optional) Remove power-ups if you want timed disappear, e.g. after N seconds

        // Spawn boss
        if (score >= 1000 && boss == null) {
            val bossX = (Gdx.graphics.width - 128f) / 2
            val bossY = Gdx.graphics.height - 150f
            boss = Boss.create(bossX, bossY)
        }

        // Check collisions
        checkCollisions()
    }

    fun render(batch: SpriteBatch) {
        player.draw(batch)
        enemies.forEach { it.draw(batch) }
        projectiles.forEach { it.draw(batch) }
        powerUps.forEach { it.draw(batch) }
        boss?.draw(batch)
    }

    private fun checkCollisions() {
        // Player-Enemy collisions
        enemies.forEach { enemy ->
            if (player.bounds.overlaps(enemy.bounds)) {
                player.takeDamage(enemy.damage)
                enemy.takeDamage(1)
            }
        }

        // Player-PowerUp collisions
        val collectedPowerUps = mutableListOf<PowerUp>()
        powerUps.forEach { powerUp ->
            if (player.bounds.overlaps(powerUp.info)) {
                // TODO: Apply power-up effect to player
                collectedPowerUps.add(powerUp)
            }
        }
        powerUps.removeAll(collectedPowerUps)


        // Projectile-Enemy collisions
        projectiles.forEach { projectile ->
            enemies.forEach { enemy ->
                if (projectile.bounds.overlaps(enemy.bounds)) {
                    projectile.destroy()
                    if (enemy.takeDamage(projectile.damage)) {
                        score += enemy.score
                        // Add materials based on enemy type
                        when (enemy.type) {
                            EnemyType.ASTEROID -> materials.addIron(1)
                            EnemyType.UFO -> materials.addGold(1)
                            EnemyType.SPACE_SHIP -> materials.addCrystal(1)
                        }
                    }
                }
            }
        }

        // Projectile-Boss collisions
        boss?.let { boss ->
            projectiles.forEach { projectile ->
                if (projectile.bounds.overlaps(boss.info)) {
                    projectile.destroy()
                    if (boss.takeDamage(projectile.damage)) {
                        score += 100
                        // Add bonus materials for defeating boss
                        materials.addIron(5)
                        materials.addGold(3)
                        materials.addCrystal(1)
                    }
                }
            }
        }
    }

    override fun dispose() {
        player.dispose()
        enemies.forEach { it.dispose() }
        projectiles.forEach { it.dispose() }
        boss?.dispose()
    }

    // InputProcessor implementation
    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }
} 