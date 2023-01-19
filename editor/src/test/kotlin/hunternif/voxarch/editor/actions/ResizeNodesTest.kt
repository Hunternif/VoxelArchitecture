package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.util.AADirection3D.*
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3i
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ResizeNodesTest : BaseAppTest() {
    private lateinit var node1: SceneNode
    private lateinit var node2: SceneNode
    private lateinit var room1: Room
    private lateinit var room2: Room

    @Before
    fun setup() = app.state.run {
        node1 = app.createRoom(Vector3i(0, 0, 0), Vector3i(1, 1, 1))
        node2 = app.createRoom(Vector3i(100, 200, 300), Vector3i(110, 210, 310))
        room1 = node1.node as Room
        room2 = node2.node as Room
    }

    @Test
    fun `expand 2 nodes, undo redo`() {
        val builder = app.resizeBuilder(listOf(node1, node2))

        builder.dragFace(POS_X, 10f)
        assertUnchangedStartOriginUncentered()
        assertEquals(Vec3(11, 1, 1), room1.size)
        assertEquals(Vec3(20, 10, 10), room2.size)

        builder.dragFace(POS_X, 5f)
        assertUnchangedStartOriginUncentered()
        assertEquals(Vec3(6, 1, 1), room1.size)
        assertEquals(Vec3(15, 10, 10), room2.size)

        builder.commit()
        app.undo()
        assertUnchangedStartOriginUncentered()
        assertEquals(Vec3(1, 1, 1), room1.size)
        assertEquals(Vec3(10, 10, 10), room2.size)

        app.redo()
        assertUnchangedStartOriginUncentered()
        assertEquals(Vec3(6, 1, 1), room1.size)
        assertEquals(Vec3(15, 10, 10), room2.size)
    }

    @Test
    fun `shrink 2 nodes beyond limit, undo redo`() {
        app.resizeBuilder(listOf(node1, node2)).apply {
            dragFace(POS_X, -5f)
            commit()
        }
        assertUnchangedStartOriginUncentered()
        assertEquals(Vec3(0, 1, 1), room1.size)
        assertEquals(Vec3(5, 10, 10), room2.size)

        app.undo()
        assertUnchangedStartOriginUncentered()
        assertEquals(Vec3(1, 1, 1), room1.size)
        assertEquals(Vec3(10, 10, 10), room2.size)

        app.redo()
        assertUnchangedStartOriginUncentered()
        assertEquals(Vec3(0, 1, 1), room1.size)
        assertEquals(Vec3(5, 10, 10), room2.size)
    }

    private fun assertUnchangedStartOriginUncentered() {
        assertEquals(Vec3(0, 0, 0), room1.origin)
        assertEquals(Vec3(0, 0, 0), room1.start)
        assertEquals(false, room1.isCentered())

        assertEquals(Vec3(100, 200, 300), room2.origin)
        assertEquals(Vec3(0, 0, 0), room2.start)
        assertEquals(false, room2.isCentered())
    }

    @Test
    fun `resize centered & uncentered node, undo redo`() {
        room2.setCentered(true)
        assertEquals(Vec3(100, 200, 300), room2.origin)
        assertEquals(Vec3(-5, 0, -5), room2.start)
        assertEquals(Vec3(10, 10, 10), room2.size)
        assertEquals(true, room2.isCentered())

        app.resizeBuilder(listOf(node1, node2)).apply {
            dragFace(POS_X, 6f)
            commit()
        }
        assertEquals(Vec3(0, 0, 0), room1.origin)
        assertEquals(Vec3(0, 0, 0), room1.start)
        assertEquals(Vec3(7, 1, 1), room1.size)
        assertEquals(false, room1.isCentered())
        assertEquals(Vec3(103, 200, 300), room2.origin)
        assertEquals(Vec3(-8, 0, -5), room2.start)
        assertEquals(Vec3(16, 10, 10), room2.size)
        assertEquals(true, room2.isCentered())

        app.undo()
        assertEquals(Vec3(0, 0, 0), room1.origin)
        assertEquals(Vec3(0, 0, 0), room1.start)
        assertEquals(Vec3(1, 1, 1), room1.size)
        assertEquals(false, room1.isCentered())
        assertEquals(Vec3(100, 200, 300), room2.origin)
        assertEquals(Vec3(-5, 0, -5), room2.start)
        assertEquals(Vec3(10, 10, 10), room2.size)
        assertEquals(true, room2.isCentered())

        app.redo()
        assertEquals(Vec3(0, 0, 0), room1.origin)
        assertEquals(Vec3(0, 0, 0), room1.start)
        assertEquals(Vec3(7, 1, 1), room1.size)
        assertEquals(false, room1.isCentered())
        assertEquals(Vec3(103, 200, 300), room2.origin)
        assertEquals(Vec3(-8, 0, -5), room2.start)
        assertEquals(Vec3(16, 10, 10), room2.size)
        assertEquals(true, room2.isCentered())
    }

    @Test
    fun `resize symmetric centered node horizontal`() {
        room2.setCentered(true)
        val builder = app.resizeBuilder(listOf(node2))
        // grow
        builder.dragFace(POS_X, 1f, true)
        assertEquals(Vec3(100, 200, 300), room2.origin)
        assertEquals(Vec3(-6, 0, -5), room2.start)
        assertEquals(Vec3(12, 10, 10), room2.size)
        assertEquals(true, room2.isCentered())
        // shrink
        builder.dragFace(NEG_X, -1f, true)
        assertEquals(Vec3(100, 200, 300), room2.origin)
        assertEquals(Vec3(-4, 0, -5), room2.start)
        assertEquals(Vec3(8, 10, 10), room2.size)
        assertEquals(true, room2.isCentered())
    }

    @Test
    fun `resize symmetric centered node vertical`() {
        // when resizing symmetrically vertically, origin can change too
        room2.setCentered(true)
        val builder = app.resizeBuilder(listOf(node2))
        // grow
        builder.dragFace(POS_Y, 1f, true)
        assertEquals(Vec3(100, 199, 300), room2.origin)
        assertEquals(Vec3(-5, 0, -5), room2.start)
        assertEquals(Vec3(10, 12, 10), room2.size)
        assertEquals(true, room2.isCentered())
        // shrink
        builder.dragFace(NEG_Y, -1f, true)
        assertEquals(Vec3(100, 201, 300), room2.origin)
        assertEquals(Vec3(-5, 0, -5), room2.start)
        assertEquals(Vec3(10, 8, 10), room2.size)
        assertEquals(true, room2.isCentered())
    }

    @Test
    fun `resize asymmetric centered node horizontal`() {
        room2.setCentered(true)
        val builder = app.resizeBuilder(listOf(node2))
        // grow
        builder.dragFace(POS_X, 1f, false)
        assertEquals(Vec3(100.5, 200.0, 300.0), room2.origin)
        assertEquals(Vec3(-5.5, 0.0, -5.0), room2.start)
        assertEquals(Vec3(11, 10, 10), room2.size)
        assertEquals(true, room2.isCentered())
        // shrink
        builder.dragFace(NEG_Z, -1f, false)
        assertEquals(Vec3(100.0, 200.0, 300.5), room2.origin)
        assertEquals(Vec3(-5.0, 0.0, -4.5), room2.start)
        assertEquals(Vec3(10, 10, 9), room2.size)
        assertEquals(true, room2.isCentered())
    }

    @Test
    fun `resize asymmetric centered node vertical`() {
        room2.setCentered(true)
        val builder = app.resizeBuilder(listOf(node2))
        // grow
        builder.dragFace(POS_Y, 1f, false)
        assertEquals(Vec3(100, 200, 300), room2.origin)
        assertEquals(Vec3(-5, 0, -5), room2.start)
        assertEquals(Vec3(10, 11, 10), room2.size)
        assertEquals(true, room2.isCentered())
        // grow
        builder.dragFace(NEG_Y, 1f, false)
        assertEquals(Vec3(100, 199, 300), room2.origin)
        assertEquals(Vec3(-5, 0, -5), room2.start)
        assertEquals(Vec3(10, 11, 10), room2.size)
        assertEquals(true, room2.isCentered())
        // shrink
        builder.dragFace(NEG_Y, -1f, false)
        assertEquals(Vec3(100, 201, 300), room2.origin)
        assertEquals(Vec3(-5, 0, -5), room2.start)
        assertEquals(Vec3(10, 9, 10), room2.size)
        assertEquals(true, room2.isCentered())
    }

    @Test
    fun `resize asymmetric uncentered node with shifted start`() {
        // for uncentered node, start is always unchanged
        room1.origin = Vec3(10, 20, 30)
        room1.start = Vec3(123, 456, 789)
        val builder = app.resizeBuilder(listOf(node1))
        // grow
        builder.dragFace(POS_X, 1f, false)
        assertEquals(Vec3(10, 20, 30), room1.origin)
        assertEquals(Vec3(123, 456, 789), room1.start)
        assertEquals(Vec3(2, 1, 1), room1.size)
        // grow
        builder.dragFace(NEG_X, 1f, false)
        assertEquals(Vec3(9, 20, 30), room1.origin)
        assertEquals(Vec3(123, 456, 789), room1.start)
        assertEquals(Vec3(2, 1, 1), room1.size)
        // shrink
        builder.dragFace(NEG_X, -1f, false)
        assertEquals(Vec3(11, 20, 30), room1.origin)
        assertEquals(Vec3(123, 456, 789), room1.start)
        assertEquals(Vec3(0, 1, 1), room1.size)
    }

    @Test
    fun `resize symmetric uncentered node with shifted start`() {
        // for uncentered node, start is always unchanged
        room1.origin = Vec3(10, 20, 30)
        room1.start = Vec3(123, 456, 789)
        room1.size = Vec3(2, 2, 2)
        val builder = app.resizeBuilder(listOf(node1))
        // grow
        builder.dragFace(POS_X, 1f, true)
        assertEquals(Vec3(9, 20, 30), room1.origin)
        assertEquals(Vec3(123, 456, 789), room1.start)
        assertEquals(Vec3(4, 2, 2), room1.size)
        // grow
        builder.dragFace(NEG_X, 1f, true)
        assertEquals(Vec3(9, 20, 30), room1.origin)
        assertEquals(Vec3(123, 456, 789), room1.start)
        assertEquals(Vec3(4, 2, 2), room1.size)
        // shrink
        builder.dragFace(NEG_X, -1f, true)
        assertEquals(Vec3(11, 20, 30), room1.origin)
        assertEquals(Vec3(123, 456, 789), room1.start)
        assertEquals(Vec3(0, 2, 2), room1.size)
    }
}