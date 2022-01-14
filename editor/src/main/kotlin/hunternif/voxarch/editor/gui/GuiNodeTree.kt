package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.centerCamera
import hunternif.voxarch.editor.selectNode
import hunternif.voxarch.plan.Node
import imgui.ImGui
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTreeNodeFlags

fun MainGui.nodeTree() {
    ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0f, 0f)
    if (ImGui.beginTable("node_tree_table", 2)) {
        ImGui.tableSetupColumn("tree")
        ImGui.tableSetupColumn("visibility",
            ImGuiTableColumnFlags.WidthFixed, 20f)
        addTreeNodeRecursive(app.rootNode)
        ImGui.endTable()
    }
    ImGui.popStyleVar(1)
}

private fun MainGui.addTreeNodeRecursive(node: Node) {
    ImGui.tableNextRow()
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
    val open = ImGui.treeNodeEx(text, flags)
    if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(0)) {
        app.selectNode(node)
        app.centerCamera()
    }

    ImGui.tableNextColumn()
    ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 6f)
    ImGui.pushStyleVar(ImGuiStyleVar.SelectableTextAlign, 0.5f, 0.5f)
    ImGui.alignTextToFramePadding()
    ImGui.selectable(FontAwesomeIcons.Eye)
    ImGui.popStyleVar(2)

    if (open && node.children.isNotEmpty()) {
        node.children.forEach { addTreeNodeRecursive(it) }
        ImGui.treePop()
    }

}