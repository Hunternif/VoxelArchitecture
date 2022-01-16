package hunternif.voxarch.editor.render

import hunternif.voxarch.editor.scene.InputListener
import hunternif.voxarch.util.clamp
import org.joml.*
import org.joml.Math.*
import org.lwjgl.glfw.GLFW.*
import java.lang.Math

class OrbitalCamera : InputListener {
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

    // temporary vectors
    private val pointRayOrigin: Vector3f = Vector3f()
    private val pointRayDir: Vector3f = Vector3f()
    private val dragStartWorldPos: Vector3f = Vector3f()
    private val projectedWorldPos: Vector3f = Vector3f()
    private val verticalPlaneNormal: Vector3f = Vector3f()
    private val pos4: Vector4f = Vector4f()
    private val screenPos: Vector2f = Vector2f()

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

    fun setPosition(pos: Vector3f) {
        setPosition(pos.x, pos.y, pos.z)
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
        unprojectPoint(mouseX, mouseY)
        val t = radius * 10f
        dragStartWorldPos.set(pointRayDir).mul(t).add(pointRayOrigin)
    }

    private fun pan(posX: Double, posY: Double) {
        // find mouse position in world coordinates
        unprojectPoint(posX.toFloat(), posY.toFloat())
        val t = radius * 10f
        val dragWorldPosition = Vector3f(pointRayDir).mul(t).add(pointRayOrigin)
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

    /**
     * Projects screen coordinates to world coordinates at Y=-0.5.
     * [posX], [posY] are screen coordinates relative to window (not viewport).
     */
    fun projectToFloor(posX: Float, posY: Float): Vector3f {
        unprojectPoint(posX, posY)
        val t = Intersectionf.intersectRayPlane(
            pointRayOrigin,
            pointRayDir,
            Vector3f(0f, -0.5f, 0f),
            Vector3f(0f, if (pointRayOrigin.y > 0) 1f else -1f, 0f),
            1E-5f
        )
        projectedWorldPos.set(pointRayDir).mul(t).add(pointRayOrigin)
        return projectedWorldPos
    }

    /**
     * Projects screen coordinates to world coordinates on a vertical plane
     * running through [point] and away from the camera.
     * Used for dragging voxels vertically.
     * [posX], [posY] are screen coordinates relative to window (not viewport).
     */
    fun projectToVertical(posX: Float, posY: Float, point: Vector3f): Vector3f {
        unprojectPoint(posX, posY)
        // create the plane normal
        verticalPlaneNormal.apply {
            set(pointRayDir)
            y = 0f
            normalize()
            negate()
        }
        val t = Intersectionf.intersectRayPlane(
            pointRayOrigin,
            pointRayDir,
            point,
            verticalPlaneNormal,
            1E-5f
        )
        projectedWorldPos.set(pointRayDir).mul(t).add(pointRayOrigin)
        return projectedWorldPos
    }

    /**
     * Projects point onto an AAB. Returns true if the point hits the AAB.
     * [posX], [posY] are screen coordinates relative to window (not viewport).
     * [aabMin], [aabMax] define the corners of the AAB.
     * [result] stores the distances to the near and far points of intersection.
     */
    fun projectToBox(
        posX: Float,
        posY: Float,
        aabMin: Vector3f,
        aabMax: Vector3f,
        result: Vector2f = screenPos,
    ): Boolean {
        unprojectPoint(posX, posY)
        return Intersectionf.intersectRayAab(
            pointRayOrigin,
            pointRayDir,
            aabMin,
            aabMax,
            result
        )
    }

    /** @see [projectToBox] */
    fun projectToBox(
        posX: Int,
        posY: Int,
        aabMin: Vector3f,
        aabMax: Vector3f,
        result: Vector2f = screenPos,
    ) = projectToBox(posX.toFloat(), posY.toFloat(), aabMin, aabMax, result)

    /**
     * Unproject the given screen coordinates to world coordinates.
     * Screen coordinates are assumed to be global, e.g. not accounting for
     * the viewport.
     * The result will be stored in [pointRayOrigin] and [pointRayDir].
     * [posX], [posY] are screen coordinates relative to window (not viewport).
     */
    private fun unprojectPoint(posX: Float, posY: Float) {
        vpMat.unprojectRay(
            posX - vp.x,
            vp.height - posY + vp.y,
            vp.getSizeArray(),
            pointRayOrigin,
            pointRayDir
        )
    }

    /** Returns screen coordinates of the given point, relative to viewport. */
    fun projectToViewport(x: Float, y: Float, z: Float): Vector2f {
        pos4.set(x, y, z, 1f)
        // apply view-projection transformation:
        pos4.mul(vpMat)
        // apply perspective projection from "homogeneous space":
        pos4.div(pos4.w)
        // apply viewport transformation:
        screenPos.set(
            (1f + pos4.x) / 2f * vp.width.toFloat(),
            (1f - pos4.y) / 2f * vp.height.toFloat()
        )
        return screenPos
    }
    /** @see [projectToViewport] */
    fun projectToViewport(point: Vector3f): Vector2f =
        projectToViewport(point.x, point.y, point.z)
}