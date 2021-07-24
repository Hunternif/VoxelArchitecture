package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Floor
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.PositionTransformer

class FloorFoundationBuilder(
    private val material: String
) : Builder<Floor>() {
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

        // 1. Fill space inside the room, starting from the corner
        transformer.translate(room.start)
        world.fillXZ(room) { x, z ->
            buildDownToGround(x, z, transformer, context)
        }
        // 2. Fill walls too, because at odd room sizes they can be 1 block away
        // Start at parent origin
        transformer.popTransformation()
        room.walls.forEach { wall ->
            line(wall.bottomStart, wall.bottomEnd) { p ->
                buildDownToGround(p.x, p.z, transformer, context)
            }
        }

        transformer.run {
            pushTransformation()
            translate(node.origin)
            rotateY(node.rotationY)
        }
        super.build(node, world, context)
    }

    private fun buildDownToGround(
        x: Double, z: Double,
        transformer: PositionTransformer,
        context: BuildContext
    ) {
        var y = 0.0
        while(true) {
            val b = transformer.getBlock(x, y, z)
            if (b != null && !context.env.shouldBuildThrough(b)) break
            val block = context.materials.get(material)
            transformer.setBlock(x, y, z, block)
            y--
        }
    }
}