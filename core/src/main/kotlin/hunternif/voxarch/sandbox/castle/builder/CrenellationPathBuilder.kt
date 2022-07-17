package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.PathHugger
import hunternif.voxarch.vector.TransformationStack

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

    override fun build(node: Path, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val wallLength = node.totalLength
        val hugger = PathHugger(node, trans, world)
        buildCrenellations(wallLength, 0, sizes, material, trans, hugger, context)
        super.build(node, trans, world, context)
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

    override fun build(node: Wall, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        if (node.transparent) return
        super.build(node, trans, world, context)
        val height = node.height.toInt()
        buildCrenellations(node.length, height+1, sizes, material, trans, world, context)
    }
}

private fun buildCrenellations(
    toX: Double,
    yOffset: Int,
    sizes: CrenellationSizes,
    material: String,
    trans: TransformationStack,
    world: IBlockStorage,
    context: BuildContext
) {
    var i = 0
    // We are rotated so that the  wall runs along the X axis.
    val p1 = trans.transform(0, 0, 0)
    val p2 = trans.transform(toX, 0, 0)
    line2(p1, p2) { p ->
        if (i < sizes.merlonLength) {
            for (dy in 0 until sizes.merlonHeight) {
                val block = context.materials.get(material)
                world.setBlock(p.x, p.y + dy + yOffset, p.z, block)
            }
        } else {
            for (dy in 0 until sizes.crenelHeight) {
                val block = context.materials.get(material)
                world.setBlock(p.x, p.y + dy + yOffset, p.z, block)
            }
        }
        i = (i + 1) % (sizes.merlonLength + sizes.crenelLength)
    }
}
