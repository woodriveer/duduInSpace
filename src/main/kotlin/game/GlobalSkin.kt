package br.com.woodriver.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin

object GlobalSkin {
    private var instance: Skin? = null

    fun getInstance(): Skin {
        if (instance == null) {
            instance = Skin(Gdx.files.internal("assets/skin/quantum-horizon-ui.json"))
        }
        return instance!!
    }

    fun dispose() {
        instance?.dispose()
        instance = null
    }
} 