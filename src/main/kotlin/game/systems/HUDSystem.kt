package br.com.woodriver.game.systems

import br.com.woodriver.domain.RunStats
import br.com.woodriver.domain.SpaceShip
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class HUDSystem(
        private val batch: SpriteBatch,
        private val shapeRenderer: ShapeRenderer,
        private val font: BitmapFont
) {
    private val layout = GlyphLayout()

    fun render(
            player: SpaceShip,
            runStats: RunStats,
            destroyedAsteroids: Int,
            materialsGained: Int,
            isPowerUpActive: Boolean
    ) {
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        // 1. Draw HUD Background
        batch.end()
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.projectionMatrix = batch.projectionMatrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.4f)
        shapeRenderer.rect(5f, screenHeight - 95f, 250f, 90f)
        shapeRenderer.end()
        batch.begin()

        // 2. Draw Text Stats
        font.color = Color.GOLD
        font.draw(batch, "Asteroids: $destroyedAsteroids", 10f, screenHeight - 20f)
        font.draw(batch, "Materials: $materialsGained", 10f, screenHeight - 50f)

        font.color = if (player.health <= 2) Color.RED else Color.GREEN
        font.draw(batch, "Health: ${player.health}/${player.maxHealth}", 10f, screenHeight - 80f)

        if (isPowerUpActive) {
            font.color = Color.WHITE
            font.draw(batch, "POWER-UP ACTIVE!", 10f, screenHeight - 110f)
        }

        batch.end()
        drawXPBar(screenWidth, runStats)
        batch.begin()
    }

    private fun drawXPBar(screenWidth: Float, runStats: RunStats) {
        val barWidth = screenWidth * 0.8f
        val barHeight = 20f
        val x = (screenWidth - barWidth) / 2
        val y = 20f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.rect(x, y, barWidth, barHeight)
        shapeRenderer.color = Color.CYAN
        shapeRenderer.rect(x, y, barWidth * runStats.getXPPercentage(), barHeight)
        shapeRenderer.end()

        batch.begin()
        val levelText = "LVL ${runStats.currentLevel}"
        layout.setText(font, levelText)
        font.color = Color.WHITE
        font.draw(
                batch,
                levelText,
                x + (barWidth - layout.width) / 2,
                y + (barHeight + layout.height) / 2
        )
        batch.end()
    }
}
