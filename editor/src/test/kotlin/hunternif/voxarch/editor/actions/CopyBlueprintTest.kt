package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.BaseAppTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CopyBlueprintTest : BaseAppTest() {
    @Test
    fun `copy bp will update name`() {
        val bp = app.newBlueprint()
        assertEquals("Untitled", bp.name)

        val copy1 = app.copyBlueprint(bp)
        assertEquals("Untitled (1)", copy1.name)

        val copy2 = app.copyBlueprint(bp)
        assertEquals("Untitled (2)", copy2.name)

        // Copy a copy:
        val copy3 = app.copyBlueprint(copy1)
        assertEquals("Untitled (3)", copy3.name)
    }
}