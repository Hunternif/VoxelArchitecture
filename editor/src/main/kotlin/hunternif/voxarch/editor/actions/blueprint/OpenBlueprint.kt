package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class OpenBlueprint(private val newBlueprint: Blueprint?) : HistoryAction(
    "Open blueprint",
    FontAwesomeIcons.FileCode
) {
    private var oldBlueprint: Blueprint? = null

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        oldBlueprint = app.state.selectedBlueprint
        app.state.selectedBlueprint = newBlueprint
    }

    override fun revert(app: EditorAppImpl) {
        app.state.selectedBlueprint = oldBlueprint
    }
}