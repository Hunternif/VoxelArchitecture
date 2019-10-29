package hunternif.voxarch.world

import hunternif.voxarch.vector.IntVec2

/**
 * Represents a flat, top-down layout of a mountain.
 * [origin] is relative to the world, the rest is relative to origin
 */
data class Mountain(
    val origin: IntVec2,
    val slope: Area,
    val top: Area,
    val perimeter: Area
) {
    /**
     * "Mountainousness" rank, i.e. how much of the perimeter is a steep slope.
     * A "real" mountain would get 1.0, a low hill would get ~0.0
     */
    val rank: Double get() {
        if (perimeter.isEmpty()) return 0.0
        return perimeter.intersect(slope).size.toDouble() / perimeter.size.toDouble()
    }

    /** top + part of perimeter that's not a slope */
    val topWithFlatPerimeter: Area get() = perimeter.subtract(slope).union(top)
}