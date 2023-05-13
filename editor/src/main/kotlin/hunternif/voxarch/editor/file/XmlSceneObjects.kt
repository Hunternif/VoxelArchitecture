package hunternif.voxarch.editor.file

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.scene.shaders.VoxelRenderMode
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.INested
import hunternif.voxarch.util.emptyArray3D
import hunternif.voxarch.util.forEachSubtree
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
@JsonIgnoreProperties(ignoreUnknown = true)
open class XmlSceneObject(
    @field:JacksonXmlProperty(isAttribute = true)
    var id: Int = -1,

    @field:JacksonXmlProperty(isAttribute = true)
    var generated: Boolean = false,
) : INested<XmlSceneObject> {
    @field:JsonIgnore
    override var parent: XmlSceneObject? = null

    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "obj")
    override var children = mutableListOf<XmlSceneObject>()
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

    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "blueprintID")
    var blueprintIDs: List<Int> = emptyList(),

    /** Populated during deserialization */
    @field:JsonIgnore
    var blueprintRefs: List<Blueprint> = emptyList(),

    /** Populated during deserialization */
    @field:JsonIgnore
    var builderRef: Builder<*>? = null,
) : XmlSceneObject(id, generated)

class XmlSceneVoxelGroup(
    id: Int = -1,

    @field:JacksonXmlProperty(isAttribute = true)
    var label: String = "voxel group",
    generated: Boolean = false,

    @field:JacksonXmlProperty(isAttribute = true)
    var origin: Vec3 = Vec3.ZERO,

    /** Populated during deserialization */
    @field:JsonIgnore
    var data: IStorage3D<out IVoxel?> = emptyArray3D(),

    @field:JacksonXmlProperty(isAttribute = true)
    var renderMode: VoxelRenderMode = VoxelRenderMode.COLORED,
) : XmlSceneObject(id, generated = generated)


internal fun SceneObject.mapToXml(): XmlSceneObject? = mapToXmlRecursive(mutableSetOf())

/** The set is passed to prevent an infinite nested loop. */
private fun SceneObject.mapToXmlRecursive(mapped: MutableSet<SceneObject>): XmlSceneObject? {
    if (this in mapped) return null
    val colorHex = color.hex.toString(16)
    mapped.add(this)
    val xmlNode: XmlSceneObject = when (this) {
        is SceneNode -> XmlSceneNode(
            id, node.mapToXmlNodeNoChildren(), colorHex, color.a, isGenerated,
            blueprints.map { it.id }
        )
        is SceneVoxelGroup -> XmlSceneVoxelGroup(id, label, isGenerated, origin)
        else -> XmlSceneObject(id, isGenerated)
    }
    children.forEach { child ->
        child.mapToXmlRecursive(mapped)?.let { xmlChild ->
            xmlNode.addChild(xmlChild)
        }
    }
    return xmlNode
}

internal fun XmlSceneObject.mapXml(): SceneObject? {
    return mapXmlRecursive(mutableSetOf())?.apply {
        // Now that all nodes are in a correct parent-child hierarchy,
        // we must re-calculate their absolute positions.
        forEachSubtree { it.update() }
    }
}

/** The set is passed to prevent an infinite nested loop. */
private fun XmlSceneObject.mapXmlRecursive(mapped: MutableSet<XmlSceneObject>): SceneObject? {
    if (this in mapped) return null
    mapped.add(this)
    val sceneNode: SceneObject = when (this) {
        is XmlSceneNode -> {
            val node = node?.mapXmlNode() ?: return null
            node.builder = builderRef
            val color = ColorRGBa.fromHex(colorHexRGB.toInt(16), colorAlpha)
            SceneNode(id, node, color, generated).apply {
                blueprintRefs.forEach { addBlueprint(it) }
            }
        }
        is XmlSceneVoxelGroup -> SceneVoxelGroup(id, label, data, renderMode, generated, origin)
        else -> SceneObject(id, isGenerated = generated)
    }
    children.forEach { xmlChild ->
        xmlChild.mapXmlRecursive(mapped)?.let { child ->
            sceneNode.addChild(child)
        }
    }
    return sceneNode
}