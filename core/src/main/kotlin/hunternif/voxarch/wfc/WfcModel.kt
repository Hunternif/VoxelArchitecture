package hunternif.voxarch.wfc

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
open class WfcSlot<P>(
    val pos: IntVec3,
    val possiblePatterns: MutableSet<P>,
    /** Final collapsed state of this slot */
    var state: P? = null,
    var entropy: Double = 1.0
) {
    override fun hashCode(): Int = pos.hashCode()
    override fun equals(other: Any?): Boolean =
        other is WfcSlot<*> && pos == other.pos
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
abstract class WfcModel<S, P : IRandomOption, Slot : WfcSlot<P>>(
    val width: Int,
    val height: Int,
    val depth: Int,
    protected val patternSet: Collection<P>,
    seed: Long = 0L
) {
    protected var rand = Random(seed)
    private val totalCount = width * height * depth
    protected val initialEntropy = calculateEntropy(patternSet)
    protected abstract val wave: Array3D<Slot>

    /** Queue for propagating collapsed state (as opposed to null state). */
    protected val constrainQueue = LinkedHashSet<Slot>()
    /** Queue for propagating null state, i.e. slots that have been reset. */
    protected val relaxQueue = LinkedHashSet<Slot>()
    /** Contains slots that haven't collapsed yet, sorted by entropy */
    protected val unobservedSet = TreeSet<Slot> { t1, t2 ->
        val entropyDiff = t1.entropy.compareTo(t2.entropy)
        // Distinguish between patterns with equal entropy values
        if (entropyDiff == 0) t1.hashCode().compareTo(t2.hashCode())
        else entropyDiff
    }

    internal val collapsedCount: Int get() = totalCount - unobservedSet.size
    val isCollapsed: Boolean get() = unobservedSet.size <= 0
    var isContradicted: Boolean = false
        protected set

    /** Returns true if this slot is in its final definite state. */
    protected abstract fun Slot.isObserved(): Boolean

    /** Selects and applies a definite state to this slot. */
    protected abstract fun Slot.observe()

    /** Finds all affected neighbors and adds them to the appropriate queue
     * when propagating relaxation. */
    protected abstract fun Slot.relaxNeighbors()

    /** Finds all affected neighbors and adds them to the appropriate queue
     * when propagating constraints. */
    protected abstract fun Slot.constrainNeighbors()

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
        if (slot.possiblePatterns.size == 0) {
            isContradicted = true
        } else {
            slot.observe()
            propagate()
        }
        if (isContradicted) {
            println("Contradiction!")
        }
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
            relaxQueue.first().also {
                relaxQueue.remove(it)
                it.relaxNeighbors()
            }
        }
    }

    /**
     * Propagates constraints from collapsed slots.
     * If any slot has only 1 possible state left, collapses it.
     */
    private fun propagateConstraints() {
        while (constrainQueue.isNotEmpty()) {
            constrainQueue.first().also {
                constrainQueue.remove(it)
                it.constrainNeighbors()
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

    protected fun Slot.updateEntropy() {
        unobservedSet.remove(this)
        // update entropy after removal, because it defines position in TreeSet
        entropy = calculateEntropy(possiblePatterns)
        if (!isObserved()) unobservedSet.add(this)
    }

    /** This is an optimization cheat, to clear the constraint queue
     * when you know you will only need to propagate from the given point. */
    internal fun resetPropagationTo(x: Int, y: Int, z: Int) {
        constrainQueue.clear()
        constrainQueue.add(wave[x, y, z])
    }

    fun getPossiblePatterns(p: IntVec3): Set<P> = getPossiblePatterns(p.x, p.y, p.z)
    fun getPossiblePatterns(x: Int, y: Int, z: Int): Set<P> = wave[x, y, z].possiblePatterns
}