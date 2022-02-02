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
 * [start], [end] define the corners of its AABB.
 * [color] is the color that is used to render its AABB.
 */
open class SceneObject(
    val start: Vector3f = Vector3f(),
    val end: Vector3f = Vector3f(),
    var color: ColorRGBa,
) {
    /** Size of the AAB in coordinates (not in voxels!) */
    val size: Vector3f = Vector3f()
        get() = field.set(end).sub(start)

    /** AABB in screen coordinates relative to viewport.
     * Can be updated at any time. */
    val screenAABB: AABB2Df = AABB2Df()

    val faces: Array<AABBFace> by lazy { boxFaces(start, end, 0.1f) }
    fun updateFaces() = boxFaces(start, end, 0.1f).copyInto(faces)
}

class SceneNode(val node: Node) : SceneObject(color = Colors.defaultNodeBox) {
    var parent: SceneNode? = null
    private val _children = LinkedHashSet<SceneNode>()
    val children: List<SceneNode> get() = _children.toList()

    fun addChild(child: SceneNode) {
        child.parent = this
        _children.add(child)
        node.addChild(child.node)
    }
    fun removeChild(child: SceneNode) {
        if (_children.remove(child)) {
            child.parent = null
            node.removeChild(child.node)
        }
    }

    fun update() {
        val origin = node.findGlobalPosition()
        if (node is Room) {
            start.set(origin).add(node.start).sub(0.5f, 0.5f, 0.5f)
            end.set(start).add(node.size).add(1f, 1f, 1f)
        } else {
            start.set(origin).sub(0.5f, 0.5f, 0.5f)
            end.set(start).add(1f, 1f, 1f)
        }
    }
}

class SceneVoxelGroup(
    val data: IStorage3D<VoxColor?>,
) : SceneObject(color = Colors.transparent) {
    init { update() }
    fun update() {
        data.run {
            end.set(start).add(width + 1f, height + 1f, length + 1f)
        }
    }
}