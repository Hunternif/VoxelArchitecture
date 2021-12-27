package hunternif.voxarch.editor

import hunternif.voxarch.editor.scene.BoxScene
import hunternif.voxarch.editor.gui.DockedGui
import hunternif.voxarch.editor.render.FrameBuffer
import hunternif.voxarch.editor.render.Viewport
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
    private val fbo = FrameBuffer()
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
        gui.render(fbo.texture.texID) { vp ->
            fbo.setViewport(vp)
            fbo.render {
                glViewport(0, 0, vp.width, vp.height)
                scene.setViewport(vp)
                glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
                scene.render()
            }
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
        fbo.init(vp)
        scene.init(window, vp)
        GLFW.glfwShowWindow(window)
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
