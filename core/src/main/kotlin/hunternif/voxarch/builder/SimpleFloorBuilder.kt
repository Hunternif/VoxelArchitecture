package hunternif.voxarch.builder

import hunternif.voxarch.plan.Floor
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage

open class SimpleFloorBuilder(
    private val material: String
): Builder<Floor>() {
    override fun build(node: Floor, world: IBlockStorage, context: BuildContext) {
        val transformer = world.transformer()
        // A floor will usually have a parent room
        val room = (node.parent as? Room) ?: node
        world.fillXZ(room) { x, z ->
            val block = context.materials.get(material)
            transformer.setBlock(x, 0.0, z, block)
        }
        super.build(node, world, context)
    }
}