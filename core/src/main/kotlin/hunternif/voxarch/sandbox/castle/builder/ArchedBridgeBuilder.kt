package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.ILinearTransformation
import hunternif.voxarch.vector.Vec3
import hunternif.voxarch.vector.inverse
import kotlin.math.*

/**
 * Builds a round arch along the X axis, fills up the space above.
 */
class ArchedBridgeBuilder(private val material: String) : ANodeBuilder() {
    //TODO: add offsets from top and sides

    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        val midPoint = node.width / 2
        val height = node.height
        if (height <= 0) return

        // The circle is placed so that it's tangent at the top, and
        // it passes through the lower bottom points.
        // But it can't go narrower than the total width.
        val r =
            if (height > midPoint) midPoint
            else (midPoint * midPoint / height + height) / 2

        // use a larger radius for testing to get a nicer round arch
        val r1 = r
        val r2 = r + 0.4

        val inverse = trans.inverse()
        val localVec = Vec3(0, 0, 0)

        node.fillXZ(trans) { x, y, z ->
            inverse.transformLocal(localVec.set(x, y, z))
            // distance to midpoint:
            val dMid = abs(midPoint - localVec.x)
            val localHeight =
                if (dMid > r2) height.toInt()
                else (height - r1 + sqrt(r2 * r2 - dMid * dMid)).toInt()
            for (dy in localHeight..height.toInt()) {
                val block = context.materials.get(material)
                world.setBlock(x, y + dy, z, block)
            }
        }
    }
}