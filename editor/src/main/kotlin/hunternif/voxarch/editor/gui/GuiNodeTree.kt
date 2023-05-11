package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scenegraph.ISceneListener
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.pushStyleColor
import imgui.ImGui
import imgui.ImGuiListClipper
import imgui.callback.ImListClipperCallback
import imgui.flag.*
import imgui.flag.ImGuiCol.*
import org.lwjgl.glfw.GLFW.*
import java.util.LinkedList

class GuiNodeTree(
    app: EditorApp,
    gui: GuiBase
) : GuiSceneTree(app, gui) {
    override val root: SceneNode get() = app.state.rootNode
    override fun itemLabel(item: SceneObject): String {
        if (item is SceneNode) {
            var result = item.nodeClassName
            val type = item.node.tags.firstOrNull()
            if (!type.isNullOrEmpty()) result += " $type"
            if (item.blueprints.isNotEmpty()) result += " []"
            return result
        }
        return item.toString()
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
    override fun itemLabel(item: SceneObject): String =
        (item as? SceneVoxelGroup)?.label ?: item.toString()
}

abstract class GuiSceneTree(
    val app: EditorApp,
    private val gui: GuiBase,
) : ISceneListener {
    /** Used to detect click outside the tree, which resets selection */
    private var isAnyTreeNodeClicked = false
    private var isThisPanelClicked = false

    abstract val root: SceneObject

    abstract fun itemLabel(item: SceneObject): String

    /** List of displayed entries */
    private val list = ArrayList<TreeEntry>()
    private var isListDirty = false
    private lateinit var parentNode: SceneObject
    private val listClipperCallback = object : ImListClipperCallback() {
        override fun accept(index: Int) {
            renderItem(list[index])
        }
    }

    open fun onClick(item: SceneObject) {
        app.setSelectedObject(item)
    }

    open fun onShiftClick(item: SceneObject) {
        if (item in app.state.selectedObjects) app.unselectObject(item)
        else app.selectObject(item)
    }

    open fun onDoubleClick(item: SceneObject) {}

    fun initState() {
        app.state.sceneTree.addListener(this)
        parentNode = root
        markListDirty()
    }

    override fun onSceneChange() {
        markListDirty()
    }

    fun render() {
        isAnyTreeNodeClicked = false
        isThisPanelClicked = ImGui.isWindowFocused() && isThisWindowClicked()
        if (parentNode != app.state.parentNode) markListDirty()
        if (isListDirty) rebuildList()

        // CellPadding = 0 makes tree rows appear next to each other without breaks
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0f, 0f)
        if (ImGui.beginTable("node_tree_table", 2)) {
            ImGui.tableSetupColumn("visibility",
                ImGuiTableColumnFlags.WidthFixed, 20f)
            ImGui.tableSetupColumn("tree")
            ImGuiListClipper.forEach(list.size, listClipperCallback)
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

    private fun renderItem(item: TreeEntry) {
        val node = item.obj
        val isGenerated = node.isGenerated
        if (isGenerated) pushStyleColor(Text, Colors.generatedLabel)

        ImGui.tableNextRow()
        val i = ImGui.tableGetRowIndex()
        ImGui.tableNextColumn()
        // Selectable would make more sense, but its size & position is bugged.
        // Button maintains the size & pos well, regardless of font.

        gui.smallIconButton(item.visibleIconForImgui, transparent = true) {
            if (item.isHidden) app.showObject(node)
            else app.hideObject(node)
            markListDirty()
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
        val isParentNode = item.isParent && !isRootNode
        val isChildNode = item.isChild
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
        ImGui.alignTextToFramePadding()
        if (item.isHidden) {
            if (isGenerated) pushStyleColor(Text, Colors.generatedHiddenLabel)
            else pushStyleColor(Text, Colors.hiddenItemLabel)
        }

        // Create fake indents to make the tree work in the 2nd column.
        for (x in 1..item.depth) ImGui.indent()
        val open = ImGui.treeNodeEx(item.labelForImgui, flags)
        if (item.isOpen != open) {
            item.isOpen = open
            markListDirty()
        }
        for (x in 1..item.depth) ImGui.unindent()

        if (item.isHidden) ImGui.popStyleColor()
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

        contextMenu(memoStrWithIndex("node_tree_context_menu", i)) {
            menuItem("Select") {
                onShiftClick(node)
            }
            menuItem("Delete") {
                app.deleteObjects(listOf(node))
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

    private fun markListDirty() {
        isListDirty = true
    }

    private fun rebuildList() {
//        println("rebuildList")
        parentNode = app.state.parentNode
        val isParentRootNode = root === app.state.rootNode // root node is never highlighted
        // remember which entries were closed:
        val closedObjs = list.filter { !it.isOpen }.map { it.obj }.toSet()
        list.clear()
        // Recursively add items using Depth-First Search
        val queue = LinkedList<TreeEntry>()
        queue.add(
            TreeEntry(
                root, itemLabel(root),
                0, root !in closedObjs,
                root in app.state.hiddenObjects,
                root === parentNode && !isParentRootNode, false,
            )
        )
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            list.add(next)
            if (next.isOpen) {
                queue.addAll(0, next.obj.children.map {
                    TreeEntry(
                        it, itemLabel(it),
                        next.depth + 1, it !in closedObjs,
                        it in app.state.hiddenObjects,
                        it === parentNode,
                        next.isChild || next.isParent,
                    )
                })
            }
        }
        isListDirty = false
    }
}

/** Data about a single entry in the UI list */
private data class TreeEntry(
    val obj: SceneObject,
    val label: String,
    val depth: Int,
    var isOpen: Boolean,
    val isHidden: Boolean,
    /** Is this the current parent node? */
    val isParent: Boolean,
    /** Is this the child of the current parent node? */
    val isChild: Boolean,
) {
    val id = obj.id
    val labelForImgui ="$label##$id"
    val visibleIconForImgui = when {
        isHidden -> "${FontAwesomeIcons.EyeSlash}##$id"
        else -> "${FontAwesomeIcons.Eye}##$id"
    }
}