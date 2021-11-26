package hunternif.voxarch.vector

import hunternif.voxarch.storage.IStorage3D

class Array3D<T>(
    override val width : Int,
    override val height: Int,
    override val length: Int,
    init: (x: Int, y: Int, z: Int) -> T
) : IStorage3D<T> {
    constructor(width: Int, height: Int, length: Int, init: T):
        this(width, height, length, {_,_, _ -> init})

    private val data = Array(width) {
        x -> Array(height) {
            y -> Array(length) {
                z-> init(x, y, z) as Any?
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun get(x: Int, y: Int, z: Int): T = data[x][y][z] as T
    override operator fun get(p: IntVec3): T = get(p.x, p.y, p.z)
    fun at(x: Int, y: Int, z: Int): T = get(x, y, z)
    fun at(p: IntVec3): T = get(p)

    override operator fun set(x: Int, y: Int, z: Int,  value: T) { data[x][y][z] = value!! }
    override operator fun set(p: IntVec3, v: T) = set(p.x, p.y, p.z, v)

    fun copy(): Array3D<T> = Array3D(width, height, length) {
        x, y, z -> this[x, y, z]
    }

    operator fun contains(p: IntVec3) = p.x in 0 until width && p.y in 0 until height && p.z in 0 until length

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