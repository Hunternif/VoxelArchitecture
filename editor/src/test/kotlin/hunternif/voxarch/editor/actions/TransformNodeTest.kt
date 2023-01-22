package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.util.toVector3i
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@Suppress("PrivatePropertyName")
class TransformNodeTest : BaseAppTest() {
    private lateinit var node: SceneNode
    private lateinit var childNode: SceneNode
    private lateinit var room: Room
    private lateinit var child: Room

    // Initial values
    private val ORIGIN = Vec3(17, 18, 19)
    private val SIZE = Vec3(10, 20, 30)
    private val START = Vec3(0, 0, 0)
    private val CHILD_ORIGIN = Vec3(-1, -1, -1)
    private val CHILD_SIZE = Vec3(1, 1, 1)

    @Before
    fun setup() = app.state.run {
        node = app.createRoom(ORIGIN.toVector3i(), (ORIGIN + SIZE).toVector3i())
        app.setParentNode(node)
        childNode = app.createRoom(
            (ORIGIN + CHILD_ORIGIN).toVector3i(),
            (ORIGIN + CHILD_ORIGIN + CHILD_SIZE).toVector3i()
        )
        room = node.node as Room
        child = childNode.node as Room
    }
    
    @Test
    fun `transform origin undo redo`() {
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)
        
        app.transformObjOrigin(node, Vec3(1, 2, 3), Vec3(4, 5, 6))
        assertEquals(Vec3(4, 5, 6), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.undo()
        assertEquals(Vec3(1, 2, 3), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)
        
        app.redo()
        assertEquals(Vec3(4, 5, 6), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)
    }

    @Test
    fun `transform size undo redo`() {
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.transformNodeSize(node, Vec3(1, 2, 3), Vec3(4, 5, 6))
        assertEquals(ORIGIN, room.origin)
        assertEquals(Vec3(4, 5, 6), room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.undo()
        assertEquals(ORIGIN, room.origin)
        assertEquals(Vec3(1, 2, 3), room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.redo()
        assertEquals(ORIGIN, room.origin)
        assertEquals(Vec3(4, 5, 6), room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)
    }

    @Test
    fun `transform start undo redo`() {
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.transformNodeStart(node, Vec3(1, 2, 3), Vec3(4, 5, 6))
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(4, 5, 6), room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.undo()
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(1, 2, 3), room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.redo()
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(4, 5, 6), room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)
    }

    @Test
    fun `transform centered true undo redo`() {
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(0, 0, 0), room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.transformNodeCentered(node, true)
        assertEquals(Vec3(22, 18, 34), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())
        assertEquals(Vec3(-6, -1, -16), child.origin)

        app.undo()
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(0, 0, 0), room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.redo()
        assertEquals(Vec3(22, 18, 34), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())
        assertEquals(Vec3(-6, -1, -16), child.origin)
    }

    @Test
    fun `transform centered false undo redo`() {
        room.setCentered(true)
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.transformNodeCentered(node, false)
        assertEquals(Vec3(12, 18, 4), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(0, 0, 0), room.start)
        assertEquals(false, room.isCentered())
        assertEquals(Vec3(4, -1, 14), child.origin)

        app.undo()
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.redo()
        assertEquals(Vec3(12, 18, 4), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(0, 0, 0), room.start)
        assertEquals(false, room.isCentered())
        assertEquals(Vec3(4, -1, 14), child.origin)
    }

    @Test
    fun `transform centered with modified start undo redo`() {
        room.start = Vec3(700, 800, 900)
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(700, 800, 900), room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.transformNodeCentered(node, true)
        assertEquals(Vec3(722, 818, 934), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())
        assertEquals(Vec3(-706, -801, -916), child.origin)

        app.undo()
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(700, 800, 900), room.start)
        assertEquals(false, room.isCentered())
        assertEquals(CHILD_ORIGIN, child.origin)

        app.redo()
        assertEquals(Vec3(722, 818, 934), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())
        assertEquals(Vec3(-706, -801, -916), child.origin)
    }

    @Test
    fun `transform rotation undo redo`() {
        assertEquals(0.0, room.rotationY, 0.00000001)

        app.transformNodeRotation(node, 0.0, 45.0)
        assertEquals(45.0, room.rotationY, 0.00000001)

        app.transformNodeRotation(node, 45.0, 99.0)
        assertEquals(99.0, room.rotationY, 0.00000001)

        app.undo()
        assertEquals(45.0, room.rotationY, 0.00000001)

        app.redo()
        assertEquals(99.0, room.rotationY, 0.00000001)
    }
}