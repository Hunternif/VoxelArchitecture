package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Structure

/** Root of the DOM. */
class DomRoot(
    val node: Node = Structure(),
) : DomBuilder() {

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