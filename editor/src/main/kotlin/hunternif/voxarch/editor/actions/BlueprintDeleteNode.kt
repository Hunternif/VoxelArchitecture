package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintDeleteNode(private val node: BlueprintNode) : HistoryAction(
    "Delete blueprint node",
    FontAwesomeIcons.TrashAlt
) {
    private val oldStart = node.bp.start

    override fun invoke(app: EditorAppImpl) {
        node.bp.removeNode(node)
    }

    override fun revert(app: EditorAppImpl) {
        node.bp.nodes.add(node)
        node.bp.start = oldStart
    }
}