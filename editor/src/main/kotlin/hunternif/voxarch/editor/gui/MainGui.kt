package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.DEBUG
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.centerCamera
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
                left = HorizontalSplit(
                    leftSize = 32,
                    left = Window("left_toolbar"),
                    right = VerticalSplit(
                        bottomSize = 32,
                        bottom = Window("bottom_toolbar"),
                        top = Window("main_window"),
                    ),
                ),
            )
        )
        toolbar("left_toolbar")
        toolbar("bottom_toolbar")
        mainWindow("main_window") { vp ->
            renderMainWindow(vp)
            if (DEBUG) overlay("debug overlay", Corner.TOP_RIGHT,
                padding = 0f) {
                ImGui.text("%.0f fps".format(fpsCounter.fps))
            }
            overlay("camera controls", Corner.BOTTOM_RIGHT,
                innerPadding=0f, bgAlpha=1f) {
                iconButton(FontAwesomeIcons.Compress, "Recenter camera") {
                    app.centerCamera()
                }
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
            ImGui.popStyleVar(3)
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
            ImGui.popStyleVar(3)
            renderWindow()
        }
        ImGui.end()
    }
}