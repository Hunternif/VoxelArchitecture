package hunternif.voxarch.util

import hunternif.voxarch.plan.Node
import hunternif.voxarch.util.SnapOrigin.*
import hunternif.voxarch.vector.Vec3

/**
 * Standard places where a node's origin could automatically snap to,
 * relative to its bounding box.
 */
enum class SnapOrigin {
    /** Not snapping */
    OFF,
    /** Low-XYZ corner */
    CORNER,
    /** Center of the floor */
    FLOOR_CENTER,
    /** Center of the box, on all axes. */
    CENTER,
}

/**
 * Changes the position of origin according to [method],
 * without moving the box.
 * Moves children to preserve their absolute positions.
 */
fun Node.snapOrigin(method: SnapOrigin) {
    val newStart = when(method) {
        OFF -> return
        CORNER -> Vec3(0, 0, 0)
        FLOOR_CENTER -> Vec3(-size.x / 2, 0.0, -size.z / 2)
        CENTER -> -size / 2
    }
    val delta = newStart - start
    origin -= delta.rotateY(rotationY)
    children.forEach { it.origin += delta }
    start = newStart
}

/**
 * Changes [Node.start] so that origin matches the snap [method],
 * without moving origin.
 * This results in the box moving.
 */
fun Node.snapStart(method: SnapOrigin) {
    start = when(method) {
        OFF -> return
        CORNER -> Vec3(0, 0, 0)
        FLOOR_CENTER -> Vec3(-size.x / 2, 0.0, -size.z / 2)
        CENTER -> -size / 2
    }
}