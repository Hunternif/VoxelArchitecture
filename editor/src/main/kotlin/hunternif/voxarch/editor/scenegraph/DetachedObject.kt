package hunternif.voxarch.editor.scenegraph

/** Contains information about a node after it's removed from the tree,
 * so that it can be attached again. */
class DetachedObject(
    val parent: SceneObject?,
    val node: SceneObject,
) {
    /** Detach this object from its parent (in case it was reattached). */
    fun detach() {
        node.detach()
    }

    /** Attach a child to its previous parent. */
    fun reattach() {
        parent?.attach(node)
    }
}

fun SceneObject.detached() = DetachedObject(parent, this)