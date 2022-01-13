package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.centerCamera
import hunternif.voxarch.editor.selectNode
import hunternif.voxarch.plan.Node
import imgui.ImGui
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiTreeNodeFlags

fun MainGui.nodeTree() {
    ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 0f)
    addTreeNodeRecursive(app.rootNode)
    ImGui.popStyleVar(1)
}

private fun MainGui.addTreeNodeRecursive(node: Node) {
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
    if (ImGui.treeNodeEx(text, flags)) {
        if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(0)) {
            app.selectNode(node)
            app.centerCamera()
        }
        if (node.children.isNotEmpty()) {
            node.children.forEach { addTreeNodeRecursive(it) }
            ImGui.treePop()
        }
    }
}