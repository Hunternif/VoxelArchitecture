package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.scene.models.Box
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.util.INested
import org.joml.Vector3f

/**
 * Base class for objects in the scene.
 *
 * This class is primarily responsible for the nested structure,
 * it encapsulates access and modification of a tree of objects.
 *
 * It also ensures the dual hierarchy of SceneNodes and Nodes is maintained
 * (see subclass [SceneNode]).
 *
 * For parameters [start], [size], [color] - see [Box].
 *
 * @param isGenerated whether this object is generated (for UI).
 */
open class SceneObject(
    start: Vector3f = Vector3f(),
    size: Vector3f = Vector3f(),
    color: ColorRGBa = Colors.defaultNodeBox,
    val isGenerated: Boolean = false,
) : Box(start, size, color), INested<SceneObject> {

    override var parent: SceneObject? = null
    override val children: LinkedHashSet<SceneObject> = LinkedHashSet()

    var tree: SceneTree? = null

    /** Attach a child to this object */
    fun attach(subtree: SceneObject) {
        subtree.tree = tree
        if (subtree !in children) {
            addChild(subtree)
            tree?.items?.addAll(subtree.iterateSubtree())
        }
    }

    /** Attach multiple children to this object */
    fun attachAll(items: Collection<SceneObject>) {
        items.forEach { attach(it) }
    }

    /** Detach this object from its parent. */
    fun detach() {
        parent?.removeChild(this)
        iterateSubtree().forEach { tree?.items?.remove(it) }
    }

    fun detachAllChildren() {
        for (child in children.toList()) {
            child.detach()
        }
    }
}
