package br.com.woodriver.domain.zbot

import br.com.woodriver.domain.SpaceShip
import com.badlogic.gdx.graphics.g2d.SpriteBatch

abstract class ZBotPower(val type: ZBotPowerType, val cooldown: Float) {
    protected var cooldownTimer: Float = 0f
    protected var isActive: Boolean = false

    /** Update the power state */
    open fun update(delta: Float, zbot: br.com.woodriver.domain.ZBot, ship: SpaceShip) {
        if (cooldownTimer > 0f) {
            cooldownTimer -= delta
        }
    }

    /** Draw power visual effects */
    abstract fun draw(batch: SpriteBatch)

    /** Activate the power if ready */
    open fun activate(): Boolean {
        if (isReady()) {
            cooldownTimer = cooldown
            isActive = true
            onActivate()
            return true
        }
        return false
    }

    /** Called when power is activated */
    protected abstract fun onActivate()

    /** Check if power is ready to use */
    fun isReady(): Boolean = cooldownTimer <= 0f

    /** Get cooldown progress (0.0 to 1.0) */
    fun getCooldownProgress(): Float {
        return if (cooldown > 0f) {
            1f - (cooldownTimer / cooldown).coerceIn(0f, 1f)
        } else 1f
    }

    /** Clean up resources */
    open fun dispose() {}
}
