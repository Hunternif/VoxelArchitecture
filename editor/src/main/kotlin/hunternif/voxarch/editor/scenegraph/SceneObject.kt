package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.models.box.AABBoxMesh
import hunternif.voxarch.editor.scene.models.box.BoxMeshWithFaces
import hunternif.voxarch.editor.util.AABB2Df
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.util.INested
import hunternif.voxarch.util.forEachSubtree
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
 * For parameters [start], [size], [color] - see [AABBoxMesh].
 *
 * @param isGenerated whether this object is generated (for UI).
 */
open class SceneObject(
    override val id: Int,
    center: Vector3f = Vector3f(),
    //TODO: deprecate [start]
    start: Vector3f = Vector3f(),
    size: Vector3f = Vector3f(),
    angleY: Float = 0f,
    val color: ColorRGBa = Colors.defaultNodeBox,
    val isGenerated: Boolean = false,
) : INested<SceneObject>, WithID {
    /** Axis-aligned bounding box. */
    val aabb = AABBoxMesh(start, size, color)

    /** More accurate bounding box with rotation. */
    val box = BoxMeshWithFaces(center, size, angleY)

    /** AABB in screen coordinates relative to viewport. */
    val screenAABB: AABB2Df get() = aabb.screenAABB

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

    /** Recalculate [aabb] based on underlying data. */
    open fun update() {
        aabb.updateFaces()
        box.updateMesh()
    }

    /** Recalculate on-screen 2D AABB. */
    fun updateAABB(camera: OrbitalCamera) {
        aabb.updateAABB(camera)
        box.updateAABB(camera)
    }

    private val strRepr: String by lazy { "${javaClass.simpleName} $id" }
    override fun toString() = strRepr
}
