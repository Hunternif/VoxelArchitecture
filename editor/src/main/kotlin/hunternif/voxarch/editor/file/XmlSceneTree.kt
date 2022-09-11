package hunternif.voxarch.editor.file

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

/** Represents the scene tree + identifies important root nodes. */
@JacksonXmlRootElement(localName = "scenetree")
class XmlSceneTree(
    @field:JacksonXmlProperty
    var noderoot: XmlSceneObject? = null,
    @field:JacksonXmlProperty
    var voxelroot: XmlSceneObject? = null,
)