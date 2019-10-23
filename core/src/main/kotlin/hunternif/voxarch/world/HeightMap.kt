package hunternif.voxarch.world

import hunternif.voxarch.vector.IntVec2

class HeightMap(
    val width: Int,
    val length: Int
) {
    private val map = Array(width) { IntArray(length) }
    var minHeight = 0
    var maxHeight = 256
    var center: IntVec2 = IntVec2(0, 0)

    fun set(x: Int, y: Int, z: Int) {
        map[x][z] = y
    }

    fun at(x: Int, z: Int): Int = map[x][z].let {
        if (it > maxHeight) return maxHeight
        if (it < minHeight) return minHeight
        return it
    }
    fun at(p: IntVec2) = at(p.x, p.y)
    operator fun contains(p: IntVec2) = p.x in 0 until width && p.y in 0 until length

    companion object {
        /**
         * Returns a snapshot of the world's height map around [center] of size [area].
         */
        fun IBlockWorld.heightMap(center: IntVec2, area: IntVec2): HeightMap =
            map(center, area) { x, z -> getHeight(x, z) }

        /**
         * Returns a snapshot of the world's height map around [center] of size [area],
         * ignoring non-terrain blocks.
         */
        fun IBlockWorld.terrainMap(center: IntVec2, area: IntVec2): HeightMap =
            map(center, area) { x, z -> getTerrainHeight(x, z) }

        private fun IBlockWorld.map(
            center: IntVec2,
            area: IntVec2,
            mapper: IBlockWorld.(Int, Int) -> Int
        ): HeightMap {
            val start = IntVec2(center.x - (area.x-1)/2, center.y - (area.y-1)/2)
            return HeightMap(area.x, area.y).apply {
                this.center = center
                minHeight = this@map.maxHeight
                maxHeight = 0
                for (x in 0 until area.x) {
                    for (z in 0 until area.y) {
                        val height = this@map.mapper(start.x + x, start.y + z)
                        map[x][z] = height
                        if (height < minHeight) minHeight = height
                        if (height > maxHeight) maxHeight = height
                    }
                }
            }
        }
    }
}