package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode

class AddBlueprint(
    private val node: SceneNode,
    private val bp: Blueprint,
    private val autoSelect: Boolean = false
) : HistoryAction("Add blueprint", FontAwesomeIcons.Landmark) {
    private var oldSelected: Blueprint? = null
    private var newSelected: Blueprint? = null

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        oldSelected = app.state.selectedBlueprint
        newSelected = if (autoSelect) bp else oldSelected
        node.addBlueprint(bp)
        OpenBlueprint(newSelected).invoke(app)
    }

    override fun revert(app: EditorAppImpl) {
        node.removeBlueprint(bp)
        OpenBlueprint(oldSelected).invoke(app)
    }
}