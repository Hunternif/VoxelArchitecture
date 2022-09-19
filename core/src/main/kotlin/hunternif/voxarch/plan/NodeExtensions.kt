package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

/** Finds offset of this node's origin in global coordinates, i.e.
 * in the coordinate space where its highest parent node exists.
 */
//TODO: cache global position when nodes aren't moved
fun Node.findGlobalPosition(): Vec3 {
    var depth = 0
    val result = origin.clone()
    var parent = this.parent
    while (parent != null) {
        depth++
        if (depth > 10000) {
            println("Possibly infinite recursion!")
            return result
        }
        result.addLocal(parent.origin)
        parent = parent.parent
    }
    return result
}

/** Finds children in the subtree matching the given class and tags. */
inline fun <reified N : Node> Node.query(vararg tags: String): Sequence<N> = sequence {
    val tagSet = tags.toSet()
    this@query.iterateSubtree().forEach {
        if (it is N && (tagSet.isEmpty() || it.tags.containsAll(tagSet))) {
            yield(it)
        }
    }
}