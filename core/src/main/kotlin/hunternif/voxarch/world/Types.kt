package hunternif.voxarch.world

import hunternif.voxarch.util.Direction
import hunternif.voxarch.vector.IntVec2

typealias Area = Set<IntVec2>

data class Slope(val dir: Direction, val height: Double)
