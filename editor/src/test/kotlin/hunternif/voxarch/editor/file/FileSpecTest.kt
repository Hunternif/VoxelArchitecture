package hunternif.voxarch.editor.file

import hunternif.voxarch.builder.DefaultBuilders
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.pct
import hunternif.voxarch.dom.style.property.height
import hunternif.voxarch.dom.style.property.width
import hunternif.voxarch.dom.style.vx
import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.DomRunBlueprint
import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.*
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.plan.*
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

    @Before
    fun setup() {
        // This structure mirrors what will be created inside AppState
        root = Structure()
        room1 = root.centeredRoom(Vec3(-4.0, 0.0, -3.5), Vec3(8, 5, 9))
        room1.builder = DefaultBuilders.Wall
        room2 = root.room(Vec3(-4, 0, 1), Vec3(3, 3, 6))
        room3 = room2.room(Vec3(1, 2, -1), Vec3(2, 3, 0))
    }

    @Test
    fun `read project 2_rooms`() {
        val path = resourcePath("project/2_rooms.voxarch")
        app.openProjectFile(path)
        // Metadata
        assertEquals(123, app.state.seed)
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
        val bpMap = app.state.blueprintLibrary.blueprintsByName
        val bp1 = bpMap["Untitled"]!!
        val bp2 = bpMap["Untitled (1)"]!!
        assertEquals(2, bpMap.size)
        assertEquals(listOf(bp1), app.state.rootNode.blueprints)
        assertEquals(listOf(bp2), (app.state.rootNode.children.toList()[1] as SceneNode).blueprints)
        assertEquals(
            setOf(app.state.rootNode),
            app.state.blueprintLibrary.usage(bp1).nodes
        )
        assertEquals(
            setOf(app.state.rootNode.children.toList()[1]),
            app.state.blueprintLibrary.usage(bp2).nodes
        )
        assertEquals(bp1, (bp2.nodes.toList()[2].domBuilder as DomRunBlueprint).blueprint)
        assertEquals(Colors.debug, bp2.nodes.toList()[1].color)
        assertEquals(BlueprintNode.defaultColor, bp2.nodes.toList()[2].color)
    }

    @Test
    fun `write project 2_rooms`() {
        app.setSeed(123)
        app.setStylesheet(Stylesheet().add {
            styleFor<Wall> {
                width { 100.pct }
            }
            style("my_class") {
                height { 50.vx }
            }
        })
        val node1 = app.createNode(Vector3i(-8, 0, -8), Vector3i(0, 5, 1), true, "Room")
        app.setNodeBuilder(node1, DefaultBuilders.Wall)

        val node2 = app.createNode(Vector3i(-4, 0, 1), Vector3i(3, 3, 6), false, "Room")
        app.setParentNode(node2)
        app.createNode(
            Vector3i(1, 2, -1).add(node2.node.origin.toVector3i()),
            Vector3i(2, 3, 0).add(node2.node.origin.toVector3i()),
            false, "Room",
        )
        val vox = app.state.registry.newVoxelGroup(
            "one block",
            Array3D(1, 1, 1, VoxColor(0xff0000))
        )
        app.state.voxelRoot.addChild(vox)

        app.hideObject(node2)
        app.selectAll()

        val bp1 = app.addNewBlueprint(app.state.rootNode)
        val bp2 = app.addNewBlueprint(node2)
        val bpNode1 = app.newBlueprintNode(bp2, "Floor", 120f, 14f)!!
        val delegateNode = app.newBlueprintNode(bp2, "Blueprint")!!
        app.setDelegateBlueprint(delegateNode, bp1)
        app.setBlueprintNodeColor(bpNode1, bpNode1.color, Colors.debug)

        val refPath = resourcePath("project/2_rooms.voxarch")
        val testPath = tempDir.resolve("2_rooms.voxarch")
        app.saveProjectFileAs(testPath)

        assertProjectFilesEqual(refPath, testPath)
    }
}