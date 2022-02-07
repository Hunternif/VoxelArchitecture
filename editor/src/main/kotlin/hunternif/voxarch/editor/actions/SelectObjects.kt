package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.scene.SceneObject

class SelectObjects(
    private val oldSet: Collection<SceneObject>,
    private val newSet: Collection<SceneObject>,
    description: String,
) : HistoryAction(description, Tool.SELECT.icon) {

    override fun invoke(app: EditorAppImpl) = app.run {
        state.selectedObjects.clear()
        for (obj in newSet) {
            if (obj == state.rootNode || obj == state.voxelRoot) continue
            state.selectedObjects.add(obj)
        }
        scene.updateSelectedNodeModel()
    }

    override fun revert(app: EditorAppImpl) = app.run {
        state.selectedObjects.clear()
        for (obj in oldSet) {
            if (obj == state.rootNode || obj == state.voxelRoot) continue
            state.selectedObjects.add(obj)
        }
        scene.updateSelectedNodeModel()
    }
}
