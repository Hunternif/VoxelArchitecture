package hunternif.voxarch.wfc.wang7x3x7

import hunternif.voxarch.wfc.WangVoxel.*

/**
 * ```
 * Y
 *  +------------> X (East)
 *  |
 *  |  =========
 *  |  ||     ||
 *  |  ||     ||
 *  |  ||     ||
 *  |  =========
 *  V
 *  Z
 * ```
 */
val tower = wangTile7x3x7 { x, y, z ->
    when {
        (x == 1 || x == 5) && z in 1..5 -> WALL
        (z == 1 || z == 5) && x in 1..5 -> WALL
        else -> AIR
    }
}

val towerOnGround = wangTile7x3x7 { x, y, z ->
    when {
        y == 0 -> GROUND
        y > 0 -> tower[x, y, z]
        else -> AIR
    }
}

//val towerOnFloor = wangTile7x7x3 { x, y, z ->
//    when {
//        y == 1 -> FLOOR
//        y > 1 -> tower[x, y, z]
//        else -> AIR
//    }
//}

val towerTop = wangTile7x3x7 { x, y, z ->
    when {
        y == 1 && x in 2..4 && z in 2..4 -> FLOOR
        y < 2 -> tower[x, y, z]
        else -> AIR
    }
}

// ----------- these are temporary help -----------
private val corridorStraightX = corridorStraight.rotateY90CW()
private val corridorOnGroundStraightX = corridorOnGroundStraight.rotateY90CW()
private val corridorTopStraightX = corridorTopStraight.rotateY90CW()
// ------------------------------------------------

/**
 * ```
 * Y
 *  +------------> X (East)
 *  |    || ||
 *  |  =========
 *  |  ||     ||
 *  |  ||     ||
 *  |  ||     ||
 *  |  =========
 *  V
 *  Z
 * ```
 */
val towerOneCorridor = wangTile7x3x7 { x, y, z ->
    when {
        z < 1 -> corridorStraight[x, y, z]
        else -> tower[x, y, z]
    }
}
val towerOneCorridorOnGround = wangTile7x3x7 { x, y, z ->
    when {
        z < 1 -> corridorOnGroundStraight[x, y, z]
        else -> towerOnGround[x, y, z]
    }
}
val towerOneCorridorTop = wangTile7x3x7 { x, y, z ->
    when {
        z < 1 -> corridorTopStraight[x, y, z]
        else -> tower[x, y, z]
    }
}

/**
 * ```
 * Y
 *  +------------> X (East)
 *  |    || ||
 *  |  =========
 *  | =||     ||
 *  |  ||     ||
 *  | =||     ||
 *  |  =========
 *  V
 *  Z
 * ```
 */
val towerL = wangTile7x3x7 { x, y, z ->
    when {
        x < 1 -> corridorStraightX[x, y, z]
        z < 1 -> corridorStraight[x, y, z]
        else -> tower[x, y, z]
    }
}
val towerLOnGround = wangTile7x3x7 { x, y, z ->
    when {
        x < 1 -> corridorOnGroundStraightX[x, y, z]
        z < 1 -> corridorOnGroundStraight[x, y, z]
        else -> towerOnGround[x, y, z]
    }
}
val towerLCorridorTop = wangTile7x3x7 { x, y, z ->
    when {
        x < 1 -> corridorTopStraightX[x, y, z]
        z < 1 -> corridorTopStraight[x, y, z]
        else -> tower[x, y, z]
    }
}

/**
 * ```
 * Y
 *  +------------> X (East)
 *  |    || ||
 *  |  =========
 *  |  ||     ||
 *  |  ||     ||
 *  |  ||     ||
 *  |  =========
 *  |    || ||
 *  V
 *  Z
 * ```
 */
val towerI = wangTile7x3x7 { x, y, z ->
    when {
        z < 1 || z > 5 -> corridorStraight[x, y, z]
        else -> tower[x, y, z]
    }
}
val towerIOnGround = wangTile7x3x7 { x, y, z ->
    when {
        z < 1 || z > 5 -> corridorOnGroundStraight[x, y, z]
        else -> towerOnGround[x, y, z]
    }
}
val towerICorridorTop = wangTile7x3x7 { x, y, z ->
    when {
        z < 1 || z > 5 -> corridorTopStraight[x, y, z]
        else -> tower[x, y, z]
    }
}

/**
 * ```
 * Y
 *  +------------> X (East)
 *  |    || ||
 *  |  =========
 *  | =||     ||=
 *  |  ||     ||
 *  | =||     ||=
 *  |  =========
 *  V
 *  Z
 * ```
 */
val towerT = wangTile7x3x7 { x, y, z ->
    when {
        x < 1 || x > 5 -> corridorStraightX[x, y, z]
        z < 1 -> corridorStraight[x, y, z]
        else -> tower[x, y, z]
    }
}
val towerTOnGround = wangTile7x3x7 { x, y, z ->
    when {
        x < 1 || x > 5 -> corridorOnGroundStraightX[x, y, z]
        z < 1 -> corridorOnGroundStraight[x, y, z]
        else -> towerOnGround[x, y, z]
    }
}
val towerTCorridorTop = wangTile7x3x7 { x, y, z ->
    when {
        x < 1 || x > 5 -> corridorTopStraightX[x, y, z]
        z < 1 -> corridorTopStraight[x, y, z]
        else -> tower[x, y, z]
    }
}

/**
 * ```
 * Y
 *  +------------> X (East)
 *  |    || ||
 *  |  =========
 *  | =||     ||=
 *  |  ||     ||
 *  | =||     ||=
 *  |  =========
 *  |    || ||
 *  V
 *  Z
 * ```
 */
val towerX = wangTile7x3x7 { x, y, z ->
    when {
        x < 1 || x > 5 -> corridorStraightX[x, y, z]
        z < 1 || z > 5 -> corridorStraight[x, y, z]
        else -> tower[x, y, z]
    }
}
val towerXOnGround = wangTile7x3x7 { x, y, z ->
    when {
        x < 1 || x > 5 -> corridorOnGroundStraightX[x, y, z]
        z < 1 || z > 5 -> corridorOnGroundStraight[x, y, z]
        else -> towerOnGround[x, y, z]
    }
}
val towerXCorridorTop = wangTile7x3x7 { x, y, z ->
    when {
        x < 1 || x > 5 -> corridorTopStraightX[x, y, z]
        z < 1 || z > 5 -> corridorTopStraight[x, y, z]
        else -> tower[x, y, z]
    }
}