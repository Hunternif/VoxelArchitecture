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

    var fromCenter = false

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

    // bottom vertices
    private val v1 = Vector3f()
    private val v2 = Vector3f()
    private val v3 = Vector3f()
    private val v4 = Vector3f()
    // top vertices
    private val v5 = Vector3f()
    private val v6 = Vector3f()
    private val v7 = Vector3f()
    private val v8 = Vector3f()
    // bottom edges
    private val e12 = Edge(v1, v2)
    private val e23 = Edge(v2, v3)
    private val e34 = Edge(v3, v4)
    private val e41 = Edge(v4, v1)
    // top edges
    private val e56 = Edge(v5, v6)
    private val e67 = Edge(v6, v7)
    private val e78 = Edge(v7, v8)
    private val e85 = Edge(v8, v5)
    // vertical edges
    private val e15 = Edge(v1, v5)
    private val e26 = Edge(v2, v6)
    private val e37 = Edge(v3, v7)
    private val e48 = Edge(v4, v8)
    private val edges = ArrayList<Edge>(12)

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
        refreshVertices()
        edges.clear()

        // base rectangle
        edges.apply {
            add(e12)
            add(e23)
            add(e34)
            add(e41)
        }

        if (state == CHOOSING_HEIGHT || state == COMPLETE) {

            // top rectangle
            edges.apply {
                add(e56)
                add(e67)
                add(e78)
                add(e85)
            }

            // vertical edges
            edges.apply {
                add(e15)
                add(e26)
                add(e37)
                add(e48)
            }
        }
        return edges
    }

    private fun refreshVertices() {
        v1.set(minX, minY, minZ)
        v2.set(minX, minY, maxZ)
        v3.set(maxX, minY, maxZ)
        v4.set(maxX, minY, minZ)
        v5.set(minX, maxY, minZ)
        v6.set(minX, maxY, maxZ)
        v7.set(maxX, maxY, maxZ)
        v8.set(maxX, maxY, minZ)
    }
}