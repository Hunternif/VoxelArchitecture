package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.centerCamera
import hunternif.voxarch.editor.render.FrameBuffer
import hunternif.voxarch.editor.render.msaa.FrameBufferMSAA
import hunternif.voxarch.editor.render.Viewport
import imgui.ImGui
import imgui.ImGuiWindowClass
import imgui.ImVec2
import imgui.flag.ImGuiDir
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt
import imgui.internal.ImGui as DockImGui

class DockedGui(val app: EditorApp) : GuiBase() {
    @PublishedApi internal val vp = Viewport(0, 0, 0, 0)
    @PublishedApi internal var mainWindowFbo = FrameBuffer()

    private var firstTime = true

    fun init(windowHandle: Long, viewport: Viewport, samplesMSAA: Int = 0) {
        super.init(windowHandle)
        vp.set(viewport)
        if (samplesMSAA > 0) mainWindowFbo = FrameBufferMSAA()
        mainWindowFbo.init(viewport)
    }

    inline fun render(crossinline renderMainWindow: (Viewport) -> Unit) = runFrame {
        horizontalDockspace(0.25f, "main window", "right panel")
        mainWindow("main window") { vp ->
            renderMainWindow(vp)
            overlay("top right overlay", Corner.TOP_RIGHT) {
                ImGui.text("overlay")
            }
            overlay("bottom right overlay", Corner.BOTTOM_RIGHT,
                innerPadding=0f, bgAlpha=1f) {
                iconButton(FontAwesomeIcons.Compress, "Recenter camera") {
                    app.centerCamera()
                }
            }
        }
        rightPanel("right panel") {
            ImGui.text("Node tree explorer goes here")
        }
    }

    @PublishedApi
    internal inline fun mainWindow(
        title: String,
        crossinline renderWindow: (Viewport) -> Unit
    ) {
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
        if (ImGui.begin(title, ImGuiWindowFlags.NoMove)) {
            renderWindow()
        }
        ImGui.end()
    }

    @PublishedApi
    internal fun horizontalDockspace(
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
            // hide tab bar for the main window
            ImGui.setNextWindowClass(
                ImGuiWindowClass().apply {
                    dockNodeFlagsOverrideSet = ImGuiDockNodeFlags.NoTabBar
                }
            )
            DockImGui.dockBuilderDockWindow(leftWindow, leftId.get())
            DockImGui.dockBuilderDockWindow(rightWindow, rightId.get())

            DockImGui.dockBuilderFinish(dockspaceId)
        }
        ImGui.end()
        ImGui.popStyleVar(3)
    }
}