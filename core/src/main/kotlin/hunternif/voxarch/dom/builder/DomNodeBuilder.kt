package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
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

    override fun prepareForLayout(ctx: DomBuildContext): StyledElement<*> {
        val node = createNode()
        node.tags += (styleClass - uniqueClass)
        ctx.parentNode.addChild(node)
        return StyledNode(node, this, ctx)
    }

    /** See [DomBuilder.postLayout] */
    open fun postLayout(element: StyledNode<N>) {}
}

/** Checks if this builder builds the right class of node and casts to it*/
@Suppress("UNCHECKED_CAST")
inline fun <reified N2 : Node> DomBuilder.asNodeBuilder(): DomNodeBuilder<N2>? =
    if (this is DomNodeBuilder<*> &&
        N2::class.java.isAssignableFrom(nodeClass)
    ) this as DomNodeBuilder<N2>
    else null