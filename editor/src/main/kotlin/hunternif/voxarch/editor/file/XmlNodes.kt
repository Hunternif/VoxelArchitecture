package hunternif.voxarch.editor.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.vector.Vec3


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "class"
)
@JsonSubTypes(value = [
    JsonSubTypes.Type(name="Structure", value = XmlStructure::class),
    JsonSubTypes.Type(name="Room", value = XmlRoom::class),
    JsonSubTypes.Type(name="PolygonRoom", value = XmlPolygonRoom::class),
    JsonSubTypes.Type(name="Floor", value = XmlFloor::class),
    JsonSubTypes.Type(name="Wall", value = XmlWall::class),
    JsonSubTypes.Type(name="Path", value = XmlPath::class),
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