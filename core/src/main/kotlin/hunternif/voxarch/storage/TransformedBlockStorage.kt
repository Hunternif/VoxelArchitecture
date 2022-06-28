package hunternif.voxarch.storage

import hunternif.voxarch.util.intRoundDown
import hunternif.voxarch.vector.ITransformation

/**
 * Convenience class that applies a coordinate transformation
 * before querying storage.
 */
open class TransformedBlockStorage(
    private val storage: IBlockStorage,
    private val trans: ITransformation,
) : IBlockStorage {
    override fun getBlock(x: Int, y: Int, z: Int): BlockData? =
        this.getBlock(x.toDouble(), y.toDouble(), z.toDouble())

    fun getBlock(x: Double, y: Double, z: Double): BlockData? {
        val pos = trans.transform(x, y, z).intRoundDown()
        return storage.getBlock(pos)
    }

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockData?) {
        this.setBlock(x.toDouble(), y.toDouble(), z.toDouble(), block)
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