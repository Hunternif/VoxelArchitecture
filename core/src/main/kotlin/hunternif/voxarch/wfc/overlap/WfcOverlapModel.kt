package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.nextWeighted
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.wfc.WfcModel

// Implements the Overlapping Model

// 1. Analyze input and prepare patterns.
//   1.1. Cut up the input into patterns of given size NxKxN.
//        Using potentially different size for Y because patterns will be
//        rotated only around the Y axis, i.e. we don't want upside-down houses.
//   1.2. (optional) Create rotations and reflections.
//   1.3. Remove duplicate patterns.
// 2. Initialize the output with a set of possible patterns at every voxel pos.
// 3. Run WFC
//   3.1. Observe 1 voxel position (collapse it).
//   3.2. Propagate constraints to other patterns.
//
// Input analyzer will be a separate class


/**
 * @param patternSet all elements are assumed to be of the same size NxKxN
 * @param C is the voxel color in the final state.
 *          Must be non-null to distinguish observed empty state.
 */
class WfcOverlapModel<C: Any>(
    width: Int,
    height: Int,
    length: Int,
    patternSet: Collection<WfcPattern<C>>,
    seed: Long = 0L
) : WfcModel<WfcPattern<C>, WfcPattern<C>, WfcVoxel<C>>(
    width, height, length, patternSet, seed
), IStorage3D<C?> {

    private val patternSize = patternSet.first().let {
        IntVec3(it.width, it.height, it.length)
    }

    //TODO: set edge conditions. We can't have patterns on voxels on the edge,
    // because the pattern wouldn't fit in the output.
    // But in the beginning this is fine, it just leads to extra processing.
    override val wave: Array3D<WfcVoxel<C>> =
        Array3D(width, height, length) { x, y, z ->
            WfcVoxel(IntVec3(x, y, z), patternSet.toMutableSet()).also {
                it.entropy = initialEntropy
                unobservedSet.add(it)
            }
        }

    // [wave] contains slots of patterns, and each slot collapses into a pattern.

    // This is still a tiled model, but for patterns!
    // Maintain 2 matrices: 1 for voxels and 1 for patterns.
    // Collapse the lowest entropy voxel.
    // Patterns get collapsed as a result.

    override fun WfcVoxel<C>.isObserved() = color != null

    override fun WfcVoxel<C>.observe() {
        // sets only the color of the randomly chosen pattern
        setColor(rand.nextWeighted(possiblePatterns)[0, 0, 0])
    }

    override fun WfcVoxel<C>.relaxNeighbors() {
        for (nextSlot in findAffectedSlots()) {
            if (nextSlot.state == null) {
                if (nextSlot.possiblePatterns.addAll(patternSet)) {
                    relaxQueue.add(nextSlot)
                    nextSlot.updateEntropy()
                }
            } else {
                // will propagate back from here
                constrainQueue.add(nextSlot)
            }
        }
    }

    override fun WfcVoxel<C>.constrainNeighbors() {
        for (nextSlot in findAffectedSlots()) {
            if (nextSlot.state == null && nextSlot.constrainToMatch(this)) {
                constrainQueue.add(nextSlot)
                nextSlot.updateEntropy()
            }
        }
    }

    /** Removes from "possiblePatterns" any patterns that doesn't match the
     * color of the given [voxel]. [voxel] is assumed to be overlapping with the
     * patterns and its color is assumed to be nonnull.
     * Returns true if at least 1 pattern was removed. */
    private fun WfcVoxel<C>.constrainToMatch(voxel: WfcVoxel<C>): Boolean {
        val offset = voxel.pos.subtract(pos)
        val originalCount = possiblePatterns.size
        possiblePatterns.removeIf {
            it[offset] != voxel.color
        }
        // TODO optimize: update colors on overlapping voxels when possible.
        val newCount = possiblePatterns.size
        if (newCount == 1) setPattern(possiblePatterns.first())
        return newCount < originalCount
    }

    /** Returns all voxels that have patterns that overlap with this voxel. */
    private fun WfcVoxel<C>.findAffectedSlots(): Sequence<WfcVoxel<C>> = sequence {
        for (x in 1-patternSize.x..0)
            for (y in 1-patternSize.y..0)
                for (z in 1-patternSize.z..0)
                    if (pos.x + x >= 0 && pos.y + y >= 0 && pos.z + z >= 0
                        && !(x == 0 && y == 0 && z == 0) // exclude itself
                    ) yield(wave[pos.x + x, pos.y + y, pos.z + z])
    }

    /** Sets the color of this voxel. This may or may not restrict its patterns. */
    private fun WfcVoxel<C>.setColor(newColor: C?) {
        if (newColor == null) {
            if (color != null) {
                color = null
                possiblePatterns.addAll(patternSet)
                updateEntropy()
                relaxQueue.add(this)
            }
        } else {
            // We will consider only reducing the number of allowed patterns.
            // The new color may reduce allowed patterns all the way to 0.
            if (color != newColor) {
                color = newColor
                // Trim patterns to match the new color
                constrainToMatch(this)
                updateEntropy()
                constrainQueue.add(this)
            }
        }
    }

    /** Sets definite pattern on this voxel and applies its colors to all
     * overlapping voxels. */
    private fun WfcVoxel<C>.setPattern(pattern: WfcPattern<C>) {
        state = pattern
        for (p in pattern) {
            val wavePos = pos.add(p)
            if (wavePos in wave) wave[wavePos].setColor(pattern[p])
        }
    }

    override operator fun iterator(): Iterator<IntVec3> = wave.iterator()
    override fun get(x: Int, y: Int, z: Int): C? = wave[x, y, z].color
    override fun get(p: IntVec3): C? = get(p.x, p.y, p.z)
    override fun set(x: Int, y: Int, z: Int, v: C?) { wave[x, y, z].setColor(v) }
    override fun set(p: IntVec3, v: C?) { wave[p].setColor(v) }
    override operator fun contains(p: IntVec3) = wave.contains(p)
    override fun contains(x: Int, y: Int, z: Int) = wave.contains(x, y, z)
}