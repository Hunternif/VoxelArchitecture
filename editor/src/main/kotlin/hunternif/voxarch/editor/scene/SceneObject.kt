package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.scene.models.boxFaces
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.findGlobalPosition
import hunternif.voxarch.storage.IStorage3D
import org.joml.Vector3f

/**
 * Base class for objects in the scene.
 * ([start], [start]+[size]) define the corners of its AABB.
 * [size] is in "natural" coordinates (not centric).
 * [color] is the color that is used to render its AABB.
 */
open class SceneObject(
    val start: Vector3f = Vector3f(),
    val size: Vector3f = Vector3f(),
    var color: ColorRGBa,
) {
    /** Read-only! Corner of the AAB in "natural" coordinates (not in voxels) */
    val end: Vector3f = Vector3f()
        get() = field.set(start).add(size)

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
    override val children: List<SceneNode> get() = _children.toList()

    init { update() }

    override fun addChild(child: SceneNode) {
        child.parent = this
        _children.add(child)
        node.addChild(child.node)
    }
    override fun removeChild(child: SceneNode) {
        if (_children.remove(child)) {
            child.parent = null
            node.removeChild(child.node)
        }
    }

    override fun update() {
        val origin = node.findGlobalPosition()
        if (node is Room) {
            start.set(origin).add(node.start).sub(0.5f, 0.5f, 0.5f)
            size.set(node.size).add(1f, 1f, 1f)
        } else {
            start.set(origin).sub(0.5f, 0.5f, 0.5f)
            size.set(1f, 1f, 1f)
        }
    }
}

class SceneVoxelGroup(
    val label: String,
    val data: IStorage3D<VoxColor?>,
    /** Voxel centric coordinates of the lower corner */
    val origin: Vector3f = Vector3f(),
) : SceneObject(color = Colors.transparent), INested<SceneVoxelGroup> {
    override var parent: SceneVoxelGroup? = null
    private val _children = LinkedHashSet<SceneVoxelGroup>()
    override val children: List<SceneVoxelGroup> get() = _children.toList()

    init { update() }

    override fun addChild(child: SceneVoxelGroup) {
        child.parent = this
        _children.add(child)
    }
    override fun removeChild(child: SceneVoxelGroup) {
        if (_children.remove(child)) {
            child.parent = null
        }
    }

    override fun update() {
        start.set(origin).sub(0.5f, 0.5f, 0.5f)
        size.set(data.width + 1f, data.height + 1f, data.length + 1f)
    }
}

interface INested<T : INested<T>> {
    var parent: T?
    val children: List<T>
    fun addChild(child: T)
    fun removeChild(child: T)
}