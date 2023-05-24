package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scenegraph.ISceneListener
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
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
    override fun initState() {
        root = app.state.rootNode
        super.initState()
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
    override fun initState() {
        root = app.state.voxelRoot
        super.initState()
    }
}

abstract class GuiSceneTree(
    val app: EditorApp,
    private val gui: GuiBase,
) : ISceneListener {
    /** Used to detect click outside the tree, which resets selection */
    private var isAnyTreeNodeClicked = false
    private var isThisPanelClicked = false

    lateinit var root: SceneObject

    /** List of displayed entries */
    private val list = ArrayList<TreeEntry>()
    private var isListDirty = false

    /** Maps object to its list entry */
    private val entryMap = mutableMapOf<SceneObject, TreeEntry>()

    /** Will scroll to this item on the next frame */
    private var nextScrollItem: SceneObject? = null

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

    open fun initState() {
        app.state.sceneTree.addListener(this)
        parentNode = root
        list.clear()
        entryMap.clear()
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
            if (nextScrollItem != null) {
                // Don't use clipper so that we render all items and scroll to the item
                list.forEach { renderItem(it) }
            } else {
                // Clipper avoids rendering items outside of view
                ImGuiListClipper.forEach(list.size, listClipperCallback)
            }
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
        item.refreshColor()
        val node = item.obj
        val isGenerated = node.isGenerated
        val isCustomColor = item.isCustomColor
        if (isGenerated) pushStyleColor(Text, Colors.generatedLabel)

        ImGui.tableNextRow()
        val i = ImGui.tableGetRowIndex()
        ImGui.tableNextColumn()
        // Selectable would make more sense, but its size & position is bugged.
        // Button maintains the size & pos well, regardless of font.

        if (isCustomColor) pushStyleColor(Button, item.color)
        gui.smallIconButton(item.visibleIconForImgui, transparent = !isCustomColor) {
            if (item.isHidden) app.showObject(node)
            else app.hideObject(node)
            markListDirty()
        }
        if (isCustomColor) ImGui.popStyleColor()

        ImGui.tableNextColumn()
        var flags = 0 or
            ImGuiTreeNodeFlags.OpenOnArrow or
            ImGuiTreeNodeFlags.SpanFullWidth or
            ImGuiTreeNodeFlags.NoTreePushOnOpen // we will be faking indents, no need to pop tree
        if (item.isOpen) {
            flags = flags or ImGuiTreeNodeFlags.DefaultOpen
        }
        if (node.children.isEmpty()) {
            flags = flags or
                ImGuiTreeNodeFlags.Leaf or
                ImGuiTreeNodeFlags.Bullet
        }
        val isRootNode = node === app.state.rootNode // root node is never highlighted
        val isParentNode = item.isParent && !isRootNode
        val isChildNode = item.isChild
        val isSelected = node in app.state.selectedObjects
        val isHighlighted = node in app.state.hoveredObjects
        val isColored = isHighlighted || isParentNode || isChildNode
        if (isSelected) {
            flags = flags or ImGuiTreeNodeFlags.Selected
        }
        if (isColored) {
            flags = flags or ImGuiTreeNodeFlags.Selected
            if (isHighlighted) applyHighlightColors()
            else if (isParentNode) applyParentNodeColors(isSelected)
            else if (isChildNode) applyChildNodeColors(isSelected)
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

        if (nextScrollItem == node) {
            ImGui.setScrollHereY()
            nextScrollItem = null
        }

        if (item.isHidden) ImGui.popStyleColor()
        if (isColored) ImGui.popStyleColor(3)
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
            app.hoverObject(node)
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

    private fun applyChildNodeColors(isSelected: Boolean) = Colors.run {
        val color = childNode.let { if (isSelected) it.blend(headerBg) else it }
        pushStyleColor(Header, color)
        pushStyleColor(HeaderHovered, color.blend(headerHovered))
        pushStyleColor(HeaderActive, color.blend(headerActive))
    }

    private fun applyHighlightColors() = Colors.run {
        pushStyleColor(Header, headerHovered)
        pushStyleColor(HeaderHovered, headerHovered)
        pushStyleColor(HeaderActive, headerActive)
    }

    /** The list will be re-built on the next frame */
    fun markListDirty() {
        isListDirty = true
    }

    /** If [nextScrollItem] is nonnull, will open its parents and set next
     * scroll position. */
    private fun rebuildList() {
//        println("rebuildList")
        parentNode = app.state.parentNode
        val isParentRootNode = root === app.state.rootNode // root node is never highlighted
        // remember which entries were opened:
        val openObjs =
            if (list.isEmpty()) mutableSetOf(root) // initially, open the root node
            else list.filter { it.isOpen }.map { it.obj }.toMutableSet()

        // Open nextScrollItem's parents:
        var scrollToParent = nextScrollItem?.parent
        while (scrollToParent != null) {
            openObjs.add(scrollToParent)
            scrollToParent = scrollToParent.parent
        }

        list.clear()
        entryMap.clear()
        // Recursively add items using Depth-First Search
        val queue = LinkedList<TreeEntry>()
        queue.add(
            TreeEntry(
                root, sceneObjectToSingleLine(root),
                0, root in openObjs,
                root in app.state.hiddenObjects,
                root === parentNode && !isParentRootNode, false,
            ).also { entry -> entryMap[root] = entry }
        )
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            list.add(next)
            if (next.isOpen) {
                queue.addAll(0, next.obj.children.map {
                    TreeEntry(
                        it, sceneObjectToSingleLine(it),
                        next.depth + 1, it in openObjs,
                        it in app.state.hiddenObjects,
                        it === parentNode,
                        next.isChild || next.isParent,
                    ).also { entry -> entryMap[it] = entry }
                })
            }
        }
        isListDirty = false
    }

    /**
     * Expand the tree leading to this object, and scroll to its position.
     */
    fun scrollToItem(obj: SceneObject) {
        nextScrollItem = obj
        markListDirty()
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
    val labelForImgui = "$label##$id"
    val visibleIconForImgui = when {
        isHidden -> "${FontAwesomeIcons.EyeSlash}##$id"
        else -> "${FontAwesomeIcons.Eye}##$id"
    }
    val isCustomColor
        get() = obj.color != Colors.defaultNodeBox
            && obj.color != Colors.defaultGeneratedNodeBox
            && obj.color != Colors.transparent
    val color = obj.color.copy()

    fun refreshColor() {
        color.set(obj.color).apply { a = 0.5f }
    }
}