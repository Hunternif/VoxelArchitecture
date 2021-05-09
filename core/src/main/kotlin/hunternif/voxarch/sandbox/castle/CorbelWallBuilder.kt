package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.symmetricSpacing
import kotlin.math.ceil

/**
 * Adds corbels to the top of the wall.
 * Corbels are the protruding slopping pillar-like supports.
 * They support the structure above, where it hangs over the top of the wall.
 *
 * Sizes are defined using the "natural" convention, see
 * [wiki](https://github.com/Hunternif/VoxelArchitecture/wiki/Definitions).
 */
class CorbelWallBuilder(
    private val wallMaterial: String,
    private val corbelMaterial: String = wallMaterial,
    private val corbelWidth: Int = 1,
    private val corbelDepth: Int = 1,
    private val corbelHeight: Int = 2,
    private val corbelMinSpacing: Int = 2,
    private val corbelMaxSpacing: Int = 3,
    /** If true, will space corbels evenly to make the wall symmetric. */
    private val symmetric: Boolean = true
) : Builder<Wall>() {

    override fun build(node: Wall, world: IBlockStorage, context: BuildContext) {
        //TODO: handle corbels at wall corners. It might need to turn 45 degrees.

        if (node.transparent) return
        val wallLength = ceil(node.length).toInt()
        val wallHeight = ceil(node.height).toInt()

        // 1. base wall
        for (x in 0..wallLength) {
            for (y in 0..wallHeight) {
                val block = context.materials.get(wallMaterial)
                world.setBlock(x, y, 0, block)
            }
        }

        // 2. corbels
        if (symmetric) {
            val places = symmetricSpacing(
                wallLength + corbelWidth,
                corbelMinSpacing + corbelWidth,
                corbelMaxSpacing + corbelWidth
            )
            for (x in places) {
                buildCorbel(x, wallHeight, world, context)
            }
        } else {
            for (x in 0..wallLength step corbelMinSpacing) {
                buildCorbel(x, wallHeight, world, context)
            }
        }
        var i = 0
        super.build(node, world, context)
    }

    private fun buildCorbel(startX: Int, topY: Int, world: IBlockStorage, context: BuildContext) {
        for (dx in 0 until corbelWidth) {
            for (dy in 0 until corbelHeight) {
                for (z in 1 .. corbelDepth) {
                    // make it sloping, i.e. closest to the wall at the bottom
                    if (z - 1  > dy * (corbelDepth - 1) / corbelHeight) continue
                    val x = startX + dx
                    val y = topY - corbelHeight + dy
                    val block = context.materials.get(corbelMaterial)
                    world.setBlock(x, y, z, block)
                }
            }
        }
    }
}