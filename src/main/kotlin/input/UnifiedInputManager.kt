package br.com.woodriver.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controllers

class UnifiedInputManager : InputHandler {

    override fun isMovingLeft(): Boolean {
        // Keyboard
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            return true
        }

        // Controller
        val activeControllers = Controllers.getControllers()
        for (i in 0 until activeControllers.size) {
            val controller = activeControllers[i]
            val mapping = controller.mapping

            // X-axis (Left Stick)
            val axisValue = controller.getAxis(mapping.axisLeftX)
            if (axisValue < -0.3f) return true // Slightly lower deadzone for responsiveness

            // D-Pad
            if (controller.getButton(mapping.buttonDpadLeft)) return true
        }

        return false
    }

    override fun isMovingRight(): Boolean {
        // Keyboard
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            return true
        }

        // Controller
        val activeControllers = Controllers.getControllers()
        for (i in 0 until activeControllers.size) {
            val controller = activeControllers[i]
            val mapping = controller.mapping

            // X-axis (Left Stick)
            val axisValue = controller.getAxis(mapping.axisLeftX)
            if (axisValue > 0.3f) return true

            // D-Pad
            if (controller.getButton(mapping.buttonDpadRight)) return true
        }

        return false
    }

    override fun isShooting(): Boolean {
        // Keyboard
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            return true
        }

        // Controller
        val activeControllers = Controllers.getControllers()
        for (i in 0 until activeControllers.size) {
            val controller = activeControllers[i]
            val mapping = controller.mapping

            // A button or Right Bumper/Trigger
            if (controller.getButton(mapping.buttonA)) return true
            if (controller.getButton(mapping.buttonR1)) return true // RB
        }

        return false
    }

    override fun isTogglePerformance(): Boolean {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) return true

        // Controller
        val activeControllers = Controllers.getControllers()
        for (i in 0 until activeControllers.size) {
            val controller = activeControllers[i]
            val mapping = controller.mapping
            // Back/Select button
            if (controller.getButton(mapping.buttonBack)) return true
        }

        return false
    }

    override fun isUp(): Boolean {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W))
                return true

        val activeControllers = Controllers.getControllers()
        for (i in 0 until activeControllers.size) {
            val controller = activeControllers[i]
            val mapping = controller.mapping
            if (controller.getButton(mapping.buttonDpadUp)) return true
            if (controller.getAxis(mapping.axisLeftY) < -0.5f) return true
        }
        return false
    }

    override fun isDown(): Boolean {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S))
                return true

        val activeControllers = Controllers.getControllers()
        for (i in 0 until activeControllers.size) {
            val controller = activeControllers[i]
            val mapping = controller.mapping
            if (controller.getButton(mapping.buttonDpadDown)) return true
            if (controller.getAxis(mapping.axisLeftY) > 0.5f) return true
        }
        return false
    }

    override fun isLeft(): Boolean {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A))
                return true

        val activeControllers = Controllers.getControllers()
        for (i in 0 until activeControllers.size) {
            val controller = activeControllers[i]
            val mapping = controller.mapping
            if (controller.getButton(mapping.buttonDpadLeft)) return true
            if (controller.getAxis(mapping.axisLeftX) < -0.5f) return true
        }
        return false
    }

    override fun isRight(): Boolean {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)
        )
                return true

        val activeControllers = Controllers.getControllers()
        for (i in 0 until activeControllers.size) {
            val controller = activeControllers[i]
            val mapping = controller.mapping
            if (controller.getButton(mapping.buttonDpadRight)) return true
            if (controller.getAxis(mapping.axisLeftX) > 0.5f) return true
        }
        return false
    }

    override fun isConfirm(): Boolean {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                        Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
        )
                return true

        val activeControllers = Controllers.getControllers()
        for (i in 0 until activeControllers.size) {
            val controller = activeControllers[i]
            val mapping = controller.mapping
            if (controller.getButton(mapping.buttonA)) return true
        }
        return false
    }

    override fun isBack(): Boolean {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
                        Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)
        )
                return true

        val activeControllers = Controllers.getControllers()
        for (i in 0 until activeControllers.size) {
            val controller = activeControllers[i]
            val mapping = controller.mapping
            if (controller.getButton(mapping.buttonB)) return true
            if (controller.getButton(mapping.buttonBack)) return true
        }
        return false
    }
}
