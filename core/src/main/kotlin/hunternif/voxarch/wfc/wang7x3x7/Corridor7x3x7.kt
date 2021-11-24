package hunternif.voxarch.wfc.wang7x3x7

import hunternif.voxarch.wfc.WangVoxel.*

/**
 * ```
 * Y
 *  +------------> X (East)
 *  |     ||  ||
 *  |     ||  ||
 *  |     ||  ||
 *  |     ||  ||
 *  |     ||  ||
 *  |     ||  ||
 *  |     ||  ||
 *  V
 *  Z
 * ```
 */
val corridorStraight = wangTile7x3x7 { x, y, z ->
    when {
        x == 2 || x == 4 -> WALL
        else -> AIR
    }
}
/**
 * ```
 *  Y
 *  ^
 *  |      ||  || outside
 *  |      ||  ||
 *  | ==== ground ====
 *  +----------------> X (East)
 * Z
 * ```
 */
val corridorOnGroundStraight = wangTile7x3x7 { x, y, z ->
    when {
        y == 0 -> GROUND
        y > 0 -> corridorStraight[x, y, z]
        else -> AIR
    }
}
/**
 * ```
 *  Y
 *  ^
 *  |      outside
 *  |      ||==||
 *  |      ||  ||
 *  +----------------> X (East)
 * Z
 * ```
 */
val corridorTopStraight = wangTile7x3x7 { x, y, z ->
    when {
        y == 1 && x == 3 -> FLOOR
        y < 2 -> corridorStraight[x, y, z]
        else -> AIR
    }
}