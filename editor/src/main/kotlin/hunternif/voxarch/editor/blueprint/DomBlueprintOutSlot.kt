package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.util.forEachReversed

/**
 * Special Dom builder for creating an "out" slot for a Blueprint.
 * Other blueprints can reference this BP, and link more elements to these "out" slots.
 *
 * This class represents the out slot declared inside the referenced Blueprint.
 * Other BPs should not add children to this Dom Builder.
 * Use [DomBlueprintOutSlotInstance] instead.
 */
class DomBlueprintOutSlot : DomBuilder() {
    var slotName: String = "slot"

    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        // This DomBuilder cannot have direct children.
        // Find a matching [DomBlueprintOutSlotInstance] and get its children:
        val start = findDelegateNode(ctx) ?: return emptyList()
        val slotInstance = findSlotInstance(start) ?: return emptyList()
        return listOf(slotInstance)
    }

    /**
     * Find the delegate node that called this blueprint.
     * If it returns null, it means we are running this BP on its own,
     * and there was no delegation.
     */
    private fun findDelegateNode(ctx: DomBuildContext): DomRunBlueprint? {
        var depth = 0 // how many layers of delegation we went through
        ctx.lineage.forEachReversed {
            when (it.domBuilder) {
                is DomBlueprintOutSlot -> {
                    // closed paren ')'
                    depth++
                }
                is DomRunBlueprint -> {
                    // open paren '('
                    depth--
                    if (depth <= 0) return it.domBuilder as DomRunBlueprint
                }
            }
        }
        return null
    }

    /**
     * Returns an "out" slot instance on [delegateNode] which corresponds to
     * this [DomBlueprintOutSlot] source.
     */
    private fun findSlotInstance(delegateNode: DomRunBlueprint): DomBlueprintOutSlotInstance? {
        delegateNode.outSlots.forEach {
            val domBuilder = it.domSlot as? DomBlueprintOutSlotInstance
            if (domBuilder?.source === this) return domBuilder
        }
        return null
    }
}

/**
 * This class represents an instance of an "out" slot on another BP's delegate node.
 * Adding children here will not affect the [source] slot.
 */
class DomBlueprintOutSlotInstance(val source: DomBlueprintOutSlot) : DomBuilder()