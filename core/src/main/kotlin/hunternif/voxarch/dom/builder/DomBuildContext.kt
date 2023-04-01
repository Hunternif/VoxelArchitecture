package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.plan.Node

/** Passed into children during building DOM tree. */
data class DomBuildContext(
    /** Immediate parent Node that is already built. */
    val parentNode: Node,

    /** this stylesheet will apply to all elements in this DOM. */
    val stylesheet: Stylesheet,

    /**
     * This is the local child's seed value.
     * Each child element will receive a seed value that's derived
     * from this root seed value by a deterministic arithmetic.
     */
    val seed: Long,

    /** Aggregates info during the build. */
    val stats: DomBuildStats,

    /** Chain of execution up to this point. */
    val lineage: List<StyledElement<*>> = listOf(),
) {
    /** Immediate parent DOM element. */
    val parent: StyledElement<*>? get() = lineage.lastOrNull()

    /**
     * Creates context for a child of this element.
     * Adds this element to lineage.
     */
    fun makeChildCtx(
        parent: StyledElement<*>,
        parentNode: Node = this.parentNode,
        stylesheet: Stylesheet = this.stylesheet,
        seed: Long = this.seed + parent.domBuilder.seedOffset,
    ) = DomBuildContext(
        parentNode,
        stylesheet,
        seed,
        stats,
        lineage + parent,
    )
}