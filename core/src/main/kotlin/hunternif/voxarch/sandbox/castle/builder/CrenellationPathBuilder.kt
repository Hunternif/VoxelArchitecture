package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.builder.SimpleWallBuilder
import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.PathHugger
import kotlin.math.ceil

data class CrenellationSizes(
    val merlonLength: Int = 1,
    val merlonHeight: Int = 2,
    val crenelLength: Int = 1,
    val crenelHeight: Int = 1
)

/**
 * Builds castle crenellations.
 * "Merlon" is the rising part, "crenel" is the dip.
 *
 * Sizes are defined using the "natural" convention, see
 * [wiki](https://github.com/Hunternif/VoxelArchitecture/wiki/Definitions).
 */
class CrenellationPathBuilder(
    private val material: String,
    private val sizes: CrenellationSizes = CrenellationSizes()
) : Builder<Path>() {

    override fun build(node: Path, world: IBlockStorage, context: BuildContext) {
        val wallLength = node.totalLength.toInt()
        val hugger = PathHugger(world, node)
        buildCrenellations(0, wallLength, 0, sizes, material, hugger, context)
        super.build(node, world, context)
    }
}

/**
 * Builds wall with crenellations on top (above height level).
 * "Merlon" is the rising part, "crenel" is the dip.
 *
 * Sizes are defined using the "natural" convention, see
 * [wiki](https://github.com/Hunternif/VoxelArchitecture/wiki/Definitions).
 */
class CrenellationWallBuilder(
    private val material: String,
    private val sizes: CrenellationSizes = CrenellationSizes(),
    downToGround: Boolean = false
) : SimpleWallBuilder(material, downToGround) {

    override fun build(node: Wall, world: IBlockStorage, context: BuildContext) {
        if (node.transparent) return
        super.build(node, world, context)
        val height = node.height.toInt()
        val length = node.length.toInt()
        buildCrenellations(0, length, height+1, sizes, material, world, context)
    }
}

private fun buildCrenellations(
    fromX: Int,
    toX: Int,
    yOffset: Int,
    sizes: CrenellationSizes,
    material: String,
    world: IBlockStorage,
    context: BuildContext
) {
    var i = 0
    for (x in fromX .. toX) {
        if (i < sizes.merlonLength) {
            for (y in 0 until sizes.merlonHeight) {
                val block = context.materials.get(material)
                world.setBlock(x, y + yOffset, 0, block)
            }
        } else {
            for (y in 0 until sizes.crenelHeight) {
                val block = context.materials.get(material)
                world.setBlock(x, y + yOffset, 0, block)
            }
        }
        i = (i + 1) % (sizes.merlonLength + sizes.crenelLength)
    }
}
