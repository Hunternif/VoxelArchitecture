package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.models.box.BoxMeshWithFaces
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.util.INested
import hunternif.voxarch.util.forEachSubtree
import hunternif.voxarch.util.toRadians
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
 * @param center absolute position in the scene (not relative to parent).
 * @param size size of the object in natural coordinates.
 * @param color the color that is used to render its AABB.
 * @param isGenerated whether this object is generated (for UI).
 */
open class SceneObject(
    override val id: Int,
    center: Vector3f = Vector3f(),
    size: Vector3f = Vector3f(),
    angleY: Float = 0f,
    color: ColorRGBa = Colors.defaultNodeBox,
    val isGenerated: Boolean = false,
) : INested<SceneObject>, WithID {

    val color: ColorRGBa = color.copy()

    /** Oriented bounding box with rotation. */
    val box = BoxMeshWithFaces(center, size, angleY.toRadians(), this.color)

    override var parent: SceneObject? = null
    override val children: LinkedHashSet<SceneObject> = LinkedHashSet()

    var tree: SceneTree? = null

    override fun addChild(child: SceneObject) {
        super.addChild(child)
        child.forEachSubtree { it.tree = tree }
        tree?.add(child)
    }

    override fun removeChild(child: SceneObject): Boolean {
        tree?.remove(child)
        child.forEachSubtree { it.tree = null }
        return super.removeChild(child)
    }

    /** Recalculate [box] based on underlying data. */
    open fun update() {
        box.updateMesh()
    }

    /** Recalculate on-screen 2D coordinates. */
    fun updateScreenProjection(camera: OrbitalCamera) {
        box.updateScreenProjection(camera)
    }

    private val strRepr: String by lazy { "${javaClass.simpleName} $id" }
    override fun toString() = strRepr
}
