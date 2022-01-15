package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.createRoom
import hunternif.voxarch.editor.scene.NewNodeFrame.*
import imgui.ImGui

fun MainGui.topPanel() {
    app.scene.newNodeController.run {
        if (frame.state == State.COMPLETE) {
            button("Create room", "Create room from the 3d frame") {
                setState(State.EMPTY)
                app.createRoom(frame.start, frame.end)
            }
            ImGui.sameLine()
            button("Cancel") {
                setState(State.EMPTY)
            }
        }
    }
}