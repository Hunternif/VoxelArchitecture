package hunternif.voxarch.editor.render

import hunternif.voxarch.editor.scene.MouseListener
import hunternif.voxarch.util.clamp
import org.joml.Intersectionf
import org.joml.Math.*
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

class OrbitalCamera : MouseListener {
    private val fov = 60.0
    val vp = Viewport(0, 0, 0, 0)
    /** Camera is looking at (0, 0, 0) - [translation]. */
    private val translation: Vector3f = Vector3f()
    private val projectionMatrix: Matrix4f = Matrix4f()
    private val viewMatrix: Matrix4f = Matrix4f()
    private var viewMatrixDirty = true

    /** Combined projection & view transform matrix, used for un-projecting
     * mouse cursor to world coordinates */
    private val vpMat: Matrix4f = Matrix4f()

    private var mouseX = 0f
    private var mouseY = 0f
    private var panning = false
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

    fun getViewProjectionMatrix(): Matrix4f {
        getViewMatrix()
        return vpMat
    }

    // ======================== MOUSE CALLBACKS ========================

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        if (panning) pan(posX, posY)
        else if (rotating) rotate(posX, posY)
        mouseX = posX.toFloat()
        mouseY = posY.toFloat()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseButton(button: Int, action: Int, mods: Int) {
        if (button == GLFW_MOUSE_BUTTON_3 && action == GLFW_PRESS && vp.contains(mouseX, mouseY)) panBegin()
        else if (button == GLFW_MOUSE_BUTTON_3 && action == GLFW_RELEASE) panEnd()
        else if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS && vp.contains(mouseX, mouseY)) rotateBegin()
        else if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE) rotateEnd()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onScroll(offsetX: Double, offsetY: Double) {
        radius *= if (offsetY > 0) 1f / 1.1f else 1.1f
        viewMatrixDirty = true
    }

    // ======================== PANNING (middle-click) ========================

    private fun panBegin() {
        panning = true
        // find mouse position in world coordinates
        vpMat.unprojectRay(
            mouseX,
            vp.height - mouseY,
            vp.toArray(),
            dragRayOrigin,
            dragRayDir
        )
        val t = radius * 10f
        dragStartWorldPos.set(dragRayDir).mul(t).add(dragRayOrigin)
    }

    private fun pan(posX: Double, posY: Double) {
        // find mouse position in world coordinates
        vpMat.unprojectRay(
            posX.toFloat(),
            vp.height - posY.toFloat(),
            vp.toArray(),
            dragRayOrigin,
            dragRayDir
        )
        val t = radius * 10f
        val dragWorldPosition = Vector3f(dragRayDir).mul(t).add(dragRayOrigin)
        translation.add(dragWorldPosition.sub(dragStartWorldPos))
        viewMatrixDirty = true
    }

    private fun panEnd() {
        panning = false
    }

    // ======================== ROTATING (right-click) ========================

    private fun rotateBegin() {
        rotating = true
    }

    private fun rotate(posX: Double, posY: Double) {
        val deltaX = posX.toFloat() - mouseX
        val deltaY = posY.toFloat() - mouseY
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

    // ======================== PROJECTIONS ========================

    /** Projects screen coordinates to world coordinates at Y=-0.5 */
    fun projectToFloor(posX: Float, posY: Float): Vector3f {
        vpMat.unprojectRay(
            posX,
            vp.height - posY,
            vp.toArray(),
            dragRayOrigin,
            dragRayDir
        )
        val t = Intersectionf.intersectRayPlane(
            dragRayOrigin,
            dragRayDir,
            Vector3f(0f, -0.5f, 0f),
            Vector3f(0f, if (dragRayOrigin.y > 0) 1f else -1f, 0f),
            1E-5f
        )
        dragStartWorldPos.set(dragRayDir).mul(t).add(dragRayOrigin)
        return dragStartWorldPos
    }
}