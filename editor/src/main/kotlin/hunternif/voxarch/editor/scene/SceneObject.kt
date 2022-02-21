package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.scene.models.boxFaces
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.*
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.max
import hunternif.voxarch.util.min
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
) {
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

class SceneNode(
    val node: Node,
    color: ColorRGBa = Colors.defaultNodeBox,
    isGenerated: Boolean = false,
) : SceneObject(
    color = color,
    isGenerated = isGenerated,
), INested<SceneNode> {
    override var parent: SceneNode? = null
    private val _children = LinkedHashSet<SceneNode>()
    override val children: Collection<SceneNode> get() = _children
    val generators = mutableListOf<IGenerator>()

    init { update() }

    override fun addChild(child: SceneNode) {
        child.parent = this
        _children.add(child)
        // prevent double-adding, especially when generating nodes
        if (child.node.parent != node) node.addChild(child.node)
    }
    override fun removeChild(child: SceneNode) {
        if (_children.remove(child)) {
            node.removeChild(child.node)
            // not resetting parent because it will be used for undo in history
        }
    }
    override fun removeAllChildren() {
        _children.clear()
    }

    override fun update() {
        updateFaces()
        val origin = node.findGlobalPosition()
        when (node) {
            is Room -> {
                wrapVoxels(origin + node.start, node.size)
            }
            is Wall -> {
                //TODO: figure out how to render walls at non-right angles
                val innerMin = min(Vec3.ZERO, node.innerEnd)
                val innerMax = max(Vec3.ZERO, node.innerEnd)
                wrapVoxels(origin + innerMin, innerMax - innerMin)
            }
            is Path -> {
                //TODO: render path as a line
                val innerMin = node.points.fold(Vec3.ZERO) { a, b -> min(a, b) }
                val innerMax = node.points.fold(Vec3.ZERO) { a, b -> max(a, b) }
                wrapVoxels(origin + innerMin, innerMax - innerMin)
            }
            is Floor -> {
                val p = parent
                if (p != null) {
                    // Floor fills a horizontal plane within its parent's bounds
                    //TODO: indicate that floor is a potentially infinite plane
                    p.update()
                    start.set(p.start).add(node.origin)
                    size.set(p.size.x, 1f, p.size.z)
                } else {
                    // if no parent, render a small flat 3x3 square
                    wrapVoxels(origin + Vec3(-1, 0, -1), Vec3(2, 0, 2))
                }
            }
            else -> {
                wrapVoxels(origin, Vec3.ZERO)
            }
        }
    }

    override fun toString() = "${node.javaClass.simpleName} ${hashCode()}"
}

class SceneVoxelGroup(
    val label: String,
    val data: IStorage3D<out IVoxel?>,
    isGenerated: Boolean = false,
    /** Voxel centric coordinates of the lower corner */
    val origin: Vector3f = Vector3f(),
) : SceneObject(
    color = Colors.transparent,
    isGenerated = isGenerated,
), INested<SceneVoxelGroup> {
    override var parent: SceneVoxelGroup? = null
    private val _children = LinkedHashSet<SceneVoxelGroup>()
    override val children: Collection<SceneVoxelGroup> get() = _children

    init { update() }

    override fun addChild(child: SceneVoxelGroup) {
        child.parent = this
        _children.add(child)
    }
    override fun removeChild(child: SceneVoxelGroup) {
        _children.remove(child)
    }
    override fun removeAllChildren() {
        _children.clear()
    }

    override fun update() {
        updateFaces()
        wrapVoxels(
            origin.toVec3() + Vec3(data.minX, data.minY, data.minZ),
            data.sizeVec.toVec3()
        )
    }

    override fun toString() = label
}

interface INested<T : INested<T>> {
    var parent: T?
    val children: Collection<T>
    fun addChild(child: T)
    fun removeChild(child: T)
    fun removeAllChildren()
}