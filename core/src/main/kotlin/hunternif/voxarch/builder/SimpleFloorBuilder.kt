package hunternif.voxarch.builder

import hunternif.voxarch.plan.Floor
import hunternif.voxarch.storage.IBlockStorage

class SimpleFloorBuilder(
    private val material: String,
    private val margin: Double = 0.25
): Builder<Floor>() {
    override fun build(node: Floor, world: IBlockStorage, context: BuildContext) {
        val transformer = world.transformer()
        // step by 0.5 in order to prevent gaps when the node is rotated.
        // extra margin on the edges is to prevent building outside walls.
        var x = margin
        while (x <= node.width - margin) {
            var z = margin
            while (z <= node.length - margin) {
                val block = context.materials.get(material)
                transformer.setBlock(x, 0.0, z, block)
                z += 0.5
            }
            x += 0.5
        }
        super.build(node, world, context)
    }
}