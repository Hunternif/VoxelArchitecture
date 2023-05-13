package hunternif.voxarch.storage

import hunternif.voxarch.util.round
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

    override fun getBlock(x: Double, y: Double, z: Double): BlockData? {
        val pos = trans.transform(x, y, z).round()
        return storage.getBlock(pos)
    }

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockData?) {
        val rotatedBlock = block?.rotate(trans.angleY)
        this.setBlock(x.toDouble(), y.toDouble(), z.toDouble(), rotatedBlock)
    }

    override fun setBlock(x: Double, y: Double, z: Double, block: BlockData?) {
        val pos = trans.transform(x, y, z).round()
        storage.setBlock(pos, block)
    }

    override fun clearBlock(x: Int, y: Int, z: Int) {
        val pos = trans.transform(x, y, z).round()
        storage.clearBlock(pos)
    }
}