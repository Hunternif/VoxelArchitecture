package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.*
import hunternif.voxarch.editor.scene.models.*
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.VoxelAABBf
import hunternif.voxarch.editor.util.toVector3f
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.findGlobalPosition
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3f
import org.lwjgl.opengl.GL32.*

class MainScene(private val app: EditorApp) {
    private val vp = Viewport(0, 0, 0, 0)

    private val inputController = InputController()

    private val voxelModel = VoxelModel()
    private val gridModel = FloorGridModel()
    private val nodeModel = NodeModel()
    private val selectedNodeModel = SelectedNodeFrameModel()

    private val camera = OrbitalCamera()

    private var data: IStorage3D<VoxColor?>? = null

    private var gridMargin = 9

    /** Area where you are allowed to place new voxels. The grid matches it. */
    private val editArea = VoxelAABBf()
    val newNodeController = NewNodeController(app, camera, editArea)

    private val models = listOf(
        gridModel,
        voxelModel,
        nodeModel,
        selectedNodeModel,
        newNodeController.model,
    )

    fun init(window: Long, viewport: Viewport) {
        setViewport(viewport)
        models.forEach { it.init() }
        inputController.run {
            init(window)
            addListener(camera)
            addListener(newNodeController)
        }
        setEditArea(0, 0, 16, 16)
    }

    fun setViewport(viewport: Viewport) {
        vp.set(viewport)
        camera.setViewport(vp)
    }

    fun setVoxelData(data: IStorage3D<VoxColor?>) {
        this.data = data
        voxelModel.setVoxels(data)
        setEditArea(data.minX, data.minZ, data.maxX, data.maxZ)
    }

    private fun setEditArea(minX: Int, minZ: Int, maxX: Int, maxZ: Int) {
        editArea.run {
            setMin(minX - gridMargin, 0, minZ - gridMargin)
            setMax(maxX + gridMargin, 0, maxZ + gridMargin)
            correctBounds()
        }
        updateGrid()
    }

    fun expandEditArea(x: Int, y: Int, z: Int) {
        editArea.union(x - gridMargin, y, z - gridMargin)
        editArea.union(x + gridMargin, y, z + gridMargin)
        updateGrid()
    }

    /** Make the grid area match edit area */
    private fun updateGrid() {
        editArea.run { gridModel.setSize(minX, minZ, maxX, maxZ) }
    }

    fun centerCameraAroundGrid() {
        // assuming the content is within [gridMargin]
        val minX = editArea.minX + gridMargin
        val minZ = editArea.minZ + gridMargin
        val minY = editArea.minY
        val maxX = editArea.maxX - gridMargin
        val maxZ = editArea.maxZ - gridMargin
        val maxY = editArea.maxY
        val width = maxX - minX + 1
        val height = maxY - minY + 1
        val length = maxZ - minZ + 1
        camera.setPosition(
            width / 2f - 0.5f + minX,
            height / 2f - 0.5f + minY,
            length / 2f - 0.5f + minZ,
        )
        camera.zoomToFitBox(
            Vector3f(minX.toFloat(), minY.toFloat(), minZ.toFloat()),
            Vector3f(maxX.toFloat(), maxY.toFloat(), maxZ.toFloat()),
        )
    }

    fun centerCameraAroundNode(node: Node) {
        val origin = node.findGlobalPosition()
        if (node is Room) {
            val center = origin + node.start + node.size / 2
            val min = origin + node.start - Vec3(0.5, 0.5, 0.5)
            val max = min + node.size + Vec3(1, 1, 1)
            camera.setPosition(center.toVector3f())
            camera.zoomToFitBox(min.toVector3f(), max.toVector3f())
        } else {
            camera.setPosition(origin.toVector3f())
        }
    }

    fun updateNodeModel() {
        nodeModel.clear()
        if (!app.isNodeHidden(app.rootNode))
            addNodeModelsRecursive(app.rootNode, Vec3.ZERO)
    }

    private fun addNodeModelsRecursive(node: Node, offset: Vec3) {
        for (child in node.children) {
            if (app.isNodeHidden(child)) continue
            if (child is Room) {
                val origin = offset + child.origin
                val start = origin + child.start
                val end = start + child.size
                nodeModel.addNode(
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
        for (node in app.selectedNodes) {
            if (node != app.rootNode)
                selectedNodeModel.addNode(node)
        }
    }

    fun render() {
        glViewport(0, 0, vp.width, vp.height)
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        val viewProj = camera.getViewProjectionMatrix()
        models.forEach { it.runFrame(viewProj) }
    }
}