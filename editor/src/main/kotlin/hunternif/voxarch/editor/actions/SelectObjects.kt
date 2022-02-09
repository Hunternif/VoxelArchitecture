package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.SelectMask.*
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.SceneVoxelGroup

class SelectObjects(
    private val oldSet: Collection<SceneObject>,
    private val newSet: Collection<SceneObject>,
    private val mask: SelectMask = ALL,
    description: String,
) : HistoryAction(description, Tool.SELECT.icon) {

    override fun invoke(app: EditorAppImpl) = app.applySelection(newSet)

    override fun revert(app: EditorAppImpl) = app.applySelection(oldSet)

    private fun EditorAppImpl.applySelection(objs: Collection<SceneObject>) {
        when (mask) {
            NODES -> state.selectedObjects.removeAll { it is SceneNode }
            VOXELS -> state.selectedObjects.removeAll { it is SceneVoxelGroup }
            ALL -> state.selectedObjects.clear()
        }
        for (obj in objs) {
            if (obj == state.rootNode || obj == state.voxelRoot) continue
            state.selectedObjects.add(obj)
        }
        scene.updateSelectedNodeModel()
    }
}

/** Only the objects filtered by this mask will be affected by the select action. */
enum class SelectMask {
    NODES, VOXELS, ALL
}