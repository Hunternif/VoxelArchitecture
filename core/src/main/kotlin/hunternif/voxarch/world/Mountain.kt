package hunternif.voxarch.world

import hunternif.voxarch.vector.IntVec2

data class Mountain(
    val slope: Set<IntVec2>,
    val top: Set<IntVec2>,
    val perimeter: Set<IntVec2>
)