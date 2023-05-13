package hunternif.voxarch.editor.actions.scene

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class BuildNodesAndVoxels : HistoryAction(
    "Build (nodes & voxels)",
    FontAwesomeIcons.Cubes
) {
    private val nodesAction = GenerateNodes()
    private val voxelsAction = BuildVoxels()

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        nodesAction.invoke(app, firstTime)
        voxelsAction.invoke(app, firstTime)
    }

    override fun revert(app: EditorAppImpl) {
        voxelsAction.revert(app)
        nodesAction.revert(app)
    }
}