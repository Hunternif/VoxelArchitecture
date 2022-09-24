package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.BlueprintLink
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintDeleteNode(private val node: BlueprintNode) : HistoryAction(
    "Delete blueprint node",
    FontAwesomeIcons.TrashAlt
) {
    private lateinit var oldLinks: List<BlueprintLink>

    override fun invoke(app: EditorAppImpl) {
        if (!::oldLinks.isInitialized) {
            oldLinks = node.inputs.flatMap { it.links } +
                node.outputs.flatMap { it.links }
        }
        node.bp.removeNode(node)
    }

    override fun revert(app: EditorAppImpl) {
        node.bp.nodes.add(node)
        oldLinks.forEach { it.from.linkTo(it.to) }
        node.applyImNodesPos()
    }
}