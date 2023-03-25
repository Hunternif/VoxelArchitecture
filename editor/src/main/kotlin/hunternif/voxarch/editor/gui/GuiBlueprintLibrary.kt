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
            //TODO: new blueprint without node
//            app.newBlueprint(sceneNode)
        }
        ImGui.separator()

        ImGui.pushFont(gui.fontSmallIcons)
        if (ImGui.beginTable("blueprints_table", 2, ImGuiTableFlags.PadOuterX)) {
            ImGui.tableSetupColumn(
                "icon",
                ImGuiTableColumnFlags.WidthFixed, 10f
            )
            ImGui.tableSetupColumn("name")

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
            }
            ImGui.endTable()
        }
        ImGui.popFont()

        if (ImGui.isWindowFocused() && ImGui.isKeyPressed(GLFW.GLFW_KEY_DELETE, false)) {
            app.deleteSelectedBlueprint()
        }
    }
}