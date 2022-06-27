package hunternif.voxarch.util

import hunternif.voxarch.plan.Path
import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack

/**
 * Transforms coordinates so that for every step along the x axis it will step
 * along the path, thus "hugging" the path.
 */
class PathHugger(
    private val path: Path,
    private val trans: TransformationStack,
    private val world: IBlockStorage
) : IBlockStorage {

    override fun getBlock(x: Int, y: Int, z: Int): BlockData? {
        val pos = trans.transform(x, y, z).intRoundDown()
        return world.getBlock(pos)
    }

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockData?) {
        this.setBlock(x.toDouble(), y.toDouble(), z.toDouble(), block)
    }

    fun setBlock(x: Double, y: Double, z: Double, block: BlockData?) {
        path.mapX(x)?.let { segment ->
            trans.apply {
                push()
                val xOffset = x - segment.distanceFromStart
                val angleY = segmentAngleY(segment.p1, segment.p2)
                translate(segment.p1)
                rotateY(angleY)
                val pos = trans.transform(xOffset, y, z).intRoundDown()
                world.setBlock(pos, block)
                pop()
            }
        }
    }

    override fun clearBlock(x: Int, y: Int, z: Int) {
        val pos = trans.transform(x, y, z).intRoundDown()
        world.clearBlock(pos)
    }
}