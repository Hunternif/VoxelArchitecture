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

        val usingParent = room === node.parent
        if (usingParent) {
            // Discard current transform so that we are at parent origin
            transformer.run {
                popTransformation()
                pushTransformation()
                translate(0.0, node.origin.y, 0.0)
            }
        }

        // Fill space inside the room, starting from the corner
        transformer.translate(room.start)
        world.fillXZ(room) { x, z ->
            val block = context.materials.get(material)
            transformer.setBlock(x, 0.0, z, block)
        }

        if (usingParent) {
            transformer.run {
                translate(node.origin)
                rotateY(node.rotationY)
            }
        }
        super.build(node, world, context)
    }
}