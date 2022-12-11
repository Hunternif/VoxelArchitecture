package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.blueprint.DomBuilderFactory
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BlueprintNewNode(
    private val bp: Blueprint,
    private val name: String,
    private val x: Float,
    private val y: Float,
    private val autoLinkFrom: BlueprintSlot.Out?,
    private val createBuilder: DomBuilderFactory,
) : HistoryAction(
    "New blueprint node",
    FontAwesomeIcons.Code
) {
    lateinit var node: BlueprintNode
        private set

    override fun invoke(app: EditorAppImpl) {
        if (!::node.isInitialized) {
            node = bp.addNode(name, x, y, createBuilder)
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