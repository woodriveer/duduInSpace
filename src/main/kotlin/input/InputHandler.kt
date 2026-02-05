package br.com.woodriver.input

interface InputHandler {
    fun isMovingLeft(): Boolean
    fun isMovingRight(): Boolean
    fun isShooting(): Boolean
    fun isTogglePerformance(): Boolean

    // Menu Actions
    fun isUp(): Boolean
    fun isDown(): Boolean
    fun isLeft(): Boolean
    fun isRight(): Boolean
    fun isConfirm(): Boolean
    fun isBack(): Boolean
}
