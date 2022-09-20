package hunternif.voxarch.editor.gui

import org.lwjgl.glfw.GLFW

/**
 * Convenience class for running an operation with lest often than every frame
 * during a GUI render.
 */
class Timer(
    @PublishedApi internal val updateIntervalSeconds: Double,
) {
    @PublishedApi internal var lastUpdateTime: Double = GLFW.glfwGetTime()

    /**
     * A mechanism to throttle expensive operations to happen less often than
     * every frame. Will run [action] only if the time passed.
     * Can only be called once per frame!
     */
    inline fun runAtInterval(crossinline action: () -> Unit) {
        val currentTime = GLFW.glfwGetTime()
        if (currentTime - lastUpdateTime > updateIntervalSeconds) {
            action()
            lastUpdateTime = currentTime
        }
    }
}