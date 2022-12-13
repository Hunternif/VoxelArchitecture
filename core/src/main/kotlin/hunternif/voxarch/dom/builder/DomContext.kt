package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Node

/**
 * Represents context shared by all DOM builders.
 */
class DomContext(
    val rootNode: Node,
) {
    val rootBuilder = DomRoot(ctx = this)
}