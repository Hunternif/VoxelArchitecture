package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.generateNodes
import hunternif.voxarch.editor.actions.openProjectFile
import hunternif.voxarch.editor.file.serializeToXmlStr
import hunternif.voxarch.editor.util.resourcePath
import org.junit.Assert.assertEquals
import org.junit.Test

class OutSlotFileTest : BaseAppTest() {
    // Opens a test project file, executes the blueprint, and verifies node tree.
    @Test
    fun `run blueprint with DomRepeat`() {
        val path = resourcePath("project/out_slot_test.voxarch")
        app.openProjectFile(path)
        val refNodeTree = serializeToXmlStr(app.state.rootNode.node, true)
        app.generateNodes()
        val generatedNodeTree = serializeToXmlStr(app.state.rootNode.node, true)
        assertEquals(refNodeTree, generatedNodeTree)
        // Verify slot name:
        val delegateBp = app.state.blueprintLibrary.blueprintsByName["all_walls"]!!
        val mainBp = app.state.blueprintLibrary.blueprintsByName["main"]!!
        val outSlot = delegateBp.nodes
            .first { it.domBuilder is DomBlueprintOutSlot }
        assertEquals("my_slot", (outSlot.domBuilder as DomBlueprintOutSlot).slotName)

        val outSlotRef = mainBp.nodes
            .first { it.domBuilder is DomRunBlueprint }
            .outputs.first()
        assertEquals("my_slot", outSlotRef.name)
        assertEquals(outSlot.domBuilder, (outSlotRef.domSlot as DomBlueprintOutSlotInstance).source)
    }
}