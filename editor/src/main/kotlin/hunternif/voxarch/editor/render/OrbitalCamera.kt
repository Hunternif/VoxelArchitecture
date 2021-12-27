package hunternif.voxarch.editor.render

import hunternif.voxarch.editor.render.Viewport
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

class OrbitalCamera {
    private val vp = Viewport(0, 0, 0, 0)
    private val translation: Vector3f = Vector3f()
    val projectionMatrix: Matrix4f = Matrix4f()
    private val viewMatrix: Matrix4f = Matrix4f()

    /** For un-projecting mouse cursor to world coordinates */
    private val vpMat: Matrix4f = Matrix4f()

    private var mouseX = 0
    private var mouseY = 0
    private var dragging = false
    private var rotating = false

    private val dragStartWorldPos: Vector3f = Vector3f()
    private val dragCamNormal: Vector3f = Vector3f()
    private val dragRayOrigin: Vector3f = Vector3f()
    private val dragRayDir: Vector3f = Vector3f()

    private var xAngle = 0.5f
    private var yAngle = 0.3f
    private var radius = 5f

    fun setViewport(viewport: Viewport) {
        vp.set(viewport)
        // adjust projection matrix
        projectionMatrix.setPerspective(
            Math.toRadians(60.0).toFloat(),
            vp.width.toFloat() / vp.height,
            0.1f,
            1000.0f
        )
    }

    fun getViewMatrix(): Matrix4f {
        viewMatrix.translation(0f, 0f, -radius)
            .rotateX(xAngle)
            .rotateY(yAngle)
            .translate(translation)
        projectionMatrix.mul(viewMatrix, vpMat)
        return viewMatrix
    }

    // ======================== MOUSE CALLBACKS ========================

    fun onMouseMove(window: Long, xpos: Double, ypos: Double) {
        if (dragging) drag(xpos, ypos) else if (rotating) rotate(xpos, ypos)
        mouseX = xpos.toInt()
        mouseY = ypos.toInt()
    }

    fun onMouseButton(window: Long, button: Int, action: Int, mods: Int) {
        if (button == GLFW_MOUSE_BUTTON_3 && action == GLFW_PRESS && vp.contains(mouseX, mouseY)) dragBegin()
        else if (button == GLFW_MOUSE_BUTTON_3 && action == GLFW_RELEASE) dragEnd()
        else if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS && vp.contains(mouseX, mouseY)) rotateBegin()
        else if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE) rotateEnd()
    }

    fun onScroll(window: Long, xoffset: Double, yoffset: Double) {
        radius *= if (yoffset > 0) 1f / 1.1f else 1.1f
    }

    // ======================== DRAGGING ========================

    private fun dragBegin() {
        dragging = true
        // find mouse position in world coordinates
        vpMat.unprojectRay(
            mouseX.toFloat(),
            vp.height - mouseY.toFloat(),
            vp.toArray(),
            dragRayOrigin,
            dragRayDir
        )
        val t = radius * 10f
        dragStartWorldPos.set(dragRayDir).mul(t).add(dragRayOrigin)
        viewMatrix.positiveZ(dragCamNormal)
    }

    private fun drag(xpos: Double, ypos: Double) {
        // find mouse position in world coordinates
        vpMat.unprojectRay(
            xpos.toFloat(),
            vp.height - ypos.toFloat(),
            vp.toArray(),
            dragRayOrigin,
            dragRayDir
        )
        val t = radius * 10f
        val dragWorldPosition = Vector3f(dragRayDir).mul(t).add(dragRayOrigin)
        translation.add(dragWorldPosition.sub(dragStartWorldPos))
    }

    private fun dragEnd() {
        dragging = false
    }

    // ======================== ROTATING ========================

    private fun rotateBegin() {
        rotating = true
    }

    private fun rotate(xpos: Double, ypos: Double) {
        val deltaX = xpos.toFloat() - mouseX
        val deltaY = ypos.toFloat() - mouseY
        xAngle += deltaY * 0.01f
        yAngle += deltaX * 0.01f
    }

    private fun rotateEnd() {
        rotating = false
    }
}