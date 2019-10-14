package hunternif.voxarch.builder

import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import kotlin.math.ceil

class SimpleWallBuilder(private val material: String): Builder<Wall>() {
    override fun build(node: Wall, world: IBlockStorage, context: BuildContext) {
        if (node.transparent) return
        val length = ceil(node.length).toInt()
        val height = ceil(node.height).toInt()
        val block = context.materials.get(material)
        for (x in 0..length) {
            for (y in 0..height) {
                world.setBlock(x, y, 0, block)
            }
        }
        super.build(node, world, context)
    }
}