package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.ILinearTransformation
import hunternif.voxarch.vector.Vec3
import hunternif.voxarch.vector.inverse
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Builds a "lancet" window on the given section of the wall.
 */
class ArchedWindowBuilder : ANodeBuilder() {
    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        val midPoint = node.width / 2
        val height = node.height

        // for the top, fill an upside down circle.
        // use a larger radius for testing to get a nicer round arch.
        val r1 = midPoint
        val r2 = r1 + 0.4

        val inverse = trans.inverse()
        val localVec = Vec3(0, 0, 0)

        node.fillXZ(trans) { x, y, z ->
            inverse.transformLocal(localVec.set(x, y, z))
            val curR = abs(r1 - localVec.x) // distance to mid-point
            val localHeight =
                if (curR > r2) -1
                else (height - r1 + sqrt(r2 * r2 - curR * curR)).toInt()
            for (dy in 0..localHeight) {
                world.clearBlock(x, y + dy, z)
            }
        }
    }
}