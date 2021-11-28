package hunternif.voxarch.vector

import hunternif.voxarch.storage.IStorage2D

open class Array2D<T>(
    private val data: Array<Array<T>>
) : IStorage2D<T> {

    override val width : Int = data.size
    override val length: Int = data[0].size

    companion object {
        inline operator fun <reified T> invoke(
            width: Int,
            length: Int,
            init: (x: Int, y: Int) -> T
        ) = Array2D(Array(width) {
            x -> Array(length) {
                y -> init(x, y)
            }
        })

        inline operator fun <reified T> invoke(
            width: Int,
            length: Int,
            init: T
        ) = invoke(width, length) { _, _ -> init }

        /** [list] must be a valid 2d list. */
        internal inline operator fun <reified T> invoke(list: List<List<T>>) =
            invoke(list[0].size, list.size) { x, y -> list[y][x] }
    }

    override operator fun get(x: Int, y: Int): T = data[x][y]
    override operator fun get(p: IntVec2): T = get(p.x, p.y)
    override operator fun set(x: Int, y: Int, value: T) { data[x][y] = value }
    override operator fun set(p: IntVec2, v: T) = set(p.x, p.y, v)

    open operator fun contains(p: IntVec2) = p.x in 0 until width && p.y in 0 until length

    override operator fun iterator(): Iterator<IntVec2> = iterator {
        for (x in 0 until width) {
            for (z in 0 until length) {
                yield(IntVec2(x, z))
            }
        }
    }
}
