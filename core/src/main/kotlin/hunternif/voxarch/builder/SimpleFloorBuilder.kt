package hunternif.voxarch.builder

import hunternif.voxarch.plan.Floor
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.RoomConstrainedStorage

class SimpleFloorBuilder(
    private val material: String,
    /** Step by 0.5 in order to prevent gaps when the node is rotated. */
    private val step: Double = 0.5,
    /** Extra margin on the edges is to prevent building outside walls. */
    private val margin: Double = 0.25
): Builder<Floor>() {
    override fun build(node: Floor, world: IBlockStorage, context: BuildContext) {
        val transformer = world.transformer()
        val constraint = if (node is Floor.RoomBound) {
            RoomConstrainedStorage(world, node.room)
        } else null

        var x = margin
        while (x <= node.width - margin) {
            var z = margin
            while (z <= node.length - margin) {
                if (constraint?.isWithinRoom(x, 0.0, z) != false) {
                    val block = context.materials.get(material)
                    transformer.setBlock(x, 0.0, z, block)
                }
                z += step
            }
            x += step
        }
        super.build(node, world, context)
    }
}