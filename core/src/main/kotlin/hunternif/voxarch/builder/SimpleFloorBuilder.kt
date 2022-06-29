package hunternif.voxarch.builder

import hunternif.voxarch.plan.Floor
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack

open class SimpleFloorBuilder(
    private val material: String
) : Builder<Floor>() {
    override fun build(node: Floor, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        // A floor must have a parent room
        val room = (node.parent as? Room) ?: return

        // Fill space inside the room
        room.fillXZ(trans) { x, y, z ->
            val block = context.materials.get(material)
            world.setBlock(x, y, z, block)
        }

        super.build(node, trans, world, context)
    }
}