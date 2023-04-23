package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.blueprint.Blueprint
import org.joml.Vector3i
import org.junit.Assert.*
import org.junit.Test

class NewBlueprintTest : BaseAppTest() {
    @Test
    fun `create new blueprint, undo redo`() {
        assertEquals(emptyList<Blueprint>(), app.state.blueprints.toList())

        val bp = app.newBlueprint()
        assertEquals(listOf(bp), app.state.blueprints.toList())

        app.undo()
        assertEquals(emptyList<Blueprint>(), app.state.blueprints.toList())

        app.redo()
        assertEquals(listOf(bp), app.state.blueprints.toList())
    }

    @Test
    fun `create new blueprint and add it to node, undo redo`() {
        val node = app.createNode(Vector3i(0, 0, 0), Vector3i(1, 1, 1), type = "Node")
        assertEquals(emptyList<Blueprint>(), app.state.blueprints.toList())
        assertEquals(emptyList<Blueprint>(), node.blueprints)

        val bp = app.addNewBlueprint(node)
        assertEquals(listOf(bp), app.state.blueprints.toList())
        assertEquals(listOf(bp), node.blueprints)

        app.undo()
        assertEquals(emptyList<Blueprint>(), app.state.blueprints.toList())
        assertEquals(emptyList<Blueprint>(), node.blueprints)

        app.redo()
        assertEquals(listOf(bp), app.state.blueprints.toList())
        assertEquals(listOf(bp), node.blueprints)
    }
}