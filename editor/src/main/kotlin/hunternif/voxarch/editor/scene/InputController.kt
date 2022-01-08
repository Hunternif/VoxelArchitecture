package hunternif.voxarch.editor.scene

import org.lwjgl.glfw.GLFW.*

interface MouseListener {
    fun onMouseMove(posX: Double, posY: Double) {}
    fun onMouseButton(button: Int, action: Int, mods: Int) {}
    fun onScroll(offsetX: Double, offsetY: Double) {}
}

class InputController {
    private val listeners = mutableListOf<MouseListener>()

    fun init(window: Long) {
        glfwSetCursorPosCallback(window, ::onMouseMove)
        glfwSetMouseButtonCallback(window, ::onMouseButton)
        glfwSetScrollCallback(window, ::onScroll)
    }

    fun addListener(listener: MouseListener) {
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
}