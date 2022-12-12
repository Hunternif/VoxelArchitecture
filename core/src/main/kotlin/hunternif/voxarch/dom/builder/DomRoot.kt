package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.plan.Node

/** Root of the DOM.
 * @param seed each child element will receive a seed value that's derived
 *             from this root seed value by a deterministic arithmetic.
 */
class DomRoot(
    seed: Long = 0,
    ctx: DomContext,
) : DomBuilder(ctx) {
    init {
        this.seed = seed
    }

    val node: Node get() = ctx.rootNode

    /** Builds the entire DOM tree. */
    fun buildDom(): Node {
        val childCtx = DomBuildContext(this, node)
            .inherit(styleClass)
        build(childCtx)
        return node
    }
}