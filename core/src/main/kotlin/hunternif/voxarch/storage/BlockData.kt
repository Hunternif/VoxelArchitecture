package hunternif.voxarch.storage

import hunternif.voxarch.util.Direction

/**
 * Contains block id, metadata and [Direction]. The orientation
 * should modify the metadata in overridden method [.setOrientation].
 * @author Hunternif
 */
open class BlockData @JvmOverloads constructor(
    var id: Int, var metadata: Int = 0
) {

    /** Override this method to set metadata appropriately.  */
    var orientation: Direction? = null

    fun hasOrientation(): Boolean {
        return orientation != null
    }

    /** Rotate the Direction (if not NONE) counterclockwise by the
     * specified angle.  */
    fun rotate(angle: Double) {
        orientation = orientation?.rotate(angle)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BlockData) return false
        return if (other === this) true
        else other.id == id && other.metadata == metadata &&
            other.orientation == orientation
    }

    fun clone(): BlockData {
        return BlockData(id, metadata)
    }

    override fun toString(): String {
        return "id: " + id + " " + orientation?.name + " meta: " + metadata
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + metadata
        result = 31 * result + (orientation?.hashCode() ?: 0)
        return result
    }
}