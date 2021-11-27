package hunternif.voxarch.util

import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.util.Direction3D.*

enum class Direction3D(val vec: IntVec3) {
    UP(IntVec3(0, 1, 0)),
    DOWN(IntVec3(0, -1, 0)),
    EAST(IntVec3(1, 0, 0)),
    SOUTH(IntVec3(0, 0, 1)),
    WEST(IntVec3(-1, 0, 0)),
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


fun IntVec3.allDirections(): Sequence<IntVec3> = sequence {
    yield(facing(DOWN))
    yield(facing(UP))
    yield(facing(NORTH))
    yield(facing(EAST))
    yield(facing(SOUTH))
    yield(facing(WEST))
}

fun IntVec3.facing(dir: Direction3D) = add(dir.vec)
