package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.generator.IGenerator

class SetGenerators(
    private val node: SceneNode,
    private val newGens: List<IGenerator>,
    description: String,
    icon: String
) : HistoryAction(description, icon) {
    private val oldGens = node.generators.toList()

    override fun invoke(app: EditorAppImpl) {
        node.generators.clear()
        node.generators.addAll(newGens)
    }

    override fun revert(app: EditorAppImpl) {
        node.generators.clear()
        node.generators.addAll(oldGens)
    }
}