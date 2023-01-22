package hunternif.voxarch.editor.scene.models.box

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.util.toRadians
import org.joml.AABBf
import org.joml.Intersectionf
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

/**
 * Oriented rectangular box with faces. Not axis-aligned!
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
    /** Convenience: absolute position of the point in the middle of the floor. */
    val floorCenter: Vector3f = Vector3f()
        get() = field.set(center).sub(0f, size.y / 2f, 0f)

    /** Axis-aligned bounding box, accounting for rotation. */
    val aabb: AABBf = AABBf()

    /** AABB in screen coordinates relative to viewport. */
    val screenAABB: AABB2Df = AABB2Df()

    /** Corner vertices in absolute coordinates, in no particular order. */
    val vertices: Array<Vector3f> = arrayOf(
        Vector3f(), Vector3f(), Vector3f(), Vector3f(),
        Vector3f(), Vector3f(), Vector3f(), Vector3f(),
    )

    /** On-screen 2D coordinates of corner vertices */
    val screenVertices: Array<Vector2f> = arrayOf(
        Vector2f(), Vector2f(), Vector2f(), Vector2f(),
        Vector2f(), Vector2f(), Vector2f(), Vector2f(),
    )

    /** Triangles that make this mesh. */
    val triangles: Array<Triangle> by lazy {
        vertices.let {
            arrayOf(
                // ordered CCW
                // bottom face
                Triangle(it[0], it[2], it[1]),
                Triangle(it[0], it[3], it[2]),
                // top face
                Triangle(it[4], it[5], it[7]),
                Triangle(it[5], it[6], it[7]),
                // front face
                Triangle(it[0], it[4], it[7]),
                Triangle(it[0], it[7], it[3]),
                // back face
                Triangle(it[1], it[2], it[5]),
                Triangle(it[2], it[6], it[5]),
                // left face
                Triangle(it[3], it[6], it[2]),
                Triangle(it[3], it[7], it[6]),
                // right face
                Triangle(it[1], it[4], it[0]),
                Triangle(it[1], it[5], it[4]),
            )
        }
    }

    /** On-screen projection of triangles, for mouse hit testing. */
    val screenTriangles: Array<Triangle2D> by lazy {
        screenVertices.let {
            arrayOf(
                // just in case, these are ordered CCW
                // bottom face
                Triangle2D(it[0], it[2], it[1]),
                Triangle2D(it[0], it[3], it[2]),
                // top face
                Triangle2D(it[4], it[5], it[7]),
                Triangle2D(it[5], it[6], it[7]),
                // front face
                Triangle2D(it[0], it[4], it[7]),
                Triangle2D(it[0], it[7], it[3]),
                // back face
                Triangle2D(it[1], it[2], it[5]),
                Triangle2D(it[2], it[6], it[5]),
                // left face
                Triangle2D(it[3], it[6], it[2]),
                Triangle2D(it[3], it[7], it[6]),
                // right face
                Triangle2D(it[1], it[4], it[0]),
                Triangle2D(it[1], it[5], it[4]),
            )
        }
    }

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

    /** Recalculate on-screen 2D coordinates. */
    fun updateScreenProjection(camera: OrbitalCamera) {
        screenAABB.reset()
        vertices.forEachIndexed { i, v ->
            val screenPos = camera.projectToViewport(v)
            screenVertices[i].set(screenPos)
            screenAABB.union(screenPos)
        }
    }

    /** Returns true if cursor ([posX], [posY]) hits this box on the screen. */
    fun testCursor(posX: Float, posY: Float): Boolean {
        val p = Vector2f(posX, posY)
        return screenTriangles.any {
            Intersectionf.testPointTriangle(p, it.p1, it.p2, it.p3)
        }
    }
}