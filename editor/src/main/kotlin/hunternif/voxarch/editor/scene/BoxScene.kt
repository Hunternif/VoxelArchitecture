package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.render.*
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.emptyArray3D
import hunternif.voxarch.util.forEachPos
import hunternif.voxarch.vector.Array3D
import org.joml.AABBf
import org.joml.Vector3f
import org.lwjgl.opengl.GL32.*

class BoxScene {
    private val vp = Viewport(0, 0, 0, 0)
    private val boxMesh = BoxMeshInstanced()
    private val selectionMesh = BoxMeshInstanced()
    private val gridMesh = FloorGridMesh()
    private val camera = OrbitalCamera()
    private var data: IStorage3D<VoxColor?>? = null

    private var gridMargin = 9

    private val editArea = AABBf()
    private var editAreaVoxels = emptyArray3D<VoxColor?>()
    private var selection: Selection? = null

    private val gridShader = Shader(
        resourcePath("shaders/floor-grid.vert.glsl"),
        resourcePath("shaders/floor-grid.frag.glsl"),
    )
    private val boxShader = Shader(
        resourcePath("shaders/magica-voxel.vert.glsl"),
        resourcePath("shaders/magica-voxel.frag.glsl"),
    )

    /** Skylight falls uniformly in this direction */
    private val skylightDir = Vector3f(-0.77f, -1.0f, -0.9f).normalize()
    private val skylightColor = ColorRGBa.fromHex(0xffffff).toVector3f()
    private val skylightPower = 1.25f
    /** Highlights the bottom of the model */
    private val backlightDir = Vector3f(0.77f, 1.0f, 0.9f).normalize()
    private val backlightColor = ColorRGBa.fromHex(0xffffff).toVector3f()
    private val backlightPower = 1.0f
    private val ambientColor = ColorRGBa.fromHex(0x353444).toVector3f()
    private val ambientPower = 1.0f
    private val gridColor = ColorRGBa.fromHex(0x333333).toVector3f()

    fun init(window: Long, viewport: Viewport) {
        setViewport(viewport)
        boxMesh.init()
        selectionMesh.init()
        gridMesh.init()
        initShaders()
        glEnable(GL_DEPTH_TEST)
        camera.init(window)
        camera.onMouseDown = ::onMouseDown
        camera.onMouseDrag = ::onMouseDrag
        camera.onMouseUp = ::onMouseUp
        // Initial empty area to show grid
        setVoxelData(Array3D(16, 2, 16, null))
    }

    private fun initShaders() {
        boxShader.init {
            uploadVec3f("uSkylightDir", skylightDir)
            uploadVec3f("uSkylightColor", skylightColor)
            uploadFloat("uSkylightPower", skylightPower)

            uploadVec3f("uBacklightDir", backlightDir)
            uploadVec3f("uBacklightColor", backlightColor)
            uploadFloat("uBacklightPower", backlightPower)

            uploadVec3f("uAmbientColor", ambientColor)
            uploadFloat("uAmbientPower", ambientPower)
        }

        gridShader.init {
            uploadVec3f("uGridColor", gridColor)
        }
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
            setMin(-0.5f + data.minX - gridMargin, -0.5f, -0.5f + data.minZ - gridMargin)
            setMax(0.5f + data.maxX + gridMargin, 0.5f, 0.5f + data.maxZ + gridMargin)
        }
        val width = data.maxX - data.minX + 1
        val length = data.maxZ - data.minZ + 1
        editAreaVoxels = editArea.run { Array3D(width, 1, length, null) }
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

    fun render() {
        glViewport(0, 0, vp.width, vp.height)
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        val viewProj = camera.getViewProjectionMatrix()

        gridShader.use {
            uploadMat4f("uViewProj", viewProj)
            gridMesh.runFrame()
        }

        boxShader.use {
            uploadMat4f("uViewProj", viewProj)
            boxMesh.runFrame()
        }

        boxShader.use {
            uploadMat4f("uViewProj", viewProj)
            selectionMesh.runFrame()
        }
    }

    // ======================== MOUSE CLICKS ========================

    private fun onMouseDown(posX: Float, posY: Float) {
        if (selection == null) {
            val posOnFloor = camera.projectToFloor(posX, posY)
            if (editArea.testPoint(posOnFloor)) {
                selection = Selection(posOnFloor.toVoxCoords(), posOnFloor.toVoxCoords())
                updateSelectionMesh()
            }
        }
    }

    private fun onMouseDrag(posX: Float, posY: Float) {
        selection?.let {
            val posOnFloor = camera.projectToFloor(posX, posY)
            it.end = posOnFloor.toVoxCoords()
            updateSelectionMesh()
        }
    }

    private fun onMouseUp(posX: Float, posY: Float) {
        selection?.let {
            selectionMesh.setVoxels(emptyArray3D())
            selection = null
        }
    }

    private fun updateSelectionMesh() {
        editAreaVoxels.forEachPos { x, y, z, _ ->
            editAreaVoxels[x, y, z] = selection?.let {
                if (it.testPoint(x, y, z)) VoxColor(0xff9966) else null
            }
        }
        selectionMesh.setVoxels(editAreaVoxels)
    }
}