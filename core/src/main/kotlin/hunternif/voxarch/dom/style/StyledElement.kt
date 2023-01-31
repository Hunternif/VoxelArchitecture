package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.*
import hunternif.voxarch.plan.Node

/** Represents a DOM element for the purpose of styling. */
@StyleDsl
open class StyledElement<D : DomBuilder>(
    val domBuilder: D,
    val ctx: DomBuildContext,
    var seed: Long = ctx.seed + domBuilder.seedOffset
) {
    val parentNode: Node get() = ctx.parentNode
    val styleClass: Set<String> get() = domBuilder.styleClass
    val inheritedStyleClass: Set<String> get() = ctx.inheritedStyleClass

    val isVisible: Boolean get() = domBuilder.visibility == Visibility.VISIBLE

    /** Finalizes this element. */
    open fun postLayout() {
        domBuilder.postLayout(this)
    }

    /** Clean up any changes it caused to the node tree. */
    open fun cleanup() {}

    open fun makeChildCtx(): DomBuildContext =
        ctx.makeChildCtx(this).inherit(styleClass)

    override fun toString(): String =
        "Styled<${domBuilder::class.java.simpleName}>"
}

/** Represents a DOM element with a [Node] for the purpose of styling. */
@StyleDsl
class StyledNode<N : Node>(
    val node: N,
    domBuilder: DomNodeBuilder<N>,
    ctx: DomBuildContext,
) : StyledElement<DomNodeBuilder<N>>(domBuilder, ctx) {

    override fun postLayout() {
        domBuilder.postLayout(this)
    }

    override fun cleanup() {
        node.remove()
    }

    override fun makeChildCtx(): DomBuildContext =
        ctx.makeChildCtx(this, node).inherit(styleClass)

    override fun toString(): String = "Styled<${node::class.java.simpleName}>"
}