package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.util.loadFromResources
import imgui.ImFont
import imgui.ImFontConfig
import imgui.ImFontGlyphRangesBuilder
import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.glfw.GLFW

/**
 * Encapsulates ImGui setup methods.
 */
object GuiBase {
    @PublishedApi internal val imGuiGlfw = ImGuiImplGlfw()
    @PublishedApi internal val imGuiGl3 = ImGuiImplGl3()
    var window: Long = 0

    fun init(window: Long) {
        this.window = window
        ImGui.createContext()
        val io = ImGui.getIO()
        io.configFlags = io.configFlags or ImGuiConfigFlags.DockingEnable
        io.iniFilename = null // This prevents "imgui.ini" from saving
        initFonts()
        imGuiGlfw.init(window, true)
        imGuiGl3.init("#version 130")
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0)
    }

    lateinit var fontDefault: ImFont
    lateinit var fontSmallIcons: ImFont
    lateinit var fontMediumIcons: ImFont
    lateinit var fontBigIcons: ImFont

    private fun initFonts() {
        val io = ImGui.getIO()
        fontDefault = io.fonts.addFontDefault()

        val fontConfigNewFont = ImFontConfig().apply { mergeMode = false }
        // config to merge solid & regular icons together
        val fontConfigMergeFont = ImFontConfig().apply { mergeMode = true }
        val iconRanges = ImFontGlyphRangesBuilder().apply {
            addRanges(io.fonts.glyphRangesDefault)
            addRanges(FontAwesomeIcons._IconRange)
        }.buildRanges()

        io.fonts.apply {
            fontSmallIcons = addFontDefault()
            addFontFromMemoryTTF(
                loadFromResources("fonts/fa-regular-400.ttf"),
                11f, fontConfigMergeFont, iconRanges
            )
            addFontFromMemoryTTF(
                loadFromResources("fonts/fa-solid-900.ttf"),
                11f, fontConfigMergeFont, iconRanges
            )

            fontMediumIcons = addFontFromMemoryTTF(
                loadFromResources("fonts/fa-regular-400.ttf"),
                14f, fontConfigNewFont, iconRanges
            )
            addFontFromMemoryTTF(
                loadFromResources("fonts/fa-solid-900.ttf"),
                14f, fontConfigMergeFont, iconRanges
            )

            fontBigIcons = addFontFromMemoryTTF(
                loadFromResources("fonts/fa-regular-400.ttf"),
                16f, fontConfigNewFont, iconRanges
            )
            addFontFromMemoryTTF(
                loadFromResources("fonts/fa-solid-900.ttf"),
                16f, fontConfigMergeFont, iconRanges
            )
            build()
        }
        fontConfigNewFont.destroy()
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