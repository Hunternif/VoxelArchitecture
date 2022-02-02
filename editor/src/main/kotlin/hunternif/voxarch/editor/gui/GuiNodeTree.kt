package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scene.INested
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.editor.util.pushStyleColor
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiTableColumnFlags
import imgui.flag.ImGuiTreeNodeFlags
import org.lwjgl.glfw.GLFW.*

class GuiNodeTree(
    app: EditorApp,
    gui: GuiBase
) : GuiSceneTree<SceneNode>(app, gui) {
    override val root: SceneNode get() = app.state.rootNode
    override fun label(item: SceneNode): String = item.node.javaClass.simpleName
    override fun onClick(item: SceneNode) { app.setSelectedObject(item) }
    override fun onDoubleClick(item: SceneNode) {
        app.setParentNode(item)
        app.centerCamera()
    }
}

class GuiVoxelTree(
    app: EditorApp,
    gui: GuiBase
) : GuiSceneTree<SceneVoxelGroup>(app, gui) {
    override val root: SceneVoxelGroup get() = app.state.voxelRoot
    override fun label(item: SceneVoxelGroup): String = item.label
    override fun onClick(item: SceneVoxelGroup) { app.setSelectedObject(item) }
    override fun onDoubleClick(item: SceneVoxelGroup) {}
}

abstract class GuiSceneTree<T: INested<T>>(
    val app: EditorApp,
    private val gui: GuiBase,
) {
    /** Used to detect click outside the tree, which resets selection */
    private var isAnyTreeNodeClicked = false
    var isThisPanelClicked = false

    abstract val root: T
    abstract fun label(item: T): String
    abstract fun onClick(item: T)
    abstract fun onDoubleClick(item: T)

    fun render() {
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
            addTreeNodeRecursive(root, 0, false)
            ImGui.endTable()
        }
        ImGui.popStyleVar(1)
        if (isThisPanelClicked && !isAnyTreeNodeClicked) {
            app.setSelectedObject(null)
        }

        if (ImGui.isWindowFocused() && ImGui.getIO().getKeysDown(GLFW_KEY_DELETE))
            app.deleteSelectedObjects()
    }

    private fun addTreeNodeRecursive(node: T, depth: Int, hidden: Boolean) {
        ImGui.tableNextRow()
        val i = ImGui.tableGetRowIndex()
        ImGui.tableNextColumn()
        // Selectable would make more sense, but its size & position is bugged.
        // Button maintains the size & pos well, regardless of font.

        var updatedHidden = false
        if (node is SceneObject) {
            updatedHidden = hidden || node in app.state.hiddenObjects
            if (updatedHidden)
                gui.smallIconButton(
                    "${FontAwesomeIcons.EyeSlash}##$i",
                    transparent = true
                ) {
                    app.showObject(node)
                }
            else
                gui.smallIconButton(
                    "${FontAwesomeIcons.Eye}##$i",
                    transparent = true
                ) {
                    app.hideObject(node)
                }
        }

        ImGui.tableNextColumn()
        var flags = 0 or
            ImGuiTreeNodeFlags.OpenOnArrow or
            ImGuiTreeNodeFlags.SpanAvailWidth or
            ImGuiTreeNodeFlags.NoTreePushOnOpen or // we will be faking indents, no need to pop tree
            ImGuiTreeNodeFlags.DefaultOpen
        if (node.children.isEmpty()) {
            flags = flags or
                ImGuiTreeNodeFlags.Leaf or
                ImGuiTreeNodeFlags.Bullet
        }
        val isParentNode = node == app.state.parentNode
        val isSelected = (node is SceneObject) && node in app.state.selectedObjects
        if (isSelected) {
            flags = flags or ImGuiTreeNodeFlags.Selected
        }
        if (isParentNode) {
            flags = flags or ImGuiTreeNodeFlags.Selected
            pushStyleColor(ImGuiCol.Header, Colors.accentBg)
            pushStyleColor(ImGuiCol.HeaderHovered, Colors.accentHovered)
            pushStyleColor(ImGuiCol.HeaderActive, Colors.accentActive)
        }
        val text = label(node)
        ImGui.alignTextToFramePadding()
        if (updatedHidden) pushStyleColor(ImGuiCol.Text, Colors.hiddenItemLabel)

        // Create fake indents to make the tree work in the 2nd column.
        for (x in 1..depth) ImGui.indent()
        val open = ImGui.treeNodeEx(text, flags)
        for (x in 1..depth) ImGui.unindent()

        if (updatedHidden) ImGui.popStyleColor()
        if (isParentNode) ImGui.popStyleColor(3)

        if (ImGui.isItemHovered()) {
            if (ImGui.isMouseClicked(0)) {
                onClick(node)
                isAnyTreeNodeClicked = true
            }
            if (ImGui.isMouseDoubleClicked(0)) {
                onDoubleClick(node)
                isAnyTreeNodeClicked = true
            }
        }

        if (open && node.children.isNotEmpty()) {
            node.children.forEach {
                addTreeNodeRecursive(it, depth + 1, updatedHidden)
            }
        }
    }
}
