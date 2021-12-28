package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.render.Viewport
import hunternif.voxarch.editor.util.resourcePath
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.forEachPos
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL32.*

class BoxScene {
    private val vp = Viewport(0, 0, 0, 0)
    private val boxMesh = BoxMeshInstanced()
    private val gridMesh = FloorGridMesh()
    private val camera = OrbitalCamera()
    private var data: IStorage3D<*>? = null

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
    private val boxColor = ColorRGBa.fromHex(0xff9966).toVector3f()
    private val gridColor = ColorRGBa.fromHex(0x333333).toVector3f()

    fun init(window: Long, viewport: Viewport) {
        setViewport(viewport)
        boxMesh.init()
        gridMesh.init()
        gridMesh.setSize(0, 0, 100, 100)
        initShaders()
        glEnable(GL_DEPTH_TEST)
        GLFW.glfwSetCursorPosCallback(window, camera::onMouseMove)
        GLFW.glfwSetMouseButtonCallback(window, camera::onMouseButton)
        GLFW.glfwSetScrollCallback(window, camera::onScroll)
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

            uploadVec3f("uObjectColor", boxColor)
        }

        gridShader.init {
            uploadVec3f("uGridColor", gridColor)
        }
    }

    fun setViewport(viewport: Viewport) {
        vp.set(viewport)
        camera.setViewport(vp)
    }

    fun setVoxelData(data: IStorage3D<*>) {
        this.data = data
        val offsets = mutableListOf<Vector3i>()
        data.forEachPos { x, y, z, v ->
            if (v != null)
                offsets.add(Vector3i(x, y, z))
        }
        boxMesh.setInstances(offsets)
    }

    fun centerCamera() {
        data?.run {
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

        gridShader.use {
            uploadMat4f("uProjection", camera.projectionMatrix)
            uploadMat4f("uView", camera.getViewMatrix())
            gridMesh.render()
        }

        boxShader.use {
            uploadMat4f("uProjection", camera.projectionMatrix)
            uploadMat4f("uView", camera.getViewMatrix())
            boxMesh.render()
        }
    }
}