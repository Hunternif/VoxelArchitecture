package hunternif.voxarch.wfc.tiled

import hunternif.voxarch.storage.IArray3D
import hunternif.voxarch.util.*
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.wfc.WfcSlot
import hunternif.voxarch.wfc.WfcModel

// Famous demos by Marian42 and Oskar Stalberg use 3d tiles of static size.
// Since I want my structures style-able via "CSS", I'll use abstract
// representations of tiles, without explicit block sizes.
// I'll use "tile size" for measuring distance.

// "WFC tile" is a 3d cube.
// Different types of content inside the tile are represented by colored voxels.
// So the tile is essentially a NxKxN storage of [WfcVoxel].
// During execution of WFC the constraint solver will match the voxels on
// adjacent tiles, so that wall connects to a wall, air connects to air etc.
// Voxels can also be easily rendered to pixels in snapshot tests.


// I will start with N = 3. This number can be changed at any point.
// But 3 seems like it would require a relatively small number of manual
// definitions, and many variants can be generated by rotating and mirroring.

// Imagine a cross-section of a castle corridor, with walls on the left and right.
// It would be composed of 2 tiles, each with a wall in the middle.

class WfcTiledModel<T: WfcTile>(
    width: Int,
    height: Int,
    length: Int,
    tileset: Collection<T>,
    seed: Long = 0L
) : WfcModel<T, T, WfcSlot<T>>(width, height, length, tileset, seed),
    IArray3D<T?>
{
    override val wave: Array3D<WfcSlot<T>> =
        Array3D(width, height, length) {
            x, y, z ->
            WfcSlot(IntVec3(x, y, z), patternSet.toMutableSet()).also {
                it.entropy = initialEntropy
                unobservedSet.add(it)
            }
        }

    override fun WfcSlot<T>.isObserved() = state != null

    override fun WfcSlot<T>.observe() {
        setState(rand.nextWeighted(possiblePatterns))
    }

    override fun WfcSlot<T>.relaxNeighbors() {
        for (nextSlot in allDirections()) {
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

    override fun WfcSlot<T>.constrainNeighbors() {
        for (nextSlot in allDirections()) {
            if (nextSlot.state == null && nextSlot.constrain()) {
                constrainQueue.add(nextSlot)
                nextSlot.updateEntropy()
            }
        }
    }

    /** Removes from "possiblePatterns" any patterns that can't be matched to
     * its neighbors. Returns true if at least 1 pattern was removed. */
    private fun WfcSlot<T>.constrain(): Boolean {
        val originalCount = possiblePatterns.size
        val directions = Direction3D.values()
            .filter { pos.facing(it) in wave }
            .sortedBy { wave[pos.facing(it)].possiblePatterns.size }
        for (dir in directions) {
            val adjSlot = wave[pos.facing(dir)]
            possiblePatterns.removeIf { state ->
                adjSlot.state?.let { !state.matchesSide(it, dir) } ?:
                adjSlot.possiblePatterns.none { state.matchesSide(it, dir) }
            }
        }
        val newCount = possiblePatterns.size
        if (newCount < originalCount) {
            if (newCount == 1) setState(possiblePatterns.first())
            return true
        }
        return false
    }

    private fun WfcSlot<T>.setState(newState: T?) {
        if (newState == null) {
            // reset (undo collapse)
            // (this potentially resets other partially-constrained slots)
            if (state != null) {
                state = null
                possiblePatterns.addAll(patternSet)
                updateEntropy()
                relaxQueue.add(this)
            }
        } else {
            // collapse
            // (the new state can be potentially incompatible with neighbors)
            if (state != newState) {
                state = newState
                possiblePatterns.clear()
                updateEntropy()
                constrainQueue.add(this)
            }
        }
    }

    /** Guaranteed to be contained inside [wave] */
    private fun WfcSlot<T>.allDirections(): Sequence<WfcSlot<T>> = sequence {
        pos.run {
            if (y > 0) yield(wave[x, y-1, z])
            if (y < height-1) yield(wave[x, y+1, z])
            if (z > 0) yield(wave[x, y, z-1])
            if (x < width-1) yield(wave[x+1, y, z])
            if (z < length-1) yield(wave[x, y, z+1])
            if (x > 0) yield(wave[x-1, y, z])
        }
    }

    override operator fun iterator(): Iterator<IntVec3> = wave.iterator()
    override fun get(x: Int, y: Int, z: Int): T? = wave[x, y, z].state
    override fun get(p: IntVec3): T? = get(p.x, p.y, p.z)
    override fun set(x: Int, y: Int, z: Int, v: T?) { wave[x, y, z].setState(v) }
    override fun set(p: IntVec3, v: T?) { wave[p].setState(v) }
    override operator fun contains(p: IntVec3) = wave.contains(p)
    override fun contains(x: Int, y: Int, z: Int) = wave.contains(x, y, z)
    override val size: Int get() = wave.size
}