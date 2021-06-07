package hunternif.voxarch.util

import hunternif.voxarch.plan.Path
import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.IBlockStorage

/**
 * Transforms coordinates so that for every step along the x axis it will step
 * along the path, thus "hugging" the path.
 */
class PathHugger(
    world: IBlockStorage,
    private val path: Path
) : PositionTransformer(world) {
    override fun setBlock(x: Double, y: Double, z: Double, block: BlockData?) {
        path.mapX(x)?.let { segment ->
            pushTransformation()
            val xOffset = x - segment.distanceFromStart
            val angleY = segmentAngleY(segment.p1, segment.p2)
            translate(segment.p1)
            rotateY(angleY)
            super.setBlock(xOffset, y, z, block)
            popTransformation()
        }
    }
}