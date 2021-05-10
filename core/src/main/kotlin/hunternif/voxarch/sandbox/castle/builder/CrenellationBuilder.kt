package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import kotlin.math.ceil

/**
 * Adds castle crenellations above ceiling level.
 * "Merlon" is the rising part, "crenel" is the dip.
 *
 * Sizes are defined using the "natural" convention, see
 * [wiki](https://github.com/Hunternif/VoxelArchitecture/wiki/Definitions).
 */
class CrenellationBuilder(
    private val material: String,
    private val merlonLength: Int = 1,
    private val merlonHeight: Int = 2,
    private val crenelLength: Int = 1,
    private val crenelHeight: Int = 1,
    private val downToGround: Boolean = false
) : Builder<Wall>() {

    override fun build(node: Wall, world: IBlockStorage, context: BuildContext) {
        if (node.transparent) return
        val wallLength = ceil(node.length).toInt()
        val wallHeight = ceil(node.height).toInt()
        // 1. base wall
        for (x in 0 .. wallLength) {
            for (y in 0..wallHeight) {
                val block = context.materials.get(material)
                world.setBlock(x, y, 0, block)
            }
        }
        // 2. crenellation
        var i = 0
        for (x in 0 .. wallLength) {
            if (i < merlonLength) {
                for (y in 1..merlonHeight) {
                    val block = context.materials.get(material)
                    world.setBlock(x, y + wallHeight, 0, block)
                }
            } else {
                for (y in 1..crenelHeight) {
                    val block = context.materials.get(material)
                    world.setBlock(x, y + wallHeight, 0, block)
                }
            }
            i = (i + 1) % (merlonLength + crenelLength)
        }
        // 3. optional foundation
        if (downToGround) {
            for (x in 0 .. wallLength) {
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