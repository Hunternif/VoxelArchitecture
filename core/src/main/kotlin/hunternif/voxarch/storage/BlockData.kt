package hunternif.voxarch.storage

import hunternif.voxarch.util.Direction

/**
 * Contains block id and [orientation].
 * @author Hunternif
 */
open class BlockData constructor(var id: Int) {

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
        else other.id == id && other.orientation == orientation
    }

    fun clone(): BlockData {
        return BlockData(id)
    }

    override fun toString(): String {
        return "id: " + id + " " + orientation?.name
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (orientation?.hashCode() ?: 0)
        return result
    }
}