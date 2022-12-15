package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.DomDsl
import hunternif.voxarch.util.CycleCounter
import hunternif.voxarch.util.Recursive

/**
 * Base class for DOM elements.
 * DOM Builder should not have any state dependent on which parent it's
 * attached to. This will allow to execute DOM builder under any root.
 */
@DomDsl
open class DomBuilder : Recursive(cycleCounter) {

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
    open fun build(ctx: DomBuildContext): Unit = guard {
        children.forEach { it.build(ctx.makeChildCtx()) }
    }

    fun addChild(
        child: DomBuilder,
        seedOffset: Long = nextChildSeedOffset()
    ) {
        child.seedOffset = seedOffset
        children.add(child)
    }

    fun removeChild(child: DomBuilder) {
        children.remove(child)
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
        copy(this@DomBuilder).inherit(styleClass)

    companion object {
        val cycleCounter = CycleCounter(20)
    }
}

enum class Visibility {
    VISIBLE,

    /** The node's children will not be created. */
    GONE
}