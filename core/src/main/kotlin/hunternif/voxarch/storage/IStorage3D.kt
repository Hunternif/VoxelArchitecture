package hunternif.voxarch.storage

import hunternif.voxarch.vector.IntVec3

interface IStorage3D<T> : Iterable<IntVec3> {
    val width: Int
    val height: Int
    val length: Int
    operator fun get(x: Int, y: Int, z: Int): T
    operator fun get(p: IntVec3): T
    operator fun set(x: Int, y: Int, z: Int,  value: T)
    operator fun set(p: IntVec3, v: T)
}