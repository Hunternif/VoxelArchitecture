package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.flag.ImGuiDir
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt
import imgui.internal.ImGui as DockImGui

/** There is a single dockspace per application, and it's initialized during
 * the first render. */
private var dockSpaceInitialized = false

// The layout is a pseudo-DSL that looks like this:
//
// horizontalSplit(
//    rightRatio=0.5f,
//    left = horizontalSplit(
//        leftSize=32,
//        left = window("left panel name"),
//        right = verticalSplit(
//            bottomSize=32,
//            top = window("main window name"),
//            bottom = window("bottom panel name"),
//        ),
//    ),
//    right = window("right panel name"),
//)
//

interface DockspaceLayoutBuilder {
    fun build(dockNodeID: Int)
}

/** Container for the whole layout. */
class DockLayout(
    private val root: DockspaceLayoutBuilder,
): DockspaceLayoutBuilder {
    internal var isBuilt = false
    override fun build(dockNodeID: Int) {
        root.build(dockNodeID)
        isBuilt = true
    }
}

class HorizontalSplit(
    private val leftSize: Int? = null,
    private val rightSize: Int? = null,
    private val leftRatio: Float? = null,
    private val rightRatio: Float? = null,
    private val left: DockspaceLayoutBuilder,
    private val right: DockspaceLayoutBuilder,
) : DockspaceLayoutBuilder {
    override fun build(dockNodeID: Int) {
        val leftId = ImInt(0)
        val rightId = ImInt(0)
        if (leftRatio != null || leftSize != null) {
            val ratio = leftRatio ?: 0.5f
            DockImGui.dockBuilderSplitNode(
                dockNodeID, ImGuiDir.Left, ratio, leftId, rightId)
        } else {
            val ratio = rightRatio ?: 0.5f
            DockImGui.dockBuilderSplitNode(
                dockNodeID, ImGuiDir.Right, ratio, rightId, leftId)
        }
        leftSize?.let {
            DockImGui.dockBuilderSetNodeSize(leftId.get(), it.toFloat(), 1f)
        }
        left.build(leftId.get())
        rightSize?.let {
            DockImGui.dockBuilderSetNodeSize(rightId.get(), it.toFloat(), 1f)
        }
        right.build(rightId.get())
    }
}

class VerticalSplit(
    private val topSize: Int? = null,
    private val bottomSize: Int? = null,
    private val topRatio: Float? = null,
    private val bottomRatio: Float? = null,
    private val top: DockspaceLayoutBuilder,
    private val bottom: DockspaceLayoutBuilder,
) : DockspaceLayoutBuilder {
    override fun build(dockNodeID: Int) {
        val topId = ImInt(0)
        val bottomId = ImInt(0)
        if (topRatio != null || topSize != null) {
            val ratio = topRatio ?: 0.5f
            DockImGui.dockBuilderSplitNode(
                dockNodeID, ImGuiDir.Up, ratio, topId, bottomId)
        } else {
            val ratio = bottomRatio ?: 0.5f
            DockImGui.dockBuilderSplitNode(
                dockNodeID, ImGuiDir.Down, ratio, bottomId, topId)
        }
        topSize?.let {
            DockImGui.dockBuilderSetNodeSize(topId.get(), 1f, it.toFloat())
        }
        top.build(topId.get())
        bottomSize?.let {
            DockImGui.dockBuilderSetNodeSize(bottomId.get(), 1f, it.toFloat())
        }
        bottom.build(bottomId.get())
    }
}

class Window(val name: String): DockspaceLayoutBuilder {
    override fun build(dockNodeID: Int) {
        DockImGui.dockBuilderDockWindow(name, dockNodeID)
    }
}

class WindowGroup(private vararg val windows: Window): DockspaceLayoutBuilder {
    override fun build(dockNodeID: Int) {
        windows.forEach {
            DockImGui.dockBuilderDockWindow(it.name, dockNodeID)
        }
    }
}

fun dockspace(
    layout: DockLayout,
    marginBottom: Float = 0f,
) {
    val dockspaceId = ImGui.getID("MyDockSpace")
    val vp = ImGui.getMainViewport()
    vp.setWorkSize(vp.workSizeX, vp.workSizeY - marginBottom)
    ImGui.setNextWindowPos(vp.workPosX, vp.workPosY)
    ImGui.setNextWindowSize(vp.workSizeX, vp.workSizeY)
    ImGui.setNextWindowViewport(vp.id)
    ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)
    ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
    val windowFlags = 0 or
        ImGuiWindowFlags.NoDocking or
        ImGuiWindowFlags.NoTitleBar or
        ImGuiWindowFlags.NoCollapse or
        ImGuiWindowFlags.NoResize or
        ImGuiWindowFlags.NoMove or
        ImGuiWindowFlags.NoBringToFrontOnFocus or
        ImGuiWindowFlags.NoNavFocus
    val dockFlags = 0 or
        ImGuiDockNodeFlags.NoWindowMenuButton // hide button to hide tab bar
    ImGui.begin("dockspace", windowFlags)
    ImGui.dockSpace(dockspaceId, vp.workSizeX, vp.workSizeY, dockFlags)
    if (!dockSpaceInitialized) {
        dockSpaceInitialized = true
        DockImGui.dockBuilderRemoveNode(dockspaceId)
        DockImGui.dockBuilderAddNode(dockspaceId, ImGuiDockNodeFlags.DockSpace)
        DockImGui.dockBuilderSetNodeSize(dockspaceId, vp.workSizeX, vp.workSizeY)
        DockImGui.dockBuilderFinish(dockspaceId)
    }
    if (!layout.isBuilt) layout.build(dockspaceId)
    ImGui.end()
    ImGui.popStyleVar(3)
}