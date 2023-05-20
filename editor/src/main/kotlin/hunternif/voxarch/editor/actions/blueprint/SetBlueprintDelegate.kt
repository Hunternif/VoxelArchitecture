package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.DomRunBlueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class SetBlueprintDelegate(
    private val node: BlueprintNode,
    delegateBp: Blueprint?,
) : HistoryAction(
    "Set blueprint delegate",
    FontAwesomeIcons.Code
) {
    private val domBuilder: DomRunBlueprint? = node.domBuilder as? DomRunBlueprint

    private val newDelegateBp: Blueprint = delegateBp ?: DomRunBlueprint.emptyBlueprint
    private var oldDelegateBp: Blueprint = domBuilder?.blueprint ?: newDelegateBp

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        domBuilder?.blueprint = newDelegateBp
        app.state.blueprintRegistry.removeUsage(oldDelegateBp, node)
        app.state.blueprintRegistry.addUsage(newDelegateBp, node)
    }

    override fun revert(app: EditorAppImpl) {
        domBuilder?.blueprint = oldDelegateBp
        app.state.blueprintRegistry.removeUsage(newDelegateBp, node)
        app.state.blueprintRegistry.addUsage(oldDelegateBp, node)
    }
}