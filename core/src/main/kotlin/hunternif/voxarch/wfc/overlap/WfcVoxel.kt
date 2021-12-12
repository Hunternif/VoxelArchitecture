package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.wfc.WfcSlot

class WfcVoxel<C>(
    pos: IntVec3,
    possiblePatterns: MutableSet<WfcPattern<C>>,
    /** Color at this position. Can be definite even when the pattern is not. */
    internal var color: C? = null
) : WfcSlot<WfcPattern<C>>(pos, possiblePatterns)