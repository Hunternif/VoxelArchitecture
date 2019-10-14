package hunternif.voxarch.builder

import hunternif.voxarch.plan.Gate
import hunternif.voxarch.plan.Hatch
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage

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
        private fun Room.clearVolume(world: IBlockStorage) {
            //TODO use RoomConstrainedStorage with offset?
            val transformer = world.transformer()
            // step by 0.5 in order to prevent gaps when the node is rotated
            var x = 0.0
            while (x <= width) {
                var z = 0.0
                while (z <= length) {
                    for (y in 0..height.toInt()) {
                        transformer.clearBlock(x, y.toDouble(), z)
                    }
                    z += 0.5
                }
                x += 0.5
            }
        }
    }
}