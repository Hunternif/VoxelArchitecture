package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.storage.IArray3D
import hunternif.voxarch.util.*
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntVec3

/**
 * @param patternWidth width N of a NxKxN pattern.
 * @param patternHeight height K of a NxKxN pattern.
 */
inline fun <reified C> IArray3D<C>.findPatterns(
    patternWidth: Int,
    patternHeight: Int
): List<WfcPattern<C>> {
    // The hashCode & equals will automatically remove duplicates
    // Maps pattern to number of occurrences
    val patterns = LinkedHashMap<PatternData<C>, Int>()
    for (x in 0 .. width-patternWidth)
        for (z in 0 .. length-patternWidth)
            for (y in 0 .. height-patternHeight) {
                val pattern = PatternData(copySection(
                    IntVec3(x, y, z),
                    IntVec3(patternWidth, patternHeight, patternWidth)
                ))
                pattern.generateVariations().forEach {
                    val count = patterns[it] ?: 0
                    patterns[it] = count + 1
                }
            }
    return patterns.map { entry ->
        WfcPattern(
            entry.key.data,
            entry.key.probability * entry.value
        )
    }
}

/** Temporarily stores pattern data for the purpose of trimming duplicates. */
@PublishedApi
internal class PatternData<C>(
    val data: Array3D<C>,
    var probability: Double = 1.0
) : IArray3D<C> by data {
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
            hash = 31 * hash + x + (z shl 16) + (y shl 25) + v.hashCode()
        }
        return hash
    }
}

/** Returns rotations and reflections of this pattern around the Y axis */
@PublishedApi
internal inline fun <reified C> PatternData<C>.generateVariations()
: List<PatternData<C>> {
    val symmetricX = data.isSymmetricX()
    val symmetricZ = data.isSymmetricZ()
    return if (symmetricX && symmetricZ) {
        val t1 = this % (probability / 2f)
        listOf(t1, t1.rotateY90CW())
    } else {
        mutableListOf<PatternData<C>>().apply {
            addAll(fourRotationsY())
            if (symmetricX) {
                addAll(mirrorZ().fourRotationsY())
            } else {
                addAll(mirrorX().fourRotationsY())
            }
            val newProbability = probability / size.toDouble()
            forEach { it.probability = newProbability }
        }
    }
}

@PublishedApi
internal inline fun <reified C> PatternData<C>.fourRotationsY()
: List<PatternData<C>> {
    val t1 = this
    val t2 = t1.rotateY90CW()
    val t3 = t2.rotateY90CW()
    val t4 = t3.rotateY90CW()
    return listOf(t1, t2, t3, t4)
}

@PublishedApi
internal inline fun <reified C> PatternData<C>.mirrorX() =
    PatternData(data.mirrorX(), probability)

@PublishedApi
internal inline fun <reified C> PatternData<C>.mirrorZ() =
    PatternData(data.mirrorZ(), probability)

@PublishedApi
internal inline fun <reified C> PatternData<C>.rotateY90CW() =
    PatternData(data.rotateY90CW(), probability)

/** Creates a tile with the same data and the given probability. */
@PublishedApi
internal inline operator fun <reified C> PatternData<C>.rem(probability: Number) =
    PatternData(data, probability.toDouble())