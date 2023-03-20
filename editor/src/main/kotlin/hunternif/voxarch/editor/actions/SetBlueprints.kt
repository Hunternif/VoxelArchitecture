package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.scenegraph.SceneNode

class SetBlueprints(
    private val node: SceneNode,
    private val newBlues: List<Blueprint>,
    private val newSelected: Blueprint? = null,
    description: String,
    icon: String,
) : HistoryAction(description, icon) {
    private val oldGens = node.blueprints.toList()
    private var oldSelected: Blueprint? = null

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        oldSelected = app.state.selectedBlueprint
        node.blueprints.clear()
        node.blueprints.addAll(newBlues)
        app.state.selectedBlueprint = newSelected
        newSelected?.nodes?.forEach { it.applyImNodesPos() }
    }

    override fun revert(app: EditorAppImpl) {
        node.blueprints.clear()
        node.blueprints.addAll(oldGens)
        app.state.selectedBlueprint = oldSelected
        oldSelected?.nodes?.forEach { it.applyImNodesPos() }
    }
}