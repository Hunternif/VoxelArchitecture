package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.builder.fillXZ
import hunternif.voxarch.builder.transformer
import hunternif.voxarch.plan.Floor
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage

class FloorFoundationBuilder(
    private val material: String
) : Builder<Floor>() {
    override fun build(node: Floor, world: IBlockStorage, context: BuildContext) {
        val transformer = world.transformer()
        // A floor will usually have a parent room
        val room = (node.parent as? Room) ?: node
        world.fillXZ(room) { x, z ->
            var y = 0.0
            while(true) {
                val b = transformer.getBlock(x, y, z)
                if (b != null && !context.env.shouldBuildThrough(b)) break
                val block = context.materials.get(material)
                transformer.setBlock(x, y, z, block)
                y--
            }
        }
        super.build(node, world, context)
    }
}