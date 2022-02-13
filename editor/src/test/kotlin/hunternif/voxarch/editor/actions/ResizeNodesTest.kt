package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.util.AADirection3D.*
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3i
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ResizeNodesTest : BaseActionTest() {
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
            dragFace(POS_X, 5f)
            commit()
        }
        assertEquals(Vec3(0, 0, 0), room1.origin)
        assertEquals(Vec3(0, 0, 0), room1.start)
        assertEquals(Vec3(6, 1, 1), room1.size)
        assertEquals(false, room1.isCentered())
        assertEquals(Vec3(100, 200, 300), room2.origin)
        assertEquals(Vec3(-10, 0, -5), room2.start)
        assertEquals(Vec3(20, 10, 10), room2.size)
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
        assertEquals(Vec3(6, 1, 1), room1.size)
        assertEquals(false, room1.isCentered())
        assertEquals(Vec3(100, 200, 300), room2.origin)
        assertEquals(Vec3(-10, 0, -5), room2.start)
        assertEquals(Vec3(20, 10, 10), room2.size)
        assertEquals(true, room2.isCentered())
    }
}