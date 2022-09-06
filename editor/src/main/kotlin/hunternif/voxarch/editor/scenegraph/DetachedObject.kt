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
    val memberships: Map<SceneTree.Subset, Set<SceneObject>>
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

}

fun SceneObject.detached(): DetachedObject {
    val memberships = this.tree?.subsets?.associate {
        it to mutableSetOf<SceneObject>()
    } ?: emptyMap()
    this.iterateSubtree().forEach { child ->
        memberships.forEach { (subset, objectSet) ->
            if (child in subset) objectSet.add(child)
        }
    }
    return DetachedObject(parent, this, memberships)
}