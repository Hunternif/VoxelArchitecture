package hunternif.voxarch.wfc.wang3x3x3

import hunternif.voxarch.util.*
import hunternif.voxarch.wfc.WangTile
import hunternif.voxarch.wfc.WangVoxel
import hunternif.voxarch.wfc.WangVoxel.*

private fun wangTile3x3x3(init: (x: Int, y: Int, z: Int) -> WangVoxel) = WangTile(3, 3, 3, init)
private fun wangTile3x3x3(vx: WangVoxel) = wangTile3x3x3 { _, _, _ -> vx }

val air = wangTile3x3x3(AIR)
// floor runs through the middle to allow connections from below
val floor = wangTile3x3x3 { _, y, _ -> if (y == 1) FLOOR else AIR }

/**
 * ```
 * Y
 *  +------------> X (East)
 *  |   inside
 *  | == wall ==
 *  |   outside
 *  V
 *  Z
 * ```
 */
val wallOnWallStraight = wangTile3x3x3(AIR).apply {
    this[0, 0, 0] = AIR // inside
    this[0, 0, 1] = WALL
    this[0, 0, 2] = AIR // outside
    copyUpXLocal()
    copyUpYLocal()
}
/**
 * ```
 *  Y
 *  ^
 *  |   inside  || outside
 *  | == floor ===
 *  |   outside
 *  +----------------> X (East)
 * Z
 * ```
 */
val wallOnFloorStraight = wangTile3x3x3(AIR).apply {
    this[0, 2, 0] = AIR // inside
    this[1, 2, 0] = WALL
    this[0, 1, 0] = FLOOR
    this[1, 1, 0] = FLOOR
    copyUpZLocal()
}
/**
 * ```
 *  Y
 *  ^
 *  |              outside
 *  | == floor ===
 *  |   inside  ||
 *  +----------------> X (East)
 * Z
 * ```
 */
val wallUnderCeilingStraight = wallOnFloorStraight.mirrorY()
/**
 * For castle ward walls
 * ```
 *  Y
 *  ^
 *  |       outside
 *  |         ||
 *  | outside || outside
 *  +----------------> X (East)
 * Z
 * ```
 */
val wallTipStraight = wangTile3x3x3(AIR).apply {
    this[1, 0, 0] = WALL
    this[1, 1, 0] = WALL
    copyUpZLocal()
}

/**
 * ```
 * Y
 *  +----------------> X (East)
 *  |  inside  ||
 *  | == wall =||
 *  |             outside
 *  V
 *  Z
 * ```
 */
val wallOnWallCorner = wangTile3x3x3(AIR).apply {
    this[0, 0, 0] = AIR // inside
    this[0, 0, 1] = WALL
    this[1, 0, 0] = WALL
    this[1, 0, 1] = WALL
    copyUpYLocal()
}
val wallOnFloorCorner = wangTile3x3x3(AIR).apply {
    this[0, 1, 0] = FLOOR
    this[0, 1, 1] = FLOOR
    this[1, 1, 0] = FLOOR
    this[1, 1, 1] = FLOOR

    this[0, 2, 0] = AIR // inside
    this[0, 2, 1] = WALL
    this[1, 2, 0] = WALL
    this[1, 2, 1] = WALL
}
val wallUnderCeilingCorner = wallOnFloorCorner.mirrorY()
val wallTipCorner = wangTile3x3x3(AIR).apply {
    this[0, 0, 0] = AIR // inside
    this[0, 0, 1] = WALL
    this[1, 0, 0] = WALL
    this[1, 0, 1] = WALL

    this[0, 1, 0] = AIR // inside
    this[0, 1, 1] = WALL
    this[1, 1, 0] = WALL
    this[1, 1, 1] = WALL
}

fun generateValidTiles3x3x3(): List<WangTile> = mutableListOf(
    air,
    floor
).apply {
    addAll(wallOnWallStraight.generateRotationsY())
    addAll(wallOnFloorStraight.generateRotationsY())
    addAll(wallUnderCeilingStraight.generateRotationsY())
    addAll(wallTipStraight.generateRotationsY())
    addAll(wallOnWallCorner.generateRotationsY())
    addAll(wallOnFloorCorner.generateRotationsY())
    addAll(wallUnderCeilingCorner.generateRotationsY())
    addAll(wallTipCorner.generateRotationsY())
}
