package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.naturalHeight
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.PerlinNoise
import hunternif.voxarch.vector.ILinearTransformation
import kotlin.math.roundToInt

class PerlinNoiseBuilder(
    private val material: String,
    /** Will divide coordinates by this value.
     * This is needed because Perlin noise returns 0 at integer grid points. */
    private val divisor: Double = 16.0,
) : ANodeBuilder() {
    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        node.fillXZ(trans) { x, y, z ->
            val noise = PerlinNoise.noise(x / divisor, y / divisor, z / divisor)
            val dY = (noise + 1.0) / 2.0 * node.naturalHeight
            val block = context.materials.get(material)
            world.setBlock(x, (y + dY).roundToInt(), z, block)
        }
        super.build(node, trans, world, context)
    }
}