package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.actions.clearNewNodeFrame
import hunternif.voxarch.editor.actions.createRoom
import hunternif.voxarch.editor.actions.deleteSelectedObjects
import hunternif.voxarch.editor.scene.NewNodeFrame.*
import imgui.ImGui

fun MainGui.topPanel() {
    if (app.state.selectedObjects.isNotEmpty()) {
        button("Delete", "Delete selected objects") {
            app.deleteSelectedObjects()
        }
    }
    app.state.newNodeFrame.run {
        if (state == State.COMPLETE) {
            button("Create room", "Create room from the 3d frame") {
                app.createRoom(start, end, fromCenter)
                app.clearNewNodeFrame()
            }
            ImGui.sameLine()
            button("Cancel", "Remove the 3d frame for new node") {
                app.clearNewNodeFrame()
            }
        }
    }
}