package hunternif.voxarch.builder

import hunternif.voxarch.plan.Path
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.PathHugger
import hunternif.voxarch.vector.TransformationStack

/**
 * Fills the path with blocks.
 */
open class SnakePathBuilder(
    private val material: String,
    private val step: Double = 1.0
): APathBuilder() {
    override fun build(node: Path, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val hugger = PathHugger(node, trans, world)
        var x = 0.0
        while (x < node.totalLength) {
            val block = context.materials.get(material)
            hugger.setBlock(x, 0.0, 0.0, block)
            x += step
        }
    }
}