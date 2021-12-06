package hunternif.voxarch.wfc

/** WFC tiles contain a 3d array of these colors.
 * When tiles are being matched, these colors are checked for equality. */
enum class WfcColor {
    AIR, GROUND, WALL, FLOOR
}