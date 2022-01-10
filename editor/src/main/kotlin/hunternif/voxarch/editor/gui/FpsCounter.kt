package hunternif.voxarch.editor.gui

import org.lwjgl.glfw.GLFW.glfwGetTime

class FpsCounter {
    private var lastUpdateTime: Double = glfwGetTime()
    private var lastFrameTime: Double = glfwGetTime()
    private var framesSinceUpdate: Int = 0

    private val updateIntervalSeconds: Double = 0.5

    /** Milliseconds per frame */
    var ms: Double = 1.0
        private set

    /** Frames per second */
    var fps: Double = 0.0
        private set

    fun run() {
        framesSinceUpdate++
        val currentTime = glfwGetTime()
        ms = (currentTime - lastFrameTime) * 1000.0
        lastFrameTime = currentTime
        if (currentTime - lastUpdateTime > updateIntervalSeconds) {
            fps = framesSinceUpdate.toDouble() / updateIntervalSeconds
            framesSinceUpdate = 0
            lastUpdateTime = currentTime
        }
    }
}