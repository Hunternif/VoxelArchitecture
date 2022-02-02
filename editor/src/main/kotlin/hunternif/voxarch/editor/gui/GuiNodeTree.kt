package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.pushStyleColor
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTreeNodeFlags
import org.lwjgl.glfw.GLFW.*

private val hiddenTextColor = ColorRGBa.fromHex(0xffffff, 0.6f)

/** Used to detect click outside the tree, which resets selection */
private var isAnyTreeNodeClicked = false
var isThisPanelClicked = false

fun MainGui.nodeTree() {
    isAnyTreeNodeClicked = false
    isThisPanelClicked = isMouseHoveringCurrentWindow() &&
        ImGui.isWindowFocused() &&
        ImGui.isMouseClicked(0)

    // CellPadding = 0 makes tree rows appear next to each other without breaks
    ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0f, 0f)
    if (ImGui.beginTable("node_tree_table", 2)) {
        ImGui.tableSetupColumn("visibility",
            ImGuiTableColumnFlags.WidthFixed, 19f)
        ImGui.tableSetupColumn("tree")
        addTreeNodeRecursive(app.state.rootNode, 0, false)
        ImGui.endTable()
    }
    ImGui.popStyleVar(1)
    if (isThisPanelClicked && !isAnyTreeNodeClicked) {
        app.setSelectedObject(null)
    }

    if (ImGui.isWindowFocused() && ImGui.getIO().getKeysDown(GLFW_KEY_DELETE))
        app.deleteSelectedObjects()
}

private fun MainGui.addTreeNodeRecursive(node: SceneNode, depth: Int, hidden: Boolean) {
    ImGui.tableNextRow()
    val i = ImGui.tableGetRowIndex()
    ImGui.tableNextColumn()
    // Selectable would make more sense, but its size & position is bugged.
    // Button maintains the size & pos well, regardless of font.
    val updatedHidden = hidden || node in app.state.hiddenObjects
    if (updatedHidden)
        smallIconButton("${FontAwesomeIcons.EyeSlash}##$i", transparent = true) {
            app.showObject(node)
        }
    else
        smallIconButton("${FontAwesomeIcons.Eye}##$i", transparent = true) {
            app.hideObject(node)
        }

    ImGui.tableNextColumn()
    var flags = 0 or
        ImGuiTreeNodeFlags.OpenOnArrow or
        ImGuiTreeNodeFlags.SpanAvailWidth or
        ImGuiTreeNodeFlags.NoTreePushOnOpen or // we will be faking indents, no need to pop tree
        ImGuiTreeNodeFlags.DefaultOpen
    if (node.node.children.isEmpty()) {
        flags = flags or
            ImGuiTreeNodeFlags.Leaf or
            ImGuiTreeNodeFlags.Bullet
    }
    val isParentNode = node == app.state.parentNode
    val isSelected = node in app.state.selectedObjects
    if (isSelected) {
        flags = flags or ImGuiTreeNodeFlags.Selected
    }
    if (isParentNode) {
        flags = flags or ImGuiTreeNodeFlags.Selected
        pushStyleColor(ImGuiCol.Header, Colors.accentBg)
        pushStyleColor(ImGuiCol.HeaderHovered, Colors.accentHovered)
        pushStyleColor(ImGuiCol.HeaderActive, Colors.accentActive)
    }
    val text = node.node.javaClass.simpleName
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
            app.setSelectedObject(node)
            isAnyTreeNodeClicked = true
        }
        if (ImGui.isMouseDoubleClicked(0)) {
            app.setParentNode(node)
            app.centerCamera()
            isAnyTreeNodeClicked = true
        }
    }

    if (open && node.node.children.isNotEmpty()) {
        node.children.forEach {
            addTreeNodeRecursive(it, depth + 1, updatedHidden)
        }
    }

}