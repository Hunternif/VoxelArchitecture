package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.IDRegistry
import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.storage.IVoxel
import org.joml.Vector3f

/** For creating and loading SceneObjects and Subsets. */
class SceneRegistry {
    val objectIDs = IDRegistry<SceneObject>()
    val subsetIDs = IDRegistry<SceneTree.Subset<*>>()

    fun newObject(
        start: Vector3f = Vector3f(),
        size: Vector3f = Vector3f(),
        color: ColorRGBa = Colors.defaultNodeBox,
        isGenerated: Boolean = false,
    ) : SceneObject {
        val id = objectIDs.newID()
        val obj = SceneObject(id, start, size, color, isGenerated)
        objectIDs.save(obj)
        return obj
    }

    fun newNode(
        node: Node,
        color: ColorRGBa = Colors.defaultNodeBox,
        isGenerated: Boolean = false,
    ) : SceneNode {
        val id = objectIDs.newID()
        val obj = SceneNode(id, node, color, isGenerated)
        objectIDs.save(obj)
        return obj
    }

    fun newVoxelGroup(
        label: String,
        data: IStorage3D<out IVoxel?>,
        isGenerated: Boolean = false,
    ) : SceneVoxelGroup {
        val id = objectIDs.newID()
        val obj = SceneVoxelGroup(id, label, data, isGenerated)
        objectIDs.save(obj)
        return obj
    }

    fun <T : SceneObject> newSubset(name: String): SceneTree.Subset<T> {
        val id = subsetIDs.newID()
        val subset = SceneTree.Subset<T>(id, name)
        subsetIDs.save(subset)
        return subset
    }

    fun save(obj: Any) {
        when (obj) {
            is SceneObject -> objectIDs.save(obj)
            is SceneTree.Subset<*> -> subsetIDs.save(obj)
        }
    }

    fun clear() {
        objectIDs.clear()
        subsetIDs.clear()
    }
}