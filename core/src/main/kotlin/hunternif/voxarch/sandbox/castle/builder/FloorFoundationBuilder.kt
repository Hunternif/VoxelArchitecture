package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Floor
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.storage.TransformedBlockStorage
import hunternif.voxarch.vector.TransformationStack

class FloorFoundationBuilder(
    private val material: String
) : Builder<Floor>() {
    override fun build(node: Floor, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        // A floor must have a parent room
        val room = (node.parent as? Room) ?: return
        val localWorld = world.toLocal(trans)

        // 1. Fill space inside the room, starting from the corner
        trans.apply {
            push()
            translate(room.start)
            world.fillXZ(room, trans) { x, z ->
                buildDownToGround(x, z, localWorld, context)
            }
            pop()
        }

        // 2. Fill walls too, because at odd room sizes they can be 1 block away
        room.walls.forEach { wall ->
            line(wall.bottomStart, wall.bottomEnd) { p ->
                buildDownToGround(p.x, p.z, localWorld, context)
            }
        }

        super.build(node, trans, world, context)
    }

    private fun buildDownToGround(
        x: Double, z: Double,
        localWorld: TransformedBlockStorage,
        context: BuildContext
    ) {
        var y = 0.0
        while(true) {
            val b = localWorld.getBlock(x, y, z)
            if (b != null && !context.env.shouldBuildThrough(b)) break
            val block = context.materials.get(material)
            localWorld.setBlock(x, y, z, block)
            y--
        }
    }
}