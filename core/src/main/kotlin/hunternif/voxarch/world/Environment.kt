package hunternif.voxarch.world

import hunternif.voxarch.storage.BlockData

/**
 * Describes some aspects of the world in which we are building
 */
interface Environment {
    val minY: Int
    fun isTerrain(block: BlockData?): Boolean
    fun shouldBuildThrough(block: BlockData?): Boolean
}

val defaultEnvironment = object : Environment {
    override val minY: Int = 0
    // All non-air blocks are non-transparent
    override fun isTerrain(block: BlockData?): Boolean = true
    // All non-air blocks are non-transparent
    override fun shouldBuildThrough(block: BlockData?): Boolean = false
}