package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.generator.GenPassthrough
import org.junit.Assert.*
import org.junit.Test

class GuiBlueprintNodeStyleTest {
    @Test
    fun `create inputs for every property`() {
        val bp = Blueprint(0, "test blueprint")
        val node = bp.addNode("test", GenPassthrough())
        val gui = GuiBlueprintNodeStyle(node)
        assertTrue(gui.items.isNotEmpty())
    }
}