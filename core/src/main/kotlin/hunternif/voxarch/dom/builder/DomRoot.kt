package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.plan.Node

/** Root of the DOM.
 * @param stylesheet this stylesheet will apply to all elements in this DOM.
 * @param seed each child element will receive a seed value that's derived
 *             from this root seed value by a deterministic arithmetic.
 */
class DomRoot(
    stylesheet: Stylesheet = defaultStyle,
    seed: Long = 0,
    ctx: DomContext,
) : DomBuilder(ctx) {
    init {
        this.seed = seed
        this.stylesheet = stylesheet
        this.parent = this
    }

    val node: Node get() = ctx.rootNode

    /** Builds the entire DOM tree. */
    fun buildDom(): Node {
        build(node)
        return node
    }
}