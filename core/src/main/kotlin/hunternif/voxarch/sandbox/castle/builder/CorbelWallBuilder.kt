package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.symmetricSpacing
import kotlin.math.ceil
import kotlin.math.floor

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
    private val spacingMode: SpacingMode = SpacingMode.SYMMETRIC,
    private val downToGround: Boolean = false
) : Builder<Wall>() {

    enum class SpacingMode {
        SYMMETRIC, ROUNDED, LINEAR
    }

    override fun build(node: Wall, world: IBlockStorage, context: BuildContext) {
        if (node.transparent) return
        val wallLength = ceil(node.length).toInt()
        val wallHeight = ceil(node.height).toInt()

        // 1. base wall
        for (x in 0 until wallLength) {
            for (y in 0..wallHeight) {
                val block = context.materials.get(wallMaterial)
                world.setBlock(x, y, 0, block)
            }
        }

        // 2. corbels
        when (spacingMode) {
            SpacingMode.SYMMETRIC -> {
                val places = symmetricSpacing(
                    wallLength + corbelWidth,
                    corbelMinSpacing + corbelWidth,
                    corbelMaxSpacing + corbelWidth
                )
                for (x in places) {
                    buildCorbel(x, wallHeight, world, context)
                }
            }
            SpacingMode.ROUNDED -> {
                val availableLength = (node.length + 1 - corbelWidth)
                val maxFit = floor(availableLength / (corbelMinSpacing + corbelWidth))

                val spacing = availableLength / maxFit

                var x = 0.0
                if (spacing == 0.0) {
                    buildCorbel(0, wallHeight, world, context)
                } else if (spacing > 0.0) {
                    while (x <= node.length) {
                        buildCorbel(x.toInt(), wallHeight, world, context)
                        x += spacing
                    }
                }
            }
            SpacingMode.LINEAR -> {
                for (x in 0 until wallLength step corbelMinSpacing) {
                    buildCorbel(x, wallHeight, world, context)
                }
            }
        }

        // 3. optional foundation
        if (downToGround) {
            for (x in 0 until wallLength) {
                var y = -1
                while(true) {
                    val b = world.getBlock(x, y, 0)
                    if (b != null && !context.env.shouldBuildThrough(b)) break
                    val block = context.materials.get(wallMaterial)
                    world.setBlock(x, y, 0, block)
                    y--
                }
            }
        }
        super.build(node, world, context)
    }

    private fun buildCorbel(startX: Int, topY: Int, world: IBlockStorage, context: BuildContext) {
        for (dx in 0 until corbelWidth) {
            for (dy in 1 .. corbelHeight) {
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