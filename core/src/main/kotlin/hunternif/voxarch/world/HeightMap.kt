package hunternif.voxarch.world

import hunternif.voxarch.vector.Array2D
import hunternif.voxarch.vector.IntVec2

class HeightMap(width: Int, length: Int): Array2D<Int>(width, length, 0) {
    var minHeight = 0
    var maxHeight = 256
    var center: IntVec2 = IntVec2(0, 0)

    /** [list] must be a valid 2d list. */
    internal constructor(list: List<List<Int>>): this(list[0].size, list.size) {
        for (p in this) {
            this[p] = list[p.y][p.x]
        }
    }

    override operator fun get(x: Int, y: Int): Int = super.get(x, y).let {
        if (it > maxHeight) return maxHeight
        if (it < minHeight) return minHeight
        return it
    }

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
                        this[x, z] = height
                        if (height < minHeight) minHeight = height
                        if (height > maxHeight) maxHeight = height
                    }
                }
            }
        }
    }
}