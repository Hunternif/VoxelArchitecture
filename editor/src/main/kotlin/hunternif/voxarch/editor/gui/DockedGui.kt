package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.render.Viewport
import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiDir
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt
import org.lwjgl.glfw.GLFW
import imgui.internal.ImGui as DockImGui

class DockedGui {
    val vp = Viewport(0, 0, 0, 0)
    val imGuiGlfw = ImGuiImplGlfw()
    val imGuiGl3 = ImGuiImplGl3()
    var firstTime = true

    fun init(windowHandle: Long, viewport: Viewport) {
        setViewport(viewport)
        ImGui.createContext()
        val io = ImGui.getIO()
        io.configFlags = io.configFlags or ImGuiConfigFlags.DockingEnable
        io.iniFilename = null // This prevents "imgui.ini" from saving
        imGuiGlfw.init(windowHandle, true)
        imGuiGl3.init("#version 130")
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0)
    }

    fun setViewport(viewport: Viewport) {
        vp.set(viewport)
    }

    inline fun render(
        textureId: Int,
        crossinline renderMainWindow: (Viewport) -> Unit
    ) {
        imGuiGlfw.newFrame()
        ImGui.newFrame()

        horizontalDockspace(0.20f, "docked left", "docked right")

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        val mainWindowFlags = 0 or
            ImGuiWindowFlags.NoMove or
            ImGuiWindowFlags.NoScrollbar
        if (ImGui.begin("docked left", mainWindowFlags)) {
            ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY())
            val pos = ImGui.getWindowPos()
            val vMin = ImGui.getWindowContentRegionMin()
            val vMax = ImGui.getWindowContentRegionMax()
            vp.set(
                vMin.x + pos.x,
                vMin.y + pos.y,
                vMax.x - vMin.x,
                vMax.y - vMin.y
            )
            renderMainWindow(vp)
            ImGui.image(textureId, vMax.x - vMin.x, vMax.y - vMin.y, 0f, 1f, 1f, 0f)
        }
        ImGui.end()
        ImGui.popStyleVar(3)
        if (ImGui.begin("docked right", ImGuiWindowFlags.NoMove)) {
            ImGui.text("Node tree explorer goes here")
        }
        ImGui.end()

        ImGui.render()
        imGuiGl3.renderDrawData(ImGui.getDrawData())
    }

    @PublishedApi
    internal inline fun horizontalDockspace(
        rightWindowRatio: Float,
        leftWindow: String,
        rightWindow: String
    ) {
        val dockspaceId = ImGui.getID("MyDockSpace")
        val vp = ImGui.getMainViewport()
        ImGui.setNextWindowPos(vp.workPosX, vp.workPosY)
        ImGui.setNextWindowSize(vp.workSizeX, vp.workSizeY)
        ImGui.setNextWindowViewport(vp.id)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        val flags = 0 or
            ImGuiWindowFlags.NoDocking or
            ImGuiWindowFlags.NoTitleBar or
            ImGuiWindowFlags.NoCollapse or
            ImGuiWindowFlags.NoResize or
            ImGuiWindowFlags.NoMove or
            ImGuiWindowFlags.NoBringToFrontOnFocus or
            ImGuiWindowFlags.NoNavFocus
        ImGui.begin("dockspace", flags)
        ImGui.dockSpace(dockspaceId)
        if (firstTime) {
            firstTime = false

            DockImGui.dockBuilderRemoveNode(dockspaceId)
            DockImGui.dockBuilderAddNode(dockspaceId, ImGuiDockNodeFlags.DockSpace)
            DockImGui.dockBuilderSetNodeSize(dockspaceId, vp.workSizeX, vp.workSizeY)

            val leftId = ImInt(0)
            val rightId = ImInt(0)
            DockImGui.dockBuilderSplitNode(
                dockspaceId, ImGuiDir.Right, rightWindowRatio, rightId, leftId
            )
            DockImGui.dockBuilderDockWindow(leftWindow, leftId.get())
            DockImGui.dockBuilderDockWindow(rightWindow, rightId.get())

            DockImGui.dockBuilderFinish(dockspaceId)
        }
        ImGui.end()
        ImGui.popStyleVar(3)
    }
}