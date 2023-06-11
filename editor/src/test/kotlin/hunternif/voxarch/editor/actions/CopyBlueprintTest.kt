package hunternif.voxarch.editor.actions

import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.blueprint.DomRunBlueprint
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

    @Test
    fun `copy bp with delegate and out slots`() {
        val bp = app.newBlueprint()
        val delegate = app.newBlueprint()
        app.newBlueprintNode(delegate, "Out slot")!!

        val refNode = app.newBlueprintNode(bp, "Blueprint")!!
        app.setDelegateBlueprint(refNode, delegate)
        app.newBlueprintNode(bp, "Room",
            autoLinkFrom = refNode.outputs.first()
        )!!

        val copy = app.copyBlueprint(bp)
        val copiedNode = copy.nodes.first { it.domBuilder is DomRunBlueprint }

        assertEquals(delegate, (copiedNode.domBuilder as DomRunBlueprint).blueprint)

        val outSlot = copiedNode.outputs.first { it.name == "slot" }
        val roomNode = copy.nodes.first { it.domBuilder is DomNodeBuilder<*> }
        assertEquals(roomNode, outSlot.links.first().to.node)
    }
}