package hunternif.voxarch.sandbox

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.builder.MaterialConfig
import hunternif.voxarch.plan.Prop
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.Direction

class TorchStandBuilder : Builder<Prop>() {
    override fun build(node: Prop, world: IBlockStorage, context: BuildContext) {
        val post = context.materials.get(MaterialConfig.POST)
        val block = context.materials.get(MaterialConfig.WALL)
        val torch = context.materials.get(MaterialConfig.TORCH)

        world.apply {
            setBlock(0, 0, 0, post)
            setBlock(0, 1, 0, post)
            setBlock(0, 2, 0, block)

            torch.orientation = Direction.EAST
            setBlock(1, 2, 0, torch)

            torch.rotate(90.0)
            setBlock(0, 2, -1, torch)

            torch.rotate(90.0)
            setBlock(-1, 2, 0, torch)

            torch.rotate(90.0)
            setBlock(0, 2, 1, torch)
        }
    }
}