package hunternif.voxarch.storage

import hunternif.voxarch.util.Direction

/**
 * Contains block id and [orientation].
 * @author Hunternif
 */
open class BlockData(var key: String) {

    var orientation: Direction? = null

    /** Rotate the Direction (if not NONE) counterclockwise by the
     * specified angle.  */
    fun rotate(angle: Double) {
        orientation = orientation?.rotate(angle)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BlockData) return false
        return if (other === this) true
        else other.key == key && other.orientation == orientation
    }

    fun clone(): BlockData {
        return BlockData(key)
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