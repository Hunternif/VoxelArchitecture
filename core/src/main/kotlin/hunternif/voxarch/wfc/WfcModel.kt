package hunternif.voxarch.wfc

import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntVec3
import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.random.Random

/**
 * A unit on the WFC grid in observed or unobserved state.
 */
abstract class WfcSlot<T>(
    val pos: IntVec3,
    /** Final collapsed state of this voxel */
    var state: T? = null,
    var entropy: Double = 1.0
) {
    override fun hashCode(): Int = pos.hashCode()
    override fun equals(other: Any?): Boolean =
        other is WfcSlot<*> && pos == other.pos
}

/**
 * Contains the general structure of the WFC algorithm that's common to both
 * the Tiled and the Overlap versions.
 * @param T unit on the final grid in observed state.
 *          For the Tiled model it's a tile,
 *          for the Overlap model it's a single voxel.
 * @param Slot unit on the initial grid, either observed or unobserved.
 */
abstract class WfcModel<T, Slot: WfcSlot<T>>(
    val width: Int,
    val height: Int,
    val length: Int,
    seed: Long = 0L
) {
    protected val rand = Random(seed)
    private val totalCount = width * height * length
    protected abstract val wave: Array3D<Slot>

    /** Queue for propagating collapsed state (as opposed to null state). */
    private val constrainQueue = LinkedHashSet<Slot>()
    /** Queue for propagating null state, i.e. slots that have been reset. */
    private val relaxQueue = LinkedHashSet<Slot>()
    /** Contains slots that haven't collapsed yet, sorted by entropy */
    protected val unobservedSet = TreeSet<Slot> { t1, t2 ->
        val entropyDiff = t1.entropy.compareTo(t2.entropy)
        // Distinguish between voxels with equal entropy values
        if (entropyDiff == 0) t1.hashCode().compareTo(t2.hashCode())
        else entropyDiff
    }

    internal val collapsedCount: Int get() = totalCount - unobservedSet.size
    val isCollapsed: Boolean get() = unobservedSet.size <= 0
    var isContradicted: Boolean = false
        private set

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

    /** Selects a single state based on what is possible at this slot. */
    protected abstract fun Slot.selectDefiniteState(): T

    /** Updates any superposition data to match this new definite state. */
    protected abstract fun Slot.setDefiniteState(newState: T)

    /** Reset this slot back to its original superposition of all states.
     * Returns true if at least 1 state was added. */
    protected abstract fun Slot.relaxConstraints(): Boolean

    /** Removes from "possibleStates" any states that can't be matched to its
     * neighbors. Returns true if at least 1 state was removed. */
    protected abstract fun Slot.constrainStates(): Boolean

    /** Propagates constraints to the entire grid from all collapsed points. */
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
                    if (nextSlot.relaxConstraints()) {
                        relaxQueue.add(slot)
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
                if (nextSlot.state == null && nextSlot.constrainStates()) {
                    constrainQueue.add(nextSlot)
                }
            }
        }
    }

    protected fun Slot.setState(newState: T?) {
        if (newState == null) {
            // reset (undo collapse)
            // (this potentially resets other partially-constrained slots)
            if (state != null) {
                relaxConstraints()
                relaxQueue.add(this)
            }
        } else {
            // collapse
            // (this potentially inserts an incompatible tile)
            setDefiniteState(newState)
            constrainQueue.add(this)
        }
    }

    /** Guaranteed to be contained inside [wave] */
    private fun Slot.allDirections(): Sequence<Slot> = sequence {
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