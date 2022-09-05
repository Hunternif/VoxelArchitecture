package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.scene.models.Box
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.util.INested
import org.joml.Vector3f

/**
 * Base class for objects in the scene.
 * This class is primarily responsible for the nested structure.
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
}
