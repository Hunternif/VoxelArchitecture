package hunternif.voxarch.editor.gui

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.glfw.GLFW

abstract class GuiBase {
    @PublishedApi internal val imGuiGlfw = ImGuiImplGlfw()
    @PublishedApi internal val imGuiGl3 = ImGuiImplGl3()

    fun init(windowHandle: Long) {
        ImGui.createContext()
        val io = ImGui.getIO()
        io.configFlags = io.configFlags or ImGuiConfigFlags.DockingEnable
        io.iniFilename = null // This prevents "imgui.ini" from saving
        imGuiGlfw.init(windowHandle, true)
        imGuiGl3.init("#version 130")
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0)
    }

    inline fun runFrame(
        crossinline renderUI: () -> Unit
    ) {
        imGuiGlfw.newFrame()
        ImGui.newFrame()
        renderUI()
        ImGui.render()
        imGuiGl3.renderDrawData(ImGui.getDrawData())
    }
}