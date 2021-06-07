package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.PathHugger
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
    private val crenelHeight: Int = 1
) : Builder<Path>() {

    override fun build(node: Path, world: IBlockStorage, context: BuildContext) {
        val wallLength = ceil(node.totalLength).toInt()
        val hugger = PathHugger(world, node)

        var i = 0
        for (x in 0 .. wallLength) {
            if (i < merlonLength) {
                for (y in 0 until merlonHeight) {
                    val block = context.materials.get(material)
                    hugger.setBlock(x, y, 0, block)
                }
            } else {
                for (y in 0 until crenelHeight) {
                    val block = context.materials.get(material)
                    hugger.setBlock(x, y, 0, block)
                }
            }
            i = (i + 1) % (merlonLength + crenelLength)
        }

        super.build(node, world, context)
    }
}