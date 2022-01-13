package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.scene.NewNodeFrame.State.*
import hunternif.voxarch.editor.util.Edge
import org.joml.AABBf
import org.joml.Vector3f
import org.joml.Vector3i

/**
 * [start] and [end] are in centric voxel coordinates.
 * [minX] and the rest follow the actual outline of those voxels.
 */
class NewNodeFrame : AABBf() {
    enum class State {
        EMPTY, CHOOSING_BASE, CHOOSING_HEIGHT, COMPLETE
    }
    var state: State = EMPTY

    /** Offset from the center of the voxel to the edge of the frame.
     * 0 makes the frame run through the middle of a voxel.
     * 0.5 makes it run on the edge of a voxel. */
    private val voxCenterOffset = 0.4f

    var start: Vector3i = Vector3i()
        set(value) {
            field = value
            correctBounds()
        }
    var end: Vector3i = Vector3i()
        set(value) {
            field = value
            correctBounds()
        }

    init {
        correctBounds()
    }

    override fun correctBounds(): AABBf {
        minX = start.x.toFloat()
        minY = start.y.toFloat()
        minZ = start.z.toFloat()
        maxX = end.x.toFloat()
        maxY = end.y.toFloat()
        maxZ = end.z.toFloat()
        super.correctBounds()
        minX -= voxCenterOffset
        minY -= 0.5f // bottom is always at floor level
        minZ -= voxCenterOffset
        maxX += voxCenterOffset
        maxY += voxCenterOffset
        maxZ += voxCenterOffset
        return this
    }

    fun getEdges(): List<Edge> {
        if (state == EMPTY) return emptyList()

        val result = mutableListOf<Edge>()

        val v1 = Vector3f(minX, minY, minZ)
        val v2 = Vector3f(minX, minY, maxZ)
        val v3 = Vector3f(maxX, minY, maxZ)
        val v4 = Vector3f(maxX, minY, minZ)

        // base rectangle
        result.apply {
            add(Edge(v1, v2))
            add(Edge(v2, v3))
            add(Edge(v3, v4))
            add(Edge(v4, v1))
        }

        if (state == CHOOSING_HEIGHT || state == COMPLETE) {
            val v5 = Vector3f(minX, maxY, minZ)
            val v6 = Vector3f(minX, maxY, maxZ)
            val v7 = Vector3f(maxX, maxY, maxZ)
            val v8 = Vector3f(maxX, maxY, minZ)

            // top rectangle
            result.apply {
                add(Edge(v5, v6))
                add(Edge(v6, v7))
                add(Edge(v7, v8))
                add(Edge(v8, v5))
            }

            // vertical edges
            result.apply {
                add(Edge(v1, v5))
                add(Edge(v2, v6))
                add(Edge(v3, v7))
                add(Edge(v4, v8))
            }
        }
        return result
    }
}