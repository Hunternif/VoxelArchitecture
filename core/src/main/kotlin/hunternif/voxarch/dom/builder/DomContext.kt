package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.defaultStyle
import hunternif.voxarch.plan.Node

/**
 * Represents context shared by all DOM builders.
 */
class DomContext(
    /** this stylesheet will apply to all elements in this DOM. */
    val stylesheet: Stylesheet = defaultStyle,
    seed: Long = 0L,
    val rootNode: Node,
) {
    constructor(root: Node) : this(defaultStyle, 0L, root)

    val rootBuilder = DomRoot(seed, ctx = this)
}