package hunternif.voxarch.storage

import hunternif.voxarch.vector.IntVec2

interface IStorage2D<T> : Iterable<IntVec2> {
    val length: Int
    val height: Int
    operator fun get(x: Int, y: Int): T
    operator fun get(p: IntVec2): T
    operator fun set(x: Int, y: Int, value: T)
    operator fun set(p: IntVec2, v: T)
}