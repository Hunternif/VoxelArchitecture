package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.style.Property
import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.Value

/** Fake temporary property to extract blueprint name */
val PropBlueprint = object : Property<String>("blueprint", DomBuilder::class.java, String::class.java, "") {
    var blueprintName: String = ""
    override fun applyTo(styled: StyledElement<*>, value: Value<String>) {
        blueprintName = value.invoke("", styled.seed)
    }
}