package hunternif.voxarch.world

import hunternif.voxarch.util.Direction
import hunternif.voxarch.util.Direction.*
import hunternif.voxarch.vector.Array2D
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.world.Segment.*
import java.util.*

data class MountainDetectorConfig(
    val slopeStartThreshold: Double = 3.0,
    val slopeEndThreshold: Double = 1.5
)

/** Gradient ascent, assuming every midSlope steep enough is a mountain. */
fun HeightMap.detectMountains(
    config: MountainDetectorConfig = MountainDetectorConfig()
): Collection<Mountain> {
    val segments = segments(config)
    val allTops = segments.filter { segments[it] == TOP }.toSet()
    val topClusters = cluster(allTops)
    return topClusters.map { top -> descendFromTop(top, segments) }
}

internal fun HeightMap.descendFromTop(top: Set<IntVec2>, segments: Array2D<Segment>) : Mountain {
    val slope = hashSetOf<IntVec2>()
    val perimeter = hashSetOf<IntVec2>()
    val explored = hashSetOf<IntVec2>()
    val queue = LinkedList<IntVec2>()
    queue.addAll(top)
    while (queue.isNotEmpty()) {
        val p = queue.pop()
        if (!explored.add(p)) continue
        val descend = { dir: Direction ->
            val next = p.next(dir)
            if (next in this) {
                // ensure we're descending, to separate 2 adjacent mountains
                if (next !in explored && segments[next] == SLOPE && at(next) <= at(p)) {
                    slope.add(next)
                    queue.push(next)
                }
                if (segments[p] == TOP && segments[next] != TOP) {
                    perimeter.add(next)
                }
            }
        }
        descend(EAST)
        descend(NORTH)
        descend(WEST)
        descend(SOUTH)
    }
    return Mountain(slope, top, perimeter)
}

/** Combine adjacent points into clusters */
internal fun cluster(points: Set<IntVec2>): Set<Set<IntVec2>> {
    val clusters = hashSetOf<Set<IntVec2>>()
    val explored = hashSetOf<IntVec2>()
    for (start in points) {
        if (start in explored) continue
        val cluster = hashSetOf<IntVec2>()
        val queue = LinkedList<IntVec2>()
        queue.push(start)
        while (queue.isNotEmpty()) {
            val p = queue.pop()
            if (!explored.add(p)) continue
            cluster.add(p)
            val explore = { dir: Direction ->
                val next = p.next(dir)
                if (next in points) {
                    queue.push(next)
                }
            }
            explore(EAST)
            explore(NORTH)
            explore(WEST)
            explore(SOUTH)
        }
        clusters.add(cluster)
    }
    return clusters
}

internal enum class Segment {
    GROUND, SLOPE, TOP
}

/** Map every point to a [Segment] based on slope. */
internal fun HeightMap.segments(config: MountainDetectorConfig): Array2D<Segment> {
    val segments = Array2D(width, length, GROUND)
    val slopeQueue = LinkedList<IntVec2>() // candidates for SLOPE or TOP

    // 1. Begin SLOPEs
    for (p in this) {
        if (segments[p] != GROUND) continue
        Direction.values().forEach {
            if (slope(p, it) >= config.slopeStartThreshold) {
                segments[p] = SLOPE
                slopeQueue.push(p.next(it))
            }
        }
    }

    // 2. Continue SLOPEs and mark possible TOPs
    val seen = hashSetOf<IntVec2>()
    val topQueue = LinkedList<IntVec2>() // candidates for continuation of TOP
    while (slopeQueue.isNotEmpty()) {
        val p = slopeQueue.pop()
        if (p !in this || !seen.add(p)) continue
        Direction.values().forEach {
            val slope = slope(p, it)
            if (slope >= config.slopeEndThreshold) {
                // continue SLOPE while under threshold
                segments[p] = SLOPE
                slopeQueue.push(p.next(it))
            } else if (slope >= 0 && segments[p] != SLOPE) {
                segments[p] = TOP
                topQueue.push(p.next(it))
            }
        }
    }

    // 3. Spread out TOPs to fill gaps
    while (topQueue.isNotEmpty()) {
        val p = topQueue.pop()
        if (p !in this || segments[p] != GROUND) continue
        Direction.values().forEach {
            val slope = slope(p, it)
            if (slope >= 0) {
                segments[p] = TOP
                topQueue.push(p.next(it))
            }
        }
    }
    return segments
}

data class Slope(val dir: Direction, val height: Double)

/** Each value points in the direction of greatest height */
fun HeightMap.gradient(): Array2D<Slope> {
    val gradient = Array2D(width, length, Slope(EAST, 0.0))
    for (p in this) {
        val dx = midSlope(p, EAST)
        val dz = midSlope(p, SOUTH)
        gradient[p] =
            mapOf(EAST to dx, WEST to -dx, SOUTH to dz, NORTH to -dz)
                .map { Slope(it.key, it.value) }
                .maxBy { it.height }!!
    }
    return gradient
}

/** Gradient slope exactly at [start] in the direction [dir] */
internal fun HeightMap.midSlope(start: IntVec2, dir: Direction): Double {
    val prev = start.next(dir.opposite)
    val next = start.next(dir)
    if (prev !in this) {
        if (next !in this) return 0.0
        return (at(next) - at(start)).toDouble()
    }
    if (next !in this) {
        return (at(start) - at(prev)).toDouble()
    }
    return (at(next) - at(prev)).toDouble()/2.0
}

/** Gradient slope at midpoint between [start] and `start.next(dir)`*/
internal fun HeightMap.slope(start: IntVec2, dir: Direction): Double {
    val next = start.next(dir)
    if (next !in this) return 0.0
    return (at(next) - at(start)).toDouble()
}
