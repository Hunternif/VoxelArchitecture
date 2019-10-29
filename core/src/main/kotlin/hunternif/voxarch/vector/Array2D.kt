package hunternif.voxarch.vector

open class Array2D<T>(
    val width : Int,
    val length: Int,
    init: (Int, Int) -> T
) : Iterable<IntVec2> {
    constructor(width: Int, length: Int, init: T): this(width, length, {_,_ -> init})

    /** [list] must be a valid 2d list. */
    internal constructor(list: List<List<T>>): this(list[0].size, list.size, list[0][0]) {
        for (y in list.indices) {
            for (x in list[0].indices) {
                set(x, y, list[y][x])
            }
        }
    }

    private val data = Array(width) { x -> Array(length) { y -> init(x, y) as Any } }

    @Suppress("UNCHECKED_CAST")
    open operator fun get(x: Int, y: Int): T = data[x][y] as T
    operator fun get(p: IntVec2): T = get(p.x, p.y)
    fun at(x: Int, y: Int): T = get(x, y)
    fun at(p: IntVec2): T = get(p)

    operator fun set(x: Int, y: Int, value: T) { data[x][y] = value!! }
    operator fun set(p: IntVec2, v: T) = set(p.x, p.y, v)

    open operator fun contains(p: IntVec2) = p.x in 0 until width && p.y in 0 until length

    override operator fun iterator(): Iterator<IntVec2> = iterator {
        for (x in 0 until width) {
            for (z in 0 until length) {
                yield(IntVec2(x, z))
            }
        }
    }
}