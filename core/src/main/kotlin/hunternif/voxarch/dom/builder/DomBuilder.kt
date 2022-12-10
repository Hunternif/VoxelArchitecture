package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.plan.Node

/** Base class for DOM elements. */
@CastleDsl
open class DomBuilder(val ctx: DomContext) {
    /** Stylesheet contains rules for styling nodes and generators.
     * This instance can be modified for each individual DOM builder
     * to make local non-cascading rules. */
    var stylesheet: Stylesheet = defaultStyle

    /** DOM builder immediately above this one.
     * In case of a cycle this is not necessarily the previous location! */
    var parent: DomBuilder = ctx.rootBuilder
        protected set

    /** Seed for randomized properties. Can be modified per DOM builder. */
    internal var seed: Long = 0

    /** Don't manually add children, use [addChild] instead.*/
    internal val children = mutableListOf<DomBuilder>()

    /** Whether the node and its children will be built or ignored. */
    internal var visibility: Visibility = Visibility.VISIBLE

    /** List of "CSS classes" applied to this element. */
    internal val styleClass = LinkedHashSet<String>()

    /** Recursively invokes this method on children. */
    open fun build(parentNode: Node) {
        children.forEach { it.build(parentNode) }
    }

    internal open fun addChild(
        child: DomBuilder,
        childSeed: Long = nextChildSeed()
    ) {
        child.parent = this
        child.seed = childSeed
        child.stylesheet = stylesheet
        children.add(child)
    }

    /** Add given style class name to this builder. */
    fun addStyle(style: String) {
        styleClass.add(style)
    }

    fun addStyles(vararg styles: String) {
        styleClass.addAll(styles)
    }

    /** Add given style class names to this builder. */
    operator fun Array<out String>.unaryPlus() {
        styleClass.addAll(this)
    }

    private fun nextChildSeed() = seed + children.size + 1
}

enum class Visibility {
    VISIBLE,

    /** The node's children will not be created. */
    GONE
}