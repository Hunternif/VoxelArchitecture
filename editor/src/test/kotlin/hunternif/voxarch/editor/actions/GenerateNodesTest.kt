package hunternif.voxarch.editor.actions

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Prop
import hunternif.voxarch.plan.prop
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3i
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GenerateNodesTest : BaseActionTest() {
    private lateinit var parent: SceneNode
    private lateinit var child: SceneNode

    @Before
    fun setup() = app.state.run {
        parent = app.createRoom(Vector3i(0, 0, 0), Vector3i(1, 1, 1))
        parentNode = parent
        child = app.createRoom(Vector3i(1, 2, 3), Vector3i(2, 2, 2))
    }

    @Test
    fun `generate nodes on parent, undo redo`() = app.state.run {
        app.addGenerator(parent, PropGenerator())
        app.generateNodes()
        assertEquals(3, sceneObjects.size)
        assertEquals(2, parent.children.size)
        assertEquals(emptySet<Node>(), child.children.toSet())
        assertProp(parent.children.last())
        app.undo()
        assertEquals(setOf(parent, child), sceneObjects)
        app.redo()
        assertEquals(3, sceneObjects.size)
        assertEquals(2, parent.children.size)
        assertEquals(emptySet<Node>(), child.children.toSet())
        assertProp(parent.children.last())
    }

    @Test
    fun `generate nodes on child, undo redo`() = app.state.run {
        app.addGenerator(child, PropGenerator())
        app.generateNodes()
        assertEquals(3, sceneObjects.size)
        assertEquals(setOf(child), parent.children.toSet())
        assertEquals(1, child.children.size)
        assertProp(child.children.first())
        app.undo()
        assertEquals(setOf(parent, child), sceneObjects)
        app.redo()
        assertEquals(3, sceneObjects.size)
        assertEquals(setOf(child), parent.children.toSet())
        assertEquals(1, child.children.size)
        assertProp(child.children.first())
    }

    @Test
    fun `generate nodes on parent and child, undo redo`() = app.state.run {
        app.addGenerator(parent, PropGenerator())
        app.addGenerator(child, PropGenerator())
        app.generateNodes()
        assertEquals(4, sceneObjects.size)
        assertEquals(2, parent.children.size)
        assertEquals(1, child.children.size)
        assertProp(parent.children.last())
        assertProp(child.children.first())
        app.undo()
        assertEquals(setOf(parent, child), sceneObjects)
        app.redo()
        assertEquals(4, sceneObjects.size)
        assertEquals(2, parent.children.size)
        assertEquals(1, child.children.size)
        assertProp(parent.children.last())
        assertProp(child.children.first())
    }

    @Test
    fun `after removing generator re-generate will remove nodes`() = app.state.run {
        val generator = PropGenerator()
        app.addGenerator(parent, generator)
        assertEquals(emptySet<SceneNode>(), generatedNodes.toSet())
        assertEquals(setOf(parent, child), sceneObjects.toSet())
        app.generateNodes()
        val prop = parent.children.last()
        assertEquals(setOf(prop), generatedNodes.toSet())
        assertEquals(setOf(parent, child, prop), sceneObjects.toSet())
        app.removeGenerator(parent, generator)
        assertEquals(setOf(prop), generatedNodes.toSet())
        assertEquals(setOf(parent, child, prop), sceneObjects.toSet())
        app.generateNodes()
        assertEquals(emptySet<SceneNode>(), generatedNodes.toSet())
        assertEquals(setOf(parent, child), sceneObjects.toSet())
        app.undo()
        assertEquals(setOf(prop), generatedNodes.toSet())
        assertEquals(setOf(parent, child, prop), sceneObjects.toSet())
        app.undo()
        assertEquals(setOf(prop), generatedNodes.toSet())
        assertEquals(setOf(parent, child, prop), sceneObjects.toSet())
        app.undo()
        assertEquals(emptySet<SceneNode>(), generatedNodes.toSet())
        assertEquals(setOf(parent, child), sceneObjects.toSet())
        app.redo()
        assertEquals(setOf(prop), generatedNodes.toSet())
        assertEquals(setOf(parent, child, prop), sceneObjects.toSet())
    }

    private fun assertProp(obj: SceneObject) {
        val node = (obj as SceneNode).node
        assertEquals(Prop::class.java, node.javaClass)
        assertEquals(emptySet<SceneObject>(), obj.children.toSet())
        assertEquals(emptySet<Node>(), node.children.toSet())
        assertEquals(Vec3(4, 5, 6), node.origin)
        assertEquals(setOf("generated prop"), node.tags)
        assertTrue(obj.isGenerated)
    }

    private class PropGenerator : IGenerator {
        override fun generate(parent: DomBuilder<Node?>) {
            parent.node?.prop(Vec3(4, 5, 6), "generated prop")
        }

    }
}