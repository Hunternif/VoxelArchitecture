package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.render.*
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.vector.Array3D
import org.joml.AABBf
import org.joml.Vector3f
import org.lwjgl.opengl.GL32.*

class BoxScene {
    private val vp = Viewport(0, 0, 0, 0)
    private val inputController = InputController()
    private val boxMesh = BoxMeshInstanced()
    private val gridMesh = FloorGridMesh()
    private val camera = OrbitalCamera()

    private var data: IStorage3D<VoxColor?>? = null

    private var gridMargin = 9

    private val editArea = AABBf()
    private var selectionController = SelectionController(camera, editArea)

    private val solidColorShader = Shader(
        resourcePath("shaders/solid-color.vert.glsl"),
        resourcePath("shaders/solid-color.frag.glsl"),
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
    private val selectionFrameColor = ColorRGBa.fromHex(0xcccccc).toVector3f()

    fun init(window: Long, viewport: Viewport) {
        setViewport(viewport)
        boxMesh.init()
        gridMesh.init()
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
        solidColorShader.init()
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

        solidColorShader.use {
            uploadMat4f("uViewProj", viewProj)
            uploadVec3f("uColor", gridColor)
            gridMesh.runFrame()
        }

        boxShader.use {
            uploadMat4f("uViewProj", viewProj)
            boxMesh.runFrame()
        }

        solidColorShader.use {
            uploadMat4f("uViewProj", viewProj)
            uploadVec3f("uColor", selectionFrameColor)
            selectionController.mesh.runFrame()
        }
    }
}