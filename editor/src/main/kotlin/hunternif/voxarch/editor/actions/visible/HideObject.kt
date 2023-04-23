package hunternif.voxarch.editor.actions.visible

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.AppAction
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import java.util.*

class HideObject(
    val obj: SceneObject,
) : AppAction {
    override fun invoke(app: EditorAppImpl) = app.run {
        state.manuallyHiddenObjects.add(obj)
        updateHiddenObjects()
        when (obj) {
            is SceneNode -> scene.updateNodeModel()
            is SceneVoxelGroup -> scene.updateVoxelModel()
        }
    }
}

/** Repopulate state.hiddenObjects by adding children of hidden parents. */
internal fun EditorAppImpl.updateHiddenObjects() {
    state.run {
        hiddenObjects.clear()
        val queue = LinkedList<SceneObject>()
        queue.addAll(manuallyHiddenObjects)
        while (queue.isNotEmpty()) {
            val child = queue.removeLast()
            hiddenObjects.add(child)
            queue.addAll(child.children)
        }
    }
}