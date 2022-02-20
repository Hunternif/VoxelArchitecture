package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.scene.models.boxFaces
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.findGlobalPosition
import hunternif.voxarch.storage.IStorage3D
import org.joml.Vector3f

/**
 * Base class for objects in the scene.
 * ([start], [start]+[size]) define the corners of its AABB.
 * [start] and [size] are in "natural" coordinates (not centric).
 * [start] is an absolute position in the scene (not relative to parent).
 * [color] is the color that is used to render its AABB.
 */
open class SceneObject(
    val start: Vector3f = Vector3f(),
    val size: Vector3f = Vector3f(),
    var color: ColorRGBa,
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
}

class SceneNode(
    val node: Node
) : SceneObject(color = Colors.defaultNodeBox), INested<SceneNode> {
    override var parent: SceneNode? = null
    private val _children = LinkedHashSet<SceneNode>()
    override val children: Collection<SceneNode> get() = _children
    val generators = mutableListOf<IGenerator>()

    init { update() }

    override fun addChild(child: SceneNode) {
        child.parent = this
        _children.add(child)
        node.addChild(child.node)
    }
    override fun removeChild(child: SceneNode) {
        if (_children.remove(child)) {
            node.removeChild(child.node)
        }
    }
    override fun removeAllChildren() {
        _children.clear()
    }

    override fun update() {
        updateFaces()
        val origin = node.findGlobalPosition()
        if (node is Room) {
            start.set(origin).add(node.start).sub(0.5f, 0.5f, 0.5f)
            size.set(node.size).add(1f, 1f, 1f)
        } else {
            start.set(origin).sub(0.5f, 0.5f, 0.5f)
            size.set(1f, 1f, 1f)
        }
    }

    override fun toString() = "${node.javaClass.simpleName} ${hashCode()}"
}

class SceneVoxelGroup(
    val label: String,
    val data: IStorage3D<VoxColor?>,
    /** Voxel centric coordinates of the lower corner */
    val origin: Vector3f = Vector3f(),
) : SceneObject(color = Colors.transparent), INested<SceneVoxelGroup> {
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
        start.set(origin).sub(0.5f, 0.5f, 0.5f)
        size.set(data.width + 1f, data.height + 1f, data.length + 1f)
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