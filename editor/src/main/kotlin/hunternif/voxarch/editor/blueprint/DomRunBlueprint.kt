package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomBuilder

/**
 * Runs blueprint
 */
class DomRunBlueprint : DomBuilder() {
    /** Used for deserialization, to reference the blueprint before it has been loaded */
    var blueprintID: Int? = null
    var blueprint: Blueprint = emptyBlueprint
        set(value) {
            field = value
            blueprintID = blueprint.id
        }

    val isEmpty get() = blueprint == emptyBlueprint

    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        ctx.stylesheet.copyRules(blueprint.internalStylesheet)
        return blueprint.start.domBuilder.children
    }

    companion object {
        /** Used to indicate that no actual blueprint is attached */
        val emptyBlueprint = Blueprint(-1, "")
    }
}