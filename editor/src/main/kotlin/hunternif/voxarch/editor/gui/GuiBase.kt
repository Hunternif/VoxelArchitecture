package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.util.loadFromResources
import imgui.ImFontConfig
import imgui.ImFontGlyphRangesBuilder
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
        initFonts()
        imGuiGlfw.init(windowHandle, true)
        imGuiGl3.init("#version 130")
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0)
    }

    fun initFonts() {
        val io = ImGui.getIO()
        io.fonts.addFontDefault()
        val fontConfig = ImFontConfig().apply {
            mergeMode = true
            rasterizerMultiply = 1.5f // this makes fonts crisper
        }
        val glyphRanges = ImFontGlyphRangesBuilder().apply {
            addRanges(io.fonts.glyphRangesDefault)
            addRanges(FontAwesomeIcons._IconRange)
        }.buildRanges()
        io.fonts.apply {
            addFontFromMemoryTTF(
                loadFromResources("fonts/fa-regular-400.ttf"),
                14f, fontConfig, glyphRanges
            )
            addFontFromMemoryTTF(
                loadFromResources("fonts/fa-solid-900.ttf"),
                14f, fontConfig, glyphRanges
            )
            build()
        }
        fontConfig.destroy()
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