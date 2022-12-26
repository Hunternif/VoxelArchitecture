package hunternif.voxarch.editor.scene.models.box

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.util.AADirection3D
import hunternif.voxarch.editor.util.AADirection3D.*
import hunternif.voxarch.editor.util.ColorRGBa
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
    color: ColorRGBa = Colors.defaultNodeBox,
) : BoxMesh(center, size, angleY, color) {
    /** Thickness of a face. */
    private val w = 0.1f

    private val facePosX = vertices.let { BoxFace.new(POS_X, it[0], it[4], it[5], it[1]) }
    private val facePosY = vertices.let { BoxFace.new(POS_Y, it[4], it[5], it[6], it[7]) }
    private val facePosZ = vertices.let { BoxFace.new(POS_Z, it[0], it[4], it[7], it[3]) }
    private val faceNegX = vertices.let { BoxFace.new(NEG_X, it[2], it[3], it[7], it[6]) }
    private val faceNegY = vertices.let { BoxFace.new(NEG_Y, it[0], it[3], it[2], it[1]) }
    private val faceNegZ = vertices.let { BoxFace.new(NEG_Z, it[1], it[5], it[6], it[2]) }

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
            it.updateMesh()
        }
    }
}

/**
 * A face on a [BoxMeshWithFaces].
 * Remembers its initial orientation [dir] after rotations.
 * The part it inherits from [BoxMesh] acts as a hit box,
 * but the actual rendered quad is defined by [quadVertices].
 */
class BoxFace(
    val dir: AADirection3D,
    val quadVertices: Array<out Vector3f>,
) : BoxMesh() {
    val normal: Vector3f = Vector3f(dir.vec)

    override fun updateMesh() {
        super.updateMesh()
        normal.set(dir.vec)
        val m = Matrix4f().rotateY(angleY.toRadians())
        normal.mulProject(m)
    }

    companion object {
        fun new(
            dir: AADirection3D,
            vararg quadVertices: Vector3f,
        ) = BoxFace(dir, quadVertices)
    }
}