package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.gen.ElementGenerator
import hunternif.voxarch.gen.Materials
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.MathUtil

/**
 * Adds castle crenellations above ceiling level.
 * "Merlon" is the rising part, "crenel" is the dip.
 */
class CrenellationGen(
    private val merlonLength: Int = 1,
    private val merlonHeight: Int = 2,
    private val crenelLength: Int = 1,
    private val crenelHeight: Int = 1

) : ElementGenerator.Wall {

    override fun generateWall(dest: IBlockStorage, wall: Wall, materials: Materials) {
        val wallLength = MathUtil.ceiling(wall.length)
        val wallHeight = MathUtil.ceiling(wall.height)
        val block = materials.wallBlocks()[0]
        // 1. base wall
        for (x in 0..wallLength) {
            for (y in 0..wallHeight) {
                dest.setBlock(x, y, 0, block)
            }
        }
        // 2. crenellation
        var i = 0
        for (x in 0..wallLength) {
            if (i < merlonLength) {
                for (y in 1..merlonHeight) {
                    dest.setBlock(x, y + wallHeight, 0, block)
                }
            } else {
                for (y in 1..crenelHeight) {
                    dest.setBlock(x, y + wallHeight, 0, block)
                }
            }
            i = (i + 1) % (merlonLength + crenelLength)
        }
    }

}
