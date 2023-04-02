package hunternif.voxarch.builder

import hunternif.voxarch.plan.Gate
import hunternif.voxarch.plan.Hatch
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack

open class RoomBuilder : ARoomBuilder() {
    override fun build(node: Room, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        node.apply {
            clearVolume(world, trans)
            floors.filter { !it.isBuilt }.forEach { buildChild(it, trans, world, context) }
            walls.filter { !it.isBuilt }.forEach { buildChild(it, trans, world, context) }
            children.filter { it !is Gate && it !is Hatch && !it.isBuilt }.forEach { buildChild(it, trans, world, context) }
            // gates and hatches is all that's left, and they must be built last:
            buildChildren(this, trans, world, context)
            isBuilt = true
        }
    }

    companion object {
        private fun Room.clearVolume(world: IBlockStorage, trans: TransformationStack) {
            fillXYZ(trans) { x, y, z ->
                world.clearBlock(x, y, z)
            }
        }
    }
}