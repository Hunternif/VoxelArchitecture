package hunternif.voxarch.editor.render

import hunternif.voxarch.util.clamp
import org.joml.Math.*
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

class OrbitalCamera {
    private val fov = 60.0
    private val vp = Viewport(0, 0, 0, 0)
    /** Camera is looking at (0, 0, 0) - [translation]. */
    private val translation: Vector3f = Vector3f()
    val projectionMatrix: Matrix4f = Matrix4f()
    private val viewMatrix: Matrix4f = Matrix4f()
    private var viewMatrixDirty = true

    /** Combined projection & view transform matrix, used for un-projecting
     * mouse cursor to world coordinates */
    private val vpMat: Matrix4f = Matrix4f()
    private var mouseX = 0
    private var mouseY = 0
    private var dragging = false
    private var rotating = false

    private val dragStartWorldPos: Vector3f = Vector3f()
    private val dragRayOrigin: Vector3f = Vector3f()
    private val dragRayDir: Vector3f = Vector3f()

    private var xAngle = 0.5f
    private var yAngle = 0.3f
    private var radius = 5f

    fun setViewport(viewport: Viewport) {
        if (vp.width != viewport.width || vp.height != viewport.height) {
            // adjust projection matrix
            projectionMatrix.setPerspective(
                Math.toRadians(fov).toFloat(),
                viewport.width.toFloat() / viewport.height,
                0.1f,
                1000.0f
            )
            viewMatrixDirty = true
        }
        vp.set(viewport)
    }

    fun setPosition(x: Float, y: Float, z: Float) {
        this.translation.set(-x, -y, -z)
        viewMatrixDirty = true
    }

    /** Adjusts camera radius so the given points are visible*/
    fun zoomToFit(vararg points: Vector3f) {
        val radii = points.map { p ->
            // vector from camera's focus point to the given point.
            // [translation] stores the translation value, i.e. minus focus point.
            val d = Vector3f()
            p.add(translation, d)

            // find angle between camera normal and vector d, in radians
            val camNormal = Vector3f()
            getViewMatrix().positiveZ(camNormal)
            val angle = d.angle(camNormal)

            val focalLength = 1f / tan(Math.toRadians(fov).toFloat() / 2f)
            d.length() * (cos(angle) + sin(angle) * focalLength)
        }
        radii.maxOrNull()?.let { maxRadius ->
            radius = maxRadius
            viewMatrixDirty = true
        }
    }

    /** Adjusts camera radius to fit all corners of this box.
     * [start] and [end] are opposite corners. */
    fun zoomToFitBox(start: Vector3f, end: Vector3f) {
        zoomToFit(
            Vector3f(start.x, start.y, start.z),
            Vector3f(start.x, start.y, end.z),
            Vector3f(start.x, end.y, start.z),
            Vector3f(start.x, end.y, end.z),
            Vector3f(end.x, start.y, start.z),
            Vector3f(end.x, start.y, end.z),
            Vector3f(end.x, end.y, start.z),
            Vector3f(end.x, end.y, end.z),
        )
    }

    fun getViewMatrix(): Matrix4f {
        if (viewMatrixDirty) {
            viewMatrixDirty = false
            viewMatrix.translation(0f, 0f, -radius)
                .rotateX(xAngle)
                .rotateY(yAngle)
                .translate(translation)
            projectionMatrix.mul(viewMatrix, vpMat)
        }
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
        viewMatrixDirty = true
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
        viewMatrixDirty = true
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
        xAngle = (xAngle + deltaY * 0.01f).clamp(MIN_X_ANGLE, MAX_X_ANGLE)
        yAngle += deltaX * 0.01f
        viewMatrixDirty = true
    }

    private fun rotateEnd() {
        rotating = false
    }

    companion object {
        private const val MIN_X_ANGLE: Float = -PI.toFloat() / 2f
        private const val MAX_X_ANGLE: Float = PI.toFloat() / 2f
    }
}