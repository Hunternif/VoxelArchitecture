package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.setHoveredObjects
import hunternif.voxarch.editor.scenegraph.SceneObject

/**
 * Keeps track of which items were hovered during this frame.
 *
 * At the start of the frame, it resets all hovers.
 * During the frame, parts of the GUI can call it to mark objects as hovered.
 * At the end of the frame, if any objects were hovered, it updates the scene.
 */
class HoverController(private val app: EditorApp) {
    /** Objects hovered last frame */
    private val prevHoverSet = mutableSetOf<SceneObject>()

    /** Objects hovered during the current frame*/
    private val newHoverSet = mutableSetOf<SceneObject>()

    private var isDirty: Boolean = false

    /** Called at the start of the frame */
    fun onStartFrame() {
        newHoverSet.clear()
        isDirty = false
    }

    /** Mark [obj] as hovered for this frame */
    fun hover(obj: SceneObject) {
        newHoverSet.add(obj)
        if (prevHoverSet.add(obj)) {
            isDirty = true
        }
    }

    /** Called at the end of the frame */
    fun onEndFrame() {
        if (prevHoverSet.retainAll(newHoverSet)) {
            isDirty = true
        }
        if (isDirty) app.setHoveredObjects(newHoverSet)
    }
}