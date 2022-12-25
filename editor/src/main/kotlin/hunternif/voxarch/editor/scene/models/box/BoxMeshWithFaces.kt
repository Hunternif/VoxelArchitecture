package hunternif.voxarch.editor.scene.models.box

import hunternif.voxarch.editor.util.AADirection3D
import hunternif.voxarch.editor.util.AADirection3D.*
import hunternif.voxarch.util.toRadians
import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * [BoxMesh] with [BoxFace]s.
 */
class BoxMeshWithFaces(
    center: Vector3f = Vector3f(),
    size: Vector3f = Vector3f(),
    angleY: Float = 0f,
) : BoxMesh(center, size, angleY) {
    /** Thickness of a face. */
    private val w = 0.1f

    private val facePosX = BoxFace(POS_X)
    private val facePosY = BoxFace(POS_Y)
    private val facePosZ = BoxFace(POS_Z)
    private val faceNegX = BoxFace(NEG_X)
    private val faceNegY = BoxFace(NEG_Y)
    private val faceNegZ = BoxFace(NEG_Z)

    /** A thin lid attached to each face, facing outwards. */
    val faces: Array<BoxFace> = arrayOf(
        facePosX, facePosY, facePosZ, faceNegX, faceNegY, faceNegZ
    )

    /** Recalculates vertices and faces,
     *  based on size, position and angle. */
    override fun updateMesh() {
        super.updateMesh()
        // reset faces and apply transformation
        facePosX.size.set(w, size.y - w * 2, size.z - w * 2)
        faceNegX.size.set(facePosX.size)
        facePosY.size.set(size.x - w * 2, w, size.z - w * 2)
        faceNegY.size.set(facePosY.size)
        facePosZ.size.set(size.x - w * 2, size.y - w * 2, w)
        faceNegZ.size.set(facePosZ.size)
        facePosX.center.set((size.x - w) / 2, 0f, 0f)
        faceNegX.center.set(-(size.x - w) / 2, 0f, 0f)
        facePosY.center.set(0f, (size.y - w) / 2, 0f)
        faceNegY.center.set(0f, -(size.y - w) / 2, 0f)
        facePosZ.center.set(0f, 0f, (size.z - w) / 2)
        faceNegZ.center.set(0f, 0f, -(size.z - w) / 2)
        val m = Matrix4f()
            .translation(center)
            .rotateY(angleY.toRadians())
        faces.forEach {
            it.angleY = angleY
            it.center.mulProject(m)
        }
    }
}

/**
 * A face on a [BoxMeshWithFaces].
 * Remembers its initial orientation [dir] after rotations.
 */
class BoxFace(
    val dir: AADirection3D,
    center: Vector3f = Vector3f(),
    size: Vector3f = Vector3f(),
    angleY: Float = 0f,
) : BoxMesh(center, size, angleY)