package hunternif.voxarch.vector

import hunternif.voxarch.storage.IStorage3D

class Array3D<T>(
    override val width : Int,
    override val height: Int,
    override val length: Int,
    private val data: Array<T>
) : IStorage3D<T> {

    companion object {
        inline operator fun <reified T> invoke(
            width: Int,
            height: Int,
            length: Int,
            init: (x: Int, y: Int, z: Int) -> T
        ) = Array3D(
            width,
            height,
            length,
            Array(width * height * length) { index ->
                val y = index % height
                val z = (index - y)/height % length
                val x = ((index - y)/height - z)/length
                init(x, y, z)
            }
        )

        inline operator fun <reified T> invoke(
            width: Int,
            height: Int,
            length: Int,
            init: T
        ) = Array3D(
            width,
            height,
            length,
            Array(width * height * length) { init }
        )

    }

    private inline fun index(x: Int, y: Int, z: Int): Int = (x*length + z)*height + y

    override operator fun get(x: Int, y: Int, z: Int): T = data[index(x, y, z)]
    override operator fun get(p: IntVec3): T = data[index(p.x, p.y, p.z)]
    fun at(x: Int, y: Int, z: Int): T = data[index(x, y, z)]
    fun at(p: IntVec3): T = data[index(p.x, p.y, p.z)]

    override operator fun set(x: Int, y: Int, z: Int, v: T) { data[index(x, y, z)] = v }
    override operator fun set(p: IntVec3, v: T) { data[index(p.x, p.y, p.z)] = v }

    override operator fun contains(p: IntVec3) = p.x in 0 until width && p.y in 0 until height && p.z in 0 until length
    override fun contains(x: Int, y: Int, z: Int) = x in 0 until width && y in 0 until height && z in 0 until length

    override operator fun iterator(): Iterator<IntVec3> = iterator {
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until length) {
                    yield(IntVec3(x, y, z))
                }
            }
        }
    }

    fun forEachIndexed(action: (IntVec3, T) -> Unit) {
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until length) {
                    action(IntVec3(x, y, z), get(x, y, z))
                }
            }
        }
    }
}