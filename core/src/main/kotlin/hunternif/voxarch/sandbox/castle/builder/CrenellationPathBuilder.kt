package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.PathHugger
import hunternif.voxarch.vector.ILinearTransformation

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
 * [hunternif.voxarch.plan.Node].
 */
class CrenellationPathBuilder(
    private val material: String,
    private val sizes: CrenellationSizes = CrenellationSizes()
) : APathBuilder() {

    override fun build(node: Path, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        val wallLength = node.totalLength.toInt()
        val hugger = PathHugger(node, trans, world)
        buildCrenellations(0, wallLength, 0, sizes, material, hugger, context)
    }
}

/**
 * Builds wall with crenellations on top (above height level).
 * "Merlon" is the rising part, "crenel" is the dip.
 *
 * Sizes are defined using the "natural" convention, see
 * [hunternif.voxarch.plan.Node].
 */
class CrenellationWallBuilder(
    private val material: String,
    private val sizes: CrenellationSizes = CrenellationSizes(),
    downToGround: Boolean = false
) : SimpleWallBuilder(material, downToGround) {

    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        super.build(node, trans, world, context)
        val height = node.height.toInt()
        val wallLength = node.width.toInt()
        val localWorld = world.toLocal(trans)
        buildCrenellations(0, wallLength, height+1, sizes, material, localWorld, context)
    }
}

private fun buildCrenellations(
    fromX: Int,
    toX: Int,
    yOffset: Int,
    sizes: CrenellationSizes,
    material: String,
    localWorld: IBlockStorage,
    context: BuildContext
) {
    var i = 0
    for (x in fromX .. toX) {
        if (i < sizes.merlonLength) {
            for (y in 0 until sizes.merlonHeight) {
                val block = context.materials.get(material)
                localWorld.setBlock(x, y + yOffset, 0, block)
            }
        } else {
            for (y in 0 until sizes.crenelHeight) {
                val block = context.materials.get(material)
                localWorld.setBlock(x, y + yOffset, 0, block)
            }
        }
        i = (i + 1) % (sizes.merlonLength + sizes.crenelLength)
    }
}
