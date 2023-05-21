package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuilder

/**
 * Special Dom builder for creating an output slot for a Blueprint.
 * Other blueprints can reference this BP, and attach to these output slots.
 */
class DomBlueprintOutSlot : DomBuilder() {
    var slotName: String = "slot"
}