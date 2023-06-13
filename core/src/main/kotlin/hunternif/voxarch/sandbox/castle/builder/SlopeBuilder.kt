package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.ANodeBuilder
import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.fillXZLocal
import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.ILinearTransformation
import kotlin.math.roundToInt

/**
 * Builds a plane from low XY to high XY. See [hunternif.voxarch.plan.Slope]
 */
class SlopeBuilder(private val material: String) : ANodeBuilder() {
    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        if (node.width == 0.0) return
        val slopeFactor = node.height / node.width
        // TODO for slopes > 45 degrees, fill YZ instead
        node.fillXZLocal(trans) { x, y, z ->
            val block = context.materials.get(material)
            val slopeY = ((x - node.start.x) * slopeFactor).roundToInt()
            val globalVec = trans.transform(x, y + slopeY, z)
            world.setBlock(globalVec, block)
        }
    }
}