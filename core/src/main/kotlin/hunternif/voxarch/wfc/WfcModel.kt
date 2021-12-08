package hunternif.voxarch.wfc

import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.IRandomOption
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntVec3
import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.math.log
import kotlin.random.Random

/**
 * A unit on the initial WFC grid in observed or unobserved state.
 */
class WfcSlot<S, P>(
    val pos: IntVec3,
    val possiblePatterns: MutableSet<P>,
    /** Final collapsed state of this slot */
    var state: S? = null,
    var entropy: Double = 1.0
) {
    override fun hashCode(): Int = pos.hashCode()
    override fun equals(other: Any?): Boolean =
        other is WfcSlot<*, *> && pos == other.pos
}

/**
 * Contains the general structure of the WFC algorithm that's common to both
 * the Tiled and the Overlap versions.
 * @param S "observed state" at every position on the final grid.
 *          For the Tiled model it's a tile,
 *          for the Overlap model it's a single voxel.
 * @param P "pattern", one of the possible variants at every position.
 *          For the Tiled model it's a tile again,
 *          for the Overlap model it's a pattern.
 */
abstract class WfcModel<S, P : IRandomOption>(
    final override val width: Int,
    final override val height: Int,
    final override val length: Int,
    private val patternSet: Collection<P>,
    seed: Long = 0L
) : IStorage3D<S?> {
    protected val rand = Random(seed)
    private val totalCount = width * height * length
    protected val wave: Array3D<WfcSlot<S, P>> by lazy {
        val initialEntropy = calculateEntropy(patternSet)
        Array3D(width, height, length) { x, y, z ->
            WfcSlot<S, P>(IntVec3(x, y, z), patternSet.toMutableSet()).also {
                it.entropy = initialEntropy
                unobservedSet.add(it)
            }
        }
    }

    /** Queue for propagating collapsed state (as opposed to null state). */
    private val constrainQueue = LinkedHashSet<WfcSlot<S, P>>()
    /** Queue for propagating null state, i.e. slots that have been reset. */
    private val relaxQueue = LinkedHashSet<WfcSlot<S, P>>()
    /** Contains slots that haven't collapsed yet, sorted by entropy */
    private val unobservedSet = TreeSet<WfcSlot<S, P>> { t1, t2 ->
        val entropyDiff = t1.entropy.compareTo(t2.entropy)
        // Distinguish between patterns with equal entropy values
        if (entropyDiff == 0) t1.hashCode().compareTo(t2.hashCode())
        else entropyDiff
    }

    internal val collapsedCount: Int get() = totalCount - unobservedSet.size
    val isCollapsed: Boolean get() = unobservedSet.size <= 0
    var isContradicted: Boolean = false
        private set

    /** Selects a single state based on what is possible at this slot. */
    protected abstract fun WfcSlot<S, P>.selectDefiniteState(): S

    /** Removes from "possiblePatterns" any patterns that can't be matched to
     * its neighbors. Returns true if at least 1 state was removed. */
    protected abstract fun WfcSlot<S, P>.constrainPatterns(): Boolean

    /**
     * Performs WFC until the entire grid is collapsed or until contradiction.
     */
    fun observe() {
        while (!isCollapsed && !isContradicted) observeStep()
    }

    /**
     * Performs 1 step of the Wave Function Collapse algo:
     * - picks a slot with the lowest entropy and collapses it
     * - propagates constraints resulting from this collapse
     */
    fun observeStep() {
        if (isCollapsed) {
            println("Nothing to collapse!")
            return
        }
        val slot = unobservedSet.first()
        if (slot.entropy <= 0f) {
            isContradicted = true
            println("Contradiction!")
            return
        }
        slot.setState(slot.selectDefiniteState())
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
                    if (nextSlot.possiblePatterns.addAll(patternSet)) {
                        relaxQueue.add(slot)
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
                if (nextSlot.state == null && nextSlot.constrainPatterns()) {
                    constrainQueue.add(nextSlot)
                    nextSlot.updateEntropy()
                }
            }
        }
    }

    protected fun WfcSlot<S, P>.setState(newState: S?) {
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

    private fun calculateEntropy(possibleStates: Collection<P>): Double {
        val sumTotal = possibleStates.sumOf { it.probability }
        return if (possibleStates.size <= 1) 0.0
        else possibleStates.sumOf {
            -it.probability * log(it.probability/sumTotal, 2.0)
        }
    }

    private fun WfcSlot<S, P>.updateEntropy() {
        unobservedSet.remove(this)
        // update entropy after removal, because it defines position in TreeSet
        entropy = calculateEntropy(possiblePatterns)
        if (state == null) unobservedSet.add(this)
    }

    /** Guaranteed to be contained inside [wave] */
    private fun WfcSlot<S, P>.allDirections(): Sequence<WfcSlot<S, P>> = sequence {
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
     * when you know you will only need to propagate from the given point. */
    internal fun resetPropagationTo(x: Int, y: Int, z: Int) {
        constrainQueue.clear()
        constrainQueue.add(wave[x, y, z])
    }

    override operator fun iterator(): Iterator<IntVec3> = wave.iterator()
    override fun get(x: Int, y: Int, z: Int): S? = wave[x, y, z].state
    override fun get(p: IntVec3): S? = get(p.x, p.y, p.z)
    override fun set(x: Int, y: Int, z: Int, v: S?) { wave[x, y, z].setState(v) }
    override fun set(p: IntVec3, v: S?) { wave[p].setState(v) }
    override operator fun contains(p: IntVec3) = wave.contains(p)
    override fun contains(x: Int, y: Int, z: Int) = wave.contains(x, y, z)

    fun getPossiblePatterns(p: IntVec3): Set<P> = getPossiblePatterns(p.x, p.y, p.z)
    fun getPossiblePatterns(x: Int, y: Int, z: Int): Set<P> = wave[x, y, z].possiblePatterns
}