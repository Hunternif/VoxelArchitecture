package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.ImGuiWindowClass
import imgui.ImVec2
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.internal.flag.ImGuiDockNodeFlags

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

inline fun menuItem(label: String, crossinline onOpen: () -> Unit) {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10f, 10f)
    if (ImGui.menuItem(label)) { onOpen() }
    ImGui.popStyleVar()
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