package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.blueprintEditorStyleProperties
import org.junit.Assert.*
import org.junit.Test

class GuiBlueprintNodeStyleTest {
    @Test
    fun `create inputs for every property`() {
        val bp = Blueprint(0, "test blueprint")
        val node = bp.addNode("test", DomBuilder())
        val gui = GuiBlueprintNodeStyle(node)
        assertEquals(blueprintEditorStyleProperties.size, gui.items.size)
    }
}