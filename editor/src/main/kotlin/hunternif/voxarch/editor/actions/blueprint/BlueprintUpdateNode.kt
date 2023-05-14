package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.DomRunBlueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintUpdateNode(
    private val node: BlueprintNode,
    description: String? = null,
    private val delegateBp: Blueprint? = null,
    x: Float? = null,
    y: Float? = null,
) : HistoryAction(
    description ?: "Update blueprint node",
    FontAwesomeIcons.Code
) {
    private val oldX: Float = node.x
    private val oldY: Float = node.y
    private val newX: Float = x ?: oldX
    private val newY: Float = y ?: oldY
    private var oldDelegateBp: Blueprint? =
        (node.domBuilder as? DomRunBlueprint)?.blueprint

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        delegateBp?.let { (node.domBuilder as? DomRunBlueprint)?.blueprint = it }
        node.x = newX
        node.y = newY
        node.applyImNodesPos()
    }

    override fun revert(app: EditorAppImpl) {
        oldDelegateBp?.let { (node.domBuilder as? DomRunBlueprint)?.blueprint = it }
        node.x = oldX
        node.y = oldY
        node.applyImNodesPos()
    }
}