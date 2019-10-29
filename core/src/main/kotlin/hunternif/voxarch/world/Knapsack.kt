package hunternif.voxarch.world

import hunternif.voxarch.util.Direction
import hunternif.voxarch.util.Direction.*
import hunternif.voxarch.vector.*
import java.util.*


/**
 * Fills the area with rectangular boxes, making them as large and few as possible.
 * Also I prefer square shapes.
 */
fun Area.fitBox(): Box2D = expandBoxAt(middlePoint(), IntVec2(Int.MAX_VALUE, Int.MAX_VALUE))

/** Expand a box from position [pos] until [sizeLimit] as long as it fits. */
fun Area.expandBoxAt(pos: IntVec2, sizeLimit: IntVec2): Box2D {
    // a-+
    // +-b
    var box = Box2D(pos, pos)
    /** returns true if expanded successfully */
    fun expand(dir: Direction): Boolean {
        when (dir) {
            EAST, WEST -> if (box.width >= sizeLimit.x) return false
            NORTH, SOUTH -> if (box.length >= sizeLimit.y) return false
        }
        box.expand(dir).let {
            if (fits(it)) { box = it; return true }
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

/**
 * Fits boxes in area along the perimeter, with size up to [size],
 * spread by [distance] apart, sorted (hopefully) in clockwise order.
 */
fun Area.spreadBoxesAlongPerimeter(size: IntVec2, distance: Double): List<Box2D> {
    val boxes = mutableListOf<Box2D>()
    // walk the perimeter and add a new box at every opportunity
    for (p in clockwiseInnerPerimeter(topLeft())) {
        if (boxes.all { it.center.distanceTo(p) >= distance })
            boxes.add(expandBoxAt(p, size))
    }
    return boxes
}

/** Returns points on the edge of this area starting from the given point,
 * assuming [start] is on the edge. */
fun Area.clockwiseInnerPerimeter(start: IntVec2): Sequence<IntVec2> {
    val area = this
    val outerPerimeter = outerPerimeter()
    val outerStart = start.aroundClockwise().firstOrNull { it in outerPerimeter }
        ?: return sequenceOf(start) // start was chosen incorrectly!

    val seen = hashSetOf(start)
    return sequence {
        yield(start)
        for (p in outerPerimeter.clockwise(outerStart)) {
            p.aroundCounterClockwise().firstOrNull { it in area && it !in seen}?.let {
                seen.add(it)
                yield(it)
            }
        }
    }
}

/** Assuming that area is a perimeter (a thin line with no acute angles)
 * and that [start] is contained in it. */
internal fun Area.clockwise(start: IntVec2): Sequence<IntVec2> {
    val area = this
    val seen = hashSetOf(start)
    var prev = start
    return sequence {
        yield(start)
        while (true) {
            val next = prev.aroundClockwise()
                .firstOrNull { it in area && it !in seen }
                ?: return@sequence
            seen.add(next)
            prev = next
            yield(next)
        }
    }
}

/** Points just outside of area. If Area has holes inside, it will include them too! */
fun Area.outerPerimeter(): Area {
    val perimeter = hashSetOf<IntVec2>()
    val explored = hashSetOf<IntVec2>()
    val queue = LinkedList<IntVec2>()
    queue.addAll(this)
    while (queue.isNotEmpty()) {
        val p = queue.pop()
        if (!explored.add(p)) continue
        fun step(dir: Direction) {
            val next = p.next(dir)
            if (next in this) queue.push(next)
            else perimeter.add(next)
        }
        step(EAST)
        step(NORTH)
        step(WEST)
        step(SOUTH)
    }
    return perimeter
}

fun IntVec2.aroundClockwise(): Sequence<IntVec2> = sequence {
    yield(IntVec2(x-1, y-1))
    yield(IntVec2(x, y-1))
    yield(IntVec2(x+1, y-1))
    yield(IntVec2(x+1, y))
    yield(IntVec2(x+1, y+1))
    yield(IntVec2(x, y+1))
    yield(IntVec2(x-1, y+1))
    yield(IntVec2(x-1, y))
}

fun IntVec2.aroundCounterClockwise(): Sequence<IntVec2> = sequence {
    yield(IntVec2(x-1, y-1))
    yield(IntVec2(x-1, y))
    yield(IntVec2(x-1, y+1))
    yield(IntVec2(x, y+1))
    yield(IntVec2(x+1, y+1))
    yield(IntVec2(x+1, y))
    yield(IntVec2(x+1, y-1))
    yield(IntVec2(x, y-1))
}

fun Area.topLeft(): IntVec2 = minBy { it.x + it.y }!!
