package hunternif.voxarch.editor.scene.models.box

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.util.AABB2Df
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.reset
import hunternif.voxarch.util.toRadians
import org.joml.AABBf
import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * A rectangular box with faces. Not axis-aligned!
 *
 * @param center center point of the box across all axes, in natural absolute coordinates.
 * @param size (length, height, width) in natural coordinates.
 * @param angleY angle around the Y axis, in degrees.
 * @param color the box is rendered with this solid color (can be transparent).
 */
open class BoxMesh(
    val center: Vector3f = Vector3f(),
    val size: Vector3f = Vector3f(),
    var angleY: Float = 0f,
    var color: ColorRGBa = Colors.defaultNodeBox,
) {
    /** Axis-aligned bounding box, accounting for rotation. */
    val aabb: AABBf = AABBf()

    /** AABB in screen coordinates relative to viewport. */
    val screenAABB: AABB2Df = AABB2Df()

    /** Corner vertices in absolute coordinates, in no particular order. */
    val vertices: Array<Vector3f> = arrayOf(
        Vector3f(), Vector3f(), Vector3f(), Vector3f(),
        Vector3f(), Vector3f(), Vector3f(), Vector3f(),
    )

    /** Recalculate vertices based on size, position and angle. */
    open fun updateMesh() {
        // reset vertices and apply transformation
        vertices[0].set(1f, -1f, 1f)
        vertices[1].set(1f, -1f, -1f)
        vertices[2].set(-1f, -1f, -1f)
        vertices[3].set(-1f, -1f, 1f)
        vertices[4].set(1f, 1f, 1f)
        vertices[5].set(1f, 1f, -1f)
        vertices[6].set(-1f, 1f, -1f)
        vertices[7].set(-1f, 1f, 1f)
        val m = Matrix4f()
            .translation(center)
            .rotateY(angleY.toRadians())
            .scale(size.x / 2, size.y / 2, size.z / 2)
        aabb.reset()
        vertices.forEach {
            it.mulProject(m)
            aabb.union(it)
        }
    }

    /** Recalculate on-screen 2D AABB. */
    fun updateAABB(camera: OrbitalCamera) = screenAABB.run {
        setMin(camera.projectToViewport(center))
        setMax(minX, minY)
        vertices.forEach {
            union(camera.projectToViewport(it))
        }
    }
}