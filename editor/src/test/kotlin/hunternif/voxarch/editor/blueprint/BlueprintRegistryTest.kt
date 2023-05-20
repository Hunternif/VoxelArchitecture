package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scenegraph.SceneNode
import org.joml.Vector3i
import org.junit.Assert.*
import org.junit.Test

class BlueprintRegistryTest : BaseAppTest() {
    @Test
    fun `deleting nodes updates blueprint usage, undo redo`() {
        val node = app.createNode(Vector3i(0, 0, 0), Vector3i(1, 1, 1))
        val bp = app.addNewBlueprint(node)
        assertEquals(listOf(node), app.state.blueprintLibrary.usage(bp).nodes)

        app.deleteObjects(listOf(node))
        assertEquals(emptyList<SceneNode>(), app.state.blueprintLibrary.usage(bp).nodes)

        app.undo()
        assertEquals(listOf(node), app.state.blueprintLibrary.usage(bp).nodes)

        app.redo()
        assertEquals(emptyList<SceneNode>(), app.state.blueprintLibrary.usage(bp).nodes)
    }

    @Test
    fun `deleting blueprint updates usage, undo redo`() {
        val node = app.createNode(Vector3i(0, 0, 0), Vector3i(1, 1, 1))
        val bp = app.addNewBlueprint(node)
        assertEquals(listOf(bp), node.blueprints)
        assertEquals(listOf(node), app.state.blueprintLibrary.usage(bp).nodes)

        app.deleteBlueprint(bp)
        assertEquals(emptyList<Blueprint>(), node.blueprints)
        assertEquals(emptyList<SceneNode>(), app.state.blueprintLibrary.usage(bp).nodes)

        app.undo()
        assertEquals(listOf(bp), node.blueprints)
        assertEquals(listOf(node), app.state.blueprintLibrary.usage(bp).nodes)

        app.redo()
        assertEquals(emptyList<Blueprint>(), node.blueprints)
        assertEquals(emptyList<SceneNode>(), app.state.blueprintLibrary.usage(bp).nodes)
    }
}