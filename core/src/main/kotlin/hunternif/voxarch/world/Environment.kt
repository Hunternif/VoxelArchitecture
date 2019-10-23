package hunternif.voxarch.world

import hunternif.voxarch.storage.BlockData

/**
 * Describes some aspects of the world in which we are building
 */
interface Environment {
    fun isTerrain(block: BlockData?): Boolean
    fun shouldBuildThrough(block: BlockData?): Boolean
}