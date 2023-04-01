package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomBuilder

/**
 * Runs blueprint
 */
class DomRunBlueprint : DomBuilder() {
    var blueprint: Blueprint = emptyBlueprint

    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        //TODO: alternative way to run blueprint: use CSS style to run blueprint
        ctx.stylesheet.copyRules(blueprint.internalStylesheet)
        return blueprint.start.domBuilder.children
    }

    companion object {
        val emptyBlueprint = Blueprint(-1, "")
    }
}