package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.util.resourcePath
import org.junit.Assert.*
import org.junit.Test

class ImportVoxFileTest : BaseActionTest() {
    @Test
    fun `test importVoxFile undo redo`() {
        app.importVoxFile(resourcePath("vox/voxarch-wfc-10x10x10-2021-12-05_19_16_49.vox"))
        assertEquals(1, app.state.voxelRoot.children.size)
        app.undo()
        assertEquals(0, app.state.voxelRoot.children.size)
        app.redo()
        assertEquals(1, app.state.voxelRoot.children.size)
    }
}