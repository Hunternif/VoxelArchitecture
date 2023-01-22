package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.scene.shaders.VoxelRenderMode
import hunternif.voxarch.editor.util.add
import hunternif.voxarch.editor.util.set
import hunternif.voxarch.plan.findGlobalPosition
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.vector.Vec3

class SceneVoxelGroup(
    id: Int,
    val label: String,
    val data: IStorage3D<out IVoxel?>,
    val renderMode: VoxelRenderMode = VoxelRenderMode.COLORED,
    isGenerated: Boolean = false,
    /** Voxel centric coordinates of the lower corner */
    val origin: Vec3 = Vec3(0, 0, 0),
) : SceneObject(
    id,
    color = Colors.transparent,
    isGenerated = isGenerated,
) {
    init {
        update()
    }

    override fun update() {
        box.center.set(findGlobalPosition())
            .add(data.minX, data.minY, data.minZ)
            .add(data.sizeVec.toVec3() / 2)
            .add(-0.5, -0.5, -0.5)
        box.size.set(data.sizeVec)
        box.updateMesh()
    }

    /** Finds offset of this node's origin in global coordinates, i.e.
     * in the coordinate space where its highest parent node exists.
     */
    fun findGlobalPosition(): Vec3 {
        var depth = 0
        val result = origin.clone()
        var parent = this.parent
        while (parent != null) {
            depth++
            if (depth > 10000) {
                println("Possibly infinite recursion!")
                return result
            }
            when (parent) {
                is SceneVoxelGroup -> result.addLocal(parent.origin)
                is SceneNode -> {
                    result.addLocal(parent.node.findGlobalPosition())
                    break
                }
            }
            parent = parent.parent
        }
        return result
    }

    override fun toString() = label
}