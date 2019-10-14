package hunternif.voxarch.builder

import hunternif.voxarch.plan.Gate
import hunternif.voxarch.storage.IBlockStorage
import kotlin.math.max

/**
 * Makes a horizontal passage along the Z axis.
 *
 * @param minWidth min passage width along the X axis
 * @param minHeight min passage height (along the Y axis)
 * @param clearance passage will be cleared this far along the Z axis in both directions
 */
class SimpleGateBuilder(
    private val minWidth: Int = 1,
    private val minHeight: Int = 2,
    private val clearance: Int = 1
): Builder<Gate>() {
    override fun build(node: Gate, world: IBlockStorage, context: BuildContext) {
        val width = max(minWidth, node.size.x.toInt())
        val height = max(minHeight, node.size.y.toInt())
        // Offset of 1 from both boundaries because the width of the gate spans
        // all available space, including where the walls, floor and ceiling go.
        for (x in 1 until width) {
            for (y in 1 until height) {
                for (z in -clearance..clearance) {
                    world.clearBlock(x, y, z)
                }
            }
        }
        super.build(node, world, context)
    }
}