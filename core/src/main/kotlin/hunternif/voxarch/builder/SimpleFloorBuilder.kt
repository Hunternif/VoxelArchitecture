package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.ILinearTransformation

open class SimpleFloorBuilder(
    private val material: String
) : ANodeBuilder() {
    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        // Fill space inside
        node.fillXZ(trans) { x, y, z ->
            val block = context.materials.get(material)
            world.setBlock(x, y, z, block)
        }
    }
}