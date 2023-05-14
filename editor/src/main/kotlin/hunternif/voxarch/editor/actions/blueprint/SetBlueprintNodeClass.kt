package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.history.StackingAction
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class SetBlueprintNodeClass(
    private val node: BlueprintNode,
    styleClass: String,
) : HistoryAction(
    "Update blueprint node class name",
    FontAwesomeIcons.Code
), StackingAction<SetBlueprintNodeClass> {
    private var newStyleClass: String = styleClass
    private val oldStyleClass: String = node.extraStyleClass

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        node.extraStyleClass = newStyleClass
    }

    override fun revert(app: EditorAppImpl) {
        node.extraStyleClass = oldStyleClass
    }

    override fun update(nextAction: SetBlueprintNodeClass) {
        this.newStyleClass = nextAction.newStyleClass
    }
}