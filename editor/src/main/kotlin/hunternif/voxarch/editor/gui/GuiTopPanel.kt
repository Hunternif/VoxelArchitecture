package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.clearNewNodeFrame
import hunternif.voxarch.editor.createRoom
import hunternif.voxarch.editor.deleteSelectedNodes
import hunternif.voxarch.editor.scene.NewNodeFrame.*
import imgui.ImGui

fun MainGui.topPanel() {
    if (app.state.selectedNodes.isNotEmpty()) {
        button("Delete", "Delete selected nodes") {
            app.deleteSelectedNodes()
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