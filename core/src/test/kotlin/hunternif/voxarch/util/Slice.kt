package hunternif.voxarch.util

import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.ArrayBlockStorage

interface Slice {
    val width: Int
    val height: Int
    fun getBlock(x: Int, y: Int): BlockData?
}

class XSlice(
    private val storage: ArrayBlockStorage,
    private val offset: Int
): Slice {
    override val width = storage.length
    override val height = storage.height
    override fun getBlock(x: Int, y: Int): BlockData? =
        storage.getBlock(offset, y, x)
}

class YSlice(
    private val storage: ArrayBlockStorage,
    private val offset: Int
): Slice {
    override val width = storage.width
    override val height = storage.length
    override fun getBlock(x: Int, y: Int): BlockData? =
        storage.getBlock(x, offset, y)
}

class ZSlice(
    private val storage: ArrayBlockStorage,
    private val offset: Int
): Slice {
    override val width = storage.width
    override val height = storage.height
    override fun getBlock(x: Int, y: Int): BlockData? =
        storage.getBlock(x, y, offset)
}
