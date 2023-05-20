package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.blueprint.Blueprint
import imgui.ImGui
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTableFlags
import org.lwjgl.glfw.GLFW

class GuiBlueprintLibrary(
    private val app: EditorApp,
    private val gui: GuiBase,
) {
    private val nameInput = GuiInputTextAutoClose("##bp_name")
    /** Bp whose name is being edited */
    private var editingNameBp: Blueprint? = null

    fun render() {
        button("New blueprint...") {
            app.newBlueprint()
        }
        ImGui.separator()

        var toDelete: Blueprint? = null
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
                if (bp == editingNameBp) {
                    withMaxWidth {
                        nameInput.render(bp.name,
                            onCancel = { editingNameBp = null },
                            onSubmit = {
                                app.renameBlueprint(bp, it)
                                editingNameBp = null
                            }
                        )
                    }
                } else {
                    val name = memoStrWithIndex(bp.name, i)
                    val isSelected = app.state.selectedBlueprint == bp
                    selectable(name, isSelected, true) {
                        app.selectBlueprint(bp)
                    }
                }
                contextMenu(memoStrWithIndex("bp_lib_context_menu", i)) {
                    menuItem("Open") {
                        app.selectBlueprint(bp)
                    }
                    menuItem("Rename") {
                        editingNameBp = bp
                    }
                    menuItem("Delete") {
                        // prevent concurrent modification:
                        toDelete = bp
                    }
                }

                ImGui.tableNextColumn()
                val usages = app.state.blueprintLibrary.usage(bp).nodes.size
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

        // This prevents imgui stack errors
        toDelete?.let { app.deleteBlueprint(it) }
        if (ImGui.isWindowFocused() && ImGui.isKeyPressed(GLFW.GLFW_KEY_DELETE, false)) {
            app.deleteSelectedBlueprint()
        }
    }
}