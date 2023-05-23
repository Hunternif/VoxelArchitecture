package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode

/**
 * If [autoAddNode] is non-null, the BP is auto-added to this node.
 */
class NewBlueprint(
    private val name: String = "Untitled",
    private val autoSelect: Boolean = true,
    private val autoAddNode: SceneNode? = null,
) : HistoryAction(
    "New blueprint",
    FontAwesomeIcons.Landmark
) {
    lateinit var bp: Blueprint
    private var oldSelected: Blueprint? = null
    private var newSelected: Blueprint? = null

    private var addAction: AddBlueprint? = null

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::bp.isInitialized) {
            bp = app.state.blueprintRegistry.newBlueprint(name)
            oldSelected = app.state.selectedBlueprint
            newSelected = if (autoSelect) bp else oldSelected
            addAction = autoAddNode?.let { AddBlueprint(it, bp, false) }
        }
        app.state.blueprintRegistry.save(bp)
        if (autoSelect) OpenBlueprint(newSelected).invoke(app)
        addAction?.invoke(app)
    }

    override fun revert(app: EditorAppImpl) {
        app.state.selectedBlueprint = oldSelected
        app.state.blueprintRegistry.remove(bp)
        OpenBlueprint(oldSelected).invoke(app)
        addAction?.revert(app)
    }
}