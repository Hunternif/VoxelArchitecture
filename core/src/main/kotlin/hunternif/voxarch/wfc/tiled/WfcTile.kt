package hunternif.voxarch.wfc.tiled

import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.IRandomOption

/**
 * This tile represents a possible final state of a slot in the wave's 3d grid.
 * Each slot will collapse into one of these tiles.
 */
interface WfcTile : IRandomOption {
    /**
     * Returns true if this tile matches to [other] tile that is placed adjacent
     * to it from direction [dir].
     */
    fun matchesSide(other: WfcTile, dir: Direction3D): Boolean
}