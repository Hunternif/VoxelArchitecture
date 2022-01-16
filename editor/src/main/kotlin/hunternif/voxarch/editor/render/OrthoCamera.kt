package hunternif.voxarch.editor.render

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

class OrthoCamera {
    val vp = Viewport(0, 0, 0, 0)
    private val projectionMatrix: Matrix4f = Matrix4f()
    private val viewMatrix: Matrix4f = Matrix4f()
    private var viewMatrixDirty = true

    /** Combined projection & view transform matrix, used for un-projecting
     * mouse cursor to world coordinates */
    private val vpMat: Matrix4f = Matrix4f()

    private val position = Vector2f()

    fun setViewport(viewport: Viewport) {
        if (vp.width != viewport.width || vp.height != viewport.height) {
            // adjust projection matrix
            projectionMatrix.identity()
            projectionMatrix.ortho(
                0.0f,
                viewport.width.toFloat(),
                viewport.height.toFloat(),
                0.0f,
                0.0f,
                100.0f
            )
            viewMatrixDirty = true
        }
        vp.set(viewport)
    }

    fun getViewMatrix(): Matrix4f {
        if (viewMatrixDirty) {
            viewMatrixDirty = false
            val cameraFront = Vector3f(0.0f, 0.0f, -1.0f)
            val cameraUp = Vector3f(0.0f, 1.0f, 0.0f)
            viewMatrix.identity()
            viewMatrix.lookAt(
                Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp
            )
            projectionMatrix.mul(viewMatrix, vpMat)
        }
        return viewMatrix
    }

    fun getViewProjectionMatrix(): Matrix4f {
        getViewMatrix()
        return vpMat
    }
}
