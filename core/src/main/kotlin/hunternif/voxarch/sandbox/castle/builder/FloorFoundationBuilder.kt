package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Floor
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.roundToInt
import hunternif.voxarch.vector.TransformationStack

class FloorFoundationBuilder(
    private val material: String
) : Builder<Floor>() {
    override fun build(node: Floor, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        // A floor must have a parent room
        val room = (node.parent as? Room) ?: return

        // 1. Fill space inside the room
        room.fillXZ(trans) { x, y, z ->
            buildDownToGround(x, y, z, world, context)
        }

        // 2. Fill walls too, because at odd room sizes they can be 1 block away
        room.walls.forEach { wall ->
            line(wall.bottomStart, wall.bottomEnd) { p ->
                val pos = trans.transform(p).roundToInt()
                buildDownToGround(pos.x, pos.y, pos.z, world, context)
            }
        }

        super.build(node, trans, world, context)
    }

    private fun buildDownToGround(
        x: Int, y: Int, z: Int,
        world: IBlockStorage,
        context: BuildContext
    ) {
        var dy = 0
        while(y + dy >= context.env.minY) {
            val b = world.getBlock(x, y + dy, z)
            if (b != null && !context.env.shouldBuildThrough(b)) break
            val block = context.materials.get(material)
            world.setBlock(x, y + dy, z, block)
            dy--
        }
    }
}