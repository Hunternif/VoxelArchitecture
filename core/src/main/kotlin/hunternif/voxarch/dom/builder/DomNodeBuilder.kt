package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.plan.Node

/** Represents any nodes below the root. */
open class DomNodeBuilder<N : Node>(
    val nodeClass: Class<N>,
    private val createNode: () -> N
) : DomBuilder() {
    companion object {
        inline operator fun <reified N : Node> invoke(
            noinline createNode: () -> N,
        ): DomNodeBuilder<N> =
            DomNodeBuilder(N::class.java, createNode)
    }

    override fun build(ctx: DomBuildContext) = guard {
        val node = createNode()
        node.tags += styleClass
        ctx.parentNode.addChild(node)
        val styled = StyledNode(node, ctx.parentNode, this,
            ctx.seed + seedOffset)
            .inherit(ctx.inheritedStyleClass)
        ctx.stylesheet.applyStyle(styled)
        if (visibility == Visibility.VISIBLE) {
            buildNode(node)
            val childCtx = ctx.copy(this, node).inherit(styleClass)
            children.forEach { it.build(childCtx) }
        } else {
            // add and then remove the node, because it needs a parent to
            // calculate styles including visibility.
            node.parent?.removeChild(node)
        }
    }

    /** Any custom initialization code for this node.
     * Don't use it to add child nodes, create a IGenerator for that instead. */
    open fun buildNode(node: N) {}
}

/** Checks if this builder builds the right class of node and casts to it*/
@Suppress("UNCHECKED_CAST")
inline fun <reified N2 : Node> DomBuilder.asNodeBuilder(): DomNodeBuilder<N2>? =
    if (this is DomNodeBuilder<*> &&
        N2::class.java.isAssignableFrom(nodeClass)
    ) this as DomNodeBuilder<N2>
    else null