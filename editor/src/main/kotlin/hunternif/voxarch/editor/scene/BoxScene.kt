package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.render.ColorRGBa
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.render.Shader
import hunternif.voxarch.editor.render.Viewport
import hunternif.voxarch.editor.resourcePath
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL32.*

class BoxScene {
    private val vp = Viewport(0, 0, 0, 0)
    private val modelMatrix = Matrix4f().identity()
    private val box = BoxMesh()
    private val camera = OrbitalCamera()

    private val shader = Shader(
        resourcePath("shaders/magica-voxel.vert.glsl"),
        resourcePath("shaders/magica-voxel.frag.glsl"),
    )

    /** Skylight falls uniformly in this direction */
    private val skylightDir = Vector3f(0.77f, -1.0f, -0.9f).normalize()
    private val skylightColor = ColorRGBa.fromHex(0xffffff).toVector3f()
    private val skylightPower = 1.25f
    /** Highlights the bottom of the model */
    private val backlightDir = Vector3f(-0.77f, 1.0f, 0.9f).normalize()
    private val backlightColor = ColorRGBa.fromHex(0xffffff).toVector3f()
    private val backlightPower = 1.0f
    private val ambientColor = ColorRGBa.fromHex(0x353444).toVector3f()
    private val ambientPower = 1.0f
    private val boxColor = ColorRGBa.fromHex(0xff9966).toVector3f()

    fun init(window: Long, viewport: Viewport) {
        setViewport(vp)
        box.init()
        shader.init()
        glEnable(GL_DEPTH_TEST)
        GLFW.glfwSetCursorPosCallback(window, camera::onMouseMove)
        GLFW.glfwSetMouseButtonCallback(window, camera::onMouseButton)
        GLFW.glfwSetScrollCallback(window, camera::onScroll)
    }

    fun setViewport(viewport: Viewport) {
        vp.set(viewport)
        camera.setViewport(vp)
    }

    fun render() {
        shader.use()

        shader.uploadMat4f("uProjection", camera.projectionMatrix)
        shader.uploadMat4f("uView", camera.getViewMatrix())
        shader.uploadMat4f("uModel", modelMatrix)

        shader.uploadVec3f("uSkylightDir", skylightDir)
        shader.uploadVec3f("uSkylightColor", skylightColor)
        shader.uploadFloat("uSkylightPower", skylightPower)

        shader.uploadVec3f("uBacklightDir", backlightDir)
        shader.uploadVec3f("uBacklightColor", backlightColor)
        shader.uploadFloat("uBacklightPower", backlightPower)

        shader.uploadVec3f("uAmbientColor", ambientColor)
        shader.uploadFloat("uAmbientPower", ambientPower)

        shader.uploadVec3f("uObjectColor", boxColor)

        box.render()

        shader.detach()
    }
}