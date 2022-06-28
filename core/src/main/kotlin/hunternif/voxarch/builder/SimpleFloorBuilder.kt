package hunternif.voxarch.builder

import hunternif.voxarch.plan.Floor
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack

open class SimpleFloorBuilder(
    private val material: String
): Builder<Floor>() {
    override fun build(node: Floor, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val localWorld = world.toLocal(trans)

        // A floor must have a parent room
        val room = (node.parent as? Room) ?: return

        // Fill space inside the room, starting from the corner
        trans.apply {
            push()
            translate(room.start)
            world.fillXZ(room, trans) { x, z ->
                val block = context.materials.get(material)
                localWorld.setBlock(x, 0.0, z, block)
            }
            pop()
        }

        super.build(node, trans, world, context)
    }
}