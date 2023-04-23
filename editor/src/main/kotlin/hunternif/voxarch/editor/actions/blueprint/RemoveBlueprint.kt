package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.BlueprintEvent
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode

/** Removes Blueprint from the node, but keeps it in the library. */
class RemoveBlueprint(
    private val node: SceneNode,
    private val bp: Blueprint,
) : HistoryAction(
    "Remove blueprint",
    FontAwesomeIcons.Ban
), BlueprintEvent {
    private var oldSelected: Blueprint? = null
    private var newSelected: Blueprint? = null

    private var insertPos: Int = 0

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        oldSelected = app.state.selectedBlueprint
        newSelected = if (oldSelected == bp) null else oldSelected
        insertPos = node.blueprints.indexOf(bp)
        if (insertPos > -1) {
            node.removeBlueprint(bp)
            app.state.registry.bpInNodes.remove(bp, node)
            OpenBlueprint(newSelected).invoke(app)
        }
    }

    override fun revert(app: EditorAppImpl) {
        if (insertPos > -1) {
            node.addBlueprint(bp, insertPos)
            app.state.registry.bpInNodes.put(bp, node)
            OpenBlueprint(oldSelected).invoke(app)
        }
    }
}