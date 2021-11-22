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

    /** Checks if this array has mirror symmetry vs the YZ plane */
    fun isSymmetricX(): Boolean {
        for (x in 0 until width/2) {
            for (y in 0 until height) {
                for (z in 0 until length) {
                    if (this[x, y, z] != this[width-1-x, y, z]) {
                        return false
                    }
                }
            }
        }
        return true
    }

    /** Checks if this array has mirror symmetry vs the XY plane */
    fun isSymmetricZ(): Boolean {
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until length/2) {
                    if (this[x, y, z] != this[x, y, length-1-z]) {
                        return false
                    }
                }
            }
        }
        return true
    }
}