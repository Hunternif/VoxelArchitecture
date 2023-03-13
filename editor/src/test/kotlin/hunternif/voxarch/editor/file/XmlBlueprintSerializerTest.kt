package hunternif.voxarch.editor.file

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.blueprint.Blueprint
import org.junit.Assert.assertEquals
import org.junit.Test

class XmlBlueprintSerializerTest : BaseAppTest() {

    @Test
    fun `serialize blueprint`() {
        app.state.registry.blueprintIDs.lastID = 23
        val bp = app.state.registry.newBlueprint("Test blueprint")
        val node1 = app.newBlueprintNode(bp, "Extend", 100f, 20f)!!
        val node2 = app.newBlueprintNode(bp, "Room", 200f, 30f)!!
        app.linkBlueprintSlots(bp.start.outputs[0], node1.inputs[0])
        app.linkBlueprintSlots(node1.outputs.first { it.name == "east" }, node2.inputs[0])

        val xml = serializeToXmlStr(bp, true)
        assertEquals(exampleBpXml, xml.fixCRLF())
    }

    @Test
    fun `deserialize blueprint`() {
        val bp = deserializeXml(exampleBpXml, Blueprint::class)
        assertEquals(23, bp.id)
        assertEquals("Test blueprint", bp.name)
        assertEquals(3, bp.nodes.size)
        assertEquals(3, bp.nodeIDs.map.size)
        assertEquals(9, bp.slotIDs.map.size)
        assertEquals(2, bp.links.size)
        assertEquals(2, bp.linkIDs.map.size)

        val reserialized = serializeToXmlStr(bp, true)
        assertEquals(exampleBpXml, reserialized.fixCRLF())
    }

    private val exampleBpXml = """
        <blueprint id="23" name="Test blueprint">
          <node id="0" name="Start" x="100.0" y="100.0">
            <outSlot id="0" name="node"/>
          </node>
          <node id="1" name="Extend" x="100.0" y="20.0">
            <inSlot id="1" name="in"/>
            <outSlot id="2" name="out"/>
            <outSlot id="3" name="north"/>
            <outSlot id="4" name="south"/>
            <outSlot id="5" name="east"/>
            <outSlot id="6" name="west"/>
          </node>
          <node id="2" name="Room" x="200.0" y="30.0">
            <inSlot id="7" name="in"/>
            <outSlot id="8" name="out"/>
          </node>
          <link fromNodeId="0" toNodeId="1">
            <fromSlot id="0" name="node"/>
            <toSlot id="1" name="in"/>
          </link>
          <link fromNodeId="1" toNodeId="2">
            <fromSlot id="5" name="east"/>
            <toSlot id="7" name="in"/>
          </link>
        </blueprint>
    """.trimIndent()
}