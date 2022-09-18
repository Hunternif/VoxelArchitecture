package hunternif.voxarch.editor.render

import hunternif.voxarch.editor.scene.MouseListener
import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * Camera to draw the gizmo overlaid over the main viewport.
 * It tracks the movement of [orbitalCamera].
 */
class GizmoCamera(
    private val orbitalCamera: OrbitalCamera
) : MouseListener {
    private val fov = 60.0
    /**
     * Viewport for drawing the gizmo.
     * Should be smaller than [orbitalCamera]'s viewport.
     * */
    val vp = Viewport(0, 0, 0, 0)
    /** Camera is looking at (0, 0, 0) - [translation]. */
    private val translation: Vector3f = Vector3f()
    private val projectionMatrix: Matrix4f = Matrix4f()
    private val viewMatrix: Matrix4f = Matrix4f()
    private var viewMatrixDirty = true

    /** Combined projection & view transform matrix, used for un-projecting
     * mouse cursor to world coordinates */
    private val vpMat: Matrix4f = Matrix4f()

    private var xAngle = 0.5f
    private var yAngle = 0.3f
    var radius = 1f
        set(value) {
            field = value
            viewMatrixDirty = true
        }


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

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        if (xAngle != orbitalCamera.xAngle ||
            yAngle != orbitalCamera.yAngle
        ) {
            xAngle = orbitalCamera.xAngle
            yAngle = orbitalCamera.yAngle
            viewMatrixDirty = true
        }
    }
}