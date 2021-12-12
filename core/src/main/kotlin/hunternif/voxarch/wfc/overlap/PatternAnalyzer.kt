package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.*
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntVec3

/**
 * @param patternWidth width N of a NxKxN pattern.
 * @param patternHeight height K of a NxKxN pattern.
 */
inline fun <reified C> IStorage3D<C>.findPatterns(
    patternWidth: Int,
    patternHeight: Int
): List<WfcPattern<C>> {
    // The hashCode & equals will automatically remove exact duplicates
    val patterns = LinkedHashSet<PatternData<C>>()
    for (x in 0 .. width-patternWidth)
        for (z in 0 .. length-patternWidth)
            for (y in 0 .. height-patternHeight)
                patterns.add(
                    PatternData(copySection(
                        IntVec3(x, y, z),
                        IntVec3(patternWidth, patternHeight, patternWidth)
                    ))
                )
    return patterns.map { WfcPattern(it.data) }
}

/** Temporarily stores pattern data for the purpose of trimming duplicates. */
@PublishedApi
internal class PatternData<C>(val data: Array3D<C>) : IStorage3D<C> by data {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PatternData<*>) return false
        if (other.width != this.width ||
            other.height != this.height ||
            other.length != this.length
        ) return false
        var matches = true
        forEachPos { x, y, z, c ->
            if (data[x, y, z] != c) {
                matches = false
                return@forEachPos
            }
        }
        return matches
    }

    override fun hashCode(): Int {
        var hash = 7
        forEachPos { x, y, z, v ->
            hash = 31 * hash + v.hashCode()
        }
        return hash
    }
}

internal inline fun <reified C> PatternData<C>.mirrorX() = PatternData(data.mirrorX())
internal inline fun <reified C> PatternData<C>.mirrorY() = PatternData(data.mirrorY())
internal inline fun <reified C> PatternData<C>.mirrorZ() = PatternData(data.mirrorZ())
internal inline fun <reified C> PatternData<C>.rotateY90CW() = PatternData(data.rotateY90CW())
internal inline fun <reified C> PatternData<C>.copy() = PatternData(data.copy())