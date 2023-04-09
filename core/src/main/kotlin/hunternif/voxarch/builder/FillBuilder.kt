package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack

open class FillBuilder(
    private val material: String,
) : ANodeBuilder() {
    override fun build(node: Node, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        node.fillXYZ(trans) { x, y, z ->
            val block = context.materials.get(material)
            world.setBlock(x, y, z, block)
        }
    }
}