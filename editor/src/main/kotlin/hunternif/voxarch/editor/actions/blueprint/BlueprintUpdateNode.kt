package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.DomRunBlueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintUpdateNode(
    private val node: BlueprintNode,
    private val delegateBp: Blueprint? = null,
) : HistoryAction(
    "Update blueprint node",
    FontAwesomeIcons.Code
) {
    private var oldDelegateBp: Blueprint? =
        (node.domBuilder as? DomRunBlueprint)?.blueprint

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        delegateBp?.let { (node.domBuilder as? DomRunBlueprint)?.blueprint = it }
    }

    override fun revert(app: EditorAppImpl) {
        oldDelegateBp?.let { (node.domBuilder as? DomRunBlueprint)?.blueprint = it }
    }
}