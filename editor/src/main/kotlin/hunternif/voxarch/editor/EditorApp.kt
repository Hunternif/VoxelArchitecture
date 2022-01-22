package hunternif.voxarch.editor

import hunternif.voxarch.editor.scene.MainScene
import hunternif.voxarch.editor.gui.MainGui
import hunternif.voxarch.editor.render.Viewport
import hunternif.voxarch.editor.util.resourcePath
import hunternif.voxarch.magicavoxel.readVoxFile
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL32.*
import org.lwjgl.system.MemoryUtil

fun main() = EditorApp().run()

const val DEBUG = true

class EditorApp {
    private val title = "Voxel Architecture Editor"
    private var window: Long = 0
    private var width: Int = 1000
    private var height: Int = 600
    val gui = MainGui(this)
    val scene = MainScene(this)

    /** Root node containing everything in the editor */
    val rootNode = Structure()
    /** The node under which new child nodes would be added */
    var parentNode: Node = rootNode
    /** Nodes currently selected by cursor, for batch modification or inspection.
     * Should not contain [rootNode]. */
    val selectedNodes = LinkedHashSet<Node>()

    /** Nodes marked as hidden in UI, and invisible in 3d viewport. */
    val hiddenNodes = mutableSetOf<Node>()
    fun isNodeHidden(node: Node) = hiddenNodes.contains(node)

    var currentTool: Tool = Tool.ADD_NODE

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
