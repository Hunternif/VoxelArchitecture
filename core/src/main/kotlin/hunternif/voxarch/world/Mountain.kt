package hunternif.voxarch.world

import hunternif.voxarch.vector.IntVec2

class Mountain(
    val slope: Set<IntVec2>,
    val top: Set<IntVec2> = hashSetOf()
)