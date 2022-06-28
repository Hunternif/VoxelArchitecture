package hunternif.voxarch.util

import hunternif.voxarch.builder.toLocal
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
    world: IBlockStorage
) : IBlockStorage {
    private val localWorld = world.toLocal(trans)

    override fun getBlock(x: Int, y: Int, z: Int): BlockData? {
        return localWorld.getBlock(x, y, z)
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
                localWorld.setBlock(xOffset, y, z, block)
                pop()
            }
        }
    }

    override fun clearBlock(x: Int, y: Int, z: Int) {
        localWorld.clearBlock(x, y, z)
    }
}