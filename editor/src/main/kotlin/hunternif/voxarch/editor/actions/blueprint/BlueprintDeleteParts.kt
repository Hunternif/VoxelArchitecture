package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.BlueprintLink
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintDeleteParts(
    private val nodes: Collection<BlueprintNode>,
    private val links: Collection<BlueprintLink>,
) : HistoryAction(
    makeDescription(nodes, links),
    FontAwesomeIcons.TrashAlt
) {
    companion object {
        private fun makeDescription(
            nodes: Collection<BlueprintNode>,
            links: Collection<BlueprintLink>,
        ) = "Delete blueprint " + when {
            nodes.isEmpty() && links.isNotEmpty() -> "links"
            nodes.isNotEmpty() && links.isEmpty() -> "nodes"
            else -> "parts"
        }
    }

    /** Actions for deleting individual nodes and links. */
    private lateinit var cleanupActions: List<HistoryAction>

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::cleanupActions.isInitialized) {
            cleanupActions = mutableListOf<HistoryAction>().apply {
                links.forEach { add(BlueprintUnlink(it)) }
                nodes.forEach { add(BlueprintDeleteNode(it)) }
            }
        }
        cleanupActions.forEach { it.invoke(app) }
    }

    override fun revert(app: EditorAppImpl) {
        cleanupActions.forEach { it.revert(app) }
    }
}