package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.plan.Path
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.PathHugger
import hunternif.voxarch.util.symmetricSpacing
import hunternif.voxarch.vector.TransformationStack
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
class CorbelBuilder(
    private val material: String,
    private val corbelWidth: Int = 1,
    private val corbelDepth: Int = 1,
    private val corbelHeight: Int = 2,
    private val corbelMinSpacing: Int = 2,
    private val corbelMaxSpacing: Int = 3,
    private val spacingMode: SpacingMode = SpacingMode.SYMMETRIC
) : Builder<Path>() {

    enum class SpacingMode {
        SYMMETRIC, ROUNDED, LINEAR
    }

    override fun build(node: Path, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val wallLength = ceil(node.totalLength).toInt()
        val hugger = PathHugger(node, trans, world)

        when (spacingMode) {
            SpacingMode.SYMMETRIC -> {
                val places = symmetricSpacing(
                    wallLength + corbelWidth,
                    corbelMinSpacing + corbelWidth,
                    corbelMaxSpacing + corbelWidth
                )
                for (x in places) {
                    buildCorbel(x, 0, hugger, context)
                }
            }
            SpacingMode.ROUNDED -> {
                val availableLength = (node.totalLength + 1 - corbelWidth)
                val maxFit = floor(availableLength / (corbelMinSpacing + corbelWidth))

                val spacing = availableLength / maxFit

                var x = 0.0
                if (spacing == 0.0) {
                    buildCorbel(0, 0, hugger, context)
                } else if (spacing > 0.0) {
                    while (x <= node.totalLength) {
                        buildCorbel(x.toInt(), 0, hugger, context)
                        x += spacing
                    }
                }
            }
            SpacingMode.LINEAR -> {
                for (x in 0 until wallLength step corbelMinSpacing) {
                    buildCorbel(x, 0, hugger, context)
                }
            }
        }

        super.build(node, trans, world, context)
    }

    private fun buildCorbel(
        startX: Int,
        topY: Int,
        world: IBlockStorage,
        context: BuildContext
    ) {
        for (dx in 0 until corbelWidth) {
            for (dy in 1..corbelHeight) {
                for (z in 1..corbelDepth) {
                    // make it sloping, i.e. closest to the wall at the bottom
                    if (z - 1 > dy * (corbelDepth - 1) / corbelHeight) continue
                    val x = startX + dx
                    val y = topY - corbelHeight + dy
                    val block = context.materials.get(material)
                    world.setBlock(x, y, z, block)
                }
            }
        }
    }
}