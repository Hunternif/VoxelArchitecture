package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import kotlin.math.ceil

/**
 * Adds castle crenellations above ceiling level.
 * "Merlon" is the rising part, "crenel" is the dip.
 */
class CrenellationBuilder(
    private val material: String,
    private val merlonLength: Int = 1,
    private val merlonHeight: Int = 2,
    private val crenelLength: Int = 1,
    private val crenelHeight: Int = 1
) : Builder<Wall>() {

    override fun build(node: Wall, world: IBlockStorage, context: BuildContext) {
        if (node.transparent) return
        val wallLength = ceil(node.length).toInt()
        val wallHeight = ceil(node.height).toInt()
        val block = context.materials.get(material)
        // 1. base wall
        for (x in 0..wallLength) {
            for (y in 0..wallHeight) {
                world.setBlock(x, y, 0, block)
            }
        }
        // 2. crenellation
        var i = 0
        for (x in 0..wallLength) {
            if (i < merlonLength) {
                for (y in 1..merlonHeight) {
                    world.setBlock(x, y + wallHeight, 0, block)
                }
            } else {
                for (y in 1..crenelHeight) {
                    world.setBlock(x, y + wallHeight, 0, block)
                }
            }
            i = (i + 1) % (merlonLength + crenelLength)
        }
        super.build(node, world, context)
    }
}