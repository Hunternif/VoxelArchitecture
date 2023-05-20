package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode

class AddBlueprint(
    private val node: SceneNode,
    private val bp: Blueprint,
    private val autoSelect: Boolean = true,
) : HistoryAction("Add blueprint", FontAwesomeIcons.PlusSquare) {
    private var oldSelected: Blueprint? = null
    private var newSelected: Blueprint? = null

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        oldSelected = app.state.selectedBlueprint
        newSelected = if (autoSelect) bp else oldSelected
        node.addBlueprint(bp)
        app.state.blueprintRegistry.addUsage(bp, node)
        if (autoSelect) OpenBlueprint(newSelected).invoke(app)
    }

    override fun revert(app: EditorAppImpl) {
        node.removeBlueprint(bp)
        app.state.blueprintRegistry.addUsage(bp, node)
        OpenBlueprint(oldSelected).invoke(app)
    }
}