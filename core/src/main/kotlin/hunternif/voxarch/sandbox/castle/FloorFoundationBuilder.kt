package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.gen.Environment
import hunternif.voxarch.plan.Floor
import hunternif.voxarch.storage.IBlockStorage
import kotlin.math.ceil

class FloorFoundationBuilder(
    private val material: String,
    private val env: Environment
) : Builder<Floor>() {

    override fun build(node: Floor, world: IBlockStorage, context: BuildContext) {
        val transformer = world.transformer()
        transformer.setCloseGaps(true)
        val width = ceil(node.width).toInt()
        val length = ceil(node.length).toInt()
        val block = context.materials.get(material)
        for (x in 0..width) {
            for (z in 0..length) {
                var y = 0
                while(true) {
                    val b = transformer.getBlock(x, y, z)
                    if (b != null && b.id !in env.buildThroughBlocks) break
                    transformer.setBlock(x, y, z, block)
                    y--
                }
            }
        }
        transformer.setCloseGaps(false)
        super.build(node, world, context)
    }
}