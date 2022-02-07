package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.scene.SceneObject

class SelectObjectsBuilder(app: EditorAppImpl) : HistoryActionBuilder(app) {
    private val oldSet = app.state.selectedObjects.toMutableSet()
    private val newSet = app.state.selectedObjects.toMutableSet()

    fun clear() = app.run {
        newSet.clear()
        state.selectedObjects.clear()
        scene.updateSelectedNodeModel()
    }

    fun add(obj: SceneObject) = app.run {
        if (obj != state.rootNode && obj != state.voxelRoot) {
            newSet.add(obj)
            state.selectedObjects.add(obj)
            scene.updateSelectedNodeModel()
        }
    }

    fun remove(obj: SceneObject) = app.run {
        newSet.remove(obj)
        state.selectedObjects.remove(obj)
        scene.updateSelectedNodeModel()
    }

    private fun makeDescription(): String = when {
        newSet.isEmpty() && oldSet.isNotEmpty() -> "Unselect all"
        newSet.size == 1 -> "Select 1 object"
        else -> "Select ${newSet.size} objects"
    }

    override fun build() = SelectObjects(oldSet, newSet, makeDescription())

    override fun commit() {
        // only commit if it actually changes the selection
        if (newSet != oldSet) super.commit()
    }
}