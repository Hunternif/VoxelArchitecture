package hunternif.voxarch.editor.file

import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.centeredRoom
import hunternif.voxarch.plan.room
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3i
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FileSpecTest : BaseActionTest() {
    private lateinit var root: Structure
    private lateinit var room1: Room
    private lateinit var room2: Room
    private lateinit var room3: Room
    private lateinit var tempDir: java.nio.file.Path

    @Before
    fun setup() {
        // This structure mirrors what will be created inside AppState
        root = Structure()
        room1 = root.centeredRoom(Vec3(-4.0, 0.0, -3.5), Vec3(8, 5, 9))
        room2 = root.room(Vec3(-4, 0, 1), Vec3(3, 3, 6))
        room3 = room2.room(Vec3(1, 2, -1), Vec3(2, 3, 0))
        tempDir = makeTestDir("project")
    }

    @Test
    fun `read project 2_rooms`() {
        val path = resourcePath("project/2_rooms.voxarch")
        app.openProjectFile(path)
        assertEquals(2, app.state.rootNode.children.size)
        assertEquals(2, app.state.rootNode.node.children.size)
        assertNodeEquals(room1, app.state.rootNode.node.children[0])
        assertNodeEquals(room2, app.state.rootNode.node.children[1])
        assertEquals(1, app.state.rootNode.node.children[1].children.size)
        assertNodeEquals(room3, app.state.rootNode.node.children[1].children[0])
    }

    @Test
    fun `write project 2_rooms`() {
        app.createRoom(Vector3i(-8, 0, -8), Vector3i(0, 5, 1), true)
        val node2 = app.createRoom(Vector3i(-4, 0, 1), Vector3i(3, 3, 6), false)
        app.setParentNode(node2)
        app.createRoom(
            Vector3i(1, 2, -1).add(node2.node.origin.toVector3i()),
            Vector3i(2, 3, 0).add(node2.node.origin.toVector3i()),
            false
        )

        app.hideObject(node2)
        app.selectAll()

        val refPath = resourcePath("project/2_rooms.voxarch")
        val testPath = tempDir.resolve("2_rooms.voxarch")
        app.saveProjectFile(testPath)

        val zipfsRef = newZipFileSystem(refPath)
        val zipfsTest = newZipFileSystem(testPath)
        zipfsRef.use {
            zipfsTest.use {
                assertTextFilesEqual(
                    zipfsRef.getPath("/scenetree.xml"),
                    zipfsTest.getPath("/scenetree.xml")
                )
            }
        }
    }
}