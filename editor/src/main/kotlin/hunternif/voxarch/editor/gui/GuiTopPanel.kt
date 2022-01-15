package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.createRoom
import hunternif.voxarch.editor.deleteSelectedNodes
import hunternif.voxarch.editor.scene.NewNodeFrame.*
import imgui.ImGui

fun MainGui.topPanel() {
    if (app.selectedNodes.isNotEmpty()) {
        button("Delete", "Delete selected nodes") {
            app.deleteSelectedNodes()
        }
    }
    app.scene.newNodeController.run {
        if (frame.state == State.COMPLETE) {
            button("Create room", "Create room from the 3d frame") {
                setState(State.EMPTY)
                app.createRoom(frame.start, frame.end)
            }
            ImGui.sameLine()
            button("Cancel", "Remove the 3d frame for new node") {
                setState(State.EMPTY)
            }
        }
    }
}