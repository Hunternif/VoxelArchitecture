package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode

/** Removes Blueprint from the node, but keeps it in the library. */
class RemoveBlueprint(
    private val node: SceneNode,
    private val bp: Blueprint,
) : HistoryAction("Remove blueprint", FontAwesomeIcons.Ban) {
    private var oldSelected: Blueprint? = null
    private var newSelected: Blueprint? = null

    private var insertPos: Int = 0

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        oldSelected = app.state.selectedBlueprint
        newSelected = if (oldSelected == bp) null else oldSelected
        insertPos = node.blueprints.indexOf(bp)
        if (insertPos > -1) {
            node.removeBlueprint(bp)
            OpenBlueprint(newSelected).invoke(app)
        }
    }

    override fun revert(app: EditorAppImpl) {
        if (insertPos > -1) {
            node.addBlueprint(bp, insertPos)
            OpenBlueprint(oldSelected).invoke(app)
        }
    }
}