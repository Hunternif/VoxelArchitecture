package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.BlueprintEvent
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

/** Deletes Blueprint from the library */
class DeleteBlueprint(
    private val bp: Blueprint,
) : HistoryAction(
    "Delete blueprint",
    FontAwesomeIcons.TrashAlt
), BlueprintEvent {
    private var oldSelected: Blueprint? = null
    private var newSelected: Blueprint? = null
    private lateinit var nodeActions: List<RemoveBlueprint>

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::nodeActions.isInitialized) {
            oldSelected = app.state.selectedBlueprint
            newSelected = if (oldSelected == bp) null else oldSelected
            nodeActions = app.state.registry.bpInNodes[bp].map {
                RemoveBlueprint(it, bp)
            }
        }
        nodeActions.forEach { it.invoke(app) }
        app.state.selectedBlueprint = null
        app.state.registry.blueprintIDs.remove(bp)
    }

    override fun revert(app: EditorAppImpl) {
        nodeActions.forEach { it.revert(app) }
        app.state.selectedBlueprint = oldSelected
        app.state.registry.blueprintIDs.save(bp)
    }
}