package hunternif.voxarch.vector

import hunternif.voxarch.storage.IStorage3D

class Array3D<T>(
    private val data: Array<Array<Array<T>>>
) : IStorage3D<T> {

    override val width : Int = data.size
    override val height: Int = data[0].size
    override val length: Int = data[0][0].size

    companion object {
        inline operator fun <reified T> invoke(
            width: Int,
            height: Int,
            length: Int,
            init: (x: Int, y: Int, z: Int) -> T
        ) = Array3D(Array(width) {
            x -> Array(height) {
                y -> Array(length) {
                    z-> init(x, y, z)
                }
            }
        })

        inline operator fun <reified T> invoke(
            width: Int,
            height: Int,
            length: Int,
            init: T
        ) = invoke(width, height, length) { _, _, _ -> init }
    }

    override operator fun get(x: Int, y: Int, z: Int): T = data[x][y][z]
    override operator fun get(p: IntVec3): T = data[p.x][p.y][p.z]
    fun at(x: Int, y: Int, z: Int): T = data[x][y][z]
    fun at(p: IntVec3): T = data[p.x][p.y][p.z]

    override operator fun set(x: Int, y: Int, z: Int, v: T) { data[x][y][z] = v }
    override operator fun set(p: IntVec3, v: T) { data[p.x][p.y][p.z] = v }

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

    fun forEachIndexed(action: (IntVec3, T) -> Unit) {
        for (x in 0 until width) {
            val dataX = data[x]
            for (y in 0 until height) {
                val dataY = dataX[y]
                for (z in 0 until length) {
                    val v = dataY[z]
                    action(IntVec3(x, y, z), v)
                }
            }
        }
    }
}