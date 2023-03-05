package hunternif.voxarch.editor.file

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.domBuilderFactoryByName
import org.junit.Assert.assertEquals
import org.junit.Test

class JsonBlueprintSerializerTest : BaseAppTest() {

    @Test
    fun `serialize blueprint`() {
        app.state.registry.blueprintIDs.lastID = 23
        val bp = app.state.registry.newBlueprint("Test blueprint")
        val domExtend = domBuilderFactoryByName["Extend"]!!()
        val node1 = app.newBlueprintNode(bp, "Extend", domExtend, 100f, 20f)
        val domRoom = domBuilderFactoryByName["Room"]!!()
        val node2 = app.newBlueprintNode(bp, "Room", domRoom, 200f, 30f)
        app.linkBlueprintSlots(bp.start.outputs[0], node1.inputs[0])
        app.linkBlueprintSlots(node1.outputs.first { it.name == "east" }, node2.inputs[0])

        val json = serializeToJsonStr(bp, true)
        assertEquals(exampleBpJson, json.trimXml())
    }

    @Test
    fun `deserialize blueprint`() {
        val bp = deserializeJson(exampleBpJson, Blueprint::class)
        assertEquals(23, bp.id)
        assertEquals("Test blueprint", bp.name)
        assertEquals(3, bp.nodes.size)
        assertEquals(3, bp.nodeIDs.map.size)
        assertEquals(9, bp.slotIDs.map.size)
        assertEquals(2, bp.links.size)
        assertEquals(2, bp.linkIDs.map.size)

        val reserialized = serializeToJsonStr(bp, true)
        assertEquals(exampleBpJson, reserialized.trimXml())
    }

    private val exampleBpJson = """
        {
          "id" : 23,
          "name" : "Test blueprint",
          "nodes" : [ {
            "id" : 0,
            "name" : "Start",
            "inputSlots" : [ ],
            "outputSlots" : [ {
              "id" : 0,
              "name" : "node"
            } ],
            "x" : 100.0,
            "y" : 100.0
          }, {
            "id" : 1,
            "name" : "Extend",
            "inputSlots" : [ {
              "id" : 1,
              "name" : "in"
            } ],
            "outputSlots" : [ {
              "id" : 2,
              "name" : "out"
            }, {
              "id" : 3,
              "name" : "north"
            }, {
              "id" : 4,
              "name" : "south"
            }, {
              "id" : 5,
              "name" : "east"
            }, {
              "id" : 6,
              "name" : "west"
            } ],
            "x" : 100.0,
            "y" : 20.0
          }, {
            "id" : 2,
            "name" : "Room",
            "inputSlots" : [ {
              "id" : 7,
              "name" : "in"
            } ],
            "outputSlots" : [ {
              "id" : 8,
              "name" : "out"
            } ],
            "x" : 200.0,
            "y" : 30.0
          } ],
          "links" : [ {
            "fromNodeId" : 0,
            "fromSlot" : {
              "id" : 0,
              "name" : "node"
            },
            "toNodeId" : 1,
            "toSlot" : {
              "id" : 1,
              "name" : "in"
            }
          }, {
            "fromNodeId" : 1,
            "fromSlot" : {
              "id" : 5,
              "name" : "east"
            },
            "toNodeId" : 2,
            "toSlot" : {
              "id" : 7,
              "name" : "in"
            }
          } ]
        }
    """.trimIndent()
}