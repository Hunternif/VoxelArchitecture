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
        // 1. Fill space inside the room
        world.fillXZ(room) { x, z ->
            buildDownToGround(x, z, transformer, context)
        }
        // 2. Fill walls too, because at odd room sizes they can be 1 block away
        if (usingParent) {
            transformer.pushTransformation()
            transformer.translate(-node.origin)
        }
        room.walls.forEach { wall ->
            line(wall.bottomStart, wall.bottomEnd) { p ->
                buildDownToGround(p.x, p.z, transformer, context)
            }
        }
        if (usingParent) {
            transformer.popTransformation()
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