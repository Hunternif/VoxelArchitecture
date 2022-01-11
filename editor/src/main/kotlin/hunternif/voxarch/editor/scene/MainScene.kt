package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.render.*
import hunternif.voxarch.editor.scene.models.*
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.VoxelAABBf
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.storage.IStorage3D
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.opengl.GL32.*

class MainScene(app: EditorApp) {
    private val vp = Viewport(0, 0, 0, 0)

    private val inputController = InputController()

    private val voxelModel = VoxelModel()
    private val gridModel = FloorGridModel()
    private val nodeModel = NodeModel()

    private val camera = OrbitalCamera()

    private var data: IStorage3D<VoxColor?>? = null

    private var gridMargin = 9

    private val editArea = VoxelAABBf()
    private var selectionController = SelectionController(app, camera, editArea)

    private val models = listOf(
        gridModel,
        voxelModel,
        nodeModel,
        selectionController.model,
    )

    fun init(window: Long, viewport: Viewport) {
        setViewport(viewport)
        models.forEach { it.init() }
        inputController.run {
            init(window)
            addListener(camera)
            addListener(selectionController)
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

    fun setEditArea(minX: Int, minZ: Int, maxX: Int, maxZ: Int) {
        editArea.run {
            setMin(minX - gridMargin, 0, minZ - gridMargin)
            setMax(maxX + gridMargin, 0, maxZ + gridMargin)
            correctBounds()
        }
        updateGrid()
    }

    /** Make the grid area match edit area */
    private fun updateGrid() {
        editArea.run { gridModel.setSize(minX, minZ, maxX, maxZ) }
    }

    fun centerCamera() {
        // assuming the content is within [gridMargin]
        val minX = editArea.minX + gridMargin
        val minZ = editArea.minZ + gridMargin
        val minY = 0
        val maxX = editArea.maxX - gridMargin
        val maxZ = editArea.maxZ - gridMargin
        val maxY = data?.maxY ?: 0
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

    fun createNode(start: Vector3i, end: Vector3i) {
        nodeModel.addNode(start, end, ColorRGBa.fromHex(0x8dc63f, 0.3f))
    }

    fun render() {
        glViewport(0, 0, vp.width, vp.height)
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        val viewProj = camera.getViewProjectionMatrix()
        models.forEach { it.runFrame(viewProj) }
    }
}