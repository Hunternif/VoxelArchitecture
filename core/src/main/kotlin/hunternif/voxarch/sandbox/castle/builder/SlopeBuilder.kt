package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.ANodeBuilder
import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.fillXZ
import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.ILinearTransformation
import hunternif.voxarch.vector.Vec3
import hunternif.voxarch.vector.inverse
import kotlin.math.roundToInt

/**
 * Builds a plane from low XY to high XY. See [hunternif.voxarch.plan.Slope]
 */
class SlopeBuilder(private val material: String) : ANodeBuilder() {
    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        if (node.width == 0.0) return
        val slopeFactor = node.height / node.width
        val inverse = trans.inverse()
        val vec = Vec3(0, 0, 0)
        node.fillXZ(trans) { x, y, z ->
            inverse.transformLocal(vec.set(x, y, z))
            val block = context.materials.get(material)
            val slopeY = ((vec.x - node.start.x) * slopeFactor).roundToInt()
            world.setBlock(x, y + slopeY, z, block)
        }
    }
}