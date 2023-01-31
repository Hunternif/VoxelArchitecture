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

    /** Chain of execution up to this point. */
    val lineage: List<StyledElement<*>> = listOf(),

    /** Style classes inherited from all parent DOM elements. */
    val inheritedStyleClass: MutableSet<String> = linkedSetOf(),
) {
    /** Immediate parent DOM element. */
    val parent: StyledElement<*>? get() = lineage.lastOrNull()

    fun inherit(ctx: DomBuildContext): DomBuildContext {
        inheritedStyleClass.addAll(ctx.inheritedStyleClass)
        return this
    }
    fun inherit(styleClasses: Iterable<String>): DomBuildContext {
        inheritedStyleClass.addAll(styleClasses)
        return this
    }

    /**
     * Creates context for a child of this element.
     * Adds this element to lineage.
     */
    fun makeChildCtx(
        parent: StyledElement<*>,
        parentNode: Node = this.parentNode,
        stylesheet: Stylesheet = this.stylesheet,
        seed: Long = this.seed,
    ) = DomBuildContext(
        parentNode,
        stylesheet,
        seed,
        lineage + parent,
    ).inherit(inheritedStyleClass)
}