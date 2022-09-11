package hunternif.voxarch.editor.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.editor.util.toVector3f
import hunternif.voxarch.util.emptyArray3D
import hunternif.voxarch.vector.Vec3

// Annotated classes for XML serialization of SceneObject and its subclasses, and Subsets.

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "class"
)
@JsonSubTypes(value = [
    JsonSubTypes.Type(name = "SceneNode", value = XmlSceneNode::class),
    JsonSubTypes.Type(name = "SceneVoxelGroup", value = XmlSceneVoxelGroup::class),
])
@JacksonXmlRootElement(localName = "obj")
@JsonInclude(JsonInclude.Include.NON_NULL)
open class XmlSceneObject(
    @field:JacksonXmlProperty(isAttribute = true)
    var id: Int = -1,

    @field:JacksonXmlProperty(isAttribute = true)
    var generated: Boolean = false,
) {
    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "obj")
    var children = mutableListOf<XmlSceneObject>()
}

class XmlSceneNode(
    id: Int = -1,

    @field:JacksonXmlProperty(localName = "node")
    val node: XmlNode? = null,

    @field:JacksonXmlProperty(isAttribute = true, localName = "color")
    var colorHexRGB: String = "ffffff",

    @field:JacksonXmlProperty(isAttribute = true, localName = "alpha")
    var colorAlpha: Float = 1f,

    generated: Boolean = false,
) : XmlSceneObject(id, generated)

class XmlSceneVoxelGroup(
    id: Int = -1,

    @field:JacksonXmlProperty(isAttribute = true)
    var label: String = "voxel group",
    generated: Boolean = false,

    @field:JacksonXmlProperty(isAttribute = true)
    var origin: Vec3 = Vec3.ZERO,
) : XmlSceneObject(id, generated = generated)


internal fun SceneObject.mapToXml(): XmlSceneObject? = mapToXmlRecursive(mutableSetOf())

/** The set is passed to prevent an infinite nested loop. */
private fun SceneObject.mapToXmlRecursive(mapped: MutableSet<SceneObject>): XmlSceneObject? {
    if (this in mapped) return null
    val colorHex = color.hex.toString(16)
    mapped.add(this)
    val xmlNode: XmlSceneObject = when (this) {
        is SceneNode -> XmlSceneNode(id, node.mapToXmlNodeNoChildren(), colorHex, color.a, isGenerated)
        is SceneVoxelGroup -> XmlSceneVoxelGroup(id, label, isGenerated, origin.toVec3())
        else -> XmlSceneObject(id, isGenerated)
    }
    children.forEach { child ->
        child.mapToXmlRecursive(mapped)?.let { xmlChild ->
            xmlNode.children.add(xmlChild)
        }
    }
    return xmlNode
}

internal fun XmlSceneObject.mapXml(): SceneObject? = mapXmlRecursive(mutableSetOf())

/** The set is passed to prevent an infinite nested loop. */
private fun XmlSceneObject.mapXmlRecursive(mapped: MutableSet<XmlSceneObject>): SceneObject? {
    if (this in mapped) return null
    mapped.add(this)
    val node: SceneObject = when (this) {
        is XmlSceneNode -> {
            val color = ColorRGBa.fromHex(colorHexRGB.toInt(16), colorAlpha)
            SceneNode(id, node?.mapXmlNode() ?: return null, color, generated)
        }
        //TODO: read voxel file
        is XmlSceneVoxelGroup -> SceneVoxelGroup(id, label, emptyArray3D(), generated, origin.toVector3f())
        else -> SceneObject(id, isGenerated = generated)
    }
    children.forEach { xmlChild ->
        xmlChild.mapXmlRecursive(mapped)?.let { child ->
            node.addChild(child)
        }
    }
    return node
}