package br.com.woodriver.game.systems

import br.com.woodriver.domain.Boss
import br.com.woodriver.domain.Enemy
import br.com.woodriver.domain.PowerUp
import br.com.woodriver.domain.SpaceShip
import br.com.woodriver.game.GameProjectile
import com.badlogic.gdx.math.Intersector

class CollisionSystem(
        private val onPlayerHit: (damage: Int) -> Unit,
        private val onEnemyHit: (enemy: Enemy, projectile: GameProjectile) -> Unit,
        private val onBossHit: (boss: Boss, projectile: GameProjectile) -> Unit,
        private val onPowerUpCollected: (powerUp: PowerUp) -> Unit,
        private val onZBotProjectileHit: (projectile: Any, target: Any) -> Unit
) {
    fun checkCollisions(
            player: SpaceShip,
            enemies: List<Enemy>,
            projectiles: List<GameProjectile>,
            boss: Boss?,
            powerUps: List<PowerUp>,
            zBotProjectiles:
                    List<Any> // Using Any for now as ZBotProjectile might be in another package
    ) {
        // Player vs Enemy
        ArrayList(enemies).forEach { enemy ->
            if (Intersector.overlaps(enemy.bounds, player.info)) {
                onPlayerHit(1)
            }
        }

        // Projectiles vs Enemies
        val currentProjectiles = ArrayList(projectiles)
        val currentEnemies = ArrayList(enemies)
        currentProjectiles.forEach { projectile ->
            currentEnemies.forEach { enemy ->
                if (Intersector.overlaps(projectile, enemy.bounds)) {
                    if (!projectile.hitEntities.contains(enemy)) {
                        projectile.hitEntities.add(enemy)
                        onEnemyHit(enemy, projectile)
                    }
                }
            }
        }

        // Projectiles vs Boss
        boss?.let { b ->
            currentProjectiles.forEach { projectile ->
                if (Intersector.overlaps(projectile, b.info)) {
                    if (!projectile.hitEntities.contains(b)) {
                        projectile.hitEntities.add(b)
                        onBossHit(b, projectile)
                    }
                }
            }
        }

        // Player vs Boss Asteroids & Boss Body
        boss?.let { b ->
            // Player vs Boss Body
            if (Intersector.overlaps(b.info, player.info)) {
                onPlayerHit(1)
            }

            // Player vs Boss Asteroids
            ArrayList(b.asteroids).forEach { asteroid ->
                if (Intersector.overlaps(asteroid, player.info)) {
                    if (b.handleAsteroidHit(asteroid)) {
                        onPlayerHit(1)
                    }
                }
            }
        }

        // Player vs PowerUp
        ArrayList(powerUps).forEach { powerUp ->
            if (Intersector.overlaps(powerUp.info, player.info)) {
                onPowerUpCollected(powerUp)
            }
        }

        // ZBot Projectiles (Future implementation logic)
    }
}
