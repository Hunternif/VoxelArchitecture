package hunternif.voxarch.editor

import org.joml.Vector3i

// This contains all actions that can be performed via UI.
// Some of them can support keyboard shortcuts, console commands, undo/redo.

// EditorApp must be injected into all classes that call these actions.

fun EditorApp.centerCamera() {
    scene.centerCamera()
}

fun EditorApp.createNode(start: Vector3i, end: Vector3i) {
    scene.createNode(start, end)
}