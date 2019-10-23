package hunternif.voxarch.world

import hunternif.voxarch.storage.IBlockStorage

interface IBlockWorld : IBlockStorage {
    val maxHeight: Int
    val seaLevel: Int
    /** Including any non-terrain blocks e.g. trees */
    fun getHeight(x: Int, z: Int): Int
    /** Excluding any non-terrain blocks e.g. trees */
    fun getTerrainHeight(x: Int, z: Int): Int
}