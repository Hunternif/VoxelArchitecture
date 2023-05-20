package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintRegistry
import hunternif.voxarch.editor.scenegraph.SceneNode
import org.joml.Vector3i
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteBlueprintTest : BaseAppTest() {
    private lateinit var node1: SceneNode
    private lateinit var node2: SceneNode
    private lateinit var bp1: Blueprint
    private lateinit var bp2: Blueprint
    private lateinit var reg: BlueprintRegistry

    @Before
    fun setup() {
        node1 = app.createNode(Vector3i(0, 0, 0), Vector3i(1, 1, 1))
        node2 = app.createNode(Vector3i(1, 0, 0), Vector3i(2, 1, 1))
        bp1 = app.addNewBlueprint(node1)
        bp2 = app.addNewBlueprint(node1)
        app.addBlueprint(node2, bp1)
        reg = app.state.blueprintRegistry
    }

    @Test
    fun `remove blueprint from node, undo redo`() {
        app.selectBlueprint(bp1)
        assertEquals(bp1, app.state.selectedBlueprint)
        assertEquals(listOf(bp1, bp2), reg.blueprints.toList())
        assertEquals(listOf(node1, node2), reg.usage(bp1).nodes)
        assertEquals(listOf(node1), reg.usage(bp2).nodes)
        assertEquals(listOf(bp1, bp2), node1.blueprints)
        assertEquals(listOf(bp1), node2.blueprints)

        app.removeBlueprint(node1, bp1)
        assertEquals(null, app.state.selectedBlueprint)
        assertEquals(listOf(bp1, bp2), reg.blueprints.toList())
        assertEquals(listOf(node2), reg.usage(bp1).nodes)
        assertEquals(listOf(node1), reg.usage(bp2).nodes)
        assertEquals(listOf(bp2), node1.blueprints)
        assertEquals(listOf(bp1), node2.blueprints)

        app.undo()
        assertEquals(bp1, app.state.selectedBlueprint)
        assertEquals(listOf(bp1, bp2), reg.blueprints.toList())
        assertEquals(setOf(node1, node2), reg.usage(bp1).nodes.toSet())
        assertEquals(listOf(node1), reg.usage(bp2).nodes)
        assertEquals(listOf(bp1, bp2), node1.blueprints)
        assertEquals(listOf(bp1), node2.blueprints)

        app.redo()
        assertEquals(null, app.state.selectedBlueprint)
        assertEquals(listOf(bp1, bp2), reg.blueprints.toList())
        assertEquals(listOf(node2), reg.usage(bp1).nodes)
        assertEquals(listOf(node1), reg.usage(bp2).nodes)
        assertEquals(listOf(bp2), node1.blueprints)
        assertEquals(listOf(bp1), node2.blueprints)
    }

    @Test
    fun `delete blueprint from library, undo redo`() {
        app.selectBlueprint(bp1)
        assertEquals(bp1, app.state.selectedBlueprint)
        assertEquals(listOf(bp1, bp2), reg.blueprints.toList())
        assertEquals(listOf(node1, node2), reg.usage(bp1).nodes)
        assertEquals(listOf(node1), reg.usage(bp2).nodes)
        assertEquals(listOf(bp1, bp2), node1.blueprints)
        assertEquals(listOf(bp1), node2.blueprints)

        app.deleteSelectedBlueprint()
        assertEquals(null, app.state.selectedBlueprint)
        assertEquals(listOf(bp2), reg.blueprints.toList())
        assertEquals(emptyList<SceneNode>(), reg.usage(bp1).nodes)
        assertEquals(listOf(node1), reg.usage(bp2).nodes)
        assertEquals(listOf(bp2), node1.blueprints)
        assertEquals(emptyList<Blueprint>(), node2.blueprints)

        app.undo()
        assertEquals(bp1, app.state.selectedBlueprint)
        assertEquals(listOf(bp2, bp1), reg.blueprints.toList())
        assertEquals(listOf(node1, node2), reg.usage(bp1).nodes)
        assertEquals(listOf(node1), reg.usage(bp2).nodes)
        assertEquals(listOf(bp1, bp2), node1.blueprints)
        assertEquals(listOf(bp1), node2.blueprints)

        app.redo()
        assertEquals(null, app.state.selectedBlueprint)
        assertEquals(listOf(bp2), reg.blueprints.toList())
        assertEquals(emptyList<SceneNode>(), reg.usage(bp1).nodes)
        assertEquals(listOf(node1), reg.usage(bp2).nodes)
        assertEquals(listOf(bp2), node1.blueprints)
        assertEquals(emptyList<Blueprint>(), node2.blueprints)
    }
}