# Refine Level System

## Goal
Implement a robust level system that supports multiple levels with distinct difficulties, enemy types, and progression logic, as requested in `openspec/project.md`.

## Why
The current `LevelManager` and `EnemySpawner` have fragmented logic. `LevelManager` handles bosses and score, while `EnemySpawner` handles regular enemies with basic random generation. To support "multiples levels, each level has different difficulty and different enemy", we need a unified system where level configurations drive the gameplay parameters.

## Proposed Solution
- **Refactor `LevelManager`**: Make it the central authority for the current level's state (enemies, boss, progress).
- **Introduce `LevelConfig`**: A data structure to define level parameters (required score, allowed enemy types, spawn intervals, boss type).
- **Integrate Spawning**: Move `EnemySpawner` logic or configuration under `LevelManager` control so it adapts to the current level.
- **Level Progression**: Implement logic to transition between levels (e.g., Level 1 -> upgrade screen -> Level 2).

## Scope
- Refactoring `LevelManager` and `EnemySpawner`.
- Creating `LevelConfig` data classes.
- Updating `SpaceShooterGame` to use the new system.
- Defining configurations for at least existing levels (1-5).
