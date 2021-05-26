package hunternif.voxarch.builder

import hunternif.voxarch.storage.BlockData

/**
 * Provides block data for material name.
 */
class MaterialConfig {
    private val map = mutableMapOf<String, () -> BlockData>()

    fun set(name: String, block: () -> BlockData) {
        map[name] = block
    }

    fun get(name: String) = map.getOrDefault(name, ::default)()

    private fun default() = BlockData("unknown")
}