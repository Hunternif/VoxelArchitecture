package hunternif.voxarch.vector

import hunternif.voxarch.storage.IArray3D

class Array3D<T>(
    override val width : Int,
    override val height: Int,
    override val depth: Int,
    private val data: Array<T>
) : IArray3D<T> {

    @PublishedApi internal var _size = 0
    override val size: Int get() = _size

    companion object {
        inline operator fun <reified T> invoke(
            width: Int,
            height: Int,
            depth: Int,
            init: (x: Int, y: Int, z: Int) -> T
        ): Array3D<T> {
            var size = 0
            val result = Array3D(
                width,
                height,
                depth,
                Array(width * height * depth) { index ->
                    val y = index % height
                    val z = (index - y)/height % depth
                    val x = ((index - y)/height - z)/depth
                    val value = init(x, y, z)
                    if (value != null) size++
                    value
                }
            )
            result._size = size
            return result
        }

        inline operator fun <reified T> invoke(
            width: Int,
            height: Int,
            depth: Int,
            init: T
        ): Array3D<T> {
            val result = Array3D(
                width,
                height,
                depth,
                Array(width * height * depth) { init }
            )
            result._size = if (init == null) 0 else width * height * depth
            return result
        }

    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun index(x: Int, y: Int, z: Int): Int {
        if (x < 0 || x >= width) throw ArrayIndexOutOfBoundsException("x = $x is out of bounds")
        if (y < 0 || y >= height) throw ArrayIndexOutOfBoundsException("y = $y is out of bounds")
        if (z < 0 || z >= depth) throw ArrayIndexOutOfBoundsException("z = $z is out of bounds")
        return (x * depth + z) * height + y
    }

    override operator fun get(x: Int, y: Int, z: Int): T = data[index(x, y, z)]
    override operator fun get(p: IntVec3): T = data[index(p.x, p.y, p.z)]
    fun at(x: Int, y: Int, z: Int): T = data[index(x, y, z)]
    fun at(p: IntVec3): T = data[index(p.x, p.y, p.z)]

    override operator fun set(x: Int, y: Int, z: Int, v: T) {
        val prevVal = data[index(x, y, z)]
        data[index(x, y, z)] = v
        if (v == null && prevVal != null) _size--
        if (v != null && prevVal == null) _size++
    }
}