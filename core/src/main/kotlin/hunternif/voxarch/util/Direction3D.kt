package hunternif.voxarch.util

import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.util.Direction3D.*

/**
 * ```
 * Y
 *  +----> X (East)
 *  |
 *  V
 *  Z
 * ```
 */
enum class Direction3D(val vec: IntVec3) {
    /** Positive Y */
    UP(IntVec3(0, 1, 0)),
    /** Negative Y */
    DOWN(IntVec3(0, -1, 0)),
    /** Positive X */
    EAST(IntVec3(1, 0, 0)),
    /** Negative X */
    WEST(IntVec3(-1, 0, 0)),
    /** Positive Z */
    SOUTH(IntVec3(0, 0, 1)),
    /** Negative Z */
    NORTH(IntVec3(0, 0, -1))
}

fun Direction3D.opposite() = when (this) {
    UP -> DOWN
    DOWN -> UP
    EAST -> WEST
    SOUTH -> NORTH
    WEST -> EAST
    NORTH -> SOUTH
}

fun Direction3D.to2D(): Direction = when(this) {
    EAST, UP, DOWN -> Direction.EAST
    WEST  -> Direction.WEST
    SOUTH ->  Direction.SOUTH
    NORTH ->  Direction.NORTH
}

fun Direction.to3D(): Direction3D = when(this) {
    Direction.EAST -> EAST
    Direction.WEST -> WEST
    Direction.SOUTH -> SOUTH
    Direction.NORTH -> NORTH
}

/** Rotates the direction's vector and returns the direction closest to it.
 * @param angleDeg angle around the Y axis in degrees. */
fun Direction3D.rotateY(angleDeg: Double): Direction3D {
    if (this == UP || this == DOWN) return this
    return this.to2D().rotate(angleDeg).to3D()
}


fun IntVec3.allDirections(): Sequence<IntVec3> = sequence {
    yield(facing(DOWN))
    yield(facing(UP))
    yield(facing(NORTH))
    yield(facing(EAST))
    yield(facing(SOUTH))
    yield(facing(WEST))
}

fun IntVec3.facing(dir: Direction3D) = add(dir.vec)
