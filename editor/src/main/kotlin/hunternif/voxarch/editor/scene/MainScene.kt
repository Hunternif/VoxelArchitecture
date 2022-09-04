package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.render.*
import hunternif.voxarch.editor.scene.models.*
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.AADirection3D.*
import hunternif.voxarch.editor.util.max
import hunternif.voxarch.editor.util.min
import hunternif.voxarch.editor.util.toVector3f
import hunternif.voxarch.plan.findGlobalPosition
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL32.*
import java.util.*

class MainScene(private val app: EditorApp) {
    // data
    private val vp = Viewport(0, 0, 0, 0)

    // 3d models
    private val voxelModel = VoxelGroupsModel { v -> app.state.voxelColorMap(v) }
    private val gridModel = InfiniteGridModel()
    private val nodeModel = NodeModel()
    private val selectedNodeModel = SelectedNodeFrameModel()
    private val originsModel = PointSpriteModel("textures/point-circle.png")
    private val highlightedFaceModel = ResizeNodeModel()

    // 2d models


    // core controllers
    private val inputController = InputController(app)
    private val keyController = KeyController(app)
    private val camera = OrbitalCamera()
    /** For drawing overlays on screen */
    private val orthoCamera = OrthoCamera()

    // Tool controllers
    private val newNodeController = NewNodeController(app, camera)
    private val selectController = SelectController(app, camera)
    private val moveController = MoveController(app, camera)
    private val resizeController = ResizeController(app, camera)


    private val models3d = listOf(
        gridModel,
        voxelModel,
        nodeModel,
        highlightedFaceModel,
        selectedNodeModel,
        originsModel,
        newNodeController.model,
    )
    /** Overlaid on top in ortho camera*/
    private val models2d = listOf(
        selectController.marqueeModel,
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
            addListener(keyController)
            addListener(camera)
            addListener(newNodeController)
            addListener(selectController)
            addListener(moveController)
            addListener(resizeController)
        }
        camera.radius = 20f
        lookAtOrigin()
    }

    fun setViewport(viewport: Viewport) {
        vp.set(viewport)
        camera.setViewport(vp)
        orthoCamera.setViewport(vp)
    }

    fun lookAtOrigin() {
        camera.setPosition(-0.5f, -0.5f, -0.5f)
    }

    fun lookAtObjects(objs: Collection<SceneObject>) {
        val minCorner = Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE)
        val maxCorner = Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)
        for (obj in objs) {
            minCorner.set(min(minCorner, obj.start))
            maxCorner.set(max(maxCorner, obj.end))
        }
        val center = Vector3f(minCorner).add(maxCorner).mul(0.5f)
        camera.setPosition(center)
        camera.zoomToFitBox(minCorner, maxCorner, true)
    }

    fun updateVoxelModel() = app.state.run {
        val visibleVoxels = findVisibleChildren(voxelRoot).filterIsInstance<SceneVoxelGroup>()
        visibleVoxels.forEach { it.update() }
        voxelModel.updateVisible(visibleVoxels)
        updateSelectedNodeModel()
    }

    fun updateNodeModel() = app.state.run {
        nodeModel.clear()
        findVisibleChildren(rootNode).filterIsInstance<SceneNode>().forEach {
            it.update()
            nodeModel.add(it)
        }
        nodeModel.uploadInstanceData()
        updateSelectedNodeModel()
    }

    /** Returns all children of [root] that are not hidden directly or
     * indirectly (i.e. by a hidden parent), excluding the root itself. */
    private fun findVisibleChildren(root: SceneObject) : List<SceneObject> {
        val result = mutableListOf<SceneObject>()
        if (root in app.state.hiddenObjects) return result
        val queue = LinkedList<SceneObject>()
        queue.addAll(root.children)
        while (queue.isNotEmpty()) {
            val child = queue.removeLast()
            if (child is SceneObject && child !in app.state.hiddenObjects) {
                result.add(child)
                queue.addAll(child.children)
            }
        }
        return result
    }

    fun updateSelectedNodeModel() {
        selectedNodeModel.clear()
        originsModel.clear()
        for (obj in app.state.selectedObjects) {
            if (obj != app.state.rootNode && obj != app.state.voxelRoot) {
                selectedNodeModel.addNode(obj)
                if (obj is SceneNode) {
                    val origin = obj.node.findGlobalPosition().toVector3f()
                    originsModel.addPoint(origin)
                }
            }
        }
        selectedNodeModel.update()
        originsModel.update()
    }

    fun updateNewNodeFrame() {
        newNodeController.model.updateEdges(app.state.newNodeFrame)
    }

    fun updateHighlightedFaces() {
        highlightedFaceModel.face = app.state.highlightedFace
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
        if (app.state.currentTool == Tool.RESIZE &&
            app.state.isMainWindowHovered
        ) {
            val cursor = when (resizeController.pickedFace?.dir) {
                POS_X, POS_Z, NEG_X, NEG_Z -> cursorResizeHor
                POS_Y, NEG_Y -> cursorResizeVer
                null -> 0L
            }
            glfwSetCursor(window, cursor)
        }
    }
}