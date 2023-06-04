package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.plan.Node

/** Creates a temporary dummy node. */
class DomTempNodeBuilder<N : Node>(
    nodeClass: Class<N>,
    createNode: () -> N,
) : DomNodeBuilder<N>(nodeClass, createNode) {
    companion object {
        inline operator fun <reified N : Node> invoke(
            noinline createNode: () -> N,
        ): DomTempNodeBuilder<N> =
            DomTempNodeBuilder(N::class.java, createNode)
    }

    override fun prepareForLayout(ctx: DomBuildContext): StyledNode<N> {
        return super.prepareForLayout(ctx).apply {
            node.transparent = true
            ctx.stats.dummyNodes.add(node)
        }
    }
}
