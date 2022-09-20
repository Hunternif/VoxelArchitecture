package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.pushStyleColor
import imgui.ImGui
import imgui.flag.*
import imgui.flag.ImGuiCol.*
import org.lwjgl.glfw.GLFW.*

class GuiNodeTree(
    app: EditorApp,
    gui: GuiBase
) : GuiSceneTree(app, gui) {
    override val root: SceneNode get() = app.state.rootNode
    override fun label(item: SceneObject): String {
        if (item is SceneNode) {
            var result = item.nodeClassName
            val type = item.node.tags.firstOrNull()
            if (!type.isNullOrEmpty()) result += " $type"
            if (item.blueprints.isNotEmpty()) result += " []"
            return result
        }
        return item.toString()
    }

    override fun onClick(item: SceneObject) {
        app.setSelectedObject(item)
    }

    override fun onShiftClick(item: SceneObject) {
        if (item in app.state.selectedObjects) app.unselectObject(item)
        else app.selectObject(item)
    }

    override fun onDoubleClick(item: SceneObject) {
        if (item is SceneNode) {
            app.setParentNode(item)
            app.centerCamera()
        }
    }
}

class GuiVoxelTree(
    app: EditorApp,
    gui: GuiBase
) : GuiSceneTree(app, gui) {
    override val root: SceneObject get() = app.state.voxelRoot
    override fun label(item: SceneObject): String = (item as? SceneVoxelGroup)?.label ?: item.toString()
    override fun onClick(item: SceneObject) {
        app.setSelectedObject(item)
    }

    override fun onShiftClick(item: SceneObject) {
        if (item in app.state.selectedObjects) app.unselectObject(item)
        else app.selectObject(item)
    }

    override fun onDoubleClick(item: SceneObject) {}
}

abstract class GuiSceneTree(
    val app: EditorApp,
    private val gui: GuiBase,
) {
    /** Used to detect click outside the tree, which resets selection */
    private var isAnyTreeNodeClicked = false
    private var isThisPanelClicked = false

    abstract val root: SceneObject
    abstract fun label(item: SceneObject): String
    abstract fun onClick(item: SceneObject)
    abstract fun onShiftClick(item: SceneObject)
    abstract fun onDoubleClick(item: SceneObject)

    fun render() {
        isAnyTreeNodeClicked = false
        isThisPanelClicked = ImGui.isWindowHovered() &&
            ImGui.isWindowFocused() &&
            !ImGui.isMouseDragging(ImGuiMouseButton.Left) &&
            ImGui.isMouseClicked(ImGuiMouseButton.Left)

        // CellPadding = 0 makes tree rows appear next to each other without breaks
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0f, 0f)
        if (ImGui.beginTable("node_tree_table", 2)) {
            ImGui.tableSetupColumn("visibility",
                ImGuiTableColumnFlags.WidthFixed, 20f)
            ImGui.tableSetupColumn("tree")
            addTreeNodeRecursive(root, 0, false)
            ImGui.endTable()
        }
        ImGui.popStyleVar(1)
        if (isThisPanelClicked && !isAnyTreeNodeClicked) {
            app.unselectAll()
        }

        if (ImGui.isWindowFocused() && ImGui.isKeyPressed(GLFW_KEY_DELETE, false)) {
            app.deleteSelectedObjects()
        }
    }

    private fun addTreeNodeRecursive(
        node: SceneObject,
        depth: Int,
        isChildNode: Boolean,
    ) {
        val isGenerated = node.isGenerated
        if (isGenerated) pushStyleColor(Text, Colors.generatedLabel)

        ImGui.tableNextRow()
        val i = ImGui.tableGetRowIndex()
        ImGui.tableNextColumn()
        // Selectable would make more sense, but its size & position is bugged.
        // Button maintains the size & pos well, regardless of font.

        val updatedHidden = node in app.state.hiddenObjects
        if (updatedHidden)
            gui.smallIconButton(
                memoStrWithIndex(FontAwesomeIcons.EyeSlash, i),
                transparent = true
            ) {
                app.showObject(node)
            }
        else
            gui.smallIconButton(
                memoStrWithIndex(FontAwesomeIcons.Eye, i),
                transparent = true
            ) {
                app.hideObject(node)
            }

        ImGui.tableNextColumn()
        var flags = 0 or
            ImGuiTreeNodeFlags.OpenOnArrow or
            ImGuiTreeNodeFlags.SpanFullWidth or
            ImGuiTreeNodeFlags.NoTreePushOnOpen or // we will be faking indents, no need to pop tree
            ImGuiTreeNodeFlags.DefaultOpen
        if (node.children.isEmpty()) {
            flags = flags or
                ImGuiTreeNodeFlags.Leaf or
                ImGuiTreeNodeFlags.Bullet
        }
        val isRootNode = node === app.state.rootNode // root node is never highlighted
        val isParentNode = node === app.state.parentNode && !isRootNode
        val isSelected = node in app.state.selectedObjects
        if (isSelected) {
            flags = flags or ImGuiTreeNodeFlags.Selected
        }
        if (isParentNode) {
            flags = flags or ImGuiTreeNodeFlags.Selected
            applyParentNodeColors(isSelected)
        } else if (isChildNode) {
            flags = flags or ImGuiTreeNodeFlags.Selected
            applyChileNodeColors(isSelected)
        }
        val text = memoStrWithIndex(label(node), node.id)
        ImGui.alignTextToFramePadding()
        if (updatedHidden) {
            if (isGenerated) pushStyleColor(Text, Colors.generatedHiddenLabel)
            else pushStyleColor(Text, Colors.hiddenItemLabel)
        }

        // Create fake indents to make the tree work in the 2nd column.
        for (x in 1..depth) ImGui.indent()
        val open = ImGui.treeNodeEx(text, flags)
        for (x in 1..depth) ImGui.unindent()

        if (updatedHidden) ImGui.popStyleColor()
        if (isParentNode || isChildNode) ImGui.popStyleColor(3)
        if (isGenerated) ImGui.popStyleColor()

        if (ImGui.isItemHovered()) {
            if (ImGui.isMouseClicked(0)) {
                if (ImGui.getIO().keyShift) onShiftClick(node)
                else onClick(node)
                isAnyTreeNodeClicked = true
            }
            if (ImGui.isMouseDoubleClicked(0)) {
                onDoubleClick(node)
                isAnyTreeNodeClicked = true
            }
        }

        if (open && node.children.isNotEmpty()) {
            node.children.forEach {
                addTreeNodeRecursive(it, depth + 1,
                    isParentNode || isChildNode
                )
            }
        }
    }

    private fun applyParentNodeColors(isSelected: Boolean) = Colors.run {
        val color = parentNode.let { if (isSelected) it.blend(headerBg) else it }
        pushStyleColor(Header, color)
        pushStyleColor(HeaderHovered, color.blend(headerHovered))
        pushStyleColor(HeaderActive, color.blend(headerActive))
    }

    private fun applyChileNodeColors(isSelected: Boolean) = Colors.run {
        val color = childNode.let { if (isSelected) it.blend(headerBg) else it }
        pushStyleColor(Header, color)
        pushStyleColor(HeaderHovered, color.blend(headerHovered))
        pushStyleColor(HeaderActive, color.blend(headerActive))
    }
}
