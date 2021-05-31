package hunternif.voxarch.world

import hunternif.voxarch.vector.Array2D
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.vector.Vec3
import kotlin.math.abs

class HeightMap(width: Int, length: Int): Array2D<Int>(width, length, 0) {
    var minHeight = 0
    var maxHeight = 256
    /** Relative to the world*/
    var center: IntVec2 = IntVec2(0, 0)
    /** Relative to the world, with height */
    val center3: Vec3 get() = Vec3(center.x, at(middle), center.y)
    /** Low-XZ corner of the map, relative to the world */
    val start: IntVec2 get() = IntVec2(center.x - middle.x, center.y - middle.y)
    /** Internal middle point of the map, i.e. between (0,0) and (width, length) */
    private val middle: IntVec2 get() = IntVec2((width-1)/2, (length-1)/2)

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

    fun average(): Double {
        var total = 0.0
        for (p in this) {
            total += at(p)
        }
        return total/width/length
    }

    //TODO: test this
    fun averageIn(area: Area): Double {
        var totalHeight = 0.0
        var totalPoints = 0
        for (p in this.intersect(area)) {
            totalHeight += at(p)
            totalPoints += 1
        }
        return totalHeight/totalPoints
    }

    companion object {
        /**
         * Returns a snapshot of the world's height map around [center] of size [area].
         */
        fun IBlockWorld.heightMap(center: IntVec2, area: IntVec2): HeightMap =
            map(center, area) { x, z -> getHeight(x, z) }

        /**
         * Returns a snapshot of the world's height map from [from] to [to],
         * ignoring non-terrain blocks.
         */
        fun IBlockWorld.terrainMap(from: IntVec2, to: IntVec2): HeightMap {
            val center = IntVec2((from.x + to.x)/2, (from.y + to.y)/2)
            val area = IntVec2(abs(to.x - from.x), abs(to.y - from.y))
            return terrainMapCentered(center, area)
        }

        /**
         * Returns a snapshot of the world's height map around [center] of size [area],
         * ignoring non-terrain blocks.
         */
        fun IBlockWorld.terrainMapCentered(center: IntVec2, area: IntVec2): HeightMap =
            map(center, area) { x, z -> getTerrainHeight(x, z) }

        private fun IBlockWorld.map(
            center: IntVec2,
            area: IntVec2,
            mapper: IBlockWorld.(Int, Int) -> Int
        ): HeightMap {
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