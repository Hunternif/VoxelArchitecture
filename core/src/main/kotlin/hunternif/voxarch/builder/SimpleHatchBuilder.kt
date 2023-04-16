package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.ILinearTransformation
import kotlin.math.max

/**
 * Makes a vertical passage along the Y axis.
 *
 * @param minWidth min passage width along the X axis
 * @param minLength min passage length along the Z axis
 * @param clearance passage will be cleared this far along the Y axis in both directions
 */
class SimpleHatchBuilder(
    private val minWidth: Int = 1,
    private val minLength: Int = 1,
    private val clearance: Int = 1
): ANodeBuilder() {
    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        val localWorld = world.toLocal(trans)
        val width = max(minWidth, node.size.x.toInt())
        val length = max(minLength, node.size.z.toInt())
        // Offset of 1 from both boundaries because the width & length of the hatch
        // span all available space, including where the walls go.
        for (x in 1 until width) {
            for (z in 1 until length) {
                for (y in -clearance..clearance) {
                    localWorld.clearBlock(x, y, z)
                }
            }
        }
    }
}