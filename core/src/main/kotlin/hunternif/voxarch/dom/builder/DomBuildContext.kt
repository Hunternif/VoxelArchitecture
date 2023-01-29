package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.plan.Node

/** Passed into children during building DOM tree. */
data class DomBuildContext(
    /** Immediate parent DOM element. */
    val parent: DomBuilder,

    /** Immediate parent Node that is already built. */
    val parentNode: Node,

    /** this stylesheet will apply to all elements in this DOM. */
    val stylesheet: Stylesheet,

    /**
     * Each child element will receive a seed value that's derived
     * from this root seed value by a deterministic arithmetic.
     */
    val seed: Long,

    /** Chain of execution up to this point. */
    val lineage: List<DomBuilder> = listOf(),

    /** Style classes inherited from all parent DOM elements. */
    val inheritedStyleClass: MutableSet<String> = linkedSetOf(),
) {
    fun inherit(ctx: DomBuildContext): DomBuildContext {
        inheritedStyleClass.addAll(ctx.inheritedStyleClass)
        return this
    }
    fun inherit(styleClasses: Iterable<String>): DomBuildContext {
        inheritedStyleClass.addAll(styleClasses)
        return this
    }

    /** Make a copy of this context and add the new parent to the lineage. */
    fun makeChildCtx(
        parent: DomBuilder = this.parent,
        parentNode: Node = this.parentNode,
        stylesheet: Stylesheet = this.stylesheet,
        seed: Long = this.seed,
    ) = DomBuildContext(
        parent,
        parentNode,
        stylesheet,
        seed,
        lineage + parent,
    ).inherit(inheritedStyleClass)
}