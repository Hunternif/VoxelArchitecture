package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Node

/** Root of the DOM. */
class DomRoot(
    ctx: DomContext,
) : DomBuilder(ctx) {

    val node: Node get() = ctx.rootNode

    /** Builds the entire DOM tree. */
    fun buildDom(): Node {
        val childCtx = DomBuildContext(this, node)
            .inherit(styleClass)
        build(childCtx)
        return node
    }
}