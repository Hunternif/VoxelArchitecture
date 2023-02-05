package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3i
import org.junit.Assert.*
import org.junit.Test

class CreateRoomTest : BaseAppTest() {
    @Test
    fun `create room, undo redo`() = app.state.run {
        val node = app.createRoom(Vector3i(1, 2, 3), Vector3i(11, 22, 33), false)
        assertEquals(setOf(node), sceneObjects.toSet())
        assertEquals(rootNode, node.parent)
        assertEquals(setOf(node), rootNode.children.toSet())
        assertEquals(setOf(node.node), rootNode.node.children.toSet())
        app.undo()
        assertEquals(emptySet<SceneObject>(), sceneObjects.toSet())
        assertEquals(emptySet<SceneObject>(), rootNode.children.toSet())
        assertEquals(emptySet<Node>(), rootNode.node.children.toSet())
        app.redo()
        assertEquals(setOf(node), sceneObjects.toSet())
        assertEquals(rootNode, node.parent)
        assertEquals(setOf(node), rootNode.children.toSet())
        assertEquals(setOf(node.node), rootNode.node.children.toSet())
    }

    @Test
    fun `create unecentered room`() = app.state.run {
        val node = app.createRoom(Vector3i(1, 2, 3), Vector3i(11, 22, 33), false)
        val room = node.node as Room
        assertEquals(setOf(node), sceneObjects.toSet())
        assertEquals(rootNode, node.parent)
        assertEquals(Vec3(1, 2, 3), room.origin)
        assertEquals(Vec3(10, 20, 30), room.size)
        assertEquals(Vec3(0, 0, 0), room.start)
    }

    @Test
    fun `create centered room`() = app.state.run {
        val node = app.createRoom(Vector3i(1, 2, 3), Vector3i(11, 22, 33), true)
        val room = node.node as Room
        assertEquals(setOf(node), sceneObjects.toSet())
        assertEquals(rootNode, node.parent)
        assertEquals(Vec3(6, 2, 18), room.origin)
        assertEquals(Vec3(10, 20, 30), room.size)
        assertEquals(Vec3(-5, 0, -15), room.start)
    }

    @Test
    fun `create room under parent`() = app.state.run {
        val parentRoom = app.createRoom(Vector3i(0, 0, 0), Vector3i(1, 1, 1))
        parentNode = parentRoom
        val childRoom = app.createRoom(Vector3i(1, 2, 3), Vector3i(2, 2, 2))
        assertEquals(setOf(parentRoom, childRoom), sceneObjects.toSet())
        assertEquals(parentRoom, childRoom.parent)
        assertEquals(parentRoom.node, childRoom.node.parent)
    }
}