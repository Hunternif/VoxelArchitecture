package hunternif.voxarch.vector

open class Array3D<T>(
    val width : Int,
    val height: Int,
    val length: Int,
    init: (x: Int, y: Int, z: Int) -> T
) : Iterable<IntVec3> {
    constructor(width: Int, height: Int, length: Int, init: T):
        this(width, height, length, {_,_, _ -> init})

    private val data = Array(width) {
        x -> Array(height) {
            y -> Array(length) {
                z-> init(x, y, z) as Any
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    open operator fun get(x: Int, y: Int, z: Int): T = data[x][y][z] as T
    operator fun get(p: IntVec3): T = get(p.x, p.y, p.z)
    fun at(x: Int, y: Int, z: Int): T = get(x, y, z)
    fun at(p: IntVec3): T = get(p)

    operator fun set(x: Int, y: Int, z: Int,  value: T) { data[x][y][z] = value!! }
    operator fun set(p: IntVec3, v: T) = set(p.x, p.y, p.z, v)

    open operator fun contains(p: IntVec2) = p.x in 0 until width && p.y in 0 until length

    override operator fun iterator(): Iterator<IntVec3> = iterator {
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until length) {
                    yield(IntVec3(x, y, z))
                }
            }
        }
    }
}