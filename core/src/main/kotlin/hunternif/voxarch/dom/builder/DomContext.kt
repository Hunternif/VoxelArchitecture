package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.plan.Node

/**
 * Represents context shared by all DOM builders.
 */
class DomContext(
    stylesheet: Stylesheet = defaultStyle,
    seed: Long = 0L,
    val rootNode: Node,
) {
    constructor(root: Node) : this(defaultStyle, 0L, root)

    val rootBuilder = DomRoot(stylesheet, seed, ctx = this)
}