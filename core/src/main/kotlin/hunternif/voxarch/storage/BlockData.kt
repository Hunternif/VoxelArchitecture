package hunternif.voxarch.storage

import hunternif.voxarch.util.Direction

/**
 * Contains block [key] and [orientation].
 * (I tried to make this an interface, but the generics are too annoying.)
 */
open class BlockData(
    val key: String,
    val orientation: Direction? = null
) : IVoxel {

    // Legacy constructor
    constructor(key: String) : this(key, null)

    /**
     * Returns an instance of this block that's rotated and facing the Direction
     * (if not NONE) counterclockwise by the specified angle.
     */
    fun rotate(angle: Double): BlockData {
        return if (angle == 0.0 || orientation == null) this
        else orient(orientation.rotate(angle))
    }

    /** Returns an instance with the same [key] and given [newOrientation] */
    fun orient(newOrientation: Direction): BlockData {
        return if (orientation == newOrientation) this
        else getInstance(newOrientation)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BlockData) return false
        return if (other === this) true
        else other.key == key && other.orientation == orientation
    }

    /** Returns an instance with the same [key] and given orientation. */
    protected open fun getInstance(orientation: Direction): BlockData {
        return BlockData(key, orientation)
    }

    override fun toString(): String {
        return "id: " + key + " " + orientation?.name
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + (orientation?.hashCode() ?: 0)
        return result
    }
}