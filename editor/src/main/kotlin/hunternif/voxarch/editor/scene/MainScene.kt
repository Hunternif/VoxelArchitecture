package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.nodeData
import hunternif.voxarch.editor.actions.setWorkArea
import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.gui.ImGuiKeyListener
import hunternif.voxarch.editor.render.*
import hunternif.voxarch.editor.scene.models.*
import hunternif.voxarch.editor.util.AADirection3D.*
import hunternif.voxarch.editor.util.toVector3f
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.findGlobalPosition
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.max
import hunternif.voxarch.util.min
import hunternif.voxarch.vector.Vec3
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL32.*

class MainScene(private val app: EditorApp) {
    // data
    private val vp = Viewport(0, 0, 0, 0)
    private var data: IStorage3D<VoxColor?>? = null

    // 3d models
    private val voxelModel = VoxelModel()
    private val gridModel = InfiniteGridModel()
    private val nodeModel = NodeModel()
    private val selectedNodeModel = SelectedNodeFrameModel()
    private val originsModel = PointSpriteModel("textures/point-circle.png")

    // 2d models


    // core controllers
    private val inputController = InputController()
    private val guiKeyListener = ImGuiKeyListener()
    private val keyController = KeyController(app)
    private val camera = OrbitalCamera()
    /** For drawing overlays on screen */
    private val orthoCamera = OrthoCamera()

    // Tool controllers
    private val newNodeController = NewNodeController(app, camera)
    private val selectionController = SelectionController(app, camera)
    private val moveController = MoveController(app, camera)
    private val resizeController = ResizeController(app, camera)


    private val models3d = listOf(
        gridModel,
        voxelModel,
        nodeModel,
        resizeController.model,
        selectedNodeModel,
        originsModel,
        newNodeController.model,
    )
    /** Overlaid on top in ortho camera*/
    private val models2d = listOf(
        selectionController.marqueeModel,
        selectionController.pointsDebugModel,
    )

    // misc technical fields
    private var window = 0L
    private var cursorResizeHor = 0L
    private var cursorResizeVer = 0L


    fun init(window: Long, viewport: Viewport) {
        this.window = window
        cursorResizeHor = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR)
        cursorResizeVer = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR)
        setViewport(viewport)
        models3d.forEach { it.init() }
        models2d.forEach { it.init() }
        inputController.run {
            init(window)
            addListener(guiKeyListener)
            addListener(keyController)
            addListener(camera)
            addListener(newNodeController)
            addListener(selectionController)
            addListener(moveController)
            addListener(resizeController)
        }
        camera.setZoom(20f)
        lookAtOrigin()
    }

    fun setViewport(viewport: Viewport) {
        vp.set(viewport)
        camera.setViewport(vp)
        orthoCamera.setViewport(vp)
    }

    fun setVoxelData(data: IStorage3D<VoxColor?>) {
        this.data = data
        voxelModel.setVoxels(data)
        app.setWorkArea(
            data.minX - app.state.gridMargin,
            data.minZ - app.state.gridMargin,
            data.maxX + app.state.gridMargin,
            data.maxZ + app.state.gridMargin,
        )
    }

    /** Make the grid area match work area */
    fun updateGrid() {
//        app.state.workArea.run { gridModel.setSize(minX, minZ, maxX, maxZ) }
    }

    fun lookAtOrigin() {
        camera.setPosition(-0.5f, -0.5f, -0.5f)
    }

    fun lookAtNodes(vararg nodes: Node) {
        val minCorner = Vec3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE)
        val maxCorner = Vec3(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE)
        for (node in nodes) {
            val origin = node.findGlobalPosition()
            if (node is Room) {
                val min = origin + node.start - Vec3(0.5, 0.5, 0.5)
                val max = min + node.size + Vec3(1, 1, 1)
                minCorner.set(min(minCorner, min))
                maxCorner.set(max(maxCorner, max))
            } else {
                val min = origin - Vec3(0.5, 0.5, 0.5)
                val max = min + Vec3(1, 1, 1)
                minCorner.set(min(minCorner, min))
                maxCorner.set(max(maxCorner, max))
            }
        }
        val center = (minCorner + maxCorner) / 2
        camera.setPosition(center.toVector3f())
        camera.zoomToFitBox(minCorner.toVector3f(), maxCorner.toVector3f(), true)
    }

    fun updateNodeModel() = app.state.run {
        nodeModel.clear()
        if (rootNode !in hiddenNodes)
            addNodeModelsRecursive(rootNode, Vec3.ZERO)
        nodeModel.update()
        updateSelectedNodeModel()
    }

    private fun addNodeModelsRecursive(node: Node, offset: Vec3) {
        for (child in node.children) {
            if (child in app.state.hiddenNodes) continue
            if (child is Room) {
                val origin = offset + child.origin
                val start = origin + child.start
                val end = start + child.size
                val inst = app.nodeData(child)
                nodeModel.addAndUpdateNode(
                    inst,
                    start.toVector3f(),
                    end.toVector3f(),
                    Colors.defaultNodeBox
                )
                addNodeModelsRecursive(child, origin)
            }
        }
    }

    fun updateSelectedNodeModel() {
        selectedNodeModel.clear()
        originsModel.clear()
        for (node in app.state.selectedNodes) {
            if (node != app.state.rootNode) {
                selectedNodeModel.addNode(node)
                val origin = node.findGlobalPosition().toVector3f()
                originsModel.addPoint(origin)
            }
        }
        originsModel.update()
    }

    fun updateNewNodeFrame() {
        newNodeController.model.updateEdges(app.state.newNodeFrame)
    }

    fun render() {
        updateCursor()
        glViewport(0, 0, vp.width, vp.height)
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        val viewProj3d = camera.getViewProjectionMatrix()
        models3d.forEach { it.runFrame(viewProj3d) }
        val viewProj2d = orthoCamera.getViewProjectionMatrix()
        models2d.forEach { it.runFrame(viewProj2d) }
    }

    private fun updateCursor() {
        if (app.state.currentTool == Tool.RESIZE && camera.isMouseInViewport()) {
            val cursor = when (resizeController.pickedFace?.dir) {
                POS_X, POS_Z, NEG_X, NEG_Z -> cursorResizeHor
                POS_Y, NEG_Y -> cursorResizeVer
                null -> 0L
            }
            glfwSetCursor(window, cursor)
        }
    }
}