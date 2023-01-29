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
}

/** Represents a DOM element with a [Node] for the purpose of styling. */
@StyleDsl
class StyledNode<N : Node>(
    val node: N,
    domBuilder: DomNodeBuilder<N>,
    ctx: DomBuildContext,
) : StyledElement<DomNodeBuilder<N>>(domBuilder, ctx)