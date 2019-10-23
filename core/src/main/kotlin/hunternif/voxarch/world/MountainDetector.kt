package hunternif.voxarch.world

import hunternif.voxarch.util.BlockOrientation
import hunternif.voxarch.util.BlockOrientation.*
import hunternif.voxarch.vector.IntVec2
import java.util.*
import kotlin.collections.HashSet

fun HeightMap.detectMountains(): HashSet<Set<IntVec2>> {
    val peaks = pointsHigherThan(maxHeight)
    val exploredPoints = hashSetOf<IntVec2>()
    val mountains = hashSetOf<Set<IntVec2>>()
    for (peak in peaks) {
        if (peak !in exploredPoints) {
            val mountain = descend(peak)
            mountains.add(mountain)
            exploredPoints.addAll(mountain)
        }
    }
    return mountains
}

private const val slopeLength = 2
private const val slopeThreshold = -2

private fun HeightMap.descend(start: IntVec2): Set<IntVec2> {
    val area = hashSetOf<IntVec2>()
    val queue = LinkedList<IntVec2>()
    queue.push(start)
    while (queue.isNotEmpty()) {
        val p = queue.pop()
        if (p !in this || !area.add(p)) continue
        val explore = { dir: BlockOrientation ->
            if (avgSlope(p, dir, slopeLength) > slopeThreshold) {
                queue.push(p.next(dir))
            }
        }
        explore(EAST)
        explore(NORTH)
        explore(WEST)
        explore(SOUTH)
    }
    return area
}

private fun HeightMap.avgSlope(start: IntVec2, dir: BlockOrientation, length: Int): Double {
    var dist = 0.0
    var dY = 0.0
    var prev = start
    var cur = start.next(dir)
    while (dist < length && cur in this) {
        dY += at(cur) - at(prev)
        dist++
        prev = cur
        cur = cur.next(dir)
    }
    return dY/dist
}

private fun HeightMap.pointsHigherThan(height: Int): Set<IntVec2> {
    val points = hashSetOf<IntVec2>()
    for (x in 0 until width) {
        for (z in 0 until width) {
            if (at(x, z) >= height)
                points.add(IntVec2(x, z))
        }
    }
    return points
}
