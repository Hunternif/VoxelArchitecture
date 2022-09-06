package hunternif.voxarch.editor.scenegraph

/**
 * Contains information about an object after it's removed from the tree,
 * so that it can be attached again.
 * */
class DetachedObject(
    val parent: SceneObject?,
    val obj: SceneObject,
    /** Maps each subset to a set of child objects in this subtree that
     * used to be in that subset. */
    val memberships: List<Membership<*>>,
) {
    /** Detach this object from its parent (in case it was reattached). */
    fun detach() {
        obj.parent?.removeChild(obj)
        obj.tree?.onDetach(this)
    }

    /** Attach a child to its previous parent. */
    fun reattach() {
        parent?.attach(obj)
        obj.tree?.onReattach(this)
    }

    data class Membership<T : SceneObject>(
        val subset: SceneTree.Subset<T>,
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

fun SceneObject.detached(): DetachedObject {
    val memberships = this.tree?.subsets?.map {
        DetachedObject.Membership(it)
    } ?: emptyList()
    this.iterateSubtree().forEach { child ->
        memberships.forEach { it.tryAdd(child) }
    }
    return DetachedObject(parent, this, memberships)
}