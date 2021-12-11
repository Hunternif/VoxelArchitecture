package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.vector.IntVec2
import hunternif.voxarch.wfc.WfcColor

/**
 * @param patternSize defines the width and height of a NxKxN pattern.
 */
fun <C> IStorage3D<C>.findPatterns(
    patternSize: IntVec2
): Collection<WfcPattern<C>> {
    //TODO implement pattern analyzer
    return emptyList()
}