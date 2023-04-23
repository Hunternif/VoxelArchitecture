package hunternif.voxarch.editor.actions.visible

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.AppAction
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup

class ShowAction(
    val obj: SceneObject,
) : AppAction {
    override fun invoke(app: EditorAppImpl) = app.run {
        // This object may have been hidden by one of its parents
        // To make it visible, we must un-hide all parents.
        state.manuallyHiddenObjects.remove(obj)
        var parent: SceneObject? = obj
        while (parent != null) {
            state.manuallyHiddenObjects.remove(parent)
            parent = parent.parent
        }
        updateHiddenObjects()
        when (obj) {
            is SceneNode -> scene.updateNodeModel()
            is SceneVoxelGroup -> scene.updateVoxelModel()
        }
    }
}