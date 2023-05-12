package hunternif.voxarch.editor.actions.transform

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.history.StackingAction
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode

class SetNodeTags(
    private val node: SceneNode,
    private var newTags: Collection<String>,
) : StackingAction<SetNodeTags>,
    HistoryAction("Set node tags", FontAwesomeIcons.Tag)
{
    private val oldTags = node.node.tags.toList()

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        node.node.tags.clear()
        node.node.tags.addAll(newTags)
        app.gui.nodeTree.markListDirty()
    }

    override fun revert(app: EditorAppImpl) {
        node.node.tags.clear()
        node.node.tags.addAll(oldTags)
        app.gui.nodeTree.markListDirty()
    }

    override fun update(nextAction: SetNodeTags) {
        this.newTags = nextAction.newTags
    }
}