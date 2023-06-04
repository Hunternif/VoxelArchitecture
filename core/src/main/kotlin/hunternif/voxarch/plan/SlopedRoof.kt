package hunternif.voxarch.plan

import hunternif.voxarch.vector.LinearTransformation
import hunternif.voxarch.vector.Plane
import hunternif.voxarch.vector.Vec3

/**
 * Describes a roof built of multiple sloped sections.
 * Uses child [Slope] nodes to define roof boundary.
 */
class SlopedRoof(origin: Vec3) : Node(origin) {
    constructor() : this(Vec3.ZERO)

    override fun getRoofBoundaries(): List<Plane> {
        return super.getRoofBoundaries().toMutableList().apply {
            // sloped roofs:
            slopes.forEach { slope ->
                val localRoofs = slope.getRoofBoundaries()
                val trans = LinearTransformation().apply {
                    translate(slope.origin)
                    rotateY(slope.rotationY)
                }
                val roofs = localRoofs.map { it.transform(trans) }
                addAll(roofs)
            }
        }
    }
}