package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.world.Environment
import hunternif.voxarch.plan.Floor
import hunternif.voxarch.storage.IBlockStorage

class FloorFoundationBuilder(
    private val material: String
) : Builder<Floor>() {

    override fun build(node: Floor, world: IBlockStorage, context: BuildContext) {
        val transformer = world.transformer()
        // step by 0.5 in order to prevent gaps when the node is rotated
        var x = 0.0
        while (x <= node.width) {
            var z = 0.0
            while (z <= node.length) {
                var y = 0.0
                while(true) {
                    val b = transformer.getBlock(x, y, z)
                    if (b != null && !context.env.shouldBuildThrough(b)) break
                    val block = context.materials.get(material)
                    transformer.setBlock(x, y, z, block)
                    y--
                }
                z += 0.5
            }
            x += 0.5
        }
        super.build(node, world, context)
    }
}