package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.generator.ChainedGenerator

class BlueprintNewNode(
    private val bp: Blueprint,
    private val generator: ChainedGenerator,
) : HistoryAction(
    "New blueprint node",
    FontAwesomeIcons.Code
) {
    lateinit var node: BlueprintNode
        private set

    override fun invoke(app: EditorAppImpl) {
        if (!::node.isInitialized) {
            node = bp.newNode(generator)
        }
        //TODO: store node position in ImNodes context
        bp.nodes.add(node)
    }

    override fun revert(app: EditorAppImpl) {
        bp.removeNode(node)
    }
}