package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.scene.models.boxFaces
import hunternif.voxarch.editor.util.AABB2Df
import hunternif.voxarch.editor.util.AABBFace
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.set
import hunternif.voxarch.util.INested
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3f

/**
 * Base class for objects in the scene.
 * Each object is presented as an axis-aligned rectangular box.
 * ([start], [start]+[size]) define the corners of its AABB.
 * [start] and [size] are in "natural" coordinates (not centric).
 * @param start absolute position in the scene (not relative to parent).
 * @param size size of the object in natural coordinates.
 * @param color the color that is used to render its AABB.
 * @param isGenerated whether this object is generated (for UI).
 */
open class SceneObject(
    val start: Vector3f = Vector3f(),
    val size: Vector3f = Vector3f(),
    var color: ColorRGBa,
    val isGenerated: Boolean = false,
) : INested<SceneObject> {
    override var parent: SceneObject? = null
    override val children: LinkedHashSet<SceneObject> = LinkedHashSet()

    /** Read-only! Corner of the AAB in "natural" coordinates (not in voxels),
     * absolute position in the scene. */
    val end: Vector3f = Vector3f()
        get() = field.set(start).add(size)

    /** Absolute position of the point in the middle of the floor. */
    val floorCenter: Vector3f = Vector3f()
        get() = field.set(start).add(end.x, 0f, end.z).mul(0.5f)

    /** AABB in screen coordinates relative to viewport.
     * Can be updated at any time. */
    val screenAABB: AABB2Df = AABB2Df()

    val faces: Array<AABBFace> by lazy { boxFaces(start, end, 0.1f) }
    fun updateFaces() = boxFaces(start, end, 0.1f).copyInto(faces)

    /** Recalculate [start] and [size] based on underlying data. */
    open fun update() {}

    /**
     * Set this object's boundaries to wrap around the given voxel AABB.
     * @param minVox lower corner of the box, in voxel-centric coordinates.
     * @param sizeVox size of the box, in voxel-centric coordinates.
     */
    protected fun wrapVoxels(minVox: Vec3, sizeVox: Vec3) {
        start.set(minVox).sub(0.5f, 0.5f, 0.5f)
        size.set(sizeVox).add(1f, 1f, 1f)
    }
}
