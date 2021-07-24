package hunternif.voxarch.builder

import hunternif.voxarch.plan.Floor
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage

open class SimpleFloorBuilder(
    private val material: String
): Builder<Floor>() {
    override fun build(node: Floor, world: IBlockStorage, context: BuildContext) {
        val transformer = world.transformer()

        // A floor must have a parent room
        val room = (node.parent as? Room) ?: return

        // Fill space inside the room, starting from the corner
        transformer.translate(room.start)
        world.fillXZ(room) { x, z ->
            val block = context.materials.get(material)
            transformer.setBlock(x, 0.0, z, block)
        }
        transformer.translate(-room.start)

        super.build(node, world, context)
    }
}