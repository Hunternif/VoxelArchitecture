package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.generator.ChainedGenerator
import imgui.extension.imnodes.ImNodes

class BlueprintNewNode(
    private val bp: Blueprint,
    private val name: String,
    private val generator: ChainedGenerator,
    private val x: Float,
    private val y: Float,
) : HistoryAction(
    "New blueprint node",
    FontAwesomeIcons.Code
) {
    lateinit var node: BlueprintNode
        private set

    override fun invoke(app: EditorAppImpl) {
        if (!::node.isInitialized) {
            node = bp.addNode(name, generator, x, y)
        }
        bp.nodes.add(node)
        ImNodes.setNodeEditorSpacePos(node.id, node.x, node.y)
    }

    override fun revert(app: EditorAppImpl) {
        bp.removeNode(node)
    }
}