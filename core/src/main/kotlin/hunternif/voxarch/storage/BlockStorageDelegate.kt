package hunternif.voxarch.storage

class BlockStorageDelegate(
    val data: IStorage3D<BlockData?>,
) : IBlockStorage, IStorage3D<BlockData?> by data {
    override fun getBlock(x: Int, y: Int, z: Int): BlockData? = get(x, y, z)

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockData?) {
        set(x, y, z, block)
    }

    override fun clearBlock(x: Int, y: Int, z: Int) {
        set(x, y, z, null)
    }
}