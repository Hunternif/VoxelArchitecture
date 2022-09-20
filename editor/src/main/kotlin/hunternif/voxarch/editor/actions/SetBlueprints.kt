package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.scenegraph.SceneNode

class SetBlueprints(
    private val node: SceneNode,
    private val newBlues: List<Blueprint>,
    description: String,
    icon: String
) : HistoryAction(description, icon) {
    private val oldGens = node.blueprints.toList()

    override fun invoke(app: EditorAppImpl) {
        node.blueprints.clear()
        node.blueprints.addAll(newBlues)
    }

    override fun revert(app: EditorAppImpl) {
        node.blueprints.clear()
        node.blueprints.addAll(oldGens)
    }
}