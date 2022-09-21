package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class OpenBlueprint(private val newBlueprint: Blueprint?) : HistoryAction(
    "Open blueprint",
    FontAwesomeIcons.FileCode
) {
    private var oldBlueprint: Blueprint? = null

    override fun invoke(app: EditorAppImpl) {
        oldBlueprint = app.state.selectedBlueprint
        app.state.selectedBlueprint = newBlueprint
    }

    override fun revert(app: EditorAppImpl) {
        app.state.selectedBlueprint = oldBlueprint
    }
}