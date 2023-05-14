package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.ImGuiWindowClass
import imgui.ImVec2
import imgui.flag.*
import imgui.internal.flag.ImGuiDockNodeFlags

inline fun contextMenu(
    name: String = "",
    padding: Float = 8f,
    crossinline content: () -> Unit,
) {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, padding, padding)
    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4f, 4f)
    ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 4f, 2f)
    ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 8f, 4f)
    if (ImGui.beginPopupContextItem(name)) {
        content()
        ImGui.endPopup()
    }
    ImGui.popStyleVar(4)
}

inline fun popup(
    name: String,
    padding: Float = 10f,
    crossinline content: () -> Unit,
) {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, padding, padding)
    if (ImGui.beginPopup(name)) {
        content()
        ImGui.endPopup()
    }
    ImGui.popStyleVar()
}

inline fun listbox(label: String, crossinline content: () -> Unit) {
    if (ImGui.beginListBox(label)) {
        content()
        ImGui.endListBox()
    }
}

inline fun selectable(
    label: String, selected: Boolean = false,
    spanAllColumns: Boolean = false,
    crossinline onClick: () -> Unit
) {
    val flags = if (spanAllColumns) ImGuiSelectableFlags.SpanAllColumns else 0
    if (ImGui.selectable(label, selected, flags)) onClick()
}

inline fun toolbar(name: String, crossinline renderWindow: () -> Unit = {}) {
    ImGui.setNextWindowClass(
        ImGuiWindowClass().apply {
            dockNodeFlagsOverrideSet = 0 or
                ImGuiDockNodeFlags.NoTabBar or
                ImGuiDockNodeFlags.NoDocking or
                ImGuiDockNodeFlags.NoDockingSplitMe
        }
    )
    val flags = 0 or
        ImGuiWindowFlags.NoTitleBar or
        ImGuiWindowFlags.NoCollapse or
        ImGuiWindowFlags.NoMove
    if (ImGui.begin(name, flags)) {
        renderWindow()
    }
    ImGui.end()
}

inline fun childWindow(
    name: String,
    paddingBottom: Float = 0f,
    crossinline render: () -> Unit
) {
    ImGui.beginChild(name, 0f, -paddingBottom, false)
    render()
    ImGui.endChild()
}

inline fun thinChildToolbar(name: String, crossinline render: () -> Unit = {}) {
    ImGui.beginChild(name, 0f, 0f, false)
    render()
    ImGui.endChild()
}

inline fun childToolbar(name: String, crossinline render: () -> Unit = {}) {
    ImGui.pushStyleColor(ImGuiCol.Border, 0, 0, 0, 0)
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 4f, 4f)
    ImGui.beginChild(name, 0f, 0f, true)
    render()
    ImGui.endChild()
    ImGui.popStyleVar()
    ImGui.popStyleColor()
}

val toolbarHeight get() = ImGui.getFrameHeightWithSpacing() +
    /* ImGui.getStyle().windowPaddingY = */ 4f * 2

val thinToolbarHeight get() = ImGui.getFrameHeightWithSpacing()

inline fun disabled(disabledIf: Boolean = true, crossinline block: () -> Unit) {
    if (disabledIf) ImGui.beginDisabled()
    block()
    if (disabledIf) ImGui.endDisabled()
}

inline fun tabBar(name: String, crossinline block: () -> Unit) {
    if (ImGui.beginTabBar(name)) {
        block()
        ImGui.endTabBar()
    }
}

inline fun tabItem(label: String, flags: Int = 0, crossinline block: () -> Unit) {
    if (ImGui.beginTabItem(label, flags)) {
        block()
        ImGui.endTabItem()
    }
}

/** Same as [tabItem] but also adds a child window inside. */
inline fun tabItemWindow(label: String, flags: Int = 0, crossinline block: () -> Unit) {
    ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 0f)
    tabItem(label, flags) {
        ImGui.popStyleVar(1)
        childWindow("tab_item_window") { block() }
    }
}

inline fun collapsingHeader(label: String, crossinline block: () -> Unit = {}) {
    if (ImGui.collapsingHeader(label)) block()
}

enum class Align {
    LEFT,
    RIGHT,
}

fun text(str: String, align: Align = Align.LEFT, maxWidth: Float? = null) {
    when (align) {
        Align.LEFT -> ImGui.text(str)
        Align.RIGHT -> {
            val min = ImGui.getWindowContentRegionMin()
            val max = ImGui.getWindowContentRegionMax()
            val regionWidth = maxWidth ?: (max.x - min.x)
            val size = calcTextSize(str)
            if (regionWidth > size.x)
                ImGui.indent(regionWidth - size.x)
            ImGui.text(str)
        }
    }
}

private val sizeBuf = ImVec2()
fun calcTextSize(str: String): ImVec2 {
    ImGui.calcTextSize(sizeBuf, str)
    return sizeBuf
}

fun isThisWindowClicked(): Boolean = ImGui.isWindowHovered() &&
    !ImGui.isMouseDragging(ImGuiMouseButton.Left) &&
    ImGui.isMouseClicked(ImGuiMouseButton.Left)

/** Display a non-empty tooltip if the last element is hovered */
fun tooltip(msg: String?) {
    if (msg != null && msg.isNotEmpty() && ImGui.isItemHovered()) {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 6f, 6f)
        ImGui.setTooltip(msg)
        ImGui.popStyleVar()
    }
}

/** Sets width for the next element */
inline fun withWidth(
    width: Float,
    crossinline block: () -> Unit,
) {
    ImGui.pushItemWidth(width)
    block()
    ImGui.popItemWidth()
}

/** Sets 100% width for the next element */
inline fun withMaxWidth(
    crossinline block: () -> Unit,
) {
    val width = ImGui.getWindowContentRegionMaxX() - ImGui.getWindowContentRegionMinX()
    withWidth(width, block)
}