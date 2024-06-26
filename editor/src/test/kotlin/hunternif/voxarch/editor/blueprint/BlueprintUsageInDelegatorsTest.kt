package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BlueprintUsageInDelegatorsTest : BaseAppTest() {
    private val lib: IBlueprintLibrary get() = app.state.blueprintLibrary

    private lateinit var mainBp: Blueprint
    private lateinit var delegateBp: Blueprint
    private lateinit var refNode: BlueprintNode

    private val refDomBuilder: DomRunBlueprint get() = refNode.domBuilder as DomRunBlueprint

    @Before
    fun setup() {
        delegateBp = app.newBlueprint()
        mainBp = app.newBlueprint()
        refNode = app.newBlueprintNode(mainBp, "Blueprint")!!
    }

    @Test
    fun `delete referencing node, undo redo`() {
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)

        app.setDelegateBlueprint(refNode, delegateBp)
        assertEquals(setOf(refNode), lib.usage(delegateBp).delegators)

        app.undo()
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)

        app.redo()
        assertEquals(setOf(refNode), lib.usage(delegateBp).delegators)

        app.deleteBlueprintNode(refNode)
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)

        app.undo()
        assertEquals(setOf(refNode), lib.usage(delegateBp).delegators)

        app.redo()
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)
    }

    @Test
    fun `delete referencing node via parts, undo redo`() {
        app.setDelegateBlueprint(refNode, delegateBp)
        app.deleteBlueprintParts(setOf(refNode), emptySet())
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)

        app.undo()
        assertEquals(setOf(refNode), lib.usage(delegateBp).delegators)

        app.redo()
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)
    }

    @Test
    fun `delete delegate blueprint, undo redo`() {
        assertEquals(DomRunBlueprint.emptyBlueprint, refDomBuilder.blueprint)

        app.setDelegateBlueprint(refNode, delegateBp)
        assertEquals(delegateBp, refDomBuilder.blueprint)
        assertEquals(setOf(refNode), lib.usage(delegateBp).delegators)

        app.deleteBlueprint(delegateBp)
        assertEquals(DomRunBlueprint.emptyBlueprint, refDomBuilder.blueprint)
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)

        app.undo()
        assertEquals(delegateBp, refDomBuilder.blueprint)
        assertEquals(setOf(refNode), lib.usage(delegateBp).delegators)

        app.redo()
        assertEquals(DomRunBlueprint.emptyBlueprint, refDomBuilder.blueprint)
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)
    }

    @Test
    fun `delete main blueprint, undo redo`() {
        app.setDelegateBlueprint(refNode, delegateBp)
        assertEquals(delegateBp, refDomBuilder.blueprint)
        assertEquals(setOf(refNode), lib.usage(delegateBp).delegators)

        app.deleteBlueprint(mainBp)
        assertEquals(delegateBp, refDomBuilder.blueprint) // keeps the reference
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)

        app.undo()
        assertEquals(delegateBp, refDomBuilder.blueprint)
        assertEquals(setOf(refNode), lib.usage(delegateBp).delegators)

        app.redo()
        assertEquals(delegateBp, refDomBuilder.blueprint)
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)

        // Make sure ref node keeps the reference to delegate BP even after
        // the delegate BP is deleted too:
        app.deleteBlueprint(delegateBp)
        assertEquals(delegateBp, refDomBuilder.blueprint)
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)

        app.undo()
        assertEquals(delegateBp, refDomBuilder.blueprint)
        assertEquals(emptySet<BlueprintNode>(), lib.usage(delegateBp).delegators)

        app.undo()
        assertEquals(delegateBp, refDomBuilder.blueprint)
        assertEquals(setOf(refNode), lib.usage(delegateBp).delegators)
    }
}