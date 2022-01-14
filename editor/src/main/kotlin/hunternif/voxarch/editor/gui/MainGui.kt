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

    fun init(windowHandle: Long, viewport: Viewport, samplesMSAA: Int = 0) {
        super.init(windowHandle)
        vp.set(viewport)
        if (samplesMSAA > 0) mainWindowFbo = FrameBufferMSAA()
        mainWindowFbo.init(viewport)
    }

    inline fun render(crossinline renderMainWindow: (Viewport) -> Unit) = runFrame {
        fpsCounter.run()
        dockspace(
            HorizontalSplit(
                rightRatio = 0.25f,
                right = Window("Node tree"),
                left = Window("main_window"),
            )
        )
        mainWindow("main_window") { vp ->
            renderMainWindow(vp)
            if (DEBUG) overlay("debug_overlay", Corner.TOP_RIGHT,
                padding = 0f) {
                ImGui.text("Tool: ${app.currentTool.description}")
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
            }
        }
        rightPanel("Node tree") {
            nodeTree()
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
        crossinline renderWindow: () -> Unit
    ) {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        if (ImGui.begin(title, 0)) {
            renderWindow()
        }
        ImGui.end()
        ImGui.popStyleVar(3)
    }
}