package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class DeleteBlueprint(
    private val bp: Blueprint,
) : HistoryAction("Remove blueprint", FontAwesomeIcons.TrashAlt) {
    private var oldSelected: Blueprint? = null

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        oldSelected = app.state.selectedBlueprint
        app.state.selectedBlueprint = null
        app.state.registry.blueprintIDs.remove(bp)
    }

    override fun revert(app: EditorAppImpl) {
        app.state.selectedBlueprint = oldSelected
        app.state.registry.blueprintIDs.save(bp)
    }
}