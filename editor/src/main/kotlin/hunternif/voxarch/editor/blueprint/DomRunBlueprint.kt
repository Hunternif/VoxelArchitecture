package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomBuilder

/**
 * Runs blueprint
 */
class DomRunBlueprint : DomBuilder() {
    /** Used for deserialization, to reference the blueprint before it has been loaded */
    //TODO: don't store this name here! store it somewhere else during de-serialization.
    // It value becomes obsolete after renaming the BP.
    var blueprintName: String? = null
    var blueprintID: Int? = null
    var blueprint: Blueprint = emptyBlueprint
        set(value) {
            field = value
            blueprintName = blueprint.name
        }

    val isEmpty get() = blueprint == emptyBlueprint

    /** Actual slots that will be displayed in UI */
    val outSlots = LinkedHashSet<BlueprintSlot.Out>()

    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        ctx.stylesheet.copyRules(blueprint.internalStylesheet)
        return blueprint.start.domBuilder.children
    }

    companion object {
        /** Used to indicate that no actual blueprint is attached */
        val emptyBlueprint = Blueprint("")
    }
}