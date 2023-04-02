package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack

open class FillBuilder(
    private val material: String,
) : ANodeBuilder() {
    override fun build(node: Node, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val block = context.materials.get(material)
        node.fillXYZ(trans) { x, y, z ->
            world.setBlock(x, y, z, block)
        }
        super.build(node, trans, world, context)
    }
}