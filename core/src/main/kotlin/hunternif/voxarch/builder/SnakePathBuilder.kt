package hunternif.voxarch.builder

import hunternif.voxarch.plan.Path
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.PathHugger
import hunternif.voxarch.util.intRoundDown
import hunternif.voxarch.vector.TransformationStack

/**
 * Fills the path with blocks.
 */
open class SnakePathBuilder(
    private val material: String,
    private val step: Double = 1.0
): Builder<Path>() {
    override fun build(node: Path, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val hugger = PathHugger(node, trans, world)
        var x = 0.0
        while (x < node.totalLength) {
            val block = context.materials.get(material)
            val pos = trans.transform(x, 0.0, 0.0).intRoundDown()
            hugger.setBlock(pos, block)
            x += step
        }
    }
}