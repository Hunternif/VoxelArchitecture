package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.actions.HistoryAction
import hunternif.voxarch.editor.util.pushStyleColor
import imgui.ImGui
import imgui.flag.*

fun MainGui.guiHistory() {
    // CellPadding = 0 makes rows appear next to each other without breaks
    ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0f, 0f)
    // Using a single font for icons and label keeps them aligned
    ImGui.pushFont(fontSmallIcons)
    if (ImGui.beginTable("history_table", 2)) {
        ImGui.tableSetupColumn("icon",
            ImGuiTableColumnFlags.WidthFixed, 19f)
        ImGui.tableSetupColumn("description")

        val lastItem = app.state.history.pastItems.lastOrNull()
        for (item in app.state.history.pastItems) {
            renderItem(item, isLast = item == lastItem)
        }

        ImGui.beginDisabled()
        pushStyleColor(ImGuiCol.Text, Colors.hiddenItemLabel) // extra dark
        for (item in app.state.history.futureItems) {
            renderItem(item)
        }
        ImGui.popStyleColor()
        ImGui.endDisabled()

        ImGui.endTable()
    }
    ImGui.popFont()
    ImGui.popStyleVar(1)
}

private fun renderItem(
    item: HistoryAction,
    isLast: Boolean = false,
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
    if (isLast) {
        // Highlight the last item to separate it from others.
        ImGui.selectable(item.description, true, ImGuiSelectableFlags.SpanAllColumns)
    } else {
        ImGui.text(item.description)
    }
}