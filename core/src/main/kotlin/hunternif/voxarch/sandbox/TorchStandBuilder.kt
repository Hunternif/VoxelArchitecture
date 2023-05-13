package hunternif.voxarch.sandbox

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.Direction.*
import hunternif.voxarch.vector.ILinearTransformation

class TorchStandBuilder : ANodeBuilder() {
    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        val post = context.materials.get(MAT_POST)
        val block = context.materials.get(MAT_WALL)
        val torch = context.materials.get(MAT_TORCH)

        world.toLocal(trans).apply {
            setBlock(0, 0, 0, post)
            setBlock(0, 1, 0, post)
            setBlock(0, 2, 0, block)

            setBlock(1, 2, 0, torch.orient(EAST))
            setBlock(0, 2, -1, torch.orient(NORTH))
            setBlock(-1, 2, 0, torch.orient(WEST))
            setBlock(0, 2, 1, torch.orient(SOUTH))
        }
    }
}