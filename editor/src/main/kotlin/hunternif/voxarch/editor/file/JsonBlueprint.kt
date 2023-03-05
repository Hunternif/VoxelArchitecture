package hunternif.voxarch.editor.file

import com.fasterxml.jackson.annotation.JsonProperty
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.blueprint.domBuilderFactoryByName

class JsonBlueprint(
    @field:JsonProperty
    var id: Int = -1,

    @field:JsonProperty
    var name: String = "",

    @field:JsonProperty
    var nodes: List<JsonBlueprintNode> = emptyList(),

    @field:JsonProperty
    var links: List<JsonBlueprintLink> = emptyList(),
)

class JsonBlueprintNode(
    @field:JsonProperty
    var id: Int = -1,

    @field:JsonProperty
    var name: String = "",

    @field:JsonProperty
    var inputSlots: List<JsonBlueprintSlot> = emptyList(),

    @field:JsonProperty
    var outputSlots: List<JsonBlueprintSlot> = emptyList(),

    @field:JsonProperty
    var x: Float = -1f,

    @field:JsonProperty
    var y: Float = -1f,
)

class JsonBlueprintSlot(
    @field:JsonProperty
    var id: Int = -1,

    @field:JsonProperty
    var name: String = "",
)

class JsonBlueprintLink(
    // node IDs are added just in case, for debugging errors
    @field:JsonProperty
    var fromNodeId: Int = -1,

    @field:JsonProperty
    var fromSlot: JsonBlueprintSlot? = null,

    @field:JsonProperty
    var toNodeId: Int = -1,

    @field:JsonProperty
    var toSlot: JsonBlueprintSlot? = null,
)

internal fun Blueprint.mapToJson(): JsonBlueprint {
    val jsonLinks = links.map {
        JsonBlueprintLink(
            it.from.node.id, JsonBlueprintSlot(it.from.id, it.from.name),
            it.to.node.id, JsonBlueprintSlot(it.to.id, it.to.name),
        )
    }
    val jsonNodes = nodes.map { n ->
        JsonBlueprintNode(n.id, n.name,
            n.inputs.map { JsonBlueprintSlot(it.id, it.name) },
            n.outputs.map { JsonBlueprintSlot(it.id, it.name) },
            n.x, n.y,
        )
    }
    return JsonBlueprint(id, name, jsonNodes, jsonLinks)
}

internal fun JsonBlueprint.mapJson(): Blueprint {
    val bp = Blueprint(id, name)
    for (n in nodes) {
        // Skip start node because it's added automatically:
        if (n.id == bp.start.id) continue
        // For other nodes, create and save it:
        val factory = domBuilderFactoryByName[n.name] ?: continue
        val domBuilder = factory()
        val bpNode = BlueprintNode(n.id, n.name, bp, domBuilder, n.x, n.y)
        bp.nodes.add(bpNode)
        bp.nodeIDs.save(bpNode)
        n.inputSlots.forEach {
            val slot = BlueprintSlot.In(it.id, it.name, bpNode)
            bpNode.inputs.add(slot)
            bp.slotIDs.save(slot)
        }
        n.outputSlots.forEach {
            val slot = BlueprintSlot.Out(it.id, it.name, bpNode, domBuilder)
            bpNode.outputs.add(slot)
            bp.slotIDs.save(slot)
        }
        //TODO: serialize CSS in nodes
    }
    // Add any output slots that may have been introduced in newer versions:
    bp.nodes.forEach { bpNode ->
        bpNode.domBuilder.slots.forEach { (name, domSlot) ->
            if (bpNode.outputs.none { it.name == name }) {
                bpNode.addOutput(name, domSlot)
            }
        }
    }
    // Create links. Their IDs don't matter.
    for (link in links) {
        val jsonFromSlot = link.fromSlot ?: continue
        val jsonToSlot = link.toSlot ?: continue
        val from = bp.slotIDs.map[jsonFromSlot.id] as? BlueprintSlot.Out ?: continue
        val to = bp.slotIDs.map[jsonToSlot.id] as? BlueprintSlot.In ?: continue
        from.linkTo(to)
    }
    return bp
}