package hunternif.voxarch.editor.file

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import hunternif.voxarch.editor.scenegraph.*

/** Represents part of the AppState related to the scene tree. */
@JacksonXmlRootElement(localName = "scenetree")
@JsonIgnoreProperties(ignoreUnknown = true)
class XmlSceneTree(
    @field:JacksonXmlProperty
    var noderoot: XmlSceneObject? = null,

    @field:JacksonXmlProperty
    var voxelroot: XmlSceneObject? = null,

    @field:JacksonXmlProperty
    var generatedNodes: XmlSubset? = null,

    @field:JacksonXmlProperty
    var generatedVoxels: XmlSubset? = null,

    @field:JacksonXmlProperty
    var selectedObjects: XmlSubset? = null,

    @field:JacksonXmlProperty
    var hiddenObjects: XmlSubset? = null,

    @field:JacksonXmlProperty
    var manuallyHiddenObjects: XmlSubset? = null,
)

@JacksonXmlRootElement(localName = "subset")
@JsonIgnoreProperties(ignoreUnknown = true)
class XmlSubset(
    @field:JacksonXmlProperty(isAttribute = true)
    var id: Int = -1,

    @field:JacksonXmlProperty(isAttribute = true)
    var name: String = "",

    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "item")
    var items: List<Int> = emptyList(),
)

internal fun <T : SceneObject> Subset<T>.mapToXml(): XmlSubset =
    XmlSubset(id, name, items.map { it.id })

internal inline fun <reified T : SceneObject> XmlSubset.mapXmlSubset(
    registry: SceneRegistry
): Subset<T> {
    val subset = Subset<T>(id, name)
    registry.save(subset)
    subset.addAll(items.map { registry.objectIDs.map[it] }.filterIsInstance<T>())
    return subset
}