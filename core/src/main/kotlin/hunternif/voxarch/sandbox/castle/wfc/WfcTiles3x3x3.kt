package hunternif.voxarch.sandbox.castle.wfc

import hunternif.voxarch.sandbox.castle.wfc.WfcVoxel.*

val air = WfcTile(AIR)
// floor runs through the middle to allow connections from below
val floor = WfcTile { _, y, _ -> if (y == 1) FLOOR else AIR }

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
val wallOnWallStraight = WfcTile(AIR).apply {
    for (x in 0 until 3) {
        for (y in 0 until 3) {
            this[x, y, 0] = AIR // inside
            this[x, y, 1] = WALL
            this[x, y, 2] = AIR // outside
        }
    }
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
val wallOnFloorStraight = WfcTile(AIR).apply {
    this[0, 2, 0] = AIR // inside
    this[1, 2, 0] = WALL
    this[0, 1, 0] = FLOOR
    this[1, 1, 0] = FLOOR
    copyAlongZ()
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
val wallTipStraight = WfcTile(AIR).apply {
    this[1, 0, 0] = WALL
    this[1, 1, 0] = WALL
    copyAlongZ()
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
val wallOnWallCorner = WfcTile(AIR).apply {
    this[0, 0, 0] = AIR // inside
    this[0, 0, 1] = WALL
    this[1, 0, 0] = WALL
    this[1, 0, 1] = WALL
    copyAlongY()
}
val wallOnFloorCorner = WfcTile(AIR).apply {
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
val wallTipCorner = WfcTile(AIR).apply {
    this[0, 0, 0] = AIR // inside
    this[0, 0, 1] = WALL
    this[1, 0, 0] = WALL
    this[1, 0, 1] = WALL

    this[0, 1, 0] = AIR // inside
    this[0, 1, 1] = WALL
    this[1, 1, 0] = WALL
    this[1, 1, 1] = WALL
}

fun generateValidTiles(): List<WfcTile> = mutableListOf(
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


/** Copy voxels from the layer Z=0 along the Z axis **/
private fun WfcTile.copyAlongZ() {
    for (z in 1 until 3)
        for (x in 0 until 3)
            for (y in 0 until 3)
                this[x, y, z] = this[x, y, 0]
}

/** Copy voxels from the layer Y=0 along the Y axis **/
private fun WfcTile.copyAlongY() {
    for (y in 1 until 3)
        for (x in 0 until 3)
            for (z in 0 until 3)
                this[x, y, z] = this[x, 0, z]
}

private fun WfcTile.mirrorY(): WfcTile = WfcTile { x, y, z -> this[x, 2-y, z] }

/** Creates a new tile by rotating this tile 90 degrees clockwise around the Y axis */
private fun WfcTile.rotateY90CW(): WfcTile = WfcTile { x, y, z -> this[z, y, 2-x] }

/** Returns 4 rotations of this tile around the Y axis */
private fun WfcTile.generateRotationsY(): List<WfcTile> {
    if (isSymmetricX() && isSymmetricZ()) {
        return listOf(this, this.rotateY90CW())
    }
    val t1 = this
    val t2 = t1.rotateY90CW()
    val t3 = t2.rotateY90CW()
    val t4 = t3.rotateY90CW()
    return listOf(t1, t2, t3, t4)
}