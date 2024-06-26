package hunternif.voxarch.editor.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import hunternif.voxarch.editor.blueprint.*
import hunternif.voxarch.editor.file.style.StyleParser
import hunternif.voxarch.editor.util.ColorRGBa

@JacksonXmlRootElement(localName = "blueprint")
@JsonInclude(JsonInclude.Include.NON_NULL)
class XmlBlueprint(
    @field:JacksonXmlProperty(isAttribute = true)
    @Deprecated("For legacy blueprints in format < 8")
    var id: Int? = null,

    @field:JacksonXmlProperty(isAttribute = true)
    var name: String = "",

    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "node")
    var nodes: List<XmlBlueprintNode> = emptyList(),

    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "link")
    var links: List<XmlBlueprintLink> = emptyList(),
)

@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @field:JacksonXmlProperty
    var styleClass: String? = null,

    @field:JacksonXmlProperty
    var style: String? = null,

    /** Reference Blueprint in [DomRunBlueprint] */
    @Deprecated("This is for legacy blueprints in format < 7. Instead use delegateBlueprintName")
    @field:JacksonXmlProperty
    var delegateBlueprintID: Int? = null,

    /** Reference Blueprint in [DomRunBlueprint] */
    @field:JacksonXmlProperty
    var delegateBlueprintName: String? = null,

    /** Slot name in [DomBlueprintOutSlot] */
    @field:JacksonXmlProperty
    var slotName: String? = null,

    @field:JacksonXmlProperty(isAttribute = true, localName = "color")
    var colorHexRGB: String? = null,
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
        val delegateBPName = (n.domBuilder as? DomRunBlueprint)?.blueprintName
        val slotName = (n.domBuilder as? DomBlueprintOutSlot)?.slotName
        val color = if (n.isCustomColor) n.color.hex.toString(16) else null
        XmlBlueprintNode(
            n.id, n.name,
            n.inputs.map { XmlBlueprintSlot(it.id, it.name) },
            n.outputs.map { XmlBlueprintSlot(it.id, it.name) },
            n.x, n.y,
            n.extraStyleClass.ifEmpty { null },
            if (n.rule.isEmpty()) null else "\n${n.rule}\n",
            null,
            delegateBPName,
            slotName,
            color,
        )
    }
    return XmlBlueprint(null, name, jsonNodes, jsonLinks)
}

internal fun XmlBlueprint.mapXml(): Blueprint {
    val bp = Blueprint(name)
    val styleParser = StyleParser()
    for (n in nodes) {
        // Skip start node because it's added automatically:
        if (n.id == bp.start.id) continue
        // For other nodes, create and save it:
        val domBuilder = DomBuilderFactory.create(n.name) ?: continue
        val bpNode = bp.createNode(n.id, n.name, n.x, n.y, domBuilder)
        bpNode.color = n.colorHexRGB?.let { ColorRGBa.fromHex(it.toInt(16)) }
            ?: BlueprintNode.defaultColor.copy()
        n.inputSlots.forEach {
            val slot = BlueprintSlot.In(it.id, it.name, bpNode)
            bpNode.addInputSlot(slot)
        }
        n.outputSlots.forEach {
            // Pick the DomBuilder from the slot name
            val domSlot = domBuilder.slots[it.name] ?: domBuilder
            val slot = BlueprintSlot.Out(it.id, it.name, bpNode, domSlot)
            bpNode.addOutputSlot(slot)
        }
        n.styleClass?.let { bpNode.extraStyleClass = it }
        val rule = n.style?.let { styleParser.parseStylesheet(it).rules.firstOrNull() }
        rule?.declarations?.forEach { bpNode.rule.add(it) }
        when (domBuilder) {
            is DomRunBlueprint -> {
                domBuilder.blueprintID = n.delegateBlueprintID
                domBuilder.blueprintName = n.delegateBlueprintName
            }
            is DomBlueprintOutSlot -> {
                domBuilder.slotName = n.slotName ?: "out_slot_${n.id}"
            }
        }
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