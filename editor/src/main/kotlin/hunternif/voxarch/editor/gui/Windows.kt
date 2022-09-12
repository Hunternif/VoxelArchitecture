package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.ImGuiWindowClass
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.internal.flag.ImGuiDockNodeFlags

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