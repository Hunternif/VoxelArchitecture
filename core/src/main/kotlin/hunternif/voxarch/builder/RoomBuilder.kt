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
            val transformer = world.transformer()
            transformer.pushTransformation()
            transformer.translate(start)
            transformer.fillXZ(
                this,
                rotationOffset = 0.1,
                rotationMargin = 0.5
            ) { x, z ->
                for (y in 0..height.toInt()) {
                    transformer.clearBlock(x, y.toDouble(), z)
                }
            }
            transformer.popTransformation()
        }
    }
}