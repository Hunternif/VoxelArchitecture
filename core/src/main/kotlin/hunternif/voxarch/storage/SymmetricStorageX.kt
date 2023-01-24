package hunternif.voxarch.storage


/**
 * Convenience class that sets blocks symmetrically on the X axis.
 */
class SymmetricStorageX(
    private val storage: IBlockStorage,
    private val midPoint: Int,
) : IBlockStorage {
    override fun getBlock(x: Int, y: Int, z: Int): BlockData? =
        storage.getBlock(x, y, z)

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockData?) {
        storage.setBlock(x, y, z, block)
        if (x != midPoint) {
            val mirrorX = midPoint * 2 - x
            storage.setBlock(mirrorX, y, z, block)
        }
    }

    override fun clearBlock(x: Int, y: Int, z: Int) {
        storage.clearBlock(x, y, z)
        if (x != midPoint) {
            val mirrorX = midPoint * 2 - x
            storage.clearBlock(mirrorX, y, z)
        }
    }
}
