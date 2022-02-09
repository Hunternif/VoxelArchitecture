package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.SelectMask.*
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.SceneVoxelGroup

class SelectObjectsBuilder(
    app: EditorAppImpl,
    private val mask: SelectMask = ALL,
) : HistoryActionBuilder(app) {
    private val oldSet = app.state.selectedObjects.toMutableSet().apply {
        when (mask) {
            NODES -> removeIf { it !is SceneNode }
            VOXELS -> removeIf { it !is SceneVoxelGroup }
            ALL -> {}
        }
    }
    private val newSet = oldSet.toMutableSet()

    fun clear() = app.run {
        when (mask) {
            NODES -> state.selectedObjects.removeAll { it is SceneNode }
            VOXELS -> state.selectedObjects.removeAll { it is SceneVoxelGroup }
            ALL -> state.selectedObjects.clear()
        }
        newSet.clear()
        scene.updateSelectedNodeModel()
    }

    fun add(obj: SceneObject) = app.run {
        if (obj != state.rootNode && obj != state.voxelRoot) {
            newSet.add(obj)
            if (state.selectedObjects.add(obj)) {
                scene.updateSelectedNodeModel()
            }
        }
    }

    fun remove(obj: SceneObject) = app.run {
        newSet.remove(obj)
        if (state.selectedObjects.remove(obj)) {
            scene.updateSelectedNodeModel()
        }
    }

    private fun makeDescription(): String = when {
        newSet.isEmpty() && oldSet.isNotEmpty() -> "Unselect all"
        newSet.size == 1 -> "Select 1 object"
        else -> "Select ${newSet.size} objects"
    }

    override fun build() = SelectObjects(oldSet, newSet, mask, makeDescription())

    override fun commit() {
        // only commit if it actually changes the selection
        if (newSet != oldSet) super.commit()
    }
}