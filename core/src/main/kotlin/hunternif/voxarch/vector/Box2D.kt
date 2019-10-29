package hunternif.voxarch.vector

import hunternif.voxarch.util.Direction
import hunternif.voxarch.util.Direction.*

/**
 * ```
 * Y
 *  +------------> X (East)
 *  | start --+
 *  | |       |
 *  | +---- end
 *  V
 *  Z
 * ```
 */
data class Box2D(val start: IntVec2, val end: IntVec2): Iterable<IntVec2> {
    val width: Int get() = end.x - start.x
    val length: Int get() = end.y - start.y
    val center: Vec2 get() = Vec2(0.5*width + start.x, 0.5*length + start.y)

    fun expand(dir: Direction): Box2D = when (dir) {
        NORTH -> Box2D(start.next(NORTH), end)
        WEST -> Box2D(start.next(WEST), end)
        EAST -> Box2D(start, end.next(EAST))
        SOUTH -> Box2D(start, end.next(SOUTH))
    }
    fun move(dir: Direction): Box2D = Box2D(start.next(dir), end.next(dir))

    override operator fun iterator(): Iterator<IntVec2> = iterator {
        for (x in start.x..end.x) {
            for (y in start.y..end.y) {
                yield(IntVec2(x, y))
            }
        }
    }
}