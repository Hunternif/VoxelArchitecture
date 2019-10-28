package hunternif.voxarch.world

import hunternif.voxarch.util.Direction
import hunternif.voxarch.util.Direction.*
import hunternif.voxarch.vector.Box2D
import hunternif.voxarch.vector.IntVec2


/**
 * Fills the area with rectangular boxes, making them as large and few as possible.
 * Also I prefer square shapes.
 */
fun Area.expandBox(): Box2D {
// expand a box from middle point
    // a-+
    // +-b
    val mid = middlePoint()
    var box = Box2D(mid, mid)
    /** returs true if expanded successfully */
    fun expand(dir: Direction): Boolean {
        box.expand(dir).let {
            if (fits(it)) {
                box = it
                return true
            }
            return false
        }
    }
    while(true) {
        val east = expand(EAST)
        val north = expand(NORTH)
        val west = expand(WEST)
        val south = expand(SOUTH)
        if (east || north || west || south) continue
        else break
    }
    return box
}

/** Select a point that is approx. in the middle. */
private fun Area.middlePoint(): IntVec2 {
    val midRow = this.sortedBy { it.x }
    val midX = midRow[midRow.size/2].x
    val midCol = midRow.filter { it.x == midX }.sortedBy { it.y }
    return midCol[midCol.size/2]
}

private fun Area.fits(box: Box2D): Boolean {
    for (p in box)
        if (p !in this) return false
    return true
}