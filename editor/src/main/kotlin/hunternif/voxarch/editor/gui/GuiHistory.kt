package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.HistoryAction
import hunternif.voxarch.editor.util.pushStyleColor
import imgui.ImGui
import imgui.flag.*

class GuiHistory(
    private val app: EditorApp,
    private val gui: GuiBase,
) {
    private var historyLength = 0

    fun render() {
        // CellPadding = 0 makes rows appear next to each other without breaks
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0f, 0f)
        // Using a single font for icons and label keeps them aligned
        ImGui.pushFont(gui.fontSmallIcons)
        if (ImGui.beginTable("history_table", 2)) {
            ImGui.tableSetupColumn(
                "icon",
                ImGuiTableColumnFlags.WidthFixed, 20f
            )
            ImGui.tableSetupColumn("description")

            val lastItem = app.state.history.pastItems.lastOrNull()
            val saved = app.state.lastSavedAction
            for (item in app.state.history.pastItems) {
                renderItem(
                    item,
                    isLast = item === lastItem,
                    isSaved = item === saved,
                )
            }

            disabled {
                pushStyleColor(ImGuiCol.Text, Colors.hiddenItemLabel) // extra dark
                for (item in app.state.history.futureItems) {
                    renderItem(item, isSaved = item === saved)
                }
                ImGui.popStyleColor()
            }

            ImGui.endTable()
        }
        ImGui.popFont()
        ImGui.popStyleVar(1)

        if (historyLength < app.state.history.pastItems.size ||
            ImGui.getScrollY() >= ImGui.getScrollMaxY()
        ) {
            ImGui.setScrollHereY(1f)
        }
        historyLength = app.state.history.pastItems.size
    }

    private fun renderItem(
        item: HistoryAction,
        isLast: Boolean = false,
        isSaved: Boolean = false,
    ) {
        ImGui.tableNextRow()

        // Column 1 is the icon
        // Using selectable is a hack, but it seems to align the best.
        ImGui.tableNextColumn()
        ImGui.alignTextToFramePadding()
        centeredText(item.icon)

        // Column 2 is the description
        ImGui.tableNextColumn()
        ImGui.alignTextToFramePadding()
        val description = if (isSaved) "${item.description} (saved)" else item.description
        if (isLast) {
            // Highlight the last item to separate it from others.
            ImGui.selectable(description, true, ImGuiSelectableFlags.SpanAllColumns)
        } else {
            ImGui.text(description)
        }
    }
}