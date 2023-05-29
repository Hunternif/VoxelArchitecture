package hunternif.voxarch.editor.builder

import hunternif.voxarch.dom.style.Property
import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.Value
import hunternif.voxarch.dom.style.property.PropBuilder
import hunternif.voxarch.dom.style.set
import hunternif.voxarch.plan.Node

/**
 * Finds builder by name and applies it to the node.
 * [updateBuilders] must be called before building the DOM tree.
 *
 * This property exists outside of the `core` module, because Builders can be
 * created and modified in the editor.
 *
 * This works in the following manner:
 * 1. Before building DOM, call [updateBuilders] to set known builders.
 * 2. During building DOM, the property is executed normally.
 * 3. A string value is passed in, we look up the matching builder by name.
 * 4. We add the builder instance via the existing style property 'builder'.
 */
class PropertyEditorBuilder : Property<String>(
    "builder", Node::class.java, String::class.java, "",
) {
    /** Map of existing builders by name. */
    private var builderMap: Map<String, BuilderLibrary.Entry> = emptyMap()

    /**
     * Updates the map of known builders.
     * Must be called before building the DOM.
     */
    fun updateBuilders(library: BuilderLibrary) {
        this.builderMap = library.buildersByName
    }

    override fun applyTo(styled: StyledElement<*>, value: Value<String>) {
        val builderName = value.invoke("", styled.seed)
        val builder = builderMap[builderName]?.builder
        PropBuilder.applyTo(styled, set(builder))
    }
}

/** This adapts [PropBuilder] to work in the editor. */
val PropEditorBuilder = PropertyEditorBuilder()