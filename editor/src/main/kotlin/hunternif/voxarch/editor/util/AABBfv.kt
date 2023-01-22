package hunternif.voxarch.editor.util

import org.joml.AABBf
import org.joml.Vector3f

/** Adds optimized convenience methods. */
class AABBfv : AABBf() {
    val minVec: Vector3f = Vector3f()
        get() = field.set(minX, minY, minZ)

    val maxVec: Vector3f = Vector3f()
        get() = field.set(maxX, maxY, maxZ)
}