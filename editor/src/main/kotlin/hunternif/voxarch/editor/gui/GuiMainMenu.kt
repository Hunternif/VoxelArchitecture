package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.actions.*
import imgui.ImGui
import imgui.flag.ImGuiStyleVar

fun MainGui.mainMenu() {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
    if (ImGui.beginMainMenuBar()) {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Open project", "Ctrl+O")) {
                app.openDialogOpenProjectFile()
            }
            if (ImGui.menuItem("Save project", "Ctrl+S")) {
                app.openDialogSaveProjectFile()
            }
            ImGui.separator()
            if (ImGui.menuItem("Import VOX file...", "Ctrl+I")) {
                app.openDialogImportVoxFile()
            }
            ImGui.endMenu()
        }
        if (ImGui.beginMenu("Edit")) {
            if (ImGui.menuItem(
                    "Undo",
                    "Ctrl+Z",
                    false,
                    app.state.history.hasPastItems()
                )) app.undo()
            if (ImGui.menuItem(
                    "Redo",
                    "Ctrl+Shift+Z",
                    false,
                    app.state.history.hasFutureItems()
                )) app.redo()
            ImGui.endMenu()
        }
        ImGui.endMainMenuBar()
    }
    ImGui.popStyleVar(1)
}
