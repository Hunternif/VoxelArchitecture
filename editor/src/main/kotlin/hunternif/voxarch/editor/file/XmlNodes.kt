package hunternif.voxarch.editor.file

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
    JsonSubTypes.Type(name = "PolygonRoom", value = XmlPolygonRoom::class),
    JsonSubTypes.Type(name = "Floor", value = XmlFloor::class),
    JsonSubTypes.Type(name = "Wall", value = XmlWall::class),
    JsonSubTypes.Type(name = "Path", value = XmlPath::class),
])
@JacksonXmlRootElement(localName = "node")
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class XmlNode {
    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "node")
    var children = mutableListOf<XmlNode>()
}

class XmlStructure(
    @field:JacksonXmlProperty(isAttribute = true)
    var origin: Vec3 = Vec3.ZERO,
) : XmlNode()

open class XmlRoom(
    @field:JacksonXmlProperty(isAttribute = true)
    var origin: Vec3 = Vec3.ZERO,
    @field:JacksonXmlProperty(isAttribute = true)
    var size: Vec3 = Vec3.ZERO,
    @field:JacksonXmlProperty(isAttribute = true)
    var start: Vec3 = Vec3.ZERO,
    @field:JacksonXmlProperty(isAttribute = true)
    var centered: Boolean = true,
) : XmlNode()

class XmlPolygonRoom(
    origin: Vec3 = Vec3.ZERO,
    size: Vec3 = Vec3.ZERO,
    start: Vec3 = Vec3.ZERO,
    centered: Boolean = true,
    @field:JacksonXmlProperty(isAttribute = true)
    var shape: PolygonShape = PolygonShape.SQUARE,
    var polygon: XmlPath = XmlPath()
) : XmlRoom(origin, size, start, centered)

class XmlWall(
    @field:JacksonXmlProperty(isAttribute = true)
    var start: Vec3 = Vec3.ZERO,
    @field:JacksonXmlProperty(isAttribute = true)
    var end: Vec3 = Vec3.ZERO,
    @field:JacksonXmlProperty(isAttribute = true)
    var transparent: Boolean = false
) : XmlNode()

class XmlFloor(
    @field:JacksonXmlProperty(isAttribute = true)
    var height: Double = 0.0,
) : XmlNode()

class XmlPath(
    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "point")
    var points: List<Vec3>? = null,
    @field:JacksonXmlProperty(isAttribute = true)
    var origin: Vec3? = null,
) : XmlNode()


internal fun Node.mapToXmlNode(): XmlNode? = mapToXmlNodeRecursive(mutableSetOf())
/** Maps to XML without mapping any of the children. */
internal fun Node.mapToXmlNodeNoChildren(): XmlNode? = when (this) {
    is Structure -> XmlStructure(origin)
    is PolygonRoom -> XmlPolygonRoom(origin, size, start, isCentered(),
        shape, polygon.mapToXmlNode() as XmlPath
    )
    is Room -> XmlRoom(origin, size, start, isCentered())
    is Wall -> XmlWall(origin, end, transparent)
    is Floor -> XmlFloor(height)
    is Path -> XmlPath(points, origin)
    else -> null
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
        is XmlPolygonRoom -> PolygonRoom(origin, size).also {
            if (!centered) it.start = start
            it.shape = shape
            it.polygon.addPoints(polygon.points ?: emptyList())
        }
        is XmlRoom -> Room(origin, size).also {
            if (!centered) it.start = start
        }
        is XmlWall -> Wall(start, end, transparent)
        is XmlFloor -> Floor(height)
        is XmlPath -> Path(origin ?: Vec3.ZERO).also {
            it.addPoints(points ?: emptyList())
        }
        else -> null
    } ?: return null
    children.forEach { xmlChild ->
        xmlChild.mapXmlNodeRecursive(mapped)?.let { child ->
            node.addChild(child)
        }
    }
    return node
}