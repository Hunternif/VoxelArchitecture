package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import org.joml.Vector2f
import org.joml.Vector3f

class HitTester(
    private val camera: OrbitalCamera,
) {
    /**
     * Returns true if the given point on screen hits this object.
     * [posX], [posY] are screen coordinates relative to window (not viewport).
     * Optional [resultDistance] stores the distances to the near and far intersection
     *      points.
     * Optional [resultNearPoint] stores the near intersection point.
     */
    fun hitTest(
        obj: SceneObject,
        posX: Number,
        posY: Number,
        resultDistance: Vector2f? = null,
        resultNearPoint: Vector3f? = null,
    ): Boolean =
        when (obj) {
            is SceneNode -> camera.projectToBox(
                posX, posY, obj.box, resultDistance, resultNearPoint)
            else -> camera.projectToAABox(
                posX, posY, obj.box.aabb.minVec, obj.box.aabb.maxVec,
                resultDistance, resultNearPoint)
        }
}