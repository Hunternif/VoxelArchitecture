package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.gen.ElementGenerator
import hunternif.voxarch.gen.Environment
import hunternif.voxarch.gen.Materials
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.MathUtil
import hunternif.voxarch.vector.Vec2

/**
 * Generates wall blocks all the way down to the ground
 */
class FloorFoundationGen(
    private val env: Environment
) : ElementGenerator.Floor {

    override fun generateFloor(dest: IBlockStorage, size: Vec2, materials: Materials) {
        val width = MathUtil.ceiling(size.x)
        val length = MathUtil.ceiling(size.y)
        val block = materials.wallBlocks()[0]
        for (x in 0..width) {
            for (z in 0..length) {
                var y = 0
                while(true) {
                    val b = dest.getBlock(x, y, z)
                    if (b != null && b.id !in env.buildThroughBlocks) break
                    dest.setBlock(x, y, z, block)
                    y--
                }
            }
        }
    }
}