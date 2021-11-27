package hunternif.voxarch.wfc

import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.allDirections
import hunternif.voxarch.util.facing
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntVec3
import java.util.*
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
interface WfcTile {
    /**
     * Returns true if this tile matches to [other] tile that is placed adjacent
     * to it from direction [dir].
     */
    fun matchesSide(other: WfcTile, dir: Direction3D): Boolean
}

/** A single cell in the wave's 3d grid. */
private class WfSlot<T: WfcTile>(
    val possibleStates: MutableSet<T>
) {
    /** Final collapsed state of this slot */
    internal var state: T? = null
    fun entropy(): Float =
        if (possibleStates.size <= 1) 0f
        else -log(1f/possibleStates.size.toFloat(), 2f)
}

class WfcGrid<T: WfcTile>(
    override val width: Int,
    override val height: Int,
    override val length: Int,
    private val tileset: List<T>,
    seed: Long = 0L
): IStorage3D<T?> {
    private val rand = Random(seed)
    internal var collapsedCount = 0
    private val totalCount = width * height * length
    private val wave by lazy {
        Array3D(width, height, length) {_, _, _ ->
            WfSlot(tileset.toMutableSet())
        }
    }
    /** Queue for propagating collapsed state (as opposed to null state). */
    private val constrainQueue = LinkedList<IntVec3>()
    /** Queue for propagating null state, i.e. removed tiles that had been
     * previously collapsed. */
    private val relaxQueue = LinkedList<IntVec3>()

    val isCollapsed: Boolean get() = collapsedCount >= totalCount
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
        val pos = findLowestEntropyPos()
        if (pos == null) {
            isContradicted = true
            println("Contradiction!")
            return
        }
        this[pos] = wave[pos].possibleStates.random(rand)
        propagate()
    }

    /** Returns the position of the slot with the lowest non-zero entropy. */
    private fun findLowestEntropyPos(): IntVec3? {
        var min = Float.MAX_VALUE
        var argMin: IntVec3? = null
        wave.forEachIndexed { p, slot ->
            val entropy = slot.entropy()
            if (entropy > 0 && entropy < min) {
                argMin = p
                min = entropy
            }
        }
        return argMin
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
            val pos = relaxQueue.pop()
            for (p in pos.allDirections()) {
                if (p !in wave) continue
                if (wave[p].state == null) {
                    if (wave[p].possibleStates.addAll(tileset)) {
                        relaxQueue.add(p)
                    }
                } else {
                    // will propagate back from here
                    constrainQueue.add(p)
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
            val pos = constrainQueue.pop()
            for (p in pos.allDirections()) {
                if (p !in wave || wave[p].state != null) continue
                if (constrainStates(p)) {
                    constrainQueue.add(p)
                }
            }
        }
    }

    /**
     * Removes from "possibleStates" any states that can't be matched to its
     * neighbors. Returns true if at least 1 state was removed.
     */
    private fun constrainStates(pos: IntVec3): Boolean {
        val originalCount = wave[pos].possibleStates.size
        val directions = Direction3D.values()
            .filter { pos.facing(it) in wave }
            .sortedBy { wave[pos.facing(it)].possibleStates.size }
        for (dir in directions) {
            val adjSlot = wave[pos.facing(dir)]
            wave[pos].possibleStates.removeIf { state ->
                adjSlot.possibleStates.none { state.matchesSide(it, dir) }
            }
        }
        val newCount = wave[pos].possibleStates.size
        if (newCount == 1) this[pos] = wave[pos].possibleStates.first()
        return newCount < originalCount
    }

    override operator fun iterator(): Iterator<IntVec3> = wave.iterator()
    override fun get(x: Int, y: Int, z: Int): T? = wave[x, y, z].state
    override fun get(p: IntVec3): T? = get(p.x, p.y, p.z)
    override fun set(x: Int, y: Int, z: Int, value: T?) = set(IntVec3(x, y, z), value)
    override fun set(p: IntVec3, v: T?) {
        val slot = wave[p]
        if (v == null) {
            // reset (undo collapse)
            // (this potentially resets other partially-constrained slots)
            if (slot.state != null) {
                slot.possibleStates.addAll(tileset)
                slot.state = null
                collapsedCount--
                relaxQueue.add(p)
            }
        } else {
            // collapse
            // (this potentially inserts an incompatible tile)
            if (slot.state == null) collapsedCount++
            slot.state = v
            slot.possibleStates.clear()
            slot.possibleStates.add(v)
            constrainQueue.add(p)
        }
    }
    fun getPossibleStates(p: IntVec3): Set<T> = getPossibleStates(p.x, p.y, p.z)
    fun getPossibleStates(x: Int, y: Int, z: Int): Set<T> = wave[x, y, z].possibleStates
}