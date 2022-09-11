package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.util.forEachSubtree

/**
 * Contains information about an object after it's removed from the tree,
 * so that it can be attached again.
 * */
class DetachedObject(
    val parent: SceneObject?,
    val obj: SceneObject,
    /** Maps each subset to a set of child objects in this subtree that
     * used to be in that subset. */
    private val memberships: List<Membership<*>>,
) {
    /** Detach this object from its parent (in case it was reattached). */
    fun detach() {
        obj.parent?.removeChild(obj)
        memberships.forEach { it.clear() }
    }

    /** Attach a child to its previous parent. */
    fun reattach() {
        parent?.addChild(obj)
        memberships.forEach { it.restore() }
    }

    data class Membership<T : SceneObject>(
        val subset: Subset<T>,
        val objects: MutableSet<T> = mutableSetOf(),
    ) {
        fun tryAdd(child: SceneObject) {
            @Suppress("UNCHECKED_CAST")
            if (child in subset) objects.add(child as T)
        }
        fun clear() {
            subset.removeAll(objects)
        }
        fun restore() {
            subset.addAll(objects)
        }
    }
}

/** Creates a detached object from this node. See [DetachedObject]. */
fun SceneObject.detached(): DetachedObject {
    val memberships = this.tree?.subsets?.map {
        DetachedObject.Membership(it)
    } ?: emptyList()
    this.forEachSubtree { child ->
        memberships.forEach { it.tryAdd(child) }
    }
    return DetachedObject(parent, this, memberships)
}