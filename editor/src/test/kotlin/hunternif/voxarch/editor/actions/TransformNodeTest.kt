package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.util.toVector3i
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@Suppress("PrivatePropertyName")
class TransformNodeTest : BaseActionTest() {
    private lateinit var node: SceneNode
    private lateinit var room: Room

    // Initial values
    private val ORIGIN = Vec3(17, 18, 19)
    private val SIZE = Vec3(10, 20, 30)
    private val START = Vec3(0, 0, 0)

    @Before
    fun setup() = app.state.run {
        node = app.createRoom(ORIGIN.toVector3i(), (ORIGIN + SIZE).toVector3i())
        room = node.node as Room
    }
    
    @Test
    fun `transform origin undo redo`() {
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        
        app.transformNodeOrigin(node, Vec3(1, 2, 3), Vec3(4, 5, 6))
        assertEquals(Vec3(4, 5, 6), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        
        app.undo()
        assertEquals(Vec3(1, 2, 3), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
        
        app.redo()
        assertEquals(Vec3(4, 5, 6), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
    }

    @Test
    fun `transform size undo redo`() {
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())

        app.transformNodeSize(node, Vec3(1, 2, 3), Vec3(4, 5, 6))
        assertEquals(ORIGIN, room.origin)
        assertEquals(Vec3(4, 5, 6), room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())

        app.undo()
        assertEquals(ORIGIN, room.origin)
        assertEquals(Vec3(1, 2, 3), room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())

        app.redo()
        assertEquals(ORIGIN, room.origin)
        assertEquals(Vec3(4, 5, 6), room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())
    }

    @Test
    fun `transform start undo redo`() {
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(START, room.start)
        assertEquals(false, room.isCentered())

        app.transformNodeStart(node, Vec3(1, 2, 3), Vec3(4, 5, 6))
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(4, 5, 6), room.start)
        assertEquals(false, room.isCentered())

        app.undo()
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(1, 2, 3), room.start)
        assertEquals(false, room.isCentered())

        app.redo()
        assertEquals(ORIGIN, room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(4, 5, 6), room.start)
        assertEquals(false, room.isCentered())
    }

    @Test
    fun `transform centered true undo redo`() {
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(0, 0, 0), room.start)
        assertEquals(false, room.isCentered())

        app.transformNodeCentered(node, true)
        assertEquals(Vec3(22, 18, 34), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())

        app.undo()
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(0, 0, 0), room.start)
        assertEquals(false, room.isCentered())

        app.redo()
        assertEquals(Vec3(22, 18, 34), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())
    }

    @Test
    fun `transform centered false undo redo`() {
        room.setCentered(true)
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())

        app.transformNodeCentered(node, false)
        assertEquals(Vec3(12, 18, 4), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(0, 0, 0), room.start)
        assertEquals(false, room.isCentered())

        app.undo()
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())

        app.redo()
        assertEquals(Vec3(12, 18, 4), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(0, 0, 0), room.start)
        assertEquals(false, room.isCentered())
    }

    @Test
    fun `transform centered with modified start undo redo`() {
        room.start = Vec3(700, 800, 900)
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(700, 800, 900), room.start)
        assertEquals(false, room.isCentered())

        app.transformNodeCentered(node, true)
        assertEquals(Vec3(722, 818, 934), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())

        app.undo()
        assertEquals(Vec3(17, 18, 19), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(700, 800, 900), room.start)
        assertEquals(false, room.isCentered())

        app.redo()
        assertEquals(Vec3(722, 818, 934), room.origin)
        assertEquals(SIZE, room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
        assertEquals(true, room.isCentered())
    }
}