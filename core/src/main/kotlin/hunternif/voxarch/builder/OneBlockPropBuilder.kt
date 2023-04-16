package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.ILinearTransformation

class OneBlockPropBuilder(private val material: String) : ANodeBuilder() {

    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        val block = context.materials.get(material)
        node.origin.apply {
            world.toLocal(trans).setBlock(x, y, z, block)
        }
    }
}