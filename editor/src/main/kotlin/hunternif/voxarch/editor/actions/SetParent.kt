package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode

class SetParent(private val newParent: SceneNode) : HistoryAction(
    "Set parent node",
    FontAwesomeIcons.Sitemap
) {
    private lateinit var oldParent: SceneNode

    override fun invoke(app: EditorAppImpl) {
        if (!::oldParent.isInitialized) {
            oldParent = app.state.parentNode
        }
        app.state.parentNode = newParent
    }

    override fun revert(app: EditorAppImpl) {
        app.state.parentNode = oldParent
    }
}