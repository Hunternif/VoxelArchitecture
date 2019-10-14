package hunternif.voxarch.builder

import hunternif.voxarch.plan.Floor
import hunternif.voxarch.storage.IBlockStorage
import kotlin.math.ceil

class SimpleFloorBuilder(private val material: String): Builder<Floor>() {
    override fun build(node: Floor, world: IBlockStorage, context: BuildContext) {
        val transformer = world.transformer()
        transformer.setCloseGaps(true)
        val width = ceil(node.width).toInt()
        val length = ceil(node.length).toInt()
        val block = context.materials.get(material)
        for (x in 0..width) {
            for (z in 0..length) {
                transformer.setBlock(x, 0, z, block)
            }
        }
        transformer.setCloseGaps(false)
        super.build(node, world, context)
    }
}