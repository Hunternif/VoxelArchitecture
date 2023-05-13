package hunternif.voxarch.editor.file

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3

// Annotated classes for XML serialization of Node and its subclasses.

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "class"
)
@JsonSubTypes(value = [
    JsonSubTypes.Type(name = "Structure", value = XmlStructure::class),
    JsonSubTypes.Type(name = "Room", value = XmlRoom::class),
    JsonSubTypes.Type(name = "Column", value = XmlColumn::class),
    JsonSubTypes.Type(name = "PolyRoom", value = XmlPolyRoom::class),
    JsonSubTypes.Type(name = "Floor", value = XmlFloor::class),
    JsonSubTypes.Type(name = "Wall", value = XmlWall::class),
    JsonSubTypes.Type(name = "Window", value = XmlWindow::class),
    JsonSubTypes.Type(name = "Path", value = XmlPath::class),
    JsonSubTypes.Type(name = "Node", value = XmlNode::class),
])
@JacksonXmlRootElement(localName = "node")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class XmlNode(
    @field:JacksonXmlProperty(isAttribute = true)
    var origin: Vec3 = Vec3.ZERO,
    @field:JacksonXmlProperty(isAttribute = true)
    var start: Vec3 = Vec3.ZERO,
    @field:JacksonXmlProperty(isAttribute = true)
    var size: Vec3 = Vec3.ZERO,
    @field:JacksonXmlProperty(isAttribute = true)
    var rotationY: Double = 0.0,
    @field:JacksonXmlProperty(isAttribute = true)
    var transparent: Boolean? = null,

    @field:JacksonXmlProperty(isAttribute = true)
    /** Name of the custom builder from BuilderLibrary.
     * The library is needed to deserialize this instance. */
    var builder: String? = null,
) {
    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "tag")
    var tags = mutableListOf<String>()

    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "node")
    var children = mutableListOf<XmlNode>()
}

class XmlStructure : XmlNode()

open class XmlRoom : XmlNode()

open class XmlPolyRoom(
    @field:JacksonXmlProperty(isAttribute = true)
    var shape: PolyShape = PolyShape.SQUARE,
    var polygon: XmlPath = XmlPath()
) : XmlRoom()

class XmlColumn : XmlNode()

class XmlWall : XmlNode()

class XmlWindow : XmlNode()

class XmlFloor(
    @field:JacksonXmlProperty(isAttribute = true)
    var y: Double = 0.0,
) : XmlNode()

class XmlPath(
    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "point")
    var points: List<Vec3>? = null,
) : XmlNode()


//================== SERIALIZATION & DESERIALIZATION FUNCTIONS =================

internal fun Node.mapToXmlNode(): XmlNode? = mapToXmlNodeRecursive(mutableSetOf())

/** Maps to XML without mapping any of the children. */
internal fun Node.mapToXmlNodeNoChildren(): XmlNode {
    val xmlNode = when (this) {
        is Structure -> XmlStructure()
        is Column -> XmlColumn()
        is PolyRoom -> XmlPolyRoom(shape, polygon.mapToXmlNode() as XmlPath)
        is Room -> XmlRoom()
        is Wall -> XmlWall()
        is Window -> XmlWindow()
        is Floor -> XmlFloor(origin.y)
        is Path -> XmlPath(points)
        else -> XmlNode()
    }
    xmlNode.origin = origin
    xmlNode.start = start
    xmlNode.size = size
    xmlNode.rotationY = rotationY
    xmlNode.tags.addAll(tags)
    xmlNode.builder = builder?.name
    if (transparent) xmlNode.transparent = true
    return xmlNode
}

/** The set is passed to prevent an infinite nested loop. */
private fun Node.mapToXmlNodeRecursive(mapped: MutableSet<Node>): XmlNode? {
    if (this in mapped) return null
    mapped.add(this)
    val xmlNode: XmlNode = mapToXmlNodeNoChildren()
    children.forEach { child ->
        child.mapToXmlNodeRecursive(mapped)?.let { xmlChild ->
            xmlNode.children.add(xmlChild)
        }
    }
    return xmlNode
}

internal fun XmlNode.mapXmlNode(): Node? = mapXmlNodeRecursive(mutableSetOf())

/** The set is passed to prevent an infinite nested loop. */
private fun XmlNode.mapXmlNodeRecursive(mapped: MutableSet<XmlNode>): Node? {
    if (this in mapped) return null
    mapped.add(this)
    val node: Node = when (this) {
        is XmlStructure -> Structure()
        is XmlColumn -> Column()
        is XmlPolyRoom -> PolyRoom().also {
            it.shape = shape
            it.polygon.origin = polygon.origin
            it.polygon.addPoints(polygon.points ?: emptyList())
        }
        is XmlRoom -> Room()
        is XmlWall -> Wall()
        is XmlWindow -> Window()
        is XmlFloor -> Floor(y)
        is XmlPath -> Path().also {
            it.addPoints(points ?: emptyList())
        }
        else -> Node()
    }
    node.origin = origin
    node.start = start
    node.rotationY = rotationY
    node.size = size
    node.tags += tags
    node.transparent = transparent == true
    children.forEach { xmlChild ->
        xmlChild.mapXmlNodeRecursive(mapped)?.let { child ->
            node.addChild(child)
        }
    }
    return node
}