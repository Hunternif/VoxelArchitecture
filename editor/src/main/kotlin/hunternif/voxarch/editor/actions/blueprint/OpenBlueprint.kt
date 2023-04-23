package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.BlueprintEvent
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class OpenBlueprint(private val newBlueprint: Blueprint?) : HistoryAction(
    "Open blueprint",
    FontAwesomeIcons.FileCode
), BlueprintEvent {
    private var oldBlueprint: Blueprint? = null

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        oldBlueprint = app.state.selectedBlueprint
        app.state.selectedBlueprint = newBlueprint
        newBlueprint?.nodes?.forEach { it.applyImNodesPos() }
    }

    override fun revert(app: EditorAppImpl) {
        app.state.selectedBlueprint = oldBlueprint
        oldBlueprint?.nodes?.forEach { it.applyImNodesPos() }
    }
}