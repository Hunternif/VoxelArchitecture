package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.render.FrameBuffer
import hunternif.voxarch.editor.render.msaa.FrameBufferMSAA
import hunternif.voxarch.editor.render.Viewport
import imgui.ImGui
import imgui.ImGuiWindowClass
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.internal.flag.ImGuiDockNodeFlags

class MainGui(val app: EditorApp) : GuiBase() {
    @PublishedApi internal val vp = Viewport(0, 0, 0, 0)
    @PublishedApi internal var mainWindowFbo = FrameBuffer()
    @PublishedApi internal val fpsCounter = FpsCounter()
    @PublishedApi internal val nodeProperties = GuiNodeProperties(app)

    fun init(windowHandle: Long, viewport: Viewport, samplesMSAA: Int = 0) {
        super.init(windowHandle)
        vp.set(viewport)
        if (samplesMSAA > 0) mainWindowFbo = FrameBufferMSAA(samplesMSAA)
        mainWindowFbo.init(viewport)
    }

    inline fun render(crossinline renderMainWindow: (Viewport) -> Unit) = runFrame {
        fpsCounter.run()
        dockspace(
            HorizontalSplit(
                rightRatio = 0.25f,
                left = Window("main_window"),
                right = VerticalSplit(
                    bottomRatio = 0.25f,
                    top = Window("Node tree"),
                    bottom = Window("Properties"),
                ),
            )
        )
        mainWindow("main_window") { vp ->
            renderMainWindow(vp)
            if (DEBUG) overlay("debug_overlay", Corner.TOP_RIGHT,
                padding = 0f) {
                ImGui.text("Tool: ${app.currentTool.toolName}")
                ImGui.text("%.0f fps".format(fpsCounter.fps))
            }
            overlay("camera_controls", Corner.BOTTOM_RIGHT,
                innerPadding=0f, bgAlpha=1f) {
                bigIconButton(FontAwesomeIcons.Compress, "Recenter camera") {
                    app.centerCamera()
                }
            }
            overlay("left_toolbar", Corner.TOP_LEFT,
                padding = 0f, bgAlpha=1f) {
                toolButton(Tool.SELECT)
                toolButton(Tool.ADD_NODE)
                toolButton(Tool.MOVE)
                toolButton(Tool.RESIZE)
            }
            overlay("top_toolbar", Corner.TOP_LEFT,
                padding = 0f, bgAlpha=0f, offsetX = 32f) {
                topPanel()
            }
        }
        rightPanel("Node tree", hasPadding = false) {
            nodeTree()
        }
        rightPanel("Properties") {
            app.selectedNodes.run {
                when (size) {
                    0 -> {
                        nodeProperties.node = null
                        nodeProperties.text = ""
                    }
                    1 -> nodeProperties.node = first()
                    else -> {
                        nodeProperties.node = null
                        nodeProperties.text = "$size nodes"
                    }
                }
            }
            nodeProperties.render()
        }
    }

    @PublishedApi
    internal inline fun mainWindow(
        title: String,
        crossinline renderWindow: (Viewport) -> Unit
    ) {
        ImGui.setNextWindowClass(
            ImGuiWindowClass().apply {
                dockNodeFlagsOverrideSet = 0 or
                    ImGuiDockNodeFlags.NoTabBar or
                    ImGuiDockNodeFlags.NoDocking
            }
        )
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        val mainWindowFlags = 0 or
            ImGuiWindowFlags.NoMove or
            ImGuiWindowFlags.NoScrollbar
        if (ImGui.begin(title, mainWindowFlags)) {
            val pos = ImGui.getWindowPos()
            val vMin = ImGui.getWindowContentRegionMin()
            val vMax = ImGui.getWindowContentRegionMax()
            vp.set(
                vMin.x + pos.x,
                vMin.y + pos.y,
                vMax.x - vMin.x,
                vMax.y - vMin.y
            )
            mainWindowFbo.setViewport(vp)
            mainWindowFbo.render {
                renderWindow(vp)
                ImGui.image(mainWindowFbo.texture.texID,
                    vMax.x - vMin.x, vMax.y - vMin.y, 0f, 1f, 1f, 0f)
            }
        }
        ImGui.end()
        ImGui.popStyleVar(3)
    }

    @PublishedApi
    internal inline fun rightPanel(
        title: String,
        hasPadding: Boolean = true,
        crossinline renderWindow: () -> Unit
    ) {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
        if (!hasPadding)
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        if (ImGui.begin(title, 0)) {
            renderWindow()
        }
        ImGui.end()
        ImGui.popStyleVar(2)
        if (!hasPadding) ImGui.popStyleVar()
    }
}