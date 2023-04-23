package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.BlueprintEvent
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.DomRunBlueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintUpdateNode(
    private val node: BlueprintNode,
    private val styleClass: String? = null,
    private val delegateBp: Blueprint? = null,
) : HistoryAction(
    "Update blueprint node",
    FontAwesomeIcons.Code
), BlueprintEvent {
    private lateinit var oldStyleClass: String
    private var oldDelegateBp: Blueprint? = null

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::oldStyleClass.isInitialized) {
            oldStyleClass = node.extraStyleClass
            oldDelegateBp = (node.domBuilder as? DomRunBlueprint)?.blueprint
        }
        node.extraStyleClass = styleClass ?: oldStyleClass
        delegateBp?.let { (node.domBuilder as? DomRunBlueprint)?.blueprint = it }
    }

    override fun revert(app: EditorAppImpl) {
        node.extraStyleClass = oldStyleClass
        oldDelegateBp?.let { (node.domBuilder as? DomRunBlueprint)?.blueprint = it }
    }
}