package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import org.lwjgl.glfw.GLFW.*

interface MouseListener {
    fun onMouseMove(posX: Double, posY: Double) {}
    fun onMouseButton(button: Int, action: Int, mods: Int) {}
    fun onScroll(offsetX: Double, offsetY: Double) {}
}

interface KeyListener {
    fun onKeyPress(key: Int, action: Int, mods: Int) {}
    fun onCharInput(char: Char) {}
}

class InputController(private val app: EditorApp) {
    private val mouseListeners = mutableListOf<MouseListener>()
    private val keyListeners = mutableListOf<KeyListener>()

    fun init(window: Long) {
        glfwSetCursorPosCallback(window, ::onMouseMove)
        glfwSetMouseButtonCallback(window, ::onMouseButton)
        glfwSetScrollCallback(window, ::onScroll)
        glfwSetKeyCallback(window, ::onKeyPress)
        glfwSetCharCallback(window, ::onCharInput)
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
        if (app.state.isMainWindowHovered || action != GLFW_PRESS)
            for (listener in mouseListeners)
                listener.onMouseButton(button, action, mods)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onScroll(window: Long, offsetX: Double, offsetY: Double) {
        if (app.state.isMainWindowHovered)
            for (listener in mouseListeners)
                listener.onScroll(offsetX, offsetY)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onKeyPress(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        for (listener in keyListeners)
            listener.onKeyPress(key, action, mods)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onCharInput(window: Long, code: Int) {
        for (listener in keyListeners)
            listener.onCharInput(code.toChar())
    }
}