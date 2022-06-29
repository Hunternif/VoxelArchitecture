package hunternif.voxarch.storage

import hunternif.voxarch.util.intRoundDown
import hunternif.voxarch.vector.ILinearTransformation

/**
 * Convenience class that applies a coordinate transformation
 * before querying storage.
 */
open class TransformedBlockStorage(
    private val storage: IBlockStorage,
    private val trans: ILinearTransformation,
) : IBlockStorage {
    override fun getBlock(x: Int, y: Int, z: Int): BlockData? =
        this.getBlock(x.toDouble(), y.toDouble(), z.toDouble())

    fun getBlock(x: Double, y: Double, z: Double): BlockData? {
        val pos = trans.transform(x, y, z).intRoundDown()
        return storage.getBlock(pos)
    }

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockData?) {
        val rotatedBlock = block?.let {
            it.clone().apply {
                rotate(trans.angleY)
            }
        }
        this.setBlock(x.toDouble(), y.toDouble(), z.toDouble(), rotatedBlock)
    }

    fun setBlock(x: Double, y: Double, z: Double, block: BlockData?) {
        val pos = trans.transform(x, y, z).intRoundDown()
        storage.setBlock(pos, block)
    }

    override fun clearBlock(x: Int, y: Int, z: Int) {
        val pos = trans.transform(x, y, z).intRoundDown()
        storage.clearBlock(pos)
    }
}