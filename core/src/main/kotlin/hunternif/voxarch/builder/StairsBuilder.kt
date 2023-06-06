package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.step
import hunternif.voxarch.vector.ILinearTransformation

/**
 * Builds a staircase as described in [hunternif.voxarch.plan.Staircase]
 */
class StairsBuilder(
    private val material: String,
    private val downToFloor: Boolean = true,
) : ANodeBuilder() {
    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        val localWorld = world.toLocal(trans)
        val lowXY = node.start.clone().apply { z = 0.0 }
        val highXY = (node.start + node.size).apply { z = 0.0 }
        //TODO: use fill to close holes in rotated nodes
        line2(lowXY, highXY) { p ->
            for (dz in 0.0 .. node.depth step 1) {
                val minY = if (downToFloor) lowXY.y else p.y
                for (y in minY .. p.y step 1) {
                    val block = context.materials.get(material)
                    localWorld.setBlock(p.x, y, p.z + dz, block)
                }
            }
        }
    }
}