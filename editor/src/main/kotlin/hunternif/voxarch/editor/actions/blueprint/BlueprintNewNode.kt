package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintNewNode(
    private val bp: Blueprint,
    private val name: String,
    private val domBuilder: DomBuilder,
    private val x: Float,
    private val y: Float,
    private val autoLinkFrom: BlueprintSlot.Out?,
) : HistoryAction(
    "New blueprint node",
    FontAwesomeIcons.Code
) {
    lateinit var node: BlueprintNode
        private set

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (!::node.isInitialized) {
            node = bp.addNode(name, domBuilder, x, y)
        }
        bp.nodes.add(node)
        node.applyImNodesPos()
        if (node.inputs.isNotEmpty())
            autoLinkFrom?.linkTo(node.inputs[0])
    }

    override fun revert(app: EditorAppImpl) {
        bp.removeNode(node)
    }
}