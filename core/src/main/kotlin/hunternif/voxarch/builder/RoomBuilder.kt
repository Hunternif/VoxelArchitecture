package hunternif.voxarch.builder

import hunternif.voxarch.plan.Gate
import hunternif.voxarch.plan.Hatch
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.RoomConstrainedStorage

open class RoomBuilder : Builder<Room>() {
    override fun build(node: Room, world: IBlockStorage, context: BuildContext) {
        node.apply {
            clearVolume(world)
            floors.filter { !it.isBuilt }.forEach { buildChild(it, world, context) }
            walls.filter { !it.isBuilt }.forEach { buildChild(it, world, context) }
            children.filter { it !is Gate && it !is Hatch && !it.isBuilt }.forEach { buildChild(it, world, context) }
            // gates and hatches is all that's left, and they must be built last:
            buildChildren(this, world, context)
            isBuilt = true
        }
    }

    companion object {
        private const val margin = 0.5

        private fun Room.clearVolume(world: IBlockStorage) {
            val transformer = world.transformer()
            transformer.pushTransformation()
            transformer.translate(start)

            val constraint = RoomConstrainedStorage(transformer, this)
            // Set offset so that blocks are not accidentally removed outside the room
            // due to aliasing from rotation:
            constraint.setOffset(0.1)

            // step by 0.5 in order to prevent gaps when the node is rotated.
            // extra margin on the edges is to prevent building outside walls.
            var x = margin
            while (x <= width - margin) {
                var z = margin
                while (z <= length - margin) {
                    for (y in 0..height.toInt()) {
                        if (constraint.isWithinRoom(x, y.toDouble(), z))
                            transformer.clearBlock(x, y.toDouble(), z)
                    }
                    z += 0.5
                }
                x += 0.5
            }
            transformer.popTransformation()
        }
    }
}