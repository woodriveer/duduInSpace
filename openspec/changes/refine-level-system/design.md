# Design: Refine Level System

## Architecture

### Level Configuration
We will introduce a `LevelConfig` interface or sealed class hierarchy to define properties for each level.

```kotlin
data class LevelConfig(
    val levelNumber: Int,
    val scoreThreshold: Int,
    val enemySpawnInterval: Float,
    val allowedEnemies: List<EnemyType>,
    val bossType: BossType // if we differentiate bosses
)
```

### Level Manager Responsibilities
`LevelManager` will accept a `LevelConfig` (or factory/repository) for the current level.
It will:
1. Configure `EnemySpawner` with `levelConfig.spawnInterval` and `levelConfig.allowedEnemies`.
2. Track score vs `levelConfig.scoreThreshold`.
3. Trigger Boss Fight when threshold is reached.
4. Signal Level Completion when Boss is defeated.

### Enemy Spawner
`EnemySpawner` will be updated to accept a list of allow-listed `EnemyType`s and spawn intervals. It will no longer hardcode "all types" unless specified.

## Component Interaction
`SpaceShooterGame` -> `LevelManager` -> `EnemySpawner`

`SpaceShooterGame` asks `LevelManager.update(delta)`.
`LevelManager` calls `EnemySpawner.update(delta)` if not in boss fight.
`LevelManager` returns list of new enemies to `SpaceShooterGame` (or manages them internally if we do a deeper refactor, but returning them keeps `SpaceShooterGame` rendering logic simple for now).

## Data Flow
1. Game Start -> Load Level 1 Config.
2. `LevelManager` init with Config.
3. Update Loop:
    - Spawn checks -> `EnemySpawner`.
    - Collision/Score updates -> `LevelManager`.
    - Boss Trigger -> `LevelManager`.
4. Level Complete -> `GameScreen` transitions to `UpgradeScreen` -> Start Level 2.
