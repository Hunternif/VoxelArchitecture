package hunternif.voxarch.editor.file

import hunternif.voxarch.editor.actions.BaseActionTest
import hunternif.voxarch.editor.actions.createRoom
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
    private val root: Structure = Structure()
    private lateinit var room1: Room
    private lateinit var room2: Room
    private lateinit var tempDir: java.nio.file.Path

    @Before
    fun setup() {
        room1 = root.centeredRoom(Vec3(-4.0, 0.0, -3.5), Vec3(8, 5, 9))
        room2 = root.room(Vec3(-4, 0, 1), Vec3(3, 3, 6))
        tempDir = makeTestDir("project")
    }

    @Test
    fun `read project 2_rooms`() {
        val path = resourcePath("project/2_rooms.voxarch")
        val state = readProject(path)
        assertEquals(2, state.rootNode.children.size)
        assertEquals(2, state.rootNode.node.children.size)
        assertNodeEquals(room1, state.rootNode.node.children[0])
        assertNodeEquals(room2, state.rootNode.node.children[1])
    }

    @Test
    fun `write project 2_rooms`() {
        app.createRoom(Vector3i(-8, 0, -8), Vector3i(0, 5, 1), true)
        app.createRoom(Vector3i(-4, 0, 1), Vector3i(3, 3, 6), false)

        val refPath = resourcePath("project/2_rooms.voxarch")
        val testPath = tempDir.resolve("2_rooms_test.voxarch")
        writeProject(app.state, testPath)

        val zipfsRef = newZipFileSystem(refPath)
        val zipfsTest = newZipFileSystem(testPath)
        zipfsRef.use {
            zipfsTest.use {
                assertFilesEqual(
                    zipfsRef.getPath("/scenetree.xml"),
                    zipfsTest.getPath("/scenetree.xml")
                )
            }
        }
    }
}