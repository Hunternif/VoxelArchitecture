package hunternif.voxarch.storage

import com.google.common.annotations.VisibleForTesting
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntVec3
import kotlin.math.*

class ChunkedStorage3D<T>(
    private val chunkSize: IntVec3,
    private val chunkFactory: () -> Array3D<T?>,
) : IStorage3D<T?> {

    companion object {
        inline operator fun <reified T> invoke(
            chunkWidth: Int = 16,
            chunkHeight: Int = 16,
            chunkLength: Int = 16,
        ) = ChunkedStorage3D<T>(IntVec3(chunkWidth, chunkHeight, chunkLength)) {
            Array3D(chunkWidth, chunkHeight, chunkLength, null)
        }
    }

    override var minX: Int = 0
        private set
    override var maxX: Int = 0
        private set
    override var minY: Int = 0
        private set
    override var maxY: Int = 0
        private set
    override var minZ: Int = 0
        private set
    override var maxZ: Int = 0
        private set

    @PublishedApi internal var _size = 0
    override val size: Int get() = _size

    @VisibleForTesting
    internal val chunkMap = mutableMapOf<IntVec3, Array3D<T?>>()
    /** Reusable instance of a key into [chunkMap] */
    private val chunkKey = IntVec3(0, 0, 0)
    /** Reusable instance of coordinates inside a chunk */
    private val arrayCoords = IntVec3(0, 0, 0)

    override fun get(x: Int, y: Int, z: Int): T? {
        val key = getChunkKey(x, y, z)
        return chunkMap[key]?.let { it[getArrayCoords(key, x, y, z)] }
    }

    override fun set(x: Int, y: Int, z: Int, v: T?) {
        val key = getChunkKey(x, y, z)
        val chunk = chunkMap[key]
            ?: chunkFactory().also { chunkMap[key.clone()] = it }
        val coords = getArrayCoords(key, x, y, z)
        val prevVal = chunk[coords]
        chunk[coords] = v
        if (v == null && prevVal != null) _size--
        if (v != null && prevVal == null) _size++
        // TODO: shrink bounds when item is removed, un-ignore unit test.
        minX = min(minX, x)
        minY = min(minY, y)
        minZ = min(minZ, z)
        maxX = max(maxX, x)
        maxY = max(maxY, y)
        maxZ = max(maxZ, z)
    }

    /** Maps voxel coordinates to the key of the chunk that contains them.
     * Returns [chunkKey] */
    private fun getChunkKey(x: Int, y: Int, z: Int): IntVec3 =
        chunkKey.set(
            floor(x.toFloat() / chunkSize.x).toInt(),
            floor(y.toFloat() / chunkSize.y).toInt(),
            floor(z.toFloat() / chunkSize.z).toInt(),
        )

    /** Maps voxel coordinates to inside a chunk. Returns [arrayCoords]. */
    private fun getArrayCoords(chunkKey: IntVec3, x: Int, y: Int, z: Int): IntVec3 =
        arrayCoords.set(
            x - chunkKey.x * chunkSize.x,
            y - chunkKey.y * chunkSize.y,
            z - chunkKey.z * chunkSize.z,
        )
}