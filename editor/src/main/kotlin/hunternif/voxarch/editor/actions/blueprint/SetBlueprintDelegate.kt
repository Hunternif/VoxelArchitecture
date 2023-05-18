package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.DomRunBlueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class SetBlueprintDelegate(
    private val node: BlueprintNode,
    private val delegateBp: Blueprint,
) : HistoryAction(
    "Set blueprint delegate",
    FontAwesomeIcons.Code
) {
    private val domBuilder: DomRunBlueprint? = node.domBuilder as? DomRunBlueprint

    private var oldDelegateBp: Blueprint = domBuilder?.blueprint ?: delegateBp

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        domBuilder?.blueprint = delegateBp
    }

    override fun revert(app: EditorAppImpl) {
        domBuilder?.blueprint = oldDelegateBp
    }
}