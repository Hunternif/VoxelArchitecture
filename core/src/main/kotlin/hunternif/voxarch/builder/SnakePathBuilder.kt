package hunternif.voxarch.builder

import hunternif.voxarch.plan.Path
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.Vec3

/**
 * Fills the path with blocks.
 */
open class SnakePathBuilder(
    private val material: String,
    step: Double = 1.0
): PathBuilder<Path>(step) {
    override fun buildAt(
        point: Vec3,
        node: Path,
        world: IBlockStorage,
        context: BuildContext
    ) {
        val transformer = world.transformer()
        val block = context.materials.get(material)
        transformer.setBlock(point, block)
    }
}