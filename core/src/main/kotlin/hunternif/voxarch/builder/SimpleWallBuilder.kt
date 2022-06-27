package hunternif.voxarch.builder

import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.intRoundDown
import hunternif.voxarch.vector.TransformationStack

open class SimpleWallBuilder(
    private val material: String,
    private val downToGround: Boolean = false
): Builder<Wall>() {
    override fun build(node: Wall, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        if (node.transparent) return
        val length = node.length.toInt()
        val height = node.height.toInt()
        // 1. base wall
        for (x in 0..length) {
            for (y in 0..height) {
                val block = context.materials.get(material)
                val pos = trans.transform(x, y, 0).intRoundDown()
                world.setBlock(pos, block)
            }
        }
        // 2. optional foundation
        if (downToGround) {
            for (x in 0 .. length) {
                var y = -1
                while(true) {
                    val pos = trans.transform(x, y, 0).intRoundDown()
                    val b = world.getBlock(pos)
                    if (b != null && !context.env.shouldBuildThrough(b)) break
                    val block = context.materials.get(material)
                    world.setBlock(pos, block)
                    y--
                }
            }
        }
        super.build(node, trans, world, context)
    }
}