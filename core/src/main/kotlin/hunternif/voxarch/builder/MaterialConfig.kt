package hunternif.voxarch.builder

import hunternif.voxarch.storage.BlockData

/**
 * Provides block data for material name.
 * See [Companion] for some default materials.
 */
class MaterialConfig {
    private val map = mutableMapOf<String, () -> BlockData>()

    fun set(name: String, block: () -> BlockData) {
        map[name] = block
    }

    fun get(name: String) = map.getOrDefault(name, ::default)()

    private fun default() = BlockData(0)

    companion object {
        // Some default materials
        const val WALL = "wall"
        const val FLOOR = "floor"
        const val ROOF = "roof"
        const val TORCH = "torch"
        const val POST = "post"
    }
}