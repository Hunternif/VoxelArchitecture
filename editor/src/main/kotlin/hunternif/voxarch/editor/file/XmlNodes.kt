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
    var transparent: Boolean? = null
) {
    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "tag")
    var tags = mutableListOf<String>()
    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "node")
    var children = mutableListOf<XmlNode>()
}

class XmlStructure(
    origin: Vec3 = Vec3.ZERO,
    start: Vec3 = Vec3.ZERO,
    size: Vec3 = Vec3.ZERO,
    rotationY: Double = 0.0,
) : XmlNode(origin, start, size, rotationY)

open class XmlRoom(
    origin: Vec3 = Vec3.ZERO,
    start: Vec3 = Vec3.ZERO,
    size: Vec3 = Vec3.ZERO,
    rotationY: Double = 0.0,
) : XmlNode(origin, start, size, rotationY)

open class XmlPolyRoom(
    origin: Vec3 = Vec3.ZERO,
    start: Vec3 = Vec3.ZERO,
    size: Vec3 = Vec3.ZERO,
    rotationY: Double = 0.0,
    @field:JacksonXmlProperty(isAttribute = true)
    var shape: PolyShape = PolyShape.SQUARE,
    var polygon: XmlPath = XmlPath()
) : XmlRoom(origin, start, size, rotationY)

class XmlColumn(
    origin: Vec3 = Vec3.ZERO,
    start: Vec3 = Vec3.ZERO,
    size: Vec3 = Vec3.ZERO,
    rotationY: Double = 0.0,
    shape: PolyShape = PolyShape.SQUARE,
    polygon: XmlPath = XmlPath(),
) : XmlPolyRoom(origin, start, size, rotationY, shape, polygon)

class XmlWall(
    origin: Vec3 = Vec3.ZERO,
    start: Vec3 = Vec3.ZERO,
    size: Vec3 = Vec3.ZERO,
    rotationY: Double = 0.0,
) : XmlNode(origin, start, size, rotationY)

class XmlFloor(
    @field:JacksonXmlProperty(isAttribute = true)
    var y: Double = 0.0,
    start: Vec3 = Vec3.ZERO,
) : XmlNode(start = start)

class XmlPath(
    origin: Vec3 = Vec3.ZERO,
    rotationY: Double = 0.0,
    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "point")
    var points: List<Vec3>? = null,
) : XmlNode(origin, rotationY = rotationY)


internal fun Node.mapToXmlNode(): XmlNode? = mapToXmlNodeRecursive(mutableSetOf())
/** Maps to XML without mapping any of the children. */
internal fun Node.mapToXmlNodeNoChildren(): XmlNode {
    val xmlNode = when (this) {
        is Structure -> XmlStructure(origin, start, size, rotationY)
        is Column -> XmlColumn(origin, start, size, rotationY,
            shape, polygon.mapToXmlNode() as XmlPath
        )
        is PolyRoom -> XmlPolyRoom(origin, start, size, rotationY,
            shape, polygon.mapToXmlNode() as XmlPath
        )
        is Room -> XmlRoom(origin, start, size, rotationY)
        is Wall -> XmlWall(origin, start, size, rotationY)
        is Floor -> XmlFloor(origin.y, start)
        is Path -> XmlPath(origin, rotationY, points)
        else -> XmlNode(origin, start, size, rotationY)
    }
    xmlNode.tags.addAll(tags)
    if (transparent) xmlNode.transparent = true
    return xmlNode
}

/** The set is passed to prevent an infinite nested loop. */
private fun Node.mapToXmlNodeRecursive(mapped: MutableSet<Node>): XmlNode? {
    if (this in mapped) return null
    mapped.add(this)
    val xmlNode: XmlNode = mapToXmlNodeNoChildren() ?: return null
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
        is XmlStructure -> Structure(origin)
        is XmlColumn -> Column(origin, size).also {
            it.shape = shape
            it.polygon.addPoints(polygon.points ?: emptyList())
        }
        is XmlPolyRoom -> PolyRoom(origin, size).also {
            it.shape = shape
            it.polygon.addPoints(polygon.points ?: emptyList())
        }
        is XmlRoom -> Room(origin, size)
        is XmlWall -> Wall(origin, origin + size)
        is XmlFloor -> Floor(y)
        is XmlPath -> Path(origin).also {
            it.addPoints(points ?: emptyList())
        }
        else -> Node(origin)
    }
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