package hunternif.voxarch.wfc

import hunternif.voxarch.storage.IVoxel

/** WFC tiles contain a 3d array of these colors.
 * When tiles are being matched, these colors are checked for equality. */
enum class WfcColor : IVoxel {
    AIR, GROUND, WALL, FLOOR
}