# Level Management Specs

## ADDED Requirements

### Requirement: Configure Level Parameters
#### Scenario: Level 1 Configuration
Given the player starts Level 1
Then the required score for boss is 5
And the enemy spawn interval is standard (e.g. 1.0s)
And only ASTEROID enemy types are spawned.

#### Scenario: Level 2 Configuration
Given the player starts Level 2
Then the required score for boss is 10
And the enemy spawn interval decreases (faster spawns)
And ASTEROID and UFO enemy types are spawned.

### Requirement: Trigger Boss Fight
#### Scenario: Boss Fight Trigger
Given the player reaches the score threshold for the level
Then the `LevelManager` stops spawning regular enemies
And the Boss is spawned.

### Requirement: Complete Level
#### Scenario: Level Completion
Given the Boss is defeated
Then `LevelManager` reports level completion
And the game transitions to the Upgrade Screen.
