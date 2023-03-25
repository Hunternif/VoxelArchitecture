package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.*
import imgui.ImGui
import imgui.flag.ImGuiSelectableFlags
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTableFlags
import org.lwjgl.glfw.GLFW

class GuiBlueprintLibrary(
    private val app: EditorApp,
    private val gui: GuiBase,
) {
    fun render() {
        button("New blueprint...") {
            app.newBlueprint()
        }
        ImGui.separator()

        ImGui.pushFont(gui.fontSmallIcons)
        if (ImGui.beginTable("blueprints_table", 3, ImGuiTableFlags.PadOuterX)) {
            ImGui.tableSetupColumn("icon", ImGuiTableColumnFlags.WidthFixed, 10f)
            ImGui.tableSetupColumn("name")
            ImGui.tableSetupColumn("usage", ImGuiTableColumnFlags.WidthFixed, 55f)

            app.state.blueprints.forEachIndexed { i, bp ->
                ImGui.tableNextRow()
                ImGui.tableNextColumn()
                centeredText(FontAwesomeIcons.Code)

                ImGui.tableNextColumn()
                val name = memoStrWithIndex(bp.name, i)
                val selected = app.state.selectedBlueprint == bp
                val flags = ImGuiSelectableFlags.SpanAllColumns
                if (ImGui.selectable(name, selected, flags)) {
                    app.selectBlueprint(bp)
                }
                contextMenu(memoStrWithIndex("bp_lib_context_menu", i)) {
                    menuItem("Open") {
                        app.selectBlueprint(bp)
                    }
                    menuItem("Delete") {
                        app.deleteSelectedBlueprint()
                    }
                }

                ImGui.tableNextColumn()
                val usages = app.state.blueprintUsage[bp].size
                if (usages > 0) {
                    disabled {
                        ImGui.text(usages.toString())
                        ImGui.sameLine()
                        ImGui.text("usages")
                    }
                }
            }
            ImGui.endTable()
        }
        ImGui.popFont()

        if (ImGui.isWindowFocused() && ImGui.isKeyPressed(GLFW.GLFW_KEY_DELETE, false)) {
            app.deleteSelectedBlueprint()
        }
    }
}