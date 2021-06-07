package hunternif.voxarch.builder

import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage

open class SimpleWallBuilder(
    private val material: String,
    private val downToGround: Boolean = false
): Builder<Wall>() {
    override fun build(node: Wall, world: IBlockStorage, context: BuildContext) {
        if (node.transparent) return
        val length = node.length.toInt()
        val height = node.height.toInt()
        // 1. base wall
        for (x in 0..length) {
            for (y in 0..height) {
                val block = context.materials.get(material)
                world.setBlock(x, y, 0, block)
            }
        }
        // 2. optional foundation
        if (downToGround) {
            for (x in 0 .. length) {
                var y = -1
                while(true) {
                    val b = world.getBlock(x, y, 0)
                    if (b != null && !context.env.shouldBuildThrough(b)) break
                    val block = context.materials.get(material)
                    world.setBlock(x, y, 0, block)
                    y--
                }
            }
        }
        super.build(node, world, context)
    }
}