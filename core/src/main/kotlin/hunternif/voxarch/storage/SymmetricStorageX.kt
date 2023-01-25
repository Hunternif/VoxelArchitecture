package hunternif.voxarch.storage

import kotlin.math.abs


/**
 * Convenience class that sets blocks symmetrically on the X axis.
 */
class SymmetricStorageX(
    private val storage: IBlockStorage,
    midPoint: Number,
) : IBlockStorage {
    private val midPoint: Double = midPoint.toDouble()

    override fun getBlock(x: Int, y: Int, z: Int): BlockData? =
        storage.getBlock(x, y, z)

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockData?) {
        this.setBlock(x.toDouble(), y.toDouble(), z.toDouble(), block)
    }

    override fun setBlock(x: Double, y: Double, z: Double, block: BlockData?) {
        storage.setBlock(x, y, z, block)
        if (abs(x - midPoint) > 0.01) {
            val mirrorX = midPoint * 2 - x
            storage.setBlock(mirrorX, y, z, block)
        }
    }

    override fun clearBlock(x: Int, y: Int, z: Int) {
        this.clearBlock(x.toDouble(), y.toDouble(), z.toDouble())
    }

    override fun clearBlock(x: Double, y: Double, z: Double) {
        storage.clearBlock(x, y, z)
        if (abs(x - midPoint) > 0.01) {
            val mirrorX = midPoint * 2 - x
            storage.clearBlock(mirrorX, y, z)
        }
    }
}
