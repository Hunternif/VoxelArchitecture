package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BlueprintOutSlotsTest : BaseAppTest() {
    private lateinit var mainBp: Blueprint
    private lateinit var delegateBp: Blueprint
    private lateinit var refNode: BlueprintNode

    @Before
    fun setup() {
        delegateBp = app.newBlueprint()
        mainBp = app.newBlueprint()
        refNode = app.newBlueprintNode(mainBp, "Blueprint")!!
    }

    @Test
    fun `adding out nodes on delegate BP should update slots, undo redo`() {
        app.setDelegateBlueprint(refNode, delegateBp)
        assertEquals(0, refNode.outputs.size)

        val outNode = app.newBlueprintNode(delegateBp, "Out slot")!!
        assertEquals(1, refNode.outputs.size)
        assertEquals("slot", refNode.outputs[0].name)
        assertEquals(outNode.domBuilder,
            (refNode.outputs[0].domSlot as DomBlueprintOutSlotInstance).source
        )

        app.undo()
        assertEquals(0, refNode.outputs.size)

        app.redo()
        assertEquals(1, refNode.outputs.size)
        assertEquals("slot", refNode.outputs[0].name)
        assertEquals(outNode.domBuilder,
            (refNode.outputs[0].domSlot as DomBlueprintOutSlotInstance).source
        )
    }

    @Test
    fun `deleting out nodes on delegate BP should update slots, undo redo`() {
        val outNode1 = app.newBlueprintNode(delegateBp, "Out slot")!!
        val outNode2 = app.newBlueprintNode(delegateBp, "Out slot")!!
        app.setDelegateBlueprint(refNode, delegateBp)
        assertEquals(2, refNode.outputs.size)
        assertEquals(outNode1.domBuilder,
            (refNode.outputs[0].domSlot as DomBlueprintOutSlotInstance).source
        )
        assertEquals(outNode2.domBuilder,
            (refNode.outputs[1].domSlot as DomBlueprintOutSlotInstance).source
        )

        // Link from the delegate node to another node:
        val newNode = app.newBlueprintNode(mainBp, "Node",
            autoLinkFrom = refNode.outputs.first()
        )!!
        val link = newNode.inputs.first().links.first()
        assertEquals(outNode1.domBuilder,
            (link.from.domSlot as DomBlueprintOutSlotInstance).source
        )

        app.deleteBlueprintNode(outNode1)
        assertEquals(1, refNode.outputs.size)
        assertEquals(outNode2.domBuilder,
            (refNode.outputs[0].domSlot as DomBlueprintOutSlotInstance).source
        )
        assertEquals(emptyList<BlueprintLink>(), newNode.inputs.first().links.toList())

        app.undo()
        assertEquals(2, refNode.outputs.size)
        // The order has changed, but I just don't care lol:
        assertEquals(outNode2.domBuilder,
            (refNode.outputs[0].domSlot as DomBlueprintOutSlotInstance).source
        )
        assertEquals(outNode1.domBuilder,
            (refNode.outputs[1].domSlot as DomBlueprintOutSlotInstance).source
        )
        assertEquals(listOf(link), newNode.inputs.first().links.toList())

        app.redo()
        assertEquals(1, refNode.outputs.size)
        assertEquals(outNode2.domBuilder,
            (refNode.outputs[0].domSlot as DomBlueprintOutSlotInstance).source
        )
        assertEquals(emptyList<BlueprintLink>(), newNode.inputs.first().links.toList())
    }

    @Test
    fun `selecting another delegate BP should update slots, undo redo`() {
        // Set up 2 out slots on Delegate BP, add a link to one:
        val outNode1 = app.newBlueprintNode(delegateBp, "Out slot")!!
        val outNode2 = app.newBlueprintNode(delegateBp, "Out slot")!!
        app.setDelegateBlueprint(refNode, delegateBp)
        val newNode = app.newBlueprintNode(mainBp, "Node",
            autoLinkFrom = refNode.outputs.first()
        )!!
        assertEquals(2, refNode.outputs.size)
        assertEquals(1, newNode.inputs.first().links.size)


        // Set up a 2nd delegate BP with 1 slot:
        val delegateBp2 = app.newBlueprint()
        val outNodeOnDele2 = app.newBlueprintNode(delegateBp2, "Out slot")!!

        // Swap delegates:
        app.setDelegateBlueprint(refNode, delegateBp2)
        assertEquals(1, refNode.outputs.size)
        assertEquals(outNodeOnDele2.domBuilder,
            (refNode.outputs[0].domSlot as DomBlueprintOutSlotInstance).source
        )
        assertEquals(0, newNode.inputs.first().links.size)

        app.undo()
        assertEquals(2, refNode.outputs.size)
        assertEquals(outNode1.domBuilder,
            (refNode.outputs[0].domSlot as DomBlueprintOutSlotInstance).source
        )
        assertEquals(outNode2.domBuilder,
            (refNode.outputs[1].domSlot as DomBlueprintOutSlotInstance).source
        )
        assertEquals(1, newNode.inputs.first().links.size)
        val link = newNode.inputs.first().links.first()
        assertEquals(refNode.outputs[0], link.from)
        assertEquals(newNode.inputs.first(), link.to)

        app.redo()
        assertEquals(1, refNode.outputs.size)
        assertEquals(outNodeOnDele2.domBuilder,
            (refNode.outputs[0].domSlot as DomBlueprintOutSlotInstance).source
        )
        assertEquals(0, newNode.inputs.first().links.size)
    }
}