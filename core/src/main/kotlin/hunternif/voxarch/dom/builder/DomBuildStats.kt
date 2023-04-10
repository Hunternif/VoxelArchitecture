package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.vector.Vec3

/**
 * Aggregates information from executing a Dom Builder tree.
 */
class DomBuildStats {
    /** Temporary nodes that will not result in voxels. Can be collapsed. */
    val dummyNodes = mutableListOf<Node>()

    /** Dom Builders added during execution (parent-child pairs).
     * The children should be deleted, to keep the DOM tree clean. */
    val addedContent = mutableListOf<Pair<DomBuilder, DomBuilder>>()

    /** Node origins after hinting. */
    val hintedOrigins = mutableMapOf<Node, Vec3>()
}