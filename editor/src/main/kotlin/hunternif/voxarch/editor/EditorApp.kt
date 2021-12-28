package hunternif.voxarch.editor

import hunternif.voxarch.editor.scene.BoxScene
import hunternif.voxarch.editor.gui.DockedGui
import hunternif.voxarch.editor.render.Viewport
import hunternif.voxarch.editor.util.resourcePath
import hunternif.voxarch.magicavoxel.readVoxFile
import hunternif.voxarch.vector.Array3D
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL32.*
import org.lwjgl.system.MemoryUtil

fun main() = RealSimpleFbo().run()

class RealSimpleFbo {
    private val title = "Voxel Architecture Editor"
    private var window: Long = 0
    private var width: Int = 800
    private var height: Int = 600
    private val gui = DockedGui()
    private val scene = BoxScene()

    fun run() {
        init()
        while (!GLFW.glfwWindowShouldClose(window)) {
            runFrame()
        }
    }

    private fun runFrame() {
        GLFW.glfwPollEvents()
        glViewport(0, 0, width, height)
        gui.render { vp ->
            scene.setViewport(vp)
            scene.render()
        }
        GLFW.glfwSwapBuffers(window)
    }

    //==================== INIT CODE =======================

    private fun init() {
        GLFW.glfwInit()
        window = createWindow(width, height, title)
        val vp = Viewport(0, 0, width, height)
        registerWindowEventHandler()
        gui.init(window, vp)
        scene.init(window, vp)
        GLFW.glfwShowWindow(window)
        val file = readVoxFile(resourcePath("vox/voxarch-wfc-10x10x10-2021-12-05_19_16_49.vox"))
        scene.setVoxelData(file)
//        scene.setVoxelData(Array3D(1, 1, 1, "lol"))
        scene.centerCamera()
    }

    private fun registerWindowEventHandler() {
        GLFW.glfwSetFramebufferSizeCallback(window, ::onFramebufferSize)
        GLFW.glfwSetWindowSizeCallback(window, ::onWindowSize)
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
    GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
    GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
    val window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
    GLFW.glfwMakeContextCurrent(window)
    GL.createCapabilities()
    return window
}
