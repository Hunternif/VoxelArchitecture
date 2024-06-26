package hunternif.voxarch.editor.render

import hunternif.voxarch.editor.scene.MouseListener
import hunternif.voxarch.editor.scene.models.box.BoxMesh
import hunternif.voxarch.editor.util.AADirection3D
import hunternif.voxarch.editor.util.Triangle
import hunternif.voxarch.util.clamp
import org.joml.*
import org.joml.Math.*
import org.lwjgl.glfw.GLFW.*
import java.lang.Math

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
    var panning = false
    var rotating = false

    // temporary vectors
    private val pointRayOrigin: Vector3f = Vector3f()
    private val pointRayDir: Vector3f = Vector3f()
    private val dragStartWorldPos: Vector3f = Vector3f()
    private val projectedWorldPos: Vector3f = Vector3f()
    private val planeNormal: Vector3f = Vector3f()
    private val dirNormal: Vector3f = Vector3f()
    private val pos4: Vector4f = Vector4f()
    private val screenPos: Vector2f = Vector2f()

    var xAngle = 0.5f
        private set
    var yAngle = 0.3f
        private set
    var radius = 5f
        set(value) {
            field = value
            viewMatrixDirty = true
        }
    var minZoomVelocity = 1f


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

    fun setPosition(x: Number, y: Number, z: Number) {
        this.translation.set(-x.toFloat(), -y.toFloat(), -z.toFloat())
        viewMatrixDirty = true
    }

    /** Adjusts camera radius so the given points are visible.
     * If [zoomOutOnly] is enabled, then camera will only zoom out, i.e. won't
     * change if all points are already visible. */
    fun zoomToFit(zoomOutOnly: Boolean = false, vararg points: Vector3f) {
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
        val maxRadius = radii.maxOrNull() ?: radius
        radius = (if (zoomOutOnly) max(radius, maxRadius) else maxRadius)
        viewMatrixDirty = true
    }

    /** Adjusts camera radius to fit all corners of this box.
     * [start] and [end] are opposite corners.
     * If [zoomOutOnly] is enabled, then camera will only zoom out, i.e. won't
     * change if all points are already visible. */
    fun zoomToFitBox(start: Vector3f, end: Vector3f, zoomOutOnly: Boolean = false) {
        zoomToFit(
            zoomOutOnly,
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
        if (vp.contains(mouseX, mouseY)) {
            val newRadius = radius * if (offsetY > 0) 1f / 1.1f else 1.1f
            val zoomVelocity = abs(newRadius - radius)
            if (zoomVelocity >= minZoomVelocity) {
                radius = newRadius
            } else {
                // we're too close. instead of zooming, move forward:
                pointRayDir.set(0f, 0f, 1f)
                pointRayDir.rotateX(-xAngle)
                pointRayDir.rotateY(-yAngle)
                translation.add(pointRayDir.mul(minZoomVelocity))
            }
            viewMatrixDirty = true
        }
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

    fun setAngle(xAngle: Float, yAngle: Float) {
        this.xAngle = xAngle
        this.yAngle = yAngle
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
     * Projects screen coordinates to world coordinates at the given Y level.
     * [posX], [posY] are screen coordinates relative to window (not viewport).
     */
    fun projectToFloor(posX: Float, posY: Float, floorY: Float = -0.5f): Vector3f {
        unprojectPoint(posX, posY)
        val t = Intersectionf.intersectRayPlane(
            pointRayOrigin,
            pointRayDir,
            Vector3f(0f, floorY, 0f),
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
    fun projectToY(posX: Float, posY: Float, point: Vector3f): Vector3f =
        projectToAxis(posX, posY, AADirection3D.POS_Y.vec, point)

    /**
     * Projects screen coordinates to world coordinates on a plane that's
     * almost parallel to the screen, running through [point] and away from
     * the camera along [dir].
     * Used for dragging voxels along the XYZ axes.
     * [posX], [posY] are screen coordinates relative to window (not viewport).
     * [dir] is the direction of the axis.
     */
    fun projectToAxis(posX: Float, posY: Float, dir: Vector3fc, point: Vector3f): Vector3f {
        unprojectPoint(posX, posY)
        // normalize [dir]
        dirNormal.apply {
            set(dir)
            normalize()
        }
        // create the plane normal
        planeNormal.apply {
            set(pointRayDir)
            // remove the normal component along [dir]
            sub(dirNormal.mul(this.dot(dirNormal)))
            normalize()
            negate()
        }
        val t = Intersectionf.intersectRayPlane(
            pointRayOrigin,
            pointRayDir,
            point,
            planeNormal,
            1E-5f
        )
        projectedWorldPos.set(pointRayDir).mul(t).add(pointRayOrigin)
        return projectedWorldPos
    }

    /**
     * Projects point onto an AAB. Returns true if the point hits the AAB.
     * [posX], [posY] are screen coordinates relative to window (not viewport).
     * [aabMin], [aabMax] define the corners of the AAB.
     * [resultDistance] stores the distances to the near and far intersection
     *      points.
     * Optional [resultNearPoint] stores the near intersection point.
     */
    fun projectToAABox(
        posX: Number,
        posY: Number,
        aabMin: Vector3f,
        aabMax: Vector3f,
        resultDistance: Vector2f? = null,
        resultNearPoint: Vector3f? = null,
    ): Boolean {
        unprojectPoint(posX.toFloat(), posY.toFloat())
        val hit = Intersectionf.intersectRayAab(
            pointRayOrigin,
            pointRayDir,
            aabMin,
            aabMax,
            screenPos
        )
        if (hit) {
            resultNearPoint?.apply {
                set(pointRayDir).mul(screenPos.x).add(pointRayOrigin)
            }
            resultDistance?.set(screenPos)
        }
        return hit
    }

    /**
     * Projects point onto a box. Returns true if the point hits the AAB.
     * [posX], [posY] are screen coordinates relative to window (not viewport).
     * Optional [resultDistance] stores the distances to the near and far
     * intersection points.
     * Optional [resultNearPoint] stores the near intersection point.
     */
    fun projectToBox(
        posX: Number,
        posY: Number,
        box: BoxMesh,
        resultDistance: Vector2f? = null,
        resultNearPoint: Vector3f? = null,
    ): Boolean =
        projectToMesh(posX, posY, box.triangles, resultDistance, resultNearPoint)

    /** See [projectToBox] */
    fun projectToMesh(
        posX: Number,
        posY: Number,
        triangles: Iterable<Triangle>,
        resultDistance: Vector2f? = null,
        resultNearPoint: Vector3f? = null,
    ): Boolean {
        unprojectPoint(posX.toFloat(), posY.toFloat())
        var hit = false
        var tNear = Float.POSITIVE_INFINITY
        var tFar = Float.NEGATIVE_INFINITY
        triangles.forEach {
            val distance = Intersectionf.intersectRayTriangle(
                pointRayOrigin,
                pointRayDir,
                it.p1, it.p2, it.p3,
                0.01f
            )
            if (distance > -1f) {
                hit = true
                tNear = min(distance, tNear)
                tFar = max(distance, tFar)
            }
        }
        if (hit) {
            screenPos.set(tNear, tFar)
            resultNearPoint?.apply {
                set(pointRayDir).mul(screenPos.x).add(pointRayOrigin)
            }
            resultDistance?.set(screenPos)
        }
        return hit
    }

    /**
     * Unproject the given SCREEN coordinates to world coordinates.
     * Screen coordinates are assumed to be global, e.g. not accounting for
     * the viewport.
     * The result will be stored in [pointRayOrigin] and [pointRayDir].
     * [posX], [posY] are screen coordinates relative to window (not viewport).
     * @return [pointRayOrigin]
     */
    private fun unprojectPoint(posX: Float, posY: Float): Vector3f {
        vpMat.unprojectRay(
            posX - vp.x,
            vp.height - posY + vp.y,
            vp.getSizeArray(),
            pointRayOrigin,
            pointRayDir
        )
        return pointRayOrigin
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

    fun isMouseInViewport() = vp.contains(mouseX, mouseY)

}