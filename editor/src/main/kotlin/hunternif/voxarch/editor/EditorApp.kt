package hunternif.voxarch.editor

import hunternif.voxarch.editor.actions.centerCamera
import hunternif.voxarch.editor.scene.MainScene
import hunternif.voxarch.editor.gui.MainGui
import hunternif.voxarch.editor.render.Viewport
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL32.*
import org.lwjgl.system.MemoryUtil

fun main() = EditorAppImpl().run()

const val DEBUG = true

/**
 * Central control mechanism for the app.
 * It should be injected into every controller and UI class.
 * It is used to access state and execute actions.
 * This interface hides details like GUI, scene etc.
 */
interface EditorApp {
    val state: AppState
}

class EditorAppImpl : EditorApp {
    private val title = "Voxel Architecture Editor"
    private var window: Long = 0
    private var width: Int = 1000
    private var height: Int = 600
    internal val gui = MainGui(this)
    internal val scene = MainScene(this)

    override val state: AppStateImpl = AppStateImpl()

    fun run() {
        init()
        while (!glfwWindowShouldClose(window)) {
            runFrame()
        }
        Callbacks.glfwFreeCallbacks(window)
        glfwTerminate()
    }

    private fun runFrame() {
        glfwPollEvents()
        glViewport(0, 0, width, height)
        gui.render { vp ->
            scene.setViewport(vp)
            scene.render()
        }
        glfwSwapBuffers(window)
    }

    //==================== INIT CODE =======================

    private fun init() {
        glfwInit()
        window = createWindow(width, height, title)
        val vp = Viewport(0, 0, width, height)
        registerWindowEventHandler()
        gui.init(window, vp, 4)
        scene.init(window, vp)
        glfwShowWindow(window)
//        val file = readVoxFile(resourcePath("vox/voxarch-wfc-10x10x10-2021-12-05_19_16_49.vox"))
//        scene.setVoxelData(file)
//        scene.setVoxelData(Array3D(1, 1, 1, VoxColor(0xff9966)))
        centerCamera()
    }

    private fun registerWindowEventHandler() {
        glfwSetFramebufferSizeCallback(window, ::onFramebufferSize)
        glfwSetWindowSizeCallback(window, ::onWindowSize)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onFramebufferSize(window: Long, w: Int, h: Int) {
        if (w > 0 && h > 0) {
            width = w
            height = h
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onWindowSize(window: Long, w: Int, h: Int) = runFrame()
}


private fun createWindow(width: Int, height: Int, title: String): Long {
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
    val window = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
    glfwMakeContextCurrent(window)
    GL.createCapabilities()
    return window
}
