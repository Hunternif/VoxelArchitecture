package hunternif.voxarch.builder

import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack

open class SimpleWallBuilder(
    private val material: String,
    private val downToGround: Boolean = false
): Builder<Wall>() {
    override fun build(node: Wall, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        if (node.transparent) return
        val height = node.height.toInt()
        // (0, 0, 0) is wall origin, which is also the starting point.
        // We are rotated so that the  wall runs along the X axis.
        val p1 = trans.transform(0, 0, 0)
        val p2 = trans.transform(node.length, 0, 0)
        line2(p1, p2) { p ->
            // 1. base wall
            for (dy in 0..height) {
                val block = context.materials.get(material)
                world.setBlock(p.x, p.y + dy, p.z, block)
            }
            // 2. optional foundation
            if (downToGround) {
                var dy = -1
                while (true) {
                    val b = world.getBlock(p.x, p.y + dy, p.z)
                    if (b != null && !context.env.shouldBuildThrough(b)) break
                    val block = context.materials.get(material)
                    world.setBlock(p.x, p.y + dy, p.z, block)
                    dy--
                }
            }
        }
        super.build(node, trans, world, context)
    }
}