package hunternif.voxarch.editor.file

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.blueprint.domBuilderFactoryByName

@JacksonXmlRootElement(localName = "blueprint")
class XmlBlueprint(
    @field:JacksonXmlProperty(isAttribute = true)
    var id: Int = -1,

    @field:JacksonXmlProperty(isAttribute = true)
    var name: String = "",

    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "node")
    var nodes: List<XmlBlueprintNode> = emptyList(),

    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "link")
    var links: List<XmlBlueprintLink> = emptyList(),
)

class XmlBlueprintNode(
    @field:JacksonXmlProperty(isAttribute = true)
    var id: Int = -1,

    @field:JacksonXmlProperty(isAttribute = true)
    var name: String = "",

    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "inSlot")
    var inputSlots: List<XmlBlueprintSlot> = emptyList(),

    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "outSlot")
    var outputSlots: List<XmlBlueprintSlot> = emptyList(),

    @field:JacksonXmlProperty(isAttribute = true)
    var x: Float = -1f,

    @field:JacksonXmlProperty(isAttribute = true)
    var y: Float = -1f,
)

class XmlBlueprintSlot(
    @field:JacksonXmlProperty(isAttribute = true)
    var id: Int = -1,

    @field:JacksonXmlProperty(isAttribute = true)
    var name: String = "",
)

class XmlBlueprintLink(
    // node IDs are added just in case, for debugging errors
    @field:JacksonXmlProperty(isAttribute = true)
    var fromNodeId: Int = -1,

    @field:JacksonXmlProperty
    var fromSlot: XmlBlueprintSlot? = null,

    @field:JacksonXmlProperty(isAttribute = true)
    var toNodeId: Int = -1,

    @field:JacksonXmlProperty
    var toSlot: XmlBlueprintSlot? = null,
)

internal fun Blueprint.mapToXml(): XmlBlueprint {
    val jsonLinks = links.map {
        XmlBlueprintLink(
            it.from.node.id, XmlBlueprintSlot(it.from.id, it.from.name),
            it.to.node.id, XmlBlueprintSlot(it.to.id, it.to.name),
        )
    }
    val jsonNodes = nodes.map { n ->
        XmlBlueprintNode(
            n.id, n.name,
            n.inputs.map { XmlBlueprintSlot(it.id, it.name) },
            n.outputs.map { XmlBlueprintSlot(it.id, it.name) },
            n.x, n.y,
        )
    }
    return XmlBlueprint(id, name, jsonNodes, jsonLinks)
}

internal fun XmlBlueprint.mapXml(): Blueprint {
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