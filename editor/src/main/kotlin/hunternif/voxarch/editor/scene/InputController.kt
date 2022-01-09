package hunternif.voxarch.editor.scene

import org.lwjgl.glfw.GLFW.*

interface InputListener {
    fun onMouseMove(posX: Double, posY: Double) {}
    fun onMouseButton(button: Int, action: Int, mods: Int) {}
    fun onScroll(offsetX: Double, offsetY: Double) {}
    fun onKeyPress(key: Int, action: Int, mods: Int) {}
}

class InputController {
    private val listeners = mutableListOf<InputListener>()

    fun init(window: Long) {
        glfwSetCursorPosCallback(window, ::onMouseMove)
        glfwSetMouseButtonCallback(window, ::onMouseButton)
        glfwSetScrollCallback(window, ::onScroll)
        glfwSetKeyCallback(window, ::onKeyPress)
    }

    fun addListener(listener: InputListener) {
        listeners.add(listener)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onMouseMove(window: Long, posX: Double, posY: Double) {
        for (listener in listeners)
            listener.onMouseMove(posX, posY)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onMouseButton(window: Long, button: Int, action: Int, mods: Int) {
        for (listener in listeners)
            listener.onMouseButton(button, action, mods)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onScroll(window: Long, offsetX: Double, offsetY: Double) {
        for (listener in listeners)
            listener.onScroll(offsetX, offsetY)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onKeyPress(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        for (listener in listeners)
            listener.onKeyPress(key, action, mods)
    }
}