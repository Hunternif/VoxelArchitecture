package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.generator.IGenerator
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

    /** Generators add more child DomBuilders.
     * Must be called prior to [build], to prevent concurrent modification. */
    internal val generators = mutableListOf<IGenerator>()

    /** Recursively invokes this method on children. */
    open fun build(parentNode: Node) {
        generators.forEach { it.generate(this, parentNode) }
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

    /** Checks if this builder builds the right class of node and casts to it*/
    @Suppress("UNCHECKED_CAST")
    inline fun <reified N2 : Node> asNodeBuilder(): DomNodeBuilder<N2>? =
        if (this is DomNodeBuilder<*> &&
            N2::class.java.isAssignableFrom(nodeClass)
        ) this as DomNodeBuilder<N2>
        else null

    private fun nextChildSeed() = seed + children.size + 1
}

enum class Visibility {
    VISIBLE,

    /** The node's children will not be created. */
    GONE
}