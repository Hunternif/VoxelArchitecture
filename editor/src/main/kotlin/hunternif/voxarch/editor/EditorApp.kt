package hunternif.voxarch.editor

import hunternif.voxarch.editor.actions.logError
import hunternif.voxarch.editor.actions.newProject
import hunternif.voxarch.editor.actions.openProjectFile
import hunternif.voxarch.editor.scene.MainScene
import hunternif.voxarch.editor.gui.MainGui
import hunternif.voxarch.editor.render.Viewport
import hunternif.voxarch.editor.scene.InputController
import hunternif.voxarch.editor.util.LogMessage
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL32.*
import org.lwjgl.system.MemoryUtil
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

fun main(args: Array<String>) = EditorAppImpl().run(*args)

/**
 * Central control mechanism for the app.
 * It should be injected into every controller and UI class.
 * It is used to access state and execute actions.
 * This interface hides details like GUI, scene etc.
 */
interface EditorApp {
    val state: AppState
    val logs: List<LogMessage>
}

class EditorAppImpl : EditorApp {
    private val title = "Voxel Architecture Editor"
    private var window: Long = 0
    private var width: Int = 1280
    private var height: Int = 720
    private val inputController = InputController(this)
    internal val gui = MainGui(this)
    val scene = MainScene(this)

    override lateinit var state: AppStateImpl
    override val logs = LinkedList<LogMessage>()

    fun run(vararg args: String) {
        init(*args)
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

    fun init(vararg args: String) {
        glfwInit()
        window = createWindow(width, height, title)
        val vp = Viewport(0, 0, width, height)
        registerWindowEventHandler()
        inputController.init(window)
        scene.init(window, vp, inputController)
        // ImGui must be initialized after other GLFW callbacks are registered
        gui.init(window, vp, inputController, 4)
        glfwShowWindow(window)

        val file = parseFileArg(args)
        file?.let { openProjectFile(it) } ?: newProject()
    }

    private fun parseFileArg(args: Array<out String>): Path? {
        if (args.isNotEmpty()) {
            val path = Paths.get(args[0])
            if (Files.exists(path)) return path
            else logError("Couldn't find project file: $path")
        }
        return null
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

    private fun createWindow(width: Int, height: Int, title: String): Long {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        val window = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        glfwMakeContextCurrent(window)
        GL.createCapabilities()
        return window
    }
}
