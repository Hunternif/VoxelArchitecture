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
    }
}