package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.centerCamera
import hunternif.voxarch.editor.selectNode
import hunternif.voxarch.plan.Node
import imgui.ImGui
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTreeNodeFlags

fun MainGui.nodeTree() {
    // CellPadding = 0 makes tree rows appear next to each other without breaks
    ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0f, 0f)
    if (ImGui.beginTable("node_tree_table", 2)) {
        ImGui.tableSetupColumn("visibility",
            ImGuiTableColumnFlags.WidthFixed, 19f)
        ImGui.tableSetupColumn("tree")
        addTreeNodeRecursive(app.rootNode, 0)
        ImGui.endTable()
    }
    ImGui.popStyleVar(1)
}

private fun MainGui.addTreeNodeRecursive(node: Node, depth: Int) {
    ImGui.tableNextRow()
    ImGui.tableNextColumn()
    // Selectable would make more sense, but its size & position is bugged.
    // Button maintains the size & pos well, regardless of font.
    smallIconButton(FontAwesomeIcons.Eye, transparent = true)

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
    if (app.currentNode == node) {
        flags = flags or ImGuiTreeNodeFlags.Selected
    }
    val text = node.javaClass.simpleName
    ImGui.alignTextToFramePadding()

    // Create fake indents to make the tree work in the 2nd column.
    for (x in 1..depth) ImGui.indent()
    val open = ImGui.treeNodeEx(text, flags)
    for (x in 1..depth) ImGui.unindent()

    if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(0)) {
        app.selectNode(node)
        app.centerCamera()
    }

    if (open && node.children.isNotEmpty()) {
        // Immediately pop the tree because we are faking indents
        ImGui.treePop()
        node.children.forEach { addTreeNodeRecursive(it, depth + 1) }
    }

}