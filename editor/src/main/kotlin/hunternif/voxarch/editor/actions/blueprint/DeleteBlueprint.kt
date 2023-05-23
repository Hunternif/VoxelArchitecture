package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

/** Deletes Blueprint from the library */
class DeleteBlueprint(
    private val bp: Blueprint,
) : HistoryAction("Delete blueprint", FontAwesomeIcons.TrashAlt) {
    private var oldSelected: Blueprint? = null
    private var newSelected: Blueprint? = null
    /**
     * Actions for deleting things that use this blueprint:
     * - scene nodes;
     * - "run" nodes in other blueprints.
     */
    private lateinit var cleanupActions: List<HistoryAction>

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::cleanupActions.isInitialized) {
            oldSelected = app.state.selectedBlueprint
            newSelected = if (oldSelected == bp) null else oldSelected
            val usage = app.state.blueprintLibrary.usage(bp)
            cleanupActions =
                usage.nodes.map { RemoveBlueprint(it, bp) } +
                    usage.delegators.map { SetBlueprintDelegate(it, null) }
        }
        cleanupActions.forEach { it.invoke(app) }
        app.state.selectedBlueprint = null
        app.state.blueprintRegistry.remove(bp)
        // Refresh all because other blueprints could be referenced inside this one:
        app.state.blueprintRegistry.refreshUsagesInBlueprints()
    }

    override fun revert(app: EditorAppImpl) {
        cleanupActions.forEach { it.revert(app) }
        app.state.selectedBlueprint = oldSelected
        app.state.blueprintRegistry.save(bp)
        // Refresh all because other blueprints could be referenced inside this one:
        app.state.blueprintRegistry.refreshUsagesInBlueprints()
    }
}