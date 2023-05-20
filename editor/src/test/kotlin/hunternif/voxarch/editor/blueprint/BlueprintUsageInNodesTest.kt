package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scenegraph.SceneNode
import org.joml.Vector3i
import org.junit.Assert.*
import org.junit.Test

class BlueprintUsageInNodesTest : BaseAppTest() {
    private val lib: IBlueprintLibrary get() = app.state.blueprintLibrary
    
    @Test
    fun `delete node, undo redo`() {
        val node = app.createNode(Vector3i(0, 0, 0), Vector3i(1, 1, 1))
        val bp = app.addNewBlueprint(node)
        assertEquals(listOf(node), lib.usage(bp).nodes)

        app.deleteObjects(listOf(node))
        assertEquals(emptyList<SceneNode>(), lib.usage(bp).nodes)

        app.undo()
        assertEquals(listOf(node), lib.usage(bp).nodes)

        app.redo()
        assertEquals(emptyList<SceneNode>(), lib.usage(bp).nodes)
    }

    @Test
    fun `delete blueprint, undo redo`() {
        val node = app.createNode(Vector3i(0, 0, 0), Vector3i(1, 1, 1))
        val bp = app.addNewBlueprint(node)
        assertEquals(listOf(bp), node.blueprints)
        assertEquals(listOf(node), lib.usage(bp).nodes)

        app.deleteBlueprint(bp)
        assertEquals(emptyList<Blueprint>(), node.blueprints)
        assertEquals(emptyList<SceneNode>(), lib.usage(bp).nodes)

        app.undo()
        assertEquals(listOf(bp), node.blueprints)
        assertEquals(listOf(node), lib.usage(bp).nodes)

        app.redo()
        assertEquals(emptyList<Blueprint>(), node.blueprints)
        assertEquals(emptyList<SceneNode>(), lib.usage(bp).nodes)
    }
}