package br.com.woodriver.repository

import br.com.woodriver.domain.Choreography
import br.com.woodriver.domain.EnemyType
import br.com.woodriver.domain.LevelConfig
import br.com.woodriver.domain.Wave
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue

class LevelRepository {

    private val json = Json()
    private val levelData: LevelData by lazy {
        val fileHandle = Gdx.files.internal("levels.json")
        val data = json.fromJson(LevelData::class.java, fileHandle)
        resolvePresets(data)
        data
    }

    private val levelConfigs: Map<Int, LevelConfig> by lazy {
        levelData.levels.associateBy { it.level }
    }

    private fun resolvePresets(data: LevelData) {
        data.levels.forEach { level ->
            level.waves =
                    level.waves.flatMap { wave ->
                        if (wave.preset != null) {
                            convertWavePresetToDomain(data.wavePresets[wave.preset])
                        } else {
                            listOf(wave)
                        }
                    }
        }
    }

    fun convertWavePresetToDomain(wavePreset: List<JsonValue>?): List<Wave> {
        val waves = ArrayList<Wave>()
        for (wave in wavePreset!!) {
            waves.add(
                Wave(
                    enemyType = EnemyType.valueOf(wave.get("enemyType").asString()),
                    totalEnemies = wave["totalEnemies"].asInt(),
                    spawnInterval = wave["spawnInterval"].asFloat(),
                    choreography = Choreography.valueOf(wave["choreography"].asString()),
                )
            )
        }
        return waves
    }

    fun getLevelConfig(level: Int): LevelConfig {
        return levelConfigs[level] ?: throw IllegalArgumentException("Level not found: $level")
    }

    private data class LevelData(
        val wavePresets: HashMap<String, ArrayList<JsonValue>> = HashMap(),
        val levels: ArrayList<LevelConfig> = ArrayList()
    )
}
