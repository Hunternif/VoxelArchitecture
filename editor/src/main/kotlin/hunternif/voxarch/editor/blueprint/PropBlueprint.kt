package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.property.PropContent

/**
 * Finds blueprint by name and adds its content to the DOM element.
 * [updateBlueprints] must be called before building the DOM tree.
 *
 * This property exists outside of the `core` module, because Blueprints only
 * exist in the `editor` module.
 *
 * This works in the following manner:
 * 1. Before building DOM, call [updateBlueprints] to set known blueprints.
 * 2. During building DOM, the property is executed normally.
 * 3. A string value is passed in, we look up the matching Blueprint by name.
 * 4. We apply the blueprint's actual content via the existing style property 'content'.
 * 5. We wrap that content in a [DomRunBlueprint] element to reuse stylesheet logic.
 */
class PropertyBlueprint : Property<String>(
    "blueprint", DomBuilder::class.java, String::class.java, "",
) {
    /** Map of existing blueprints by name. */
    private var blueprintMap: Map<String, Blueprint> = emptyMap()

    /**
     * Updates the map of known blueprints.
     * Must be called before building the DOM.
     */
    fun updateBlueprints(blueprints: Collection<Blueprint>) {
        blueprintMap = blueprints.associateBy { it.name }
    }

    override fun applyTo(styled: StyledElement<*>, value: Value<String>) {
        val bpName = value.invoke("", styled.seed)
        val blueprint = blueprintMap[bpName] ?: return
        val content = DomRunBlueprint().apply { this.blueprint = blueprint }
        val setContent = value<DomBuilder?> { base, _ -> base?.apply { addChild(content) } }
        PropContent.applyTo(styled, setContent)
    }
}

val PropBlueprint = PropertyBlueprint()