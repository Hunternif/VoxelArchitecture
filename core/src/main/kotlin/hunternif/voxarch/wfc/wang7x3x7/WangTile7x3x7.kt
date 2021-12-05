package hunternif.voxarch.wfc.wang7x3x7

import hunternif.voxarch.wfc.WangTile
import hunternif.voxarch.wfc.WangVoxel
import hunternif.voxarch.wfc.WangVoxel.*

internal fun wangTile7x3x7(init: (x: Int, y: Int, z: Int) -> WangVoxel) = WangTile(7, 3, 7, init)
internal fun wangTile7x3x7(vx: WangVoxel) = wangTile7x3x7 { _, _, _ -> vx }

val air = wangTile7x3x7(AIR)
val ground = wangTile7x3x7(GROUND)
val groundedAir = wangTile7x3x7 { _, y, _ -> if (y == 0) GROUND else AIR }
// floor runs through the middle to allow connections from below
val floor = wangTile7x3x7 { _, y, _ -> if (y == 1) FLOOR else AIR }

// This tileset has different kinds of interiors:
// - narrow corridors
// - wider square towers
// - large rooms
//
// corridor:
// ..@.@..
// ..@.@..
//
// room & tower:
// .......
// .@@@@@.
// .@...@.
//

fun generateValidTiles7x3x7(): List<WangTile> = listOf(
    corridorStraight         % 2,
    corridorOnGroundStraight % 1,
    corridorTopStraight      % 1,
    towerOneCorridor         % 0.01,
    towerOneCorridorOnGround % 0.01,
    towerOneCorridorTop      % 0.01,
    towerL,
    towerLOnGround,
    towerLCorridorTop,
    towerI                  % 0.7,
    towerIOnGround          % 0.7,
    towerICorridorTop       % 0.7,
    towerT                  % 1,
    towerTOnGround          % 1,
    towerTCorridorTop       % 1
).flatMap { it.generateRotationsY() } + listOf(
    air             % 15,
    floor,
    groundedAir, // this is necessary to build on the ground
    ground,
    tower           % 8,
    towerOnGround   % 0.1,
    towerTop,
    towerX          % 1,
    towerXOnGround  % 1,
    towerXCorridorTop % 1
)