package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.plan.Node
import hunternif.voxarch.util.CycleCounter
import hunternif.voxarch.util.Recursive

/**
 * Base class for DOM elements.
 * DOM Builder should not have any state dependent on which parent it's
 * attached to. This will allow to execute DOM builder under any root.
 */
@CastleDsl
open class DomBuilder : Recursive(cycleCounter) {

    /** DOM builder immediately above this one.
     * In case of a cycle this is not necessarily the previous location! */
    var parent: DomBuilder? = null
        protected set

    /** Offset from the main seed for randomized properties.
     * Can be modified per DOM builder. */
    internal var seedOffset: Long = 0

    /** Don't manually add children, use [addChild] instead.*/
    protected val children = linkedSetOf<DomBuilder>()

    /** Whether the node and its children will be built or ignored. */
    internal var visibility: Visibility = Visibility.VISIBLE

    /** List of "CSS classes" applied to this element. */
    internal val styleClass = linkedSetOf<String>()

    /** Recursively invokes this method on children. */
    open fun build(bldCtx: DomBuildContext): Unit = guard {
        children.forEach { it.build(bldCtx) }
    }

    fun addChild(
        child: DomBuilder,
        seedOffset: Long = nextChildSeedOffset()
    ) {
        child.parent = this
        child.seedOffset = seedOffset
        children.add(child)
    }

    /** Add given style class name to this builder. */
    fun addStyle(style: String) {
        styleClass.add(style)
    }

    fun addStyles(vararg styles: String) {
        styleClass.addAll(styles)
    }

    fun addAllStyles(styles: Iterable<String>) {
        styleClass.addAll(styles)
    }

    /** Add given style class names to this builder. */
    operator fun Array<out String>.unaryPlus() {
        styleClass.addAll(this)
    }

    private fun nextChildSeedOffset(): Long = seedOffset + children.size + 1

    /** Creates context to pass into a child, with default parameters. */
    fun DomBuildContext.makeChildCtx() =
        DomBuildContext(this@DomBuilder, parentNode, stylesheet, seed)
            .inherit(this)
            .inherit(styleClass)

    companion object {
        val cycleCounter = CycleCounter(20)
    }
}

/** Passed into children during building DOM tree. */
//TODO: maybe re-use StyledElement for this
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

    /** Style classes inherited from all parent DOM elements. */
    val inheritedStyleClass: MutableSet<String> = linkedSetOf(),
) {
    fun inherit(bldCtx: DomBuildContext): DomBuildContext {
        inheritedStyleClass.addAll(bldCtx.inheritedStyleClass)
        return this
    }
    fun inherit(styleClasses: Iterable<String>): DomBuildContext {
        inheritedStyleClass.addAll(styleClasses)
        return this
    }
}

enum class Visibility {
    VISIBLE,

    /** The node's children will not be created. */
    GONE
}