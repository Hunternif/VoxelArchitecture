package hunternif.voxarch.editor.scene

import org.lwjgl.glfw.GLFW.*

interface MouseListener {
    fun onMouseMove(posX: Double, posY: Double) {}
    fun onMouseButton(button: Int, action: Int, mods: Int) {}
    fun onScroll(offsetX: Double, offsetY: Double) {}
}

interface KeyListener {
    fun onKeyPress(key: Int, action: Int, mods: Int) {}
}

class InputController {
    private val mouseListeners = mutableListOf<MouseListener>()
    private val keyListeners = mutableListOf<KeyListener>()

    fun init(window: Long) {
        glfwSetCursorPosCallback(window, ::onMouseMove)
        glfwSetMouseButtonCallback(window, ::onMouseButton)
        glfwSetScrollCallback(window, ::onScroll)
        glfwSetKeyCallback(window, ::onKeyPress)
    }

    fun addListener(listener: MouseListener) {
        mouseListeners.add(listener)
    }

    fun addListener(listener: KeyListener) {
        keyListeners.add(listener)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onMouseMove(window: Long, posX: Double, posY: Double) {
        for (listener in mouseListeners)
            listener.onMouseMove(posX, posY)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onMouseButton(window: Long, button: Int, action: Int, mods: Int) {
        for (listener in mouseListeners)
            listener.onMouseButton(button, action, mods)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onScroll(window: Long, offsetX: Double, offsetY: Double) {
        for (listener in mouseListeners)
            listener.onScroll(offsetX, offsetY)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onKeyPress(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        for (listener in keyListeners)
            listener.onKeyPress(key, action, mods)
    }
}