package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.ILinearTransformation

open class SimpleWallBuilder(
    private val material: String,
    private val downToGround: Boolean = false
): ANodeBuilder() {
    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        val localWorld = world.toLocal(trans)
        val wallLength = node.width.toInt()
        val height = node.height.toInt()
        val depth = node.depth.toInt()
        // 1. base wall
        for (x in 0..wallLength) {
            for (y in 0..height) {
                for (z in 0 .. depth) {
                    val block = context.materials.get(material)
                    localWorld.setBlock(x, y, z, block)
                }
            }
        }
        // 2. optional foundation
        if (downToGround) {
            for (x in 0..wallLength) {
                for (z in 0 .. depth) {
                    var y = -1
                    while (true) {
                        val p = trans.transform(x, y, z)
                        if (p.y < context.env.minY) break
                        val b = world.getBlock(p)
                        if (b != null && !context.env.shouldBuildThrough(b)) break
                        val block = context.materials.get(material)
                        world.setBlock(p, block)
                        y--
                    }
                }
            }
        }
    }
}