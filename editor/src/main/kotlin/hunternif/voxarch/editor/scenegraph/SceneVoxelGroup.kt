package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.scene.shaders.VoxelRenderMode
import hunternif.voxarch.editor.util.add
import hunternif.voxarch.editor.util.set
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3f

class SceneVoxelGroup(
    id: Int,
    val label: String,
    val data: IStorage3D<out IVoxel?>,
    val renderMode: VoxelRenderMode = VoxelRenderMode.COLORED,
    isGenerated: Boolean = false,
    /** Voxel centric coordinates of the lower corner */
    val origin: Vector3f = Vector3f(),
) : SceneObject(
    id,
    color = Colors.transparent,
    isGenerated = isGenerated,
) {
    init {
        update()
    }

    override fun update() {
        box.center.set(origin)
            .add(data.minX, data.minY, data.minZ)
            .add(data.sizeVec.toVec3() / 2)
            .add(-0.5, -0.5, -0.5)
        box.size.set(data.sizeVec)
        box.updateMesh()
        aabb.wrapVoxels(
            origin.toVec3() + Vec3(data.minX, data.minY, data.minZ),
            data.sizeVec.toVec3()
        )
    }

    override fun toString() = label
}