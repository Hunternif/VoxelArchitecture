package hunternif.voxarch.util

import hunternif.voxarch.storage.BlockData
import hunternif.voxarch.storage.ArrayBlockStorage

interface Slice {
    val length: Int
    val height: Int
    fun getBlock(x: Int, y: Int): BlockData?
    fun getName(): String
}

class XSlice(
    private val storage: ArrayBlockStorage,
    private val offset: Int
): Slice {
    override val length = storage.width
    override val height = storage.height
    override fun getBlock(x: Int, y: Int): BlockData? =
        storage.getBlock(offset, y, x)
    override fun getName(): String = "x=$offset"
}

class YSlice(
    private val storage: ArrayBlockStorage,
    private val offset: Int
): Slice {
    override val length = storage.length
    override val height = storage.width
    override fun getBlock(x: Int, y: Int): BlockData? =
        storage.getBlock(x, offset, y)
    override fun getName(): String = "y=$offset"
}

class ZSlice(
    private val storage: ArrayBlockStorage,
    private val offset: Int
): Slice {
    override val length = storage.length
    override val height = storage.height
    override fun getBlock(x: Int, y: Int): BlockData? =
        storage.getBlock(x, y, offset)
    override fun getName(): String = "z=$offset"
}
