package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.pushStyleColor
import hunternif.voxarch.plan.Node
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTreeNodeFlags

private val hiddenTextColor = ColorRGBa.fromHex(0xffffff, 0.6f)

fun MainGui.nodeTree() {
    // CellPadding = 0 makes tree rows appear next to each other without breaks
    ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0f, 0f)
    if (ImGui.beginTable("node_tree_table", 2)) {
        ImGui.tableSetupColumn("visibility",
            ImGuiTableColumnFlags.WidthFixed, 19f)
        ImGui.tableSetupColumn("tree")
        addTreeNodeRecursive(app.rootNode, 0, false)
        ImGui.endTable()
    }
    ImGui.popStyleVar(1)
}

private fun MainGui.addTreeNodeRecursive(node: Node, depth: Int, hidden: Boolean) {
    ImGui.tableNextRow()
    val i = ImGui.tableGetRowIndex()
    ImGui.tableNextColumn()
    // Selectable would make more sense, but its size & position is bugged.
    // Button maintains the size & pos well, regardless of font.
    val updatedHidden = hidden || app.isNodeHidden(node)
    if (updatedHidden)
        smallIconButton("${FontAwesomeIcons.EyeSlash}##$i", transparent = true) {
            app.showNode(node)
        }
    else
        smallIconButton("${FontAwesomeIcons.Eye}##$i", transparent = true) {
            app.hideNode(node)
        }

    ImGui.tableNextColumn()
    var flags = 0 or
        ImGuiTreeNodeFlags.OpenOnArrow or
        ImGuiTreeNodeFlags.SpanAvailWidth or
        ImGuiTreeNodeFlags.DefaultOpen
    if (node.children.isEmpty()) {
        flags = flags or
            ImGuiTreeNodeFlags.Leaf or
            ImGuiTreeNodeFlags.Bullet or
            ImGuiTreeNodeFlags.NoTreePushOnOpen
    }
    val isParentNode = node == app.parentNode
    val isSelected = app.selectedNodes.contains(node)
    if (isSelected) {
        flags = flags or ImGuiTreeNodeFlags.Selected
    }
    if (isParentNode) {
        flags = flags or ImGuiTreeNodeFlags.Selected
        pushStyleColor(ImGuiCol.Header, accentColorBg)
        pushStyleColor(ImGuiCol.HeaderHovered, accentColorHovered)
        pushStyleColor(ImGuiCol.HeaderActive, accentColorActive)
    }
    val text = node.javaClass.simpleName
    ImGui.alignTextToFramePadding()
    if (updatedHidden) pushStyleColor(ImGuiCol.Text, hiddenTextColor)



    // Create fake indents to make the tree work in the 2nd column.
    for (x in 1..depth) ImGui.indent()
    val open = ImGui.treeNodeEx(text, flags)
    for (x in 1..depth) ImGui.unindent()

    if (updatedHidden) ImGui.popStyleColor()
    if (isParentNode) ImGui.popStyleColor(3)

    if (ImGui.isItemHovered()) {
        if (ImGui.isMouseClicked(0)) {
            app.setSelectedNode(node)
        }
        if (ImGui.isMouseDoubleClicked(0)) {
            app.setParentNode(node)
            app.centerCamera()
        }
    }

    if (open && node.children.isNotEmpty()) {
        // Immediately pop the tree because we are faking indents
        ImGui.treePop()
        node.children.forEach {
            addTreeNodeRecursive(it, depth + 1, updatedHidden)
        }
    }

}