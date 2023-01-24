package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.storage.IArray3D
import hunternif.voxarch.util.forEachPos
import hunternif.voxarch.util.nextWeighted
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.wfc.WfcModel
import kotlin.random.Random

// Implements the Overlapping Model

// 1. Analyze input and prepare patterns.
//   1.1. Cut up the input into patterns of given size NxKxN.
//        Using potentially different size for Y because patterns will be
//        rotated only around the Y axis, i.e. we don't want upside-down houses.
//   1.2. (optional) Create rotations and reflections.
//   1.3. Remove duplicate patterns.
// 2. Initialize the output with a set of possible patterns at every voxel pos.
// 3. Run WFC
//   3.1. Observe 1 voxel position (collapse the pattern located at that voxel).
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
    depth: Int,
    patternSet: Collection<WfcPattern<C>>,
    seed: Long = 0L
) : WfcModel<WfcPattern<C>, WfcPattern<C>, WfcVoxel<C>>(
    width, height, depth, patternSet, seed
), IArray3D<C?> {

    private val patternSize = patternSet.first().let {
        IntVec3(it.width, it.height, it.depth)
    }

    //TODO: set edge conditions. We can't have patterns on voxels on the edge,
    // because the pattern wouldn't fit in the output.
    // But in the beginning this is fine, it just leads to extra processing.
    override val wave: Array3D<WfcVoxel<C>> =
        Array3D(width, height, depth) { x, y, z ->
            WfcVoxel(IntVec3(x, y, z), patternSet.toMutableSet()).also {
                it.entropy = initialEntropy
                unobservedSet.add(it)
            }
        }

    override fun WfcVoxel<C>.isObserved() = state != null

    override fun WfcVoxel<C>.observe() {
        setPattern(rand.nextWeighted(possiblePatterns))
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
            if (isContradicted) break
            if (nextSlot.state == null && nextSlot.constrain()) {
                constrainQueue.add(nextSlot)
                nextSlot.updateEntropy()
            }
        }
    }

    /** Removes from "possiblePatterns" any pattern that doesn't match all
     * overlapping voxels. We consider what colors are possible at all positions
     * that overlap with this pattern, and ensure that this pattern matches with
     * at least one pattern.
     * Returns true if at least 1 pattern was removed. */
    private fun WfcVoxel<C>.constrain(): Boolean {
        val originalCount = possiblePatterns.size
        if (originalCount == 0) {
            isContradicted = true
            return false
        }
        // iterate over pattern domain space, assuming all patterns are equal size
        possiblePatterns.first().forEachPos { x, y, z, _ ->
            // ensure we are within wave bounds
            if (pos.x + x >= width || pos.y + y >= height || pos.z + z >= depth)
                return@forEachPos
            possiblePatterns.removeIf { pattern ->
                val patternColor = pattern[x, y, z]
                val slot = wave[pos.x + x, pos.y + y, pos.z + z]
                slot.color?.let { it != patternColor } ?:
                slot.possiblePatterns.none { it[0, 0, 0] == patternColor  }
            }
        }
        // TODO optimize: update colors on overlapping voxels when possible.
        val newCount = possiblePatterns.size
        if (newCount == 0) isContradicted = true
        else if (newCount == 1) setPattern(possiblePatterns.first())
        return newCount < originalCount
    }

    /** Returns all voxels that have patterns that overlap with this voxel.
     * Guaranteed to be contained in [wave]. */
    private fun WfcVoxel<C>.findAffectedSlots(): Sequence<WfcVoxel<C>> = sequence {
        for (x in 1-patternSize.x..0)
            for (y in 1-patternSize.y..0)
                for (z in 1-patternSize.z..0)
                    if (pos.x + x >= 0 && pos.y + y >= 0 && pos.z + z >= 0)
                        yield(wave[pos.x + x, pos.y + y, pos.z + z])
    }

    /** Sets the color of this voxel. This may or may not restrict its patterns. */
    private fun WfcVoxel<C>.setColor(newColor: C?) {
        if (newColor == null) {
            if (color != null) {
                color = null
                state = null
                if (possiblePatterns.addAll(patternSet)) {
                    updateEntropy()
                    relaxQueue.add(this)
                }
            }
        } else {
            // We will consider only reducing the number of allowed patterns.
            // The new color may reduce allowed patterns all the way to 0.
            if (color != newColor) {
                color = newColor
                // Trim patterns in affected area to match the new color
                findAffectedSlots().forEach { slot ->
                    val offset = pos.subtract(slot.pos)
                    if (slot.possiblePatterns.removeIf { it[offset] != color }) {
                        slot.updateEntropy()
                        constrainQueue.add(slot)
                    }
                }
            }
        }
    }

    /** Sets definite pattern on this voxel and applies its colors to all
     * overlapping voxels. */
    private fun WfcVoxel<C>.setPattern(pattern: WfcPattern<C>) {
        state = pattern
        pattern.forEachPos { x, y, z, patternColor ->
            if (pos.x + x < wave.width && pos.y + y < wave.height && pos.z + z < wave.depth) {
                wave[pos.x + x, pos.y + y, pos.z + z].setColor(patternColor)
            }
        }
        updateEntropy()
    }

    fun reset(seed: Long = 0L) {
        for (p in this) this[p] = null
        isContradicted = false
        rand = Random(seed)
    }

    override operator fun iterator(): Iterator<IntVec3> = wave.iterator()
    override fun get(x: Int, y: Int, z: Int): C? = wave[x, y, z].color
    override fun get(p: IntVec3): C? = get(p.x, p.y, p.z)
    override fun set(x: Int, y: Int, z: Int, v: C?) { wave[x, y, z].setColor(v) }
    override fun set(p: IntVec3, v: C?) { wave[p].setColor(v) }
    override operator fun contains(p: IntVec3) = wave.contains(p)
    override fun contains(x: Int, y: Int, z: Int) = wave.contains(x, y, z)
    override val size: Int get() = wave.size
}