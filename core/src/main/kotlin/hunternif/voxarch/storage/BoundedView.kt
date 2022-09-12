package hunternif.voxarch.storage

/**
 * Returns a bounded view of this storage, from min to max across all axes.
 * Coordinates shift so that `boundedView()[0, 0, 0]` equals
 * `storage[minX, minY, minZ]`.
 *
 * If the data changes in the underlying storage, the bounded view
 * will automatically have new size and coordinates.
 */
class BoundedView<T>(private val storage: IStorage3D<T?>) : IArray3D<T?> {
    override val width: Int get() = storage.width
    override val height: Int get() = storage.height
    override val length: Int get() = storage.length

    override val size: Int get() = storage.size

    override fun get(x: Int, y: Int, z: Int): T? = storage.run {
        get(x + minX, y + minY, z + minZ)
    }

    override fun set(x: Int, y: Int, z: Int, v: T?) = storage.run {
        set(x + minX, y + minY, z + minZ, v)
    }
}

/** See [BoundedView] */
fun <T> IStorage3D<T?>.boundedView() = BoundedView(this)