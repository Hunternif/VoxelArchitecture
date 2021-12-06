package hunternif.voxarch.wfc

import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.*
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntVec3
import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.math.log
import kotlin.random.Random

// Famous demos by Marian42 and Oskar Stalberg use 3d tiles of static size.
// Since I want my structures style-able via "CSS", I'll use abstract
// representations of tiles, without explicit block sizes.
// I'll use "tile size" for measuring distance.

// "WFC tile" is a 3d cube.
// Different types of content inside the tile are represented by colored voxels.
// So the tile is essentially a NxNxN storage of [WfcVoxel].
// During execution of WFC the constraint solver will match the voxels on
// adjacent tiles, so that wall connects to a wall, air connects to air etc.
// Voxels can also be easily rendered to pixels in snapshot tests.


// I will start with N = 3. This number can be changed at any point.
// But 3 seems like it would require a relatively small number of manual
// definitions, and many variants can be generated by rotating and mirroring.

// Imagine a cross-section of a castle corridor, with walls on the left and right.
// It would be composed of 2 tiles, each with a wall in the middle.

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

/** A single cell in the wave's 3d grid. */
private data class WfSlot<T: WfcTile>(
    val pos: IntVec3,
    val possibleStates: MutableSet<T>,
    /** Final collapsed state of this slot */
    var state: T? = null,
    var entropy: Double = 1.0
) {
    override fun hashCode(): Int = pos.hashCode()
    override fun equals(other: Any?): Boolean =
        other is WfSlot<*> && pos == other.pos
}

class WfcGrid<T: WfcTile>(
    override val width: Int,
    override val height: Int,
    override val length: Int,
    private val tileset: List<T>,
    seed: Long = 0L
): IStorage3D<T?> {
    private val rand = Random(seed)
    private val totalCount = width * height * length
    private val wave by lazy {
        val initialEntropy = calculateEntropy(tileset)
        Array3D(width, height, length) { x, y, z ->
            WfSlot(IntVec3(x, y, z), tileset.toMutableSet()).also {
                it.entropy = initialEntropy
                uncollapsedSet.add(it)
            }
        }
    }
    /** Queue for propagating collapsed state (as opposed to null state). */
    private val constrainQueue = LinkedHashSet<WfSlot<T>>()
    /** Queue for propagating null state, i.e. removed tiles that had been
     * previously collapsed. */
    private val relaxQueue = LinkedHashSet<WfSlot<T>>()
    /** Contains slots that haven't collapsed yet, sorted by entropy */
    private val uncollapsedSet = TreeSet<WfSlot<T>> { t1, t2 ->
        val entropyDiff = t1.entropy.compareTo(t2.entropy)
        // Distinguish between slots with equal entropy values
        if (entropyDiff == 0) t1.hashCode().compareTo(t2.hashCode())
        else entropyDiff
    }

    internal val collapsedCount: Int get() = totalCount - uncollapsedSet.size
    val isCollapsed: Boolean get() = uncollapsedSet.size <= 0
    var isContradicted: Boolean = false
        private set

    fun collapse() {
        while (!isCollapsed && !isContradicted) collapseStep()
    }

    /**
     * Performs 1 step of the Wave Function Collapse algo:
     * - picks a slot with the lowest entropy and collapses it
     * - propagates constraints resulting from this collapse
     */
    fun collapseStep() {
        if (isCollapsed) {
            println("Nothing to collapse!")
            return
        }
        val slot = uncollapsedSet.first()
        if (slot.entropy <= 0f) {
            isContradicted = true
            println("Contradiction!")
            return
        }
        slot.setState(rand.nextWeighted(slot.possibleStates))
        propagate()
    }

    /**
     * Propagates constraints to the entire grid from all collapsed points.
     */
    fun propagate() {
        propagateRelaxation()
        propagateConstraints()
    }

    /**
     * This should be called after any state is reset to null. This relaxes
     * constraints on all slots reachable from [relaxQueue].
     * Any states that had been previously removed may become possible again.
     */
    private fun propagateRelaxation() {
        while (relaxQueue.isNotEmpty()) {
            val slot = relaxQueue.first().also { relaxQueue.remove(it) }
            for (nextSlot in slot.allDirections()) {
                if (nextSlot.state == null) {
                    if (nextSlot.possibleStates.addAll(tileset)) {
                        relaxQueue.add(nextSlot)
                        nextSlot.updateEntropy()
                    }
                } else {
                    // will propagate back from here
                    constrainQueue.add(nextSlot)
                }
            }
        }
    }

    /**
     * Propagates constraints from collapsed slots.
     * If any slot has only 1 possible state left, collapses it.
     */
    private fun propagateConstraints() {
        while (constrainQueue.isNotEmpty()) {
            val slot = constrainQueue.first().also { constrainQueue.remove(it) }
            for (nextSlot in slot.allDirections()) {
                if (nextSlot.state == null && constrainStates(nextSlot)) {
                    constrainQueue.add(nextSlot)
                }
            }
        }
    }

    /**
     * Removes from "possibleStates" any states that can't be matched to its
     * neighbors. Returns true if at least 1 state was removed.
     */
    private fun constrainStates(slot: WfSlot<T>): Boolean {
        val originalCount = slot.possibleStates.size
        val directions = values()
            .filter { slot.pos.facing(it) in wave }
            .sortedBy { wave[slot.pos.facing(it)].possibleStates.size }
        for (dir in directions) {
            val adjSlot = wave[slot.pos.facing(dir)]
            slot.possibleStates.removeIf { state ->
                adjSlot.possibleStates.none { state.matchesSide(it, dir) }
            }
        }
        val newCount = slot.possibleStates.size
        if (newCount < originalCount) {
            if (newCount == 1) slot.setState(slot.possibleStates.first())
            else slot.updateEntropy()
            return true
        }
        return false
    }

    override operator fun iterator(): Iterator<IntVec3> = wave.iterator()
    override fun get(x: Int, y: Int, z: Int): T? = wave[x, y, z].state
    override fun get(p: IntVec3): T? = get(p.x, p.y, p.z)
    override fun set(x: Int, y: Int, z: Int, v: T?) { wave[x, y, z].setState(v) }
    override fun set(p: IntVec3, v: T?) { wave[p].setState(v) }
    override operator fun contains(p: IntVec3) = wave.contains(p)
    override fun contains(x: Int, y: Int, z: Int) = wave.contains(x, y, z)

    fun getPossibleStates(p: IntVec3): Set<T> = getPossibleStates(p.x, p.y, p.z)
    fun getPossibleStates(x: Int, y: Int, z: Int): Set<T> = wave[x, y, z].possibleStates

    private fun WfSlot<T>.setState(newState: T?) {
        if (newState == null) {
            // reset (undo collapse)
            // (this potentially resets other partially-constrained slots)
            if (state != null) {
                possibleStates.addAll(tileset)
                state = null
                updateEntropy()
                relaxQueue.add(this)
            }
        } else {
            // collapse
            // (this potentially inserts an incompatible tile)
            state = newState
            possibleStates.clear()
            possibleStates.add(newState)
            updateEntropy()
            constrainQueue.add(this)
        }
    }

    private fun calculateEntropy(possibleStates: Collection<WfcTile>): Double {
        val sumTotal = possibleStates.sumOf { it.probability }
        return if (possibleStates.size <= 1) 0.0
        else possibleStates.sumOf {
            -it.probability * log(it.probability/sumTotal, 2.0)
        }
    }

    private fun WfSlot<T>.updateEntropy() {
        uncollapsedSet.remove(this)
        // update entropy after removal, because it defines position in TreeSet
        entropy = calculateEntropy(possibleStates)
        if (state == null) uncollapsedSet.add(this)
    }

    /** Guaranteed to be contained inside [wave] */
    private fun WfSlot<T>.allDirections(): Sequence<WfSlot<T>> = sequence {
        pos.run {
            if (y > 0) yield(wave[x, y-1, z])
            if (y < height-1) yield(wave[x, y+1, z])
            if (z > 0) yield(wave[x, y, z-1])
            if (x < width-1) yield(wave[x+1, y, z])
            if (z < length-1) yield(wave[x, y, z+1])
            if (x > 0) yield(wave[x-1, y, z])
        }
    }

    /** This is an optimization cheat, to clear the constraint queue
     * when you know you will only need to propagate from 1 point. */
    internal fun resetPropagation() { constrainQueue.clear() }
}