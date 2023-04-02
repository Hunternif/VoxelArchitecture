package hunternif.voxarch.editor.actions

import hunternif.voxarch.builder.Builder
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode

class SetNodeBuilder(
    private val sceneNode: SceneNode,
    private val newBuilder: Builder<*>?,
) : HistoryAction("Set node builder", FontAwesomeIcons.SlidersH) {
    private var oldBuilder = sceneNode.node.builder

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        sceneNode.node.builder = newBuilder
    }

    override fun revert(app: EditorAppImpl) {
        sceneNode.node.builder = oldBuilder
    }
}