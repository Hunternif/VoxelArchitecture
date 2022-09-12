package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.actions.*
import imgui.ImGui
import imgui.flag.ImGuiStyleVar

fun MainGui.mainMenu() {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
    if (ImGui.beginMainMenuBar()) {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New project", "Ctrl+N")) {
                app.newProject()
            }
            if (ImGui.menuItem("Open project", "Ctrl+O")) {
                app.openDialogOpenProjectFile()
            }
            disabled(app.state.projectPath == null) {
                if (ImGui.menuItem("Save project", "Ctrl+S")) {
                    app.saveProjectFile()
                }
            }
            if (ImGui.menuItem("Save project as...", "Ctrl+Shift+S")) {
                app.openDialogSaveProjectFile()
            }
            ImGui.separator()
            if (ImGui.menuItem("Import VOX file...", "Ctrl+I")) {
                app.openDialogImportVoxFile()
            }
            if (ImGui.menuItem("Export as VOX file...")) {
                app.openDialogExportVoxFile()
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
