package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.plan.Node

/** Root of the DOM. */
class DomRoot(
    ctx: DomContext,
) : DomBuilder(ctx) {

    val node: Node get() = ctx.rootNode

    /** Builds the entire DOM tree. */
    fun buildDom(
        stylesheet: Stylesheet = defaultStyle,
        seed: Long = 0L,
    ): Node {
        val childCtx = DomBuildContext(this, node, stylesheet, seed)
            .inherit(styleClass)
        build(childCtx)
        return node
    }
}