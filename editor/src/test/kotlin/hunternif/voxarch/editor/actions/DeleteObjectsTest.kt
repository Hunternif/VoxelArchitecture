package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.plan.Node
import hunternif.voxarch.util.emptyArray3D
import org.joml.Vector3i
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteObjectsTest : BaseActionTest() {
    private lateinit var node: SceneNode
    private lateinit var childNode: SceneNode
    private lateinit var voxels: SceneVoxelGroup

    @Before
    fun setup() = app.state.run {
        node = app.createRoom(Vector3i(0, 0, 0), Vector3i(1, 1, 1))
        app.setParentNode(node)
        childNode = app.createRoom(Vector3i(1, 2, 3), Vector3i(2, 2, 2))
        voxels = SceneVoxelGroup("voxels", emptyArray3D())
        sceneObjects.add(voxels)
        voxelRoot.addChild(voxels)
    }

    @Test
    fun `delete child node, undo & redo, maintain hierarchy`() = app.state.run {
        app.deleteObjects(listOf(childNode))
        assertEquals(node, parentNode)
        assertEquals(setOf(node, voxels), sceneObjects)
        assertEquals(setOf(node), rootNode.children.toSet())

        assertEquals(rootNode, node.parent)
        assertEquals(rootNode.node, node.node.parent)
        assertEquals(emptySet<SceneNode>(), node.children.toSet())
        assertEquals(emptySet<Node>(), node.node.children.toSet())

        assertEquals(node, childNode.parent)
        assertEquals(null, childNode.node.parent)

        app.undo()
        assertEquals(node, parentNode)
        assertEquals(setOf(node, childNode, voxels), sceneObjects)
        assertEquals(setOf(node), rootNode.children.toSet())

        assertEquals(rootNode, node.parent)
        assertEquals(rootNode.node, node.node.parent)
        assertEquals(setOf(childNode), node.children.toSet())
        assertEquals(setOf(childNode.node), node.node.children.toSet())

        assertEquals(node, childNode.parent)
        assertEquals(node.node, childNode.node.parent)

        app.redo()
        assertEquals(node, parentNode)
        assertEquals(setOf(node, voxels), sceneObjects)
        assertEquals(setOf(node), rootNode.children.toSet())

        assertEquals(rootNode, node.parent)
        assertEquals(rootNode.node, node.node.parent)
        assertEquals(emptySet<SceneNode>(), node.children.toSet())
        assertEquals(emptySet<Node>(), node.node.children.toSet())

        assertEquals(node, childNode.parent)
        assertEquals(null, childNode.node.parent)
    }

    @Test
    fun `delete parent node, undo & redo, maintain hierarchy`() = app.state.run {
        app.deleteObjects(listOf(node))
        assertEquals(rootNode, parentNode) // reset parent node
        assertEquals(setOf(voxels), sceneObjects)
        assertEquals(emptySet<SceneNode>(), rootNode.children.toSet())

        assertEquals(rootNode, node.parent)
        assertEquals(null, node.node.parent)
        assertEquals(emptySet<SceneNode>(), node.children.toSet())
        assertEquals(emptySet<Node>(), node.node.children.toSet())

        assertEquals(node, childNode.parent)
        assertEquals(null, childNode.node.parent)

        app.undo()
        assertEquals(rootNode, parentNode) // don't reset parent node back
        assertEquals(setOf(node, childNode, voxels), sceneObjects)
        assertEquals(setOf(node), rootNode.children.toSet())

        assertEquals(rootNode, node.parent)
        assertEquals(rootNode.node, node.node.parent)
        assertEquals(setOf(childNode), node.children.toSet())
        assertEquals(setOf(childNode.node), node.node.children.toSet())

        assertEquals(node, childNode.parent)
        assertEquals(node.node, childNode.node.parent)

        app.redo()
        assertEquals(rootNode, parentNode)
        assertEquals(setOf(voxels), sceneObjects)
        assertEquals(emptySet<SceneNode>(), rootNode.children.toSet())

        assertEquals(rootNode, node.parent)
        assertEquals(null, node.node.parent)
        assertEquals(emptySet<SceneNode>(), node.children.toSet())
        assertEquals(emptySet<Node>(), node.node.children.toSet())

        assertEquals(node, childNode.parent)
        assertEquals(null, childNode.node.parent)
    }

    @Test
    fun `delete undo restores selected & hidden status`() = app.state.run {
        app.selectObject(childNode)
        app.hideObject(voxels)

        app.deleteObjects(listOf(node, voxels))
        assertEquals(emptySet<SceneObject>(), selectedObjects.toSet())
        assertEquals(emptySet<SceneObject>(), hiddenObjects.toSet())

        app.undo()
        assertEquals(setOf(childNode), selectedObjects.toSet())
        assertEquals(setOf(voxels), hiddenObjects.toSet())

        app.redo()
        assertEquals(emptySet<SceneObject>(), selectedObjects.toSet())
        assertEquals(emptySet<SceneObject>(), hiddenObjects.toSet())
    }
}