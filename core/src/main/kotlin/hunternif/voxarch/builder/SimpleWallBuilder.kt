package hunternif.voxarch.builder

import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage

open class SimpleWallBuilder(private val material: String): Builder<Wall>() {
    override fun build(node: Wall, world: IBlockStorage, context: BuildContext) {
        if (node.transparent) return
        val length = node.length.toInt()
        val height = node.height.toInt()
        for (x in 0..length) {
            for (y in 0..height) {
                val block = context.materials.get(material)
                world.setBlock(x, y, 0, block)
            }
        }
        super.build(node, world, context)
    }
}