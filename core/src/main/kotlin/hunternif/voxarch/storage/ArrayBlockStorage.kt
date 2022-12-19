package hunternif.voxarch.storage

import hunternif.voxarch.vector.Array3D

open class ArrayBlockStorage(
    val data: IArray3D<BlockData?>,
) : IBlockStorage, IArray3D<BlockData?> by data {

    constructor(length: Int, height: Int, width: Int):
        this(Array3D(length, height, width, null))

    /** If true, getting or setting blocks outside the array will not throw. */
    var safeBoundary = false

    override fun getBlock(x: Int, y: Int, z: Int): BlockData? {
        return try {
            get(x, y, z)
        } catch (e: ArrayIndexOutOfBoundsException) {
            if (safeBoundary) BlockData("outside")
            else throw e
        }
    }

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockData?) {
        try {
            set(x, y, z, block)
        } catch (e: ArrayIndexOutOfBoundsException) {
            if (!safeBoundary) throw e
        }
    }

    override fun clearBlock(x: Int, y: Int, z: Int) {
        try {
            set(x, y, z, null)
        } catch (e: ArrayIndexOutOfBoundsException) {
            if (!safeBoundary) throw e
        }
    }
}