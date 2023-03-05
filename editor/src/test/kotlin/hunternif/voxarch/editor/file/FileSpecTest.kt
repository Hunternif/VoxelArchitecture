package hunternif.voxarch.editor.file

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.centeredRoom
import hunternif.voxarch.plan.room
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3i
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FileSpecTest : BaseAppTest() {
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
        // Nodes
        assertEquals(2, app.state.rootNode.children.size)
        assertEquals(2, app.state.rootNode.node.children.size)
        assertNodeEquals(room1, app.state.rootNode.node.children[0])
        assertNodeEquals(room2, app.state.rootNode.node.children[1])
        assertEquals(1, app.state.rootNode.node.children[1].children.size)
        assertNodeEquals(room3, app.state.rootNode.node.children[1].children[0])
        // Voxels
        assertEquals(1, app.state.voxelRoot.children.size)
        val vox = app.state.voxelRoot.children.first() as SceneVoxelGroup
        assertEquals("one block", vox.label)
        assertEquals(1, vox.data.size)
        assertEquals(VoxColor(0xff0000), vox.data[0, 0, 0])
        // Blueprints
        val bpMap = app.state.registry.blueprintIDs.map
        assertEquals(2, bpMap.size)
        assertEquals(listOf(bpMap[0]), app.state.rootNode.blueprints)
        assertEquals(listOf(bpMap[1]), (app.state.rootNode.children.toList()[1] as SceneNode).blueprints)
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
        val vox = app.state.registry.newVoxelGroup(
            "one block",
            Array3D(1, 1, 1, VoxColor(0xff0000))
        )
        app.state.voxelRoot.addChild(vox)

        app.hideObject(node2)
        app.selectAll()

        app.newBlueprint(app.state.rootNode)
        app.newBlueprint(node2)
        val bp = node2.blueprints[0]
        app.newBlueprintNode(bp, "Floor", DomBuilder(), 120f, 14f)

        val refPath = resourcePath("project/2_rooms.voxarch")
        val testPath = tempDir.resolve("2_rooms.voxarch")
        app.saveProjectFileAs(testPath)

        val zipfsRef = newZipFileSystem(refPath)
        val zipfsTest = newZipFileSystem(testPath)
        zipfsRef.use {
            zipfsTest.use {
                // for comparing entire file tree recursively:
//                val refFiles = Files.walk(zipfsRef.getPath("/"), 2)
//                    .filter { it.isRegularFile() }
//                    .collect(Collectors.toList())
//                refFiles.forEach {
//                    assertTextFilesEqual(it, zipfsTest.getPath(it.toString()))
//                }
                assertTextFilesEqual(
                    zipfsRef.getPath("/metadata.yaml"),
                    zipfsTest.getPath("/metadata.yaml")
                )
                assertTextFilesEqual(
                    zipfsRef.getPath("/scenetree.xml"),
                    zipfsTest.getPath("/scenetree.xml")
                )
                assertFilesEqual(
                    zipfsRef.getPath("/voxels/group_6.vox"),
                    zipfsTest.getPath("/voxels/group_6.vox")
                )
                assertFilesEqual(
                    zipfsRef.getPath("/blueprints/blueprint_0.json"),
                    zipfsTest.getPath("/blueprints/blueprint_0.json")
                )
                assertFilesEqual(
                    zipfsRef.getPath("/blueprints/blueprint_1.json"),
                    zipfsTest.getPath("/blueprints/blueprint_1.json")
                )
            }
        }
    }
}