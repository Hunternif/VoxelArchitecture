package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3f

class SceneVoxelGroup(
    id: Int,
    val label: String,
    val data: IStorage3D<out IVoxel?>,
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
        updateFaces()
        wrapVoxels(
            origin.toVec3() + Vec3(data.minX, data.minY, data.minZ),
            data.sizeVec.toVec3()
        )
    }

    override fun toString() = label
}