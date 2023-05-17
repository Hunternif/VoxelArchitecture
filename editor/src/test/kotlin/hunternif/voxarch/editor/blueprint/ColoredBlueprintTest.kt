package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.plan.Column
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ColoredBlueprintTest : BaseAppTest() {
    private lateinit var bp: Blueprint
    private lateinit var n1: BlueprintNode
    private lateinit var n2: BlueprintNode
    private lateinit var n3: BlueprintNode

    @Before
    fun setup() {
        bp = app.addNewBlueprint(app.state.rootNode)

        n1 = app.newBlueprintNode(bp, "Room",
            autoLinkFrom = bp.start.outputs.first())!!
        app.setBlueprintNodeClass(n1, "parent")

        n2 = app.newBlueprintNode(bp, "Wall",
            autoLinkFrom = n1.outputs.first())!!
        app.setBlueprintNodeClass(n2, "child")

        n3 = app.newBlueprintNode(bp, "Column",
            autoLinkFrom = n2.outputs.first())!!
        app.setBlueprintNodeClass(n3, "grandchild")
    }

    @Test
    fun `default build with default colors`() {
        app.generateNodes()

        val parent = app.state.rootNode.query<Room>("parent").first()
        val child = app.state.rootNode.query<Wall>("child").first()
        val grandchild = app.state.rootNode.query<Column>("grandchild").first()

        assertEquals(BlueprintNode.defaultColor, parent.color)
        assertEquals(BlueprintNode.defaultColor, child.color)
        assertEquals(BlueprintNode.defaultColor, grandchild.color)
    }

    @Test
    fun `apply color from blueprint node`() {
        app.setBlueprintNodeColor(n2, Colors.debug, ColorRGBa.fromHex(0x00ff00))

        app.generateNodes()

        val parent = app.state.rootNode.query<Room>("parent").first()
        val child = app.state.rootNode.query<Wall>("child").first()
        val grandchild = app.state.rootNode.query<Column>("grandchild").first()

        assertEquals(BlueprintNode.defaultColor, parent.color)
        assertEquals(ColorRGBa.fromHex(0x00ff00), child.color)
        assertEquals(ColorRGBa.fromHex(0x00ff00), grandchild.color)
    }

    @Test
    fun `inherit color from ancestor blueprint nodes`() {
        app.setBlueprintNodeColor(n1, Colors.debug, ColorRGBa.fromHex(0xff0000))
        app.setBlueprintNodeColor(n3, Colors.debug, ColorRGBa.fromHex(0x0000ff))

        app.generateNodes()

        val parent = app.state.rootNode.query<Room>("parent").first()
        val child = app.state.rootNode.query<Wall>("child").first()
        val grandchild = app.state.rootNode.query<Column>("grandchild").first()

        assertEquals(ColorRGBa.fromHex(0xff0000), parent.color)
        assertEquals(ColorRGBa.fromHex(0xff0000), child.color)
        assertEquals(ColorRGBa.fromHex(0x0000ff), grandchild.color)
    }

    @Test
    fun `apply color from referenced blueprint`() {
        app.removeBlueprint(app.state.rootNode, bp)
        val otherBp = app.addNewBlueprint(app.state.rootNode)
        val refNode = app.newBlueprintNode(otherBp, "Blueprint")!!
        app.setDelegateBlueprint(refNode, bp)
        app.linkBlueprintSlots(otherBp.start.outputs.first(), refNode.inputs.first())

        app.setBlueprintNodeColor(n1, Colors.debug, ColorRGBa.fromHex(0xff0000))
        app.setBlueprintNodeColor(n2, Colors.debug, ColorRGBa.fromHex(0x00ff00))

        app.generateNodes()

        val parent = app.state.rootNode.query<Room>("parent").first()
        val child = app.state.rootNode.query<Wall>("child").first()
        val grandchild = app.state.rootNode.query<Column>("grandchild").first()

        assertEquals(ColorRGBa.fromHex(0xff0000), parent.color)
        assertEquals(ColorRGBa.fromHex(0x00ff00), child.color)
        assertEquals(ColorRGBa.fromHex(0x00ff00), grandchild.color)
    }
}