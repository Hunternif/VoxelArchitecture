package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.ILinearTransformation

/** Fills room volume with empty blocks */
open class RoomBuilder : ANodeBuilder() {
    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        node.fillXYZ(trans) { x, y, z ->
            world.clearBlock(x, y, z)
        }
    }
}