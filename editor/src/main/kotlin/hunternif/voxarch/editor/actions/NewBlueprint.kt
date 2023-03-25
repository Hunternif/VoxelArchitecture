package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class NewBlueprint(
    private val name: String = "Untitled",
    private val autoSelect: Boolean = true,
) : HistoryAction("New blueprint", FontAwesomeIcons.Landmark) {
    lateinit var bp: Blueprint
    private var oldSelected: Blueprint? = null
    private var newSelected: Blueprint? = null

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::bp.isInitialized) {
            bp = app.state.registry.newBlueprint(name)
            oldSelected = app.state.selectedBlueprint
            newSelected = if (autoSelect) bp else oldSelected
        }
        app.state.registry.blueprintIDs.save(bp)
        if (autoSelect) OpenBlueprint(newSelected).invoke(app)
    }

    override fun revert(app: EditorAppImpl) {
        app.state.selectedBlueprint = oldSelected
        app.state.registry.blueprintIDs.remove(bp)
        OpenBlueprint(oldSelected).invoke(app)
    }
}