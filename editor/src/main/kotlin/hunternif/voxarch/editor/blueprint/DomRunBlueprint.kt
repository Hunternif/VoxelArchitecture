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

    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        //TODO: alternative way to run blueprint: use CSS style to run blueprint
        ctx.stylesheet.copyRules(blueprint.internalStylesheet)
        return blueprint.start.domBuilder.children
    }

    companion object {
        val emptyBlueprint = Blueprint(-1, "")
    }
}