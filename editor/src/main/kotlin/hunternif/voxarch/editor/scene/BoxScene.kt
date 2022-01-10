package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.render.*
import hunternif.voxarch.editor.scene.meshes.BoxMeshInstanced
import hunternif.voxarch.editor.scene.meshes.FloorGridMesh
import hunternif.voxarch.editor.scene.meshes.NodeMesh
import hunternif.voxarch.editor.scene.shaders.MagicaVoxelShader
import hunternif.voxarch.editor.scene.shaders.SolidColorShader
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.vector.Array3D
import org.joml.AABBf
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.opengl.GL32.*

class BoxScene(app: EditorApp) {
    private val vp = Viewport(0, 0, 0, 0)

    private val inputController = InputController()
    private val boxMesh = BoxMeshInstanced()
    private val gridMesh = FloorGridMesh()
    private val nodeMeshes = mutableListOf<NodeMesh>()

    private val camera = OrbitalCamera()

    private var data: IStorage3D<VoxColor?>? = null

    private var gridMargin = 9

    private val editArea = AABBf()
    private var selectionController = SelectionController(app, camera, editArea)

    private val boxShader = MagicaVoxelShader()
    private val gridShader = SolidColorShader(0x333333)
    private val selectionShader = SolidColorShader(0xcccccc)

    fun init(window: Long, viewport: Viewport) {
        setViewport(viewport)
        initMeshes()
        initShaders()
        selectionController.init()
        inputController.run {
            init(window)
            addListener(camera)
            addListener(selectionController)
        }
        // Initial empty area to show grid
        setVoxelData(Array3D(16, 2, 16, null))
    }

    private fun initMeshes() {
        boxMesh.init()
        gridMesh.init()
    }

    private fun initShaders() {
        boxShader.init()
        gridShader.init()
        selectionShader.init()
    }

    fun setViewport(viewport: Viewport) {
        vp.set(viewport)
        camera.setViewport(vp)
    }

    fun setVoxelData(data: IStorage3D<VoxColor?>) {
        this.data = data
        boxMesh.setVoxels(data)
        gridMesh.setSize(
            data.minX - gridMargin, data.minZ - gridMargin,
            data.maxX + gridMargin, data.maxZ + gridMargin
        )
        editArea.run {
            setMin(-0.5f + data.minX - gridMargin, -1f, -0.5f + data.minZ - gridMargin)
            setMax(0.5f + data.maxX + gridMargin, 0f, 0.5f + data.maxZ + gridMargin)
        }
    }

    fun centerCamera() {
        data?.run {
            val width = maxX - minX + 1
            val height = maxY - minY + 1
            val length = maxZ - minZ + 1
            camera.setPosition(
                width / 2f - 0.5f,
                height / 2f - 0.5f,
                length / 2f - 0.5f
            )
            camera.zoomToFitBox(
                Vector3f(0f, 0f, 0f),
                Vector3f(width.toFloat(), height.toFloat(), length.toFloat())
            )
        }
    }

    fun createNode(start: Vector3i, end: Vector3i) {
        NodeMesh(ColorRGBa.fromHex(0x8dc63f, 0.3f)).apply {
            init()
            setPosition(start, end)
            nodeMeshes.add(this)
        }
    }

    fun render() {
        glViewport(0, 0, vp.width, vp.height)
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        val viewProj = camera.getViewProjectionMatrix()

        gridShader.render(viewProj) {
            gridMesh.runFrame()
        }

        boxShader.render(viewProj) {
            boxMesh.runFrame()
            nodeMeshes.forEach { it.runFrame() }
        }

        selectionShader.render(viewProj) {
            selectionController.mesh.runFrame()
        }
    }
}