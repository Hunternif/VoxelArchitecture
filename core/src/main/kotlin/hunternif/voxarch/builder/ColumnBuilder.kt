package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.ILinearTransformation

open class ColumnBuilder(
    private val material: String,
) : ANodeBuilder() {
    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        if (node.width == 0.0 && node.depth == 0.0) {
            // Single block column
            val localWorld = world.toLocal(trans)
            val x = node.start.x
            val y = node.start.y
            val z = node.start.z
            for (dy in 0..node.height.toInt()) {
                val block = context.materials.get(material)
                localWorld.setBlock(x, y + dy, z, block)
            }
        } else {
            // Fill area
            node.fillXYZ(trans) { x, y, z ->
                val block = context.materials.get(material)
                world.setBlock(x, y, z, block)
            }
        }
    }
}